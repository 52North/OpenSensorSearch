/**
 * ﻿Copyright (C) 2012 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.sor.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.xml.namespace.QName;

import net.opengis.ows.x11.ExceptionReportDocument;
import net.opengis.swe.x101.PhenomenonDocument;
import net.opengis.swe.x101.PhenomenonType;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.n52.sor.OwsExceptionReport;
import org.n52.sor.OwsExceptionReport.ExceptionCode;
import org.n52.sor.PhenomenonManager;
import org.n52.sor.PropertiesManager;
import org.n52.sor.util.XmlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sor.x031.GetDefinitionRequestDocument;
import org.x52North.sor.x031.GetDefinitionRequestDocument.GetDefinitionRequest;
import org.x52North.sor.x031.GetDefinitionURIsRequestDocument;
import org.x52North.sor.x031.GetDefinitionURIsRequestDocument.GetDefinitionURIsRequest;
import org.x52North.sor.x031.GetDefinitionURIsResponseDocument;
import org.x52North.sor.x031.GetDefinitionURIsResponseDocument.GetDefinitionURIsResponse;

import com.google.inject.servlet.RequestScoped;

/**
 * 
 * A very simple restful API for the SOR.
 * 
 * @author Jan Schulte, Daniel Nüst
 * 
 */
@Path("/sor/rest")
@RequestScoped
public class Frontend extends RestWebService {

    private final static Logger log = LoggerFactory.getLogger(Frontend.class);

    private static final String POST_REQUEST_METHOD = "POST";

    private static final List<String> CHARS_TO_DECODE = Arrays.asList(new String[] {"%3A", "%2B", "%5D", "%5B"});

