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

    /**
     * 
     */
    private static final long serialVersionUID = -8056397366588482503L;

    /**
     * The init parameter of the configFile
     */
    private static final String INIT_PARAM_CONFIG_FILE = "configFile";

    /**
     * The init parameter of the database configFile
     */
    private static final String INIT_PARAM_DBCONFIG_FILE = "dbConfigFile";

    /**
     * The logger, used to log exceptions and additional information
     */
    private static Logger log = LoggerFactory.getLogger(SIR.class);

    /**
     * Handles POST and GET operations
     */
    private RequestOperator requestOperator;

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.GenericServlet#init()
     */
    @Override
    public void init() throws ServletException {
        super.init();

        // get ServletContext
        ServletContext context = getServletContext();
        String basepath = context.getRealPath("/");

        // get configFile as Inputstream
        InputStream configStream = context.getResourceAsStream(getInitParameter(INIT_PARAM_CONFIG_FILE));
        if (configStream == null) {
            throw new UnavailableException("could not open the config file");
        }

        // get dbconfigFile as Inputstream
        InputStream dbConfigStream = context.getResourceAsStream(getInitParameter(INIT_PARAM_DBCONFIG_FILE));
        if (dbConfigStream == null) {
            throw new UnavailableException("could not open the database config file");
        }

        // get the timer servlet
        TimerServlet timerServlet = (TimerServlet) context.getAttribute(TimerServlet.NAME_IN_CONTEXT);

        // initialize configurator
        SirConfigurator configurator;
        try {
            configurator = SirConfigurator.getInstance(configStream, dbConfigStream, basepath, timerServlet);
        }
        catch (OwsExceptionReport e) {
            log.error("Error instantiating SirConfigurator.", e);
            throw new RuntimeException(e);
        }

        // initialize requestOperator
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
}