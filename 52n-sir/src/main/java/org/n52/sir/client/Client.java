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

package org.n52.sir.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import net.opengis.ows.x11.VersionType;
import net.opengis.sos.x10.GetCapabilitiesDocument;
import net.opengis.sos.x10.GetCapabilitiesDocument.GetCapabilities;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sir.SirConstants;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * @author Jan Schulte, Daniel Nüst (daniel.nuest@uni-muenster.de)
 * 
 */
public class Client {

    private static final String GET_METHOD = "GET";

    // private static final String HTML_TAG_IN_RESPONSE = "<html>";

    private static Logger log = LoggerFactory.getLogger(Client.class);

    private static final String POST_METHOD = "POST";

    private static final int CONNECTION_TIMEOUT = 1000 * 30;

    private URI sirURI;

    @Inject
    public Client(@Named("oss.sir.serviceurl")
    String sirUrl) {
        this.sirURI = URI.create(sirUrl);

        log.info("NEW {}", this);
    }

    private String createGetCapabilities(String serviceType) {
        if (serviceType.equals(SirConstants.SOS_SERVICE_TYPE)) {
            GetCapabilitiesDocument gcdoc = GetCapabilitiesDocument.Factory.newInstance();
            GetCapabilities gc = gcdoc.addNewGetCapabilities();
            gc.setService(serviceType);
            VersionType version = gc.addNewAcceptVersions().addNewVersion();
            version.setStringValue(SirConstants.SOS_VERSION);
            return gcdoc.xmlText();
        }
        if (serviceType.equals(SirConstants.SPS_SERVICE_TYPE)) {
            net.opengis.sps.x10.GetCapabilitiesDocument gcdoc = net.opengis.sps.x10.GetCapabilitiesDocument.Factory.newInstance();
            net.opengis.sps.x10.GetCapabilitiesDocument.GetCapabilities gc = gcdoc.addNewGetCapabilities();
            gc.setService(serviceType);
            return gcdoc.xmlText();
        }

        throw new IllegalArgumentException("Service type not supported: " + serviceType);
    }

    /**
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     * @throws IOException
     * @throws HttpException
     * @throws OwsExceptionReport
     */
    private XmlObject doSend(String request, String requestMethod, URI uri) throws UnsupportedEncodingException,
            IOException,
            HttpException,
            OwsExceptionReport {
        if (log.isDebugEnabled())
            log.debug("Sending request (first 100 characters): "
                    + request.substring(0, Math.min(request.length(), 100)));

        HttpClient client = new DefaultHttpClient();
        // configure timeout to handle really slow servers
        HttpParams params = client.getParams();
        HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, CONNECTION_TIMEOUT);

        HttpRequestBase method = null;

        if (requestMethod.equals(GET_METHOD)) {
            if (log.isDebugEnabled())
                log.debug("Client connecting via GET to " + uri);

            HttpGet get = new HttpGet(request);
            method = get;
        }
        else if (requestMethod.equals(POST_METHOD)) {
            if (log.isDebugEnabled())
                log.debug("Client connecting via POST to " + uri);
            HttpPost postMethod = new HttpPost(uri.toString());

            // postMethod.setRequestEntity(new StringRequestEntity(request, REQUEST_CONTENT_TYPE,
            // REQUEST_CONTENT_CHARSET));
            postMethod.setEntity(new StringEntity(request,
                                                  SirConstants.REQUEST_CONTENT_TYPE,
                                                  SirConstants.REQUEST_CONTENT_CHARSET));

            method = postMethod;
        }
        else {
            throw new IllegalArgumentException("requestMethod not supported!");
        }