    /**
     * 
     * @param stream
     * @return
     * @throws IOException
     */
    public static String inputStreamAsString(InputStream stream) {
        try (InputStreamReader isr = new InputStreamReader(stream);) {
            StringBuilder sb = new StringBuilder();
            String line = null;

            try (BufferedReader br = new BufferedReader(isr);) {
                while ( (line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            }

            return sb.toString();
        }
        catch (IOException e1) {
            return "IOException reading InputStream " + stream.toString();

        }
    }

    private String serviceURL;

    private String[] cachedAvailableResources;

    private Date cachingDate;

    private static final QName PHENOMENON_RETURN_ELEMENT = new QName("http://www.opengis.net/swe/1.0/gml32",
                                                                     "Phenomenon");

    private static final String OGC_DEFINITION_BRANCH_PREFIX = "urn:ogc:def";

    private long cacheExprirationTimeSeconds = 0;

    public Frontend() {
        PropertiesManager props = PropertiesManager.getInstance();
        this.cachingDate = new Date();
        this.cacheExprirationTimeSeconds = Long.parseLong(props.getRestChachingTime());
    }

    private boolean containsEncodedCharacters(String requestURL) {
        for (String s : CHARS_TO_DECODE) {
            if (requestURL.contains(s))
                return true;
        }
        return false;
    }

    @GET
    public void doGet(@Context
    HttpServletRequest req, @Context
    HttpServletResponse resp) throws ServletException, IOException {
        String requestURL = req.getRequestURL().toString();

        if (containsEncodedCharacters(requestURL)) {
            requestURL = URLDecoder.decode(requestURL, PropertiesManager.getInstance().getUrlDecoderEncoding());
        }

        String servletPath = req.getServletPath();
        String[] resources = getResourcesStringArray(requestURL, servletPath);

        log.debug("Request URL: {}", requestURL);

        try {
            // check if base url or resources in URL are given, if no resources in URL, just render available
            // phenomena
            if (resources.length < 3) {
                writeHtmlResourceListing(resp, requestURL, servletPath, resources);
            }
            else if (resources.length >= 3) {
                // send getDefinition to get individual dictionary entry (possibly versioned)

                // build urn from given parameters
                String phenomenon = getIdentifier(resources);

                // request actual definition
                XmlObject def = requestDefinition(phenomenon, this.serviceURL);

                if (def instanceof ExceptionReportDocument) {
                    writeXmlResponse(resp, def);
                }
                else if (def instanceof PhenomenonDocument) {
                    /* all good */
                    writeXmlResponse(resp, def);
                }
                else if (def instanceof PhenomenonType) {
                    // little trick not to have a xml-fragment as the outermost element
                    XmlOptions trick = new XmlOptions();
                    trick.setSaveSyntheticDocumentElement(PHENOMENON_RETURN_ELEMENT);
                    // System.out.println(obj.xmlText(new XmlOptions().setSaveOuter())); does not work because
                    // it needs to be done on the server side already!

                    writeXmlResponse(resp, def);
                }
                else {
                    // XmlObject not recognized
                    throw new OwsExceptionReport(ExceptionCode.NoApplicableCode,
                                                 null,
                                                 "Definition was of unrecognized type:\n" + def.xmlText());
                }
            }
            else {
                // Neither zero nor more than three resources
                throw new OwsExceptionReport(ExceptionCode.InvalidRequest,
                                             null,
                                             "URL containts to many, not enough, or unavailable resources! Allowed patterns are '.../REST and '>/REST/observable/<AUTHORITY>/<NAME_OF_OBSERVABLE>/<VERSION (optional)>'");
            }
        }
        catch (OwsExceptionReport e) {
            log.error("Error servicing REST request", e);
            writeXmlResponse(resp, e.getDocument());
        }
    }

    private String[] extractAuthorities(String objectType, String[] uris) {
        HashSet<String> authorities = new HashSet<>();

        for (String uri : uris) {
            if (uri.contains(objectType)) {
                String s = uri.substring(uri.indexOf(objectType) + objectType.length() + 1);
                s = s.substring(0, s.indexOf(":"));
                authorities.add(s);
            }
        }

        return authorities.toArray(new String[0]);
    }

    private String[] extractObjectTypes(String[] uris) {
        HashSet<String> objectTypes = new HashSet<>();

        for (String uri : uris) {
            String s = uri.substring(uri.indexOf(OGC_DEFINITION_BRANCH_PREFIX) + OGC_DEFINITION_BRANCH_PREFIX.length()
                    + 1);
            s = s.substring(0, s.indexOf(":"));
            objectTypes.add(s);
        }

        return objectTypes.toArray(new String[0]);
    }

    private String[] extractObservableNames(String objectType, String authority, String[] uris) {
        ArrayList<String> observables = new ArrayList<>();

        for (String uri : uris) {
            String typeAndAuth = objectType + ":" + authority;
            if (uri.contains(typeAndAuth)) {
                String s = uri.substring(uri.indexOf(typeAndAuth) + typeAndAuth.length() + 1);
                s = s.substring(s.indexOf(":") + 1, s.length());

                // TODO remove illegal characters?!

                observables.add(s);
            }
        }

        return observables.toArray(new String[0]);
    }

    private String getAuthority(String[] resources) {
        return resources[1];
    }

    private String getIdentifier(String[] resources) {
        String objectType = getObjectType(resources);
        String authority = getAuthority(resources);
        String observableName = getObservableName(resources);
        String version = getVersion(resources);

        StringBuilder sb = new StringBuilder();

        sb.append(OGC_DEFINITION_BRANCH_PREFIX);
        sb.append(":");

        sb.append(objectType);
        sb.append(":");

        sb.append(authority);
        sb.append(":");

        if ( !version.isEmpty())
            sb.append(version);
        sb.append(":");

        sb.append(observableName);

        return sb.toString();
    }

    private String getObjectType(String[] resources) {
        return resources[0];
    }

    private String getObservableName(String[] resources) {
        return resources[2];
    }

    private String getVersion(String[] resources) {
        if (resources.length == 4) {
            return resources[3];
        }
        return "";
    }

    private String[] requestAvailableResources() throws ServletException, IOException, OwsExceptionReport {
        if (this.cachedAvailableResources == null || this.cachingDate.before(new Date())) {
            int numberOfEntries = PhenomenonManager.getInstance().getPhenomenaList().size();

            // request DefinitionURIs with correct number of entries
            GetDefinitionURIsResponse getDefResp = requestDefinitionURIs(numberOfEntries, this.serviceURL);
            this.cachedAvailableResources = getDefResp.getDefinitionURIArray();

            this.cachingDate.setTime(System.currentTimeMillis() + this.cacheExprirationTimeSeconds * 1000);
        }

        return this.cachedAvailableResources;
    }

    private XmlObject requestDefinition(String inputURI, String sURL) throws IOException, ServletException {
        log.debug("Requesting definition for {} from {}", inputURI, sURL);

        GetDefinitionRequestDocument getDefReqDoc = GetDefinitionRequestDocument.Factory.newInstance();
        GetDefinitionRequest getDefReq = getDefReqDoc.addNewGetDefinitionRequest();
        getDefReq.setInputURI(inputURI);

        try (InputStream is = sendPostMessage(sURL, getDefReqDoc);) {
            XmlObject obj = XmlObject.Factory.parse(is);
            return obj;
        }
        catch (IOException e) {
            log.error("Could not request definition for URI " + inputURI + " from SOR @ " + sURL);
            throw new IOException("Could not request definition for URI " + inputURI + " from SOR @ " + sURL, e);
        }
        catch (XmlException e) {
            log.error("Could not parse response from SOR @ " + sURL);
            throw new ServletException("Could not parse response from SOR @ " + sURL + "\n", e);
        }
    }

    private GetDefinitionURIsResponse requestDefinitionURIs(int maxNumberOfResults, String sURL) throws IOException,
            ServletException {
        GetDefinitionURIsRequestDocument getDefURIsReqDoc = GetDefinitionURIsRequestDocument.Factory.newInstance();
        GetDefinitionURIsRequest getDefURIsReq = getDefURIsReqDoc.addNewGetDefinitionURIsRequest();
        getDefURIsReq.setMaxNumberOfResults(maxNumberOfResults);
        GetDefinitionURIsResponseDocument response;

        try (InputStream is = sendPostMessage(sURL, getDefURIsReqDoc);) {

            try {

                response = GetDefinitionURIsResponseDocument.Factory.parse(is);
                return response.getGetDefinitionURIsResponse();
            }
            catch (IOException e) {
                log.error("Could not request definition URIs from SOR @ " + sURL);
                throw new IOException("Could not request definition URIs from SOR @ " + sURL, e);
            }
            catch (XmlException e) {
                log.error("Could not parse response from SOR @ " + sURL + "\n" + inputStreamAsString(is));
                throw new ServletException("Could not parse response from SOR @ " + sURL + "\n"
                        + inputStreamAsString(is), e);
            }
        }
    }

    /**
     * 
     * @param sURL
     * @param request
     * @return
     */
    private InputStream sendPostMessage(String sURL, XmlObject request) throws IOException {

        log.info("Request: " + request);
        URL location = new URL(sURL);

        HttpURLConnection con = (HttpURLConnection) location.openConnection();
        con.setRequestMethod(POST_REQUEST_METHOD);
        con.setDoOutput(true);

        XmlOptions options = new XmlOptions();
        options.setUseDefaultNamespace();
        options.setSaveAggressiveNamespaces();

        try (OutputStream out = con.getOutputStream();) {
            request.save(out, options);
        }

        InputStream is = con.getInputStream();
        return is;
    }

    private void writeHtmlResourceListing(HttpServletResponse resp,
                                          String requestURL,
                                          String servletPath,
                                          String[] resources) throws ServletException, IOException, OwsExceptionReport {
        String[] availableResources = requestAvailableResources();
        String headline = "";

        if (resources.length == 0) {
            availableResources = extractObjectTypes(availableResources); // new String[]
                                                                         // {OBJECT_TYPE_OBSERVABLE};
            headline = "Available object types:";
        }
        else if (resources.length == 1) {
            availableResources = extractAuthorities(resources[0], availableResources);
            headline = "Available authorities:";
        }
        else if (resources.length == 2) {
            // return a list of all phenomena
            headline = "Available observables of type " + getObjectType(resources) + " from authority "
                    + getAuthority(resources) + ":";

            availableResources = extractObservableNames(resources[0], resources[1], availableResources);
        }
        else if (resources.length == 3) {
            // TODO add handling of observed properties

        }
        else {
            throw new OwsExceptionReport(ExceptionCode.InvalidRequest, null, "URL not valid!");
        }

        // write the listing to a HTML string
        String htmlString = renderResourcesHtml(requestURL, servletPath, headline, availableResources);

        try (OutputStream out = resp.getOutputStream();) {
            resp.setContentType(PropertiesManager.getInstance().getResponseContentTypeHtml());
            resp.setCharacterEncoding(RestWebService.RESPONSE_CHARSET);
            out.write(htmlString.getBytes());
        }
    }

    private void writeXmlResponse(HttpServletResponse resp, XmlObject def) throws IOException {
        log.debug("Writing XML response:\n{}", def);

        try (OutputStream out = resp.getOutputStream();) {
            resp.setContentType(PropertiesManager.getInstance().getResponseContentTypeXml());
            out.write(def.xmlText(XmlTools.DEFAULT_OPTIONS).getBytes());
        }
    }

}