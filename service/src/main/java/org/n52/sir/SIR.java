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

package org.n52.sir;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

import org.n52.oss.config.ApplicationConstants;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import com.sun.jersey.api.view.Viewable;

/**
 * 
 * @author Jan Schulte, Daniel Nüst
 * 
 */
@Path("/sir")
@RequestScoped
public class SIR {

    private static Logger log = LoggerFactory.getLogger(SIR.class);

    private ApplicationConstants appConstants;

    @Inject
    RequestOperator requestOperator;

    @Inject
    public SIR(ApplicationConstants constants) {
        this.appConstants = constants;

        log.info("{} | Version: {} | Build: {} | From: {}",
                 this,
                 this.appConstants.getApplicationVersion(),
                 this.appConstants.getApplicationCommit(),
                 this.appConstants.getApplicationTimestamp());

        log.info(" ***** NEW {} *****", this);
    }

    @PreDestroy
    protected void shutdown() throws Throwable {
        log.info("SHUTDOWN called...");
        // SirConfigurator.getInstance().getExecutor().shutdown();
    }

    @GET
    public Response doGet(@Context
    UriInfo uriInfo) {
        String query = uriInfo.getRequestUri().getQuery();
        log.debug(" ****** (GET) Connected: {} ****** ", query);

        // TODO limit the scope of the input parameters to this method
        // MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();

        ISirResponse sirResp = this.requestOperator.doGetOperation(query);
        return doResponse(sirResp);
    }

    @POST
    @Produces(MediaType.APPLICATION_XML)
    public Response doPost(@Context
    HttpServletRequest req, String body) {
        log.debug(" ****** (POST) Connected from: {} {}", req.getRemoteAddr(), req.getRemoteHost());
        log.debug("POST body: {}", body);

        // Read the request
        String inputString = body;

        // // try (BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));) {
        // try (BufferedReader br = req.getReader();) {
        // String line;
        // StringBuffer sb = new StringBuffer();
        // while ( (line = br.readLine()) != null) {
        // sb.append(line + "\n");
        // }
        // br.close();
        // inputString = sb.toString();

        try {
            // discard "request="
            if (inputString.startsWith("request=")) {
                inputString = inputString.substring(8, inputString.length());
                inputString = java.net.URLDecoder.decode(inputString, "UTF-8");
            }
        }
        catch (Exception e) {
            log.error("Exception reading input stream.", e);
            return doResponse(new ExceptionResponse(e));
        }

        if (inputString.isEmpty())
            return doResponse(new ExceptionResponse(new OwsExceptionReport(ExceptionCode.InvalidRequest,
                                                                           "request",
                                                                           "request is empty.")));

        ISirResponse sirResp = this.requestOperator.doPostOperation(inputString);
        return doResponse(sirResp);
    }

    private Response doResponse(final ISirResponse sirResp) {
        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                try (BufferedOutputStream bus = new BufferedOutputStream(os);) {
                    log.debug("Writing streamed response of: {}", sirResp);

                    byte[] bytes = sirResp.getByteArray();
                    bus.write(bytes);
                }
                catch (Exception e) {
                    log.error("Could not write to response stream.", e);
                }

            }
        };

        return Response.ok(stream).build();
    }

    @GET
    @Path("/search")
    public Response index() {
        return Response.ok().entity(new Viewable("/search")).build();
    }

}