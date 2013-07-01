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
import java.net.MalformedURLException;
import java.net.URL;

import javax.naming.OperationNotSupportedException;

import net.opengis.ows.x11.VersionType;
import net.opengis.sos.x10.GetCapabilitiesDocument;
import net.opengis.sos.x10.GetCapabilitiesDocument.GetCapabilities;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.oss.sir.SirConfig;
import org.n52.sir.SirConstants;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.name.Named;

/**
 * @author Jan Schulte, Daniel Nüst (daniel.nuest@uni-muenster.de)
 * 
 */
public class Client {

    private static final String GET_METHOD = "GET";

    private static Logger log = LoggerFactory.getLogger(Client.class);

    private static final String POST_METHOD = "POST";

    private static final int CONNECTION_TIMEOUT = 1000 * 30;

    private URL url;

    public Client(URL url) {
        this.url = url;
    }

    public Client(@Named(SirConfig.SERVICEURL)
    String url) throws MalformedURLException {
        this.url = new URL(url);
    }

    private static String createGetCapabilities(String serviceType) {
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
    private static XmlObject doSend(String request, String requestMethod, String url) throws UnsupportedEncodingException,
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
            log.trace("Ignoring url for GET request: ", url);

            if (log.isDebugEnabled())
                log.debug("Client connecting via GET to " + request);

            HttpGet get = new HttpGet(request);
            method = get;
        }
        else if (requestMethod.equals(POST_METHOD)) {
            if (log.isDebugEnabled())
                log.debug("Client connecting via POST to " + url);
            HttpPost postMethod = new HttpPost(url.toString());

            postMethod.setEntity(new StringEntity(request, ContentType.APPLICATION_XML));

            method = postMethod;
        }
        else {
            throw new IllegalArgumentException("requestMethod not supported!");
        }

        try {
            HttpResponse httpResponse = client.execute(method);

            StatusLine status = httpResponse.getStatusLine();
            log.debug("Got response, status: {}", status);

            int code = status.getStatusCode();
            switch (code) {
            case 200:
                XmlObject responseObject = XmlObject.Factory.parse(httpResponse.getEntity().getContent());
                return responseObject;
            case 404:
                throw new RuntimeException(method.getURI().toString() + " --- " + status.toString());
            default:
                throw new RuntimeException("Unhandled response code!");
            }
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
            log.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }

    public URL getURL() {
        return this.url;
        /*
         * try { SirConfigurator conf = SirConfigurator.getInstance(); if(conf == null) throw new
         * RuntimeException("SirConfigurator is null, cannot use this client: ");
         * 
         * URL url = conf.getServiceUrl(); URI uri = url.toURI(); return uri; } catch (URISyntaxException e) {
         * throw new OwsExceptionReport("Could not transform service URL to URI", e); }
         */
    }

    public static XmlObject requestCapabilities(String serviceType, URL url) {
        // create getCapabilities request
        String gcDoc = createGetCapabilities(serviceType);

        if (log.isDebugEnabled())
            log.debug("GetCapabilities to be send to " + serviceType + " @ " + url.toString() + ": " + gcDoc);

        // send getCapabilities request
        XmlObject caps = null;
        XmlObject getCapXmlResponse = null;
        try {
            XmlObject obj = XmlObject.Factory.parse(gcDoc);
            getCapXmlResponse = xSendPostRequest(obj, url);
            caps = XmlObject.Factory.parse(getCapXmlResponse.getDomNode());
            return caps;
        }
        catch (XmlException | OwsExceptionReport | IOException | HttpException e) {
            String msg = "Error on parsing Capabilities document: " + e.getMessage()
                    + (getCapXmlResponse == null ? "" : "\n" + getCapXmlResponse.xmlText());
            log.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }

    /**
     * 
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     * @throws HttpException
     * @throws IOException
     * @throws OwsExceptionReport
     * @throws OperationNotSupportedException
     */
    public String sendGetRequest(String request) throws UnsupportedEncodingException,
            HttpException,
            IOException,
            OwsExceptionReport,
            OperationNotSupportedException {
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
     * @throws OperationNotSupportedException
     */
    public String sendPostRequest(String request) throws IOException,
            OwsExceptionReport,
            HttpException,
            OperationNotSupportedException {
        URL u = getURL();
        if (u == null)
            throw new OperationNotSupportedException("URL is not defined.");
        return sendPostRequest(request, u);
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
    public static String sendPostRequest(String request, URL serviceURL) throws UnsupportedEncodingException,
            HttpException,
            IOException,
            OwsExceptionReport {
        if (request.isEmpty()) {
            return "The request is empty!";
        }

        XmlObject response = doSend(request, POST_METHOD, serviceURL.toString());
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
     * @throws OperationNotSupportedException
     */
    public XmlObject xSendGetRequest(String request) throws UnsupportedEncodingException,
            HttpException,
            IOException,
            OwsExceptionReport,
            OperationNotSupportedException {
        if (log.isDebugEnabled())
            log.debug("Sending request: " + request);
        XmlObject response = doSend(request, GET_METHOD, null);
        return response;
    }

    /**
     * 
     * @param uri
     * @return
     * @throws OwsExceptionReport
     */
    public static XmlObject xSendGetRequest(URL url) throws OwsExceptionReport {
        if (log.isDebugEnabled())
            log.debug("Sending request: " + url);
        XmlObject response;
        try {
            response = doSend(null, GET_METHOD, url.toString());
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
     * @throws OperationNotSupportedException
     */
    public XmlObject xSendPostRequest(XmlObject request) throws IOException,
            OwsExceptionReport,
            HttpException,
            OperationNotSupportedException {
        if (log.isDebugEnabled())
            log.debug("Sending request: " + request);
        URL u = getURL();
        if (u == null)
            throw new OperationNotSupportedException("URI is not defined, use setURI().");
        return xSendPostRequest(request, u);
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
    public static XmlObject xSendPostRequest(XmlObject request, URL serviceURL) throws UnsupportedEncodingException,
            HttpException,
            IOException,
            OwsExceptionReport {

        String requestString = request.xmlText();
        String url = serviceURL.toString();
        XmlObject response = doSend(requestString, POST_METHOD, url);
        return response;
    }

}
