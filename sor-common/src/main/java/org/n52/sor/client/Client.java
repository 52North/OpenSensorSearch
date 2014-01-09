/**
 * Copyright 2013 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.sor.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sor.OwsExceptionReport;
import org.n52.sor.OwsExceptionReport.ExceptionCode;
import org.n52.sor.PropertiesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * A simple client to connect to a SOR. It sends the given request string to the url defined
 * 
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class Client {

    private static Logger log = LoggerFactory.getLogger(Client.class);

    private static final String GET_METHOD = "GET";

    private static final String POST_METHOD = "POST";

    /**
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     * @throws IOException
     * @throws HttpException
     */
    private static XmlObject doSend(String request, String requestMethod) throws UnsupportedEncodingException,
            IOException {
            log.debug("Sending request (first 100 characters): {}", request.substring(0, Math.min(request.length(), 100)));

        PropertiesManager pm = PropertiesManager.getInstance();

        // create and set up HttpClient
        try (CloseableHttpClient client = HttpClientBuilder.create().build();) {

            HttpRequestBase method = null;
            if (requestMethod.equals(GET_METHOD)) {
                String sorURL = pm.getServiceEndpointGet();
                    log.debug("Client connecting via GET to {}", sorURL);

                HttpGet get = new HttpGet(request);
                method = get;
            }
            else if (requestMethod.equals(POST_METHOD)) {
                String sorURL = pm.getServiceEndpointPost();
                    log.debug("Client connecting via POST to {}", sorURL);
                HttpPost postMethod = new HttpPost(sorURL.toString());

                postMethod.setEntity(new StringEntity(request,
                                                      PropertiesManager.getInstance().getClientRequestContentType()));

                method = postMethod;
            }
            else
                throw new IllegalArgumentException("requestMethod not supported!");

            HttpResponse httpResponse = client.execute(method);
            XmlObject response = XmlObject.Factory.parse(httpResponse.getEntity().getContent());

            return response;
        }
        catch (XmlException e) {
            log.error("Error parsing response.", e);
        }
        return null;
    }

    /**
     * 
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     * @throws HttpException
     * @throws IOException
     */
    public static String sendGetRequest(String request) throws UnsupportedEncodingException, HttpException, IOException {
        if (request.isEmpty()) {
            return "The request is empty!";
        }
        return xSendGetRequest(request).xmlText();
    }

    public static String sendPostRequest(String request) throws IOException, OwsExceptionReport {
        if (request.isEmpty()) {
            return "The request is empty!";
        }
        XmlObject response = doSend(request, POST_METHOD);
        if (response != null)
            return response.toString();

        throw new OwsExceptionReport(ExceptionCode.NoApplicableCode, "sendPostRequest", "Sent request returned null: "
                + request);
    }

    /**
     * 
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     * @throws HttpException
     * @throws IOException
     */
    public static XmlObject xSendGetRequest(String request) throws UnsupportedEncodingException,
            HttpException,
            IOException {
        log.debug("Sending request: {}", request);
        XmlObject response = doSend(request, GET_METHOD);
        return response;
    }

    public static XmlObject xSendPostRequest(XmlObject request) throws IOException {
        log.debug("Sending request: {}", request);
        XmlObject response = doSend(request.xmlText(), POST_METHOD);
        return response;
    }

}