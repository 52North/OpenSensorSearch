/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */

package org.n52.sir.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import net.opengis.ows.x11.VersionType;
import net.opengis.sos.x10.GetCapabilitiesDocument;
import net.opengis.sos.x10.GetCapabilitiesDocument.GetCapabilities;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sir.SirConfigurator;
import org.n52.sir.SirConstants;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schulte, Daniel Nüst (daniel.nuest@uni-muenster.de)
 * 
 */
public class Client {

    private static final String GET_METHOD = "GET";

    private static final String HTML_TAG_IN_RESPONSE = "<html>";

    private static Logger log = LoggerFactory.getLogger(Client.class);

    private static final String POST_METHOD = "POST";

    private static final String REQUEST_CONTENT_CHARSET = "UTF-8";

    private static final String REQUEST_CONTENT_TYPE = "text/xml";

    private static final String SYSTEM_PROPERTY_PROXY_HOST = "http.proxyHost";

    private static final String SYSTEM_PROPERTY_PROXY_PORT = "http.proxyPort";

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
    private static XmlObject doSend(String request, String requestMethod, URI uri) throws UnsupportedEncodingException,
            IOException,
            HttpException,
            OwsExceptionReport {
        if (log.isDebugEnabled())
            log.debug("Sending request (first 100 characters): "
                    + request.substring(0, Math.min(request.length(), 100)));

        // create and set up HttpClient
        HttpClient httpClient = new HttpClient();
        String host = System.getProperty(SYSTEM_PROPERTY_PROXY_HOST);
        String port = System.getProperty(SYSTEM_PROPERTY_PROXY_PORT);
        if (host != null && host.length() > 0 && port != null && port.length() > 0) {
            int portNumber = Integer.parseInt(port);
            HostConfiguration hostConfig = new HostConfiguration();
            hostConfig.setProxy(host, portNumber);
            httpClient.setHostConfiguration(hostConfig);
        }

        HttpMethodBase method = null;

        if (requestMethod.equals(GET_METHOD)) {
            if (log.isDebugEnabled())
                log.debug("Client connecting via GET to " + uri);
            GetMethod getMethod = new GetMethod(uri.toString());
            if (request != null && !request.isEmpty())
                getMethod.setQueryString(request);

            method = getMethod;
        }
        else if (requestMethod.equals(POST_METHOD)) {
            if (log.isDebugEnabled())
                log.debug("Client connecting via POST to " + uri);
            PostMethod postMethod = new PostMethod(uri.toString());

            postMethod.setRequestEntity(new StringRequestEntity(request, REQUEST_CONTENT_TYPE, REQUEST_CONTENT_CHARSET));

            method = postMethod;
        }
        else {
            throw new IllegalArgumentException("requestMethod not supported!");
        }

        String responseString = "";
        try {
            httpClient.executeMethod(method);
        }
        catch (Exception e) {
            log.error("Error executing method on httpClient.", e);
            responseString = e.getMessage();

            return new OwsExceptionReport(ExceptionCode.NoApplicableCode, "service", responseString).getDocument();
        }

        XmlObject response = null;
        try {
            responseString = new String(method.getResponseBody());

            response = XmlObject.Factory.parse(responseString);
        }
        catch (XmlException e) {
            log.error("Error parsing response.", e);

            if (responseString.contains(HTML_TAG_IN_RESPONSE)) {
                log.error("Received HTML!\n" + responseString + "\n");
            }

            OwsExceptionReport er = new OwsExceptionReport(ExceptionCode.NoApplicableCode,
                                                           "Client.doSend()",
                                                           "Could not parse response (received via " + requestMethod
                                                                   + ") to the request " + request + "\n\n\n"
                                                                   + Tools.getStackTrace(e)
                                                                   + "\n\nRESPONSE STRING:\n<![CDATA[" + responseString
                                                                   + "]]>");
            return er.getDocument();
        }

        return response;
    }

    /**
     * 
     * @return
     * @throws OwsExceptionReport
     */
    private static URI getSirURI() throws OwsExceptionReport {
        try {
            return SirConfigurator.getInstance().getServiceUrl().toURI();
        }
        catch (URISyntaxException e) {
            throw new OwsExceptionReport("Could not transform service URL to URI", e);
        }
    }

    public static XmlObject requestCapabilities(String serviceType, URI uri) throws OwsExceptionReport {
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
    public static String sendGetRequest(String request) throws UnsupportedEncodingException,
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
     */
    public static String sendPostRequest(String request) throws IOException, OwsExceptionReport {
        URI sirURI = getSirURI();
        return sendPostRequest(request, sirURI);
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
    public static String sendPostRequest(String request, URI serviceURI) throws UnsupportedEncodingException,
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
    public static XmlObject xSendGetRequest(String request) throws UnsupportedEncodingException,
            HttpException,
            IOException,
            OwsExceptionReport {
        if (log.isDebugEnabled())
            log.debug("Sending request: " + request);
        URI sirURI = getSirURI();
        XmlObject response = doSend(request, GET_METHOD, sirURI);
        return response;
    }

    /**
     * 
     * @param uri
     * @return
     * @throws OwsExceptionReport
     */
    public static XmlObject xSendGetRequest(URI uri) throws OwsExceptionReport {
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
     */
    public static XmlObject xSendPostRequest(XmlObject request) throws IOException, OwsExceptionReport {
        if (log.isDebugEnabled())
            log.debug("Sending request: " + request);
        URI sirURI = getSirURI();
        return xSendPostRequest(request, sirURI);
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
    public static XmlObject xSendPostRequest(XmlObject request, URI serviceURI) throws UnsupportedEncodingException,
            HttpException,
            IOException,
            OwsExceptionReport {

        XmlObject response = doSend(request.xmlText(), POST_METHOD, serviceURI);
        return response;
    }

}