        try {
            HttpResponse httpResponse = client.execute(method);

            XmlObject responseObject = XmlObject.Factory.parse(httpResponse.getEntity().getContent());
            return responseObject;
        }
        catch (XmlException e) {
            log.error("Error parsing response.", e);

            // TODO add handling to identify HTML response
            // if (responseString.contains(HTML_TAG_IN_RESPONSE)) {
            // log.error("Received HTML!\n" + responseString + "\n");
            // }

            String msg = "Could not parse response (received via " + requestMethod + ") to the request " + request
                    + "\n\n\n" + Tools.getStackTrace(e);
            // msg = msg + "\n\nRESPONSE STRING:\n<![CDATA[" + responseObject.xmlText() + "]]>";

            OwsExceptionReport er = new OwsExceptionReport(ExceptionCode.NoApplicableCode, "Client.doSend()", msg);
            return er.getDocument();
        }
        catch (Exception e) {
            log.error("Error executing method on httpClient.", e);
            return new OwsExceptionReport(ExceptionCode.NoApplicableCode, "service", e.getMessage()).getDocument();
        }
    }

    // private URI getSirURI() {
    // return this.sirURI;
    // try {
    // return SirConfigurator.getInstance().getServiceUrl().toURI();
    // }
    // catch (URISyntaxException e) {
    // throw new OwsExceptionReport("Could not transform service URL to URI", e);
    // }
    // }

    public XmlObject requestCapabilities(String serviceType, URI uri) throws OwsExceptionReport {
        // create getCapabilities request
        String gcDoc = createGetCapabilities(serviceType);

        if (log.isDebugEnabled())
            log.debug("GetCapabilities to be send to " + serviceType + " @ " + uri.toString() + ": " + gcDoc);

        // send getCapabilities request
        XmlObject caps = null;
        XmlObject getCapXmlResponse = null;
        try {
            getCapXmlResponse = xSendPostRequest(XmlObject.Factory.parse(gcDoc), uri);
            caps = XmlObject.Factory.parse(getCapXmlResponse.getDomNode());
        }
        catch (XmlException xmle) {
            String msg = "Error on parsing Capabilities document: " + xmle.getMessage()
                    + (getCapXmlResponse == null ? "" : "\n" + getCapXmlResponse.xmlText());
            log.warn(msg);
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidRequest, null, msg);
            throw se;
        }
        catch (IOException ioe) {
            String errMsg = "Error sending GetCapabilities to " + serviceType + " @ " + uri.toString() + " : "
                    + ioe.getMessage();
            log.warn(errMsg);
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidRequest, null, errMsg);
            throw se;
        }
        catch (Exception e) {
            String errMsg = "Error doing GetCapabilities to " + serviceType + " @ " + uri.toString() + " : "
                    + e.getMessage();
            log.warn(errMsg);
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidRequest, null, errMsg);
            throw se;
        }

        return caps;
    }

    /**
     * 
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     * @throws HttpException
     * @throws IOException
     * @throws OwsExceptionReport
     */
    public String sendGetRequest(String request) throws UnsupportedEncodingException,
            HttpException,
            IOException,
            OwsExceptionReport {
        if (request.isEmpty()) {
            return "The request is empty!";
        }
        return xSendGetRequest(request).xmlText();
    }

    /**
     * 
     * send to SIR
     * 
     * @param request
     * @return
     * @throws IOException
     * @throws OwsExceptionReport
     * @throws HttpException
     */
    public String sendPostRequest(String request) throws IOException, OwsExceptionReport, HttpException {
        return sendPostRequest(request, this.sirURI);
    }

    /**
     * 
     * @param request
     * @param sirURI
     * @return
     * @throws OwsExceptionReport
     * @throws IOException
     * @throws HttpException
     * @throws UnsupportedEncodingException
     */
    public String sendPostRequest(String request, URI serviceURI) throws UnsupportedEncodingException,
            HttpException,
            IOException,
            OwsExceptionReport {
        if (request.isEmpty()) {
            return "The request is empty!";
        }

        XmlObject response = doSend(request, POST_METHOD, serviceURI);
        return response.toString();
    }

    /**
     * 
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     * @throws HttpException
     * @throws IOException
     * @throws OwsExceptionReport
     */
    public XmlObject xSendGetRequest(String request) throws UnsupportedEncodingException,
            HttpException,
            IOException,
            OwsExceptionReport {
        if (log.isDebugEnabled())
            log.debug("Sending request: " + request);
        XmlObject response = doSend(request, GET_METHOD, this.sirURI);
        return response;
    }

    /**
     * 
     * @param uri
     * @return
     * @throws OwsExceptionReport
     */
    public XmlObject xSendGetRequest(URI uri) throws OwsExceptionReport {
        if (log.isDebugEnabled())
            log.debug("Sending request: " + uri);
        XmlObject response;
        try {
            response = doSend("", GET_METHOD, uri);
        }
        catch (UnsupportedEncodingException e) {
            throw new OwsExceptionReport(e);
        }
        catch (HttpException e) {
            throw new OwsExceptionReport(e);
        }
        catch (IOException e) {
            throw new OwsExceptionReport(e);
        }
        catch (OwsExceptionReport e) {
            throw new OwsExceptionReport(e);
        }
        return response;
    }

    /**
     * 
     * @param request
     * @return
     * @throws IOException
     * @throws OwsExceptionReport
     * @throws HttpException
     */
    public XmlObject xSendPostRequest(XmlObject request) throws IOException, OwsExceptionReport, HttpException {
        if (log.isDebugEnabled())
            log.debug("Sending request: " + request);
        return xSendPostRequest(request, this.sirURI);
    }

    /**
     * 
     * @param request
     * @param sirURI
     * @return
     * @throws OwsExceptionReport
     * @throws IOException
     * @throws HttpException
     * @throws UnsupportedEncodingException
     */
    public XmlObject xSendPostRequest(XmlObject request, URI serviceURI) throws UnsupportedEncodingException,
            HttpException,
            IOException,
            OwsExceptionReport {

        XmlObject response = doSend(request.xmlText(), POST_METHOD, serviceURI);
        return response;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Client [sirURI=");
        builder.append(this.sirURI);
        builder.append("]");
        return builder.toString();
    }

}
