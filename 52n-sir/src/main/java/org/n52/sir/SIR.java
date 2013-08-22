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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.xml.transform.TransformerException;

import org.n52.oss.config.ApplicationConstants;
import org.n52.sir.catalog.ICatalogStatusHandler;
import org.n52.sir.response.ISirResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;

/**
 * 
 * @author Jan Schulte, Daniel Nüst
 * 
 */
@Path("/sir")
@RequestScoped
public class SIR {

    private static Logger log = LoggerFactory.getLogger(SIR.class);

    private SirConfigurator configurator;

    private ApplicationConstants appConstants;

    @Inject
    RequestOperator requestOperator;

    @Inject
    public SIR(SirConfigurator config, ApplicationConstants constants) {
        this.configurator = config.getInstance(); // TODO remove getInstance() call when injection of
                                                  // properties is configured
        this.appConstants = constants;

        log.info("{} | Version: {} | Build: {} | From: {}",
                 this,
                 this.appConstants.getApplicationVersion(),
                 this.appConstants.getApplicationCommit(),
                 this.appConstants.getApplicationTimestamp());

        log.info(" ***** NEW {} *****", this);
    }

    @Override
    protected void finalize() throws Throwable {
        log.info("finalize() called...");

        super.finalize();
        SirConfigurator.getInstance().getExecutor().shutdown();
    }

    @GET
    public void doGet(@Context
    UriInfo uriInfo, @Context
    HttpServletResponse resp) { // throws ServletException, IOException {
        String query = uriInfo.getRequestUri().getQuery();
        log.debug(" ****** (GET) Connected: {} ****** ", query);

        // TODO limit the scope of the input parameters to this method
        // MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();

        ISirResponse sirResp = this.requestOperator.doGetOperation(query);
        doResponse(resp, sirResp);
    }

    @POST
    public void doPost(@Context
    HttpServletRequest req, @Context
    HttpServletResponse resp) { // throws ServletException, IOException {
        if (log.isDebugEnabled())
            log.debug(" ****** (POST) Connected from: " + req.getRemoteAddr() + " " + req.getRemoteHost());

        // Read the request
        String inputString = "";

        try (BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));) {
            String line;
            StringBuffer sb = new StringBuffer();
            while ( (line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            inputString = sb.toString();

            // discard "request="
            if (inputString.startsWith("request=")) {
                inputString = inputString.substring(8, inputString.length());
                inputString = java.net.URLDecoder.decode(inputString, "UTF-8");
            }
        }
        catch (Exception e) {
            log.error("Exception reading input stream.", e);
        }

        ISirResponse sirResp = this.requestOperator.doPostOperation(inputString);
        doResponse(resp, sirResp);
    }

    private void doResponse(HttpServletResponse resp, ISirResponse sirResp) {
        try {
            String contentType = sirResp.getContentType();
            int contentLength = sirResp.getContentLength();
            byte[] bytes = sirResp.getByteArray();
            resp.setContentLength(contentLength);

            try (OutputStream out = resp.getOutputStream();) {
                resp.setContentType(contentType);
                out.write(bytes);
                out.close();
            }
            catch (Exception e) {
                log.error("Could not write to response stream.", e);
            }

        }
        catch (IOException ioe) {
            log.error("doResponse", ioe);
        }
        catch (TransformerException te) {
            log.error("doResponse", te);
        }
    }
}