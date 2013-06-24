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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;

import org.n52.sir.catalog.ICatalogStatusHandler;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.response.ISirResponse;
import org.n52.sir.util.jobs.impl.TimerServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class SIR extends HttpServlet {

    private static final String CONFIG_FILE = "/sir.properties";

    private static final String DBCONFIG_FILE = "/db.properties";

    private static Logger log = LoggerFactory.getLogger(SIR.class);

    private static final long serialVersionUID = -8056397366588482503L;

    private RequestOperator requestOperator;

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.GenericServlet#destroy()
     */
    @Override
    public void destroy() {
        log.info("destroy() called...");

        super.destroy();
        SirConfigurator.getInstance().getExecutor().shutdown();

        log.info("done destroy()");
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest ,
     * javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug(" ****** (GET) Connected from: " + req.getRemoteAddr() + " " + req.getRemoteHost());
        String queryString = req.getQueryString();
        if (log.isDebugEnabled())
            log.debug("Query String: " + queryString);
        ISirResponse sirResp = this.requestOperator.doGetOperation(queryString);
        doResponse(resp, sirResp);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (log.isDebugEnabled())
            log.debug(" ****** (POST) Connected from: " + req.getRemoteAddr() + " " + req.getRemoteHost());

        // Read the request
        InputStream in = req.getInputStream();
        String inputString = "";

        BufferedReader br = new BufferedReader(new InputStreamReader(in));
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

        ISirResponse sirResp = this.requestOperator.doPostOperation(inputString);
        doResponse(resp, sirResp);
    }

    /**
     * 
     * @param resp
     * @param sirResp
     */
    private void doResponse(HttpServletResponse resp, ISirResponse sirResp) {
        try {
            String contentType = sirResp.getContentType();
            int contentLength = sirResp.getContentLength();
            byte[] bytes = sirResp.getByteArray();
            resp.setContentLength(contentLength);
            OutputStream out = resp.getOutputStream();
            resp.setContentType(contentType);
            out.write(bytes);
            out.close();
        }
        catch (IOException ioe) {
            log.error("doResponse", ioe);
        }
        catch (TransformerException te) {
            log.error("doResponse", te);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.GenericServlet#init()
     */
    @Override
    public void init() throws ServletException {
        super.init();

        ServletContext context = getServletContext();
        String basepath = context.getRealPath("/");

        InputStream configStream = SIR.class.getResourceAsStream(CONFIG_FILE);
        if (configStream == null) {
            throw new UnavailableException("could not open the config file");
        }

        InputStream dbConfigStream = SIR.class.getResourceAsStream(DBCONFIG_FILE);
        if (dbConfigStream == null) {
            throw new UnavailableException("could not open the database config file");
        }

        TimerServlet timerServlet = (TimerServlet) context.getAttribute(TimerServlet.NAME_IN_CONTEXT);

        SirConfigurator configurator;
        try {
            configurator = SirConfigurator.getInstance(configStream, dbConfigStream, basepath, timerServlet);
        }
        catch (OwsExceptionReport e) {
            log.error("Error instantiating SirConfigurator.", e);
            throw new RuntimeException(e);
        }

        try {
            this.requestOperator = configurator.buildRequestOperator();
        }
        catch (OwsExceptionReport se) {
            log.error("the instantiation of RequestOperator failed");
            throw new UnavailableException(se.getMessage());
        }

        // put handler for status updated into context (where it is used by
        // other servlets)
        ICatalogStatusHandler handler = configurator.getCatalogStatusHandler();
        context.setAttribute(ICatalogStatusHandler.NAME_IN_CONTEXT, handler);

        File manifestFile = new File(basepath, "META-INF/MANIFEST.MF");
        Manifest mf = null;
        try {
            mf = new Manifest();
            mf.read(new FileInputStream(manifestFile));

            Attributes atts = mf.getMainAttributes();
            log.info("Version: {} | Build: {}",
                     atts.getValue("Implementation-Version"),
                     atts.getValue("Implementation-Build"));
        }
        catch (FileNotFoundException e) {
            log.warn("Could not read manifest file.");
        }
        catch (IOException e) {
            log.warn("Could not read manifest file.");
        }

        log.info(" ***** SIR initiated successfully! ***** ");
    }
}