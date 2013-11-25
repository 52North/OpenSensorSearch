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

package org.n52.oss.sir;

import java.io.InputStream;
import java.net.URI;

import org.apache.http.HttpResponse;
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
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.oss.util.Tools;
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

    private static Logger log = LoggerFactory.getLogger(Client.class);

    private static final String POST_METHOD = "POST";

    private static final int CONNECTION_TIMEOUT = 1000 * 30;

    private URI uri;

    @Inject
    public Client(@Named("oss.sir.serviceurl")
    String endpoint) {
        this.uri = URI.create(endpoint);

        log.info("NEW {}", this);
    }

    private XmlObject doSend(String request, String requestMethod, URI requestUri) {
        log.debug("Sending request (first 100 characters): {}", request.substring(0, Math.min(request.length(), 100)));

        HttpClient client = new DefaultHttpClient();
        // configure timeout to handle really slow servers
        HttpParams params = client.getParams();
        HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, CONNECTION_TIMEOUT);

        HttpRequestBase method = null;

        if (requestMethod.equals(GET_METHOD)) {
            log.debug("Client connecting via GET to '{}' with request '{}'", requestUri, request);

            String fullUri = null;
            if (request == null || request.isEmpty())
                fullUri = requestUri.toString();
            else
                fullUri = requestUri.toString() + "?" + request;

            log.debug("GET call: {}", fullUri);
            HttpGet get = new HttpGet(fullUri);
            method = get;
        }
        else if (requestMethod.equals(POST_METHOD)) {
            log.debug("Client connecting via POST to {}" + requestUri);
            HttpPost postMethod = new HttpPost(requestUri.toString());

            postMethod.setEntity(new StringEntity(request, ContentType.create(SirConstants.REQUEST_CONTENT_TYPE)));

            method = postMethod;
        }
        else {
            throw new IllegalArgumentException("requestMethod not supported!");
        }

        try {
            HttpResponse httpResponse = client.execute(method);

            try (InputStream is = httpResponse.getEntity().getContent();) {
                XmlObject responseObject = XmlObject.Factory.parse(is);
                return responseObject;
            }
        }
        catch (XmlException e) {
            log.error("Error parsing response.", e);

            // TODO add handling to identify HTML response
            // if (responseString.contains(HTML_TAG_IN_RESPONSE)) {
            // log.error("Received HTML!\n" + responseString + "\n");
            // }

            String msg = "Could not parse response (received via " + requestMethod + ") to the request\n\n" + request
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

    public String sendGetRequest(String request) {
        if (request.isEmpty()) {
            return "The request is empty!";
        }
        return xSendGetRequest(request).xmlText();
    }

    /**
     * send to predefined url
     */
    public String sendPostRequest(String request) {
        return sendPostRequest(request, this.uri);
    }

    public String sendPostRequest(String request, URI serviceURI) {
        if (request.isEmpty()) {
            return "The request is empty!";
        }

        XmlObject response = doSend(request, POST_METHOD, serviceURI);
        return response.toString();
    }

    public XmlObject xSendGetRequest(String request) {
        log.debug("Sending request: {}", request);
        XmlObject response = doSend(request, GET_METHOD, this.uri);
        return response;
    }

    public XmlObject xSendGetRequest(URI requestUri) {
        log.debug("Sending request: {}", requestUri);
        XmlObject response;
        response = doSend(null, GET_METHOD, requestUri);
        return response;
    }

    public XmlObject xSendPostRequest(XmlObject request) {
        log.debug("Sending request: {}", request);
        return xSendPostRequest(request, this.uri);
    }

    public XmlObject xSendPostRequest(XmlObject request, URI serviceURI) {
        XmlObject response = doSend(request.xmlText(), POST_METHOD, serviceURI);
        return response;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Client [sirURI=");
        builder.append(this.uri);
        builder.append("]");
        return builder.toString();
    }

}
