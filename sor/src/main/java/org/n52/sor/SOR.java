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

package org.n52.sor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.n52.sor.OwsExceptionReport.ExceptionCode;
import org.n52.sor.response.SorExceptionReportResponse;
import org.n52.sor.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.servlet.RequestScoped;

/**
 * The Servlet of the SOR, which receives requests and send back the response.
 * 
 * @created 15-Okt-2008 16:25:09
 * @author Jan Schulte, Daniel Nüst
 * @version 1.0
 */
@Path("/sor")
@RequestScoped
public class SOR {

    private static Logger log = LoggerFactory.getLogger(SOR.class);

    private RequestOperator requestOperator = new RequestOperator();

    public SOR() {
        log.info("NEW {}", this);
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public void doGet(@Context
    HttpServletRequest request, @Context
    HttpServletResponse response) {
        String queryString = request.getQueryString();
        log.info("(GET) Connected from: {} {}\nRequest: ",
                 request.getRemoteAddr(),
                 request.getRemoteHost(),
                 queryString);

        ISorResponse sorResponse = this.requestOperator.doGetOperation(queryString);
        log.info("Response: " + sorResponse);

        doResponse(response, sorResponse);
    }

    @POST
    @Produces(MediaType.APPLICATION_XML)
    public void doPost(@Context
    HttpServletRequest request, @Context
    HttpServletResponse response) {
        log.debug("(POST) request!");
        try (InputStream in = request.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(in));) {

            String inputString;
            String decodedString;
            String line;

            StringBuffer sb = new StringBuffer();
            while ( (line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            inputString = sb.toString();

            // remove "request=" input string header
            if (inputString.startsWith("request=")) {
                inputString = inputString.substring(8, inputString.length());
            }

            // decode the query string
            decodedString = URLDecoder.decode(inputString, PropertiesManager.getInstance().getUrlDecoderEncoding());

            log.info("(POST) Connected from: {} {}", request.getRemoteAddr(), request.getRemoteHost());

            ISorResponse sorResponse = this.requestOperator.doPostOperation(decodedString);

            log.info("Returning response: " + sorResponse);

            doResponse(response, sorResponse);
        }
        catch (IOException e) {
            log.error("Could not open input stream request!");
        }
        catch (Exception e) {
            log.error("Could not open process request!", e);
            OwsExceptionReport erd = new OwsExceptionReport(ExceptionCode.NoApplicableCode,
                                                            null,
                                                            "Exception processing the request:\n\n"
                                                                    + Tools.getStackTrace(e) + "\n\n");
            SorExceptionReportResponse r = new SorExceptionReportResponse(erd.getDocument());
            doResponse(response, r);
        }
    }

    private void doResponse(HttpServletResponse response, ISorResponse sorResponse) {
        try (OutputStream out = response.getOutputStream();) {
            String ct = PropertiesManager.getInstance().getResponseContentTypeXml();
            response.setContentType(ct);
            byte[] responseByteArray = sorResponse.getByteArray();
            response.setContentLength(responseByteArray.length);
            out.write(responseByteArray);
            out.close();
        }
        catch (Exception e) {
            log.error("Error on sending the response! " + e.getMessage());
        }
    }

}