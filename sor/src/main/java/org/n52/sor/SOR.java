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

package org.n52.sor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLDecoder;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.n52.sor.OwsExceptionReport.ExceptionCode;
import org.n52.sor.response.ISorResponse;
import org.n52.sor.response.SorExceptionReportResponse;
import org.n52.sor.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Servlet of the SOR, which receives requests and send back the response.
 * 
 * @created 15-Okt-2008 16:25:09
 * @author Jan Schulte
 * @version 1.0
 */
public class SOR extends HttpServlet {

    private static final long serialVersionUID = -2606497510984790264L;

    private static final String CONFIG_FILE_INIT_PARAMETER = "configFile";

    private static Logger log = LoggerFactory.getLogger(SOR.class);

    /**
     * The RequestOperator which handles the request
     */
    private RequestOperator requestOperator = new RequestOperator();
    
    /**
     * This Methode handles all incoming GET-requests and send them to the RequestDecoder, where the request
     * is decoded to an intern SOR request.
     * 
     * @param request
     *        The incoming request
     * @param response
     *        The outgoing reponse to the request response
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {

        String queryString = request.getQueryString();
        log.info("(GET) Connected from: " + request.getRemoteAddr() + " " + request.getRemoteHost() + " Request: "
                + queryString);

        ISorResponse sorResponse = this.requestOperator.doGetOperation(queryString);
        log.info("Response: " + sorResponse);

        // prepare response
        doResponse(response, sorResponse);
    }

    /**
     * This methode handles all incoming POST-request and send them to the RequestDecoder, where the request
     * is decoded to an intern SOR request.
     * 
     * @param request
     *        The incoming request
     * @param response
     *        The outgoing response to the request
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            InputStream in = request.getInputStream();
            String inputString;
            String decodedString;

            BufferedReader br = new BufferedReader(new InputStreamReader(in));
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

            log.info("(POST) Connected from: " + request.getRemoteAddr() + " " + request.getRemoteHost());

            ISorResponse sorResponse = this.requestOperator.doPostOperation(decodedString);

            log.info("Returning response: " + sorResponse);

            // prepare response
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

    /**
     * Sends the SorResponse to the Outputstream of the HttpServletResponse
     * 
     * @param response
     *        HttpServletResponse which gets the SorResponse
     * @param sorResponse
     *        SorResponse which is send back to the client
     */
    private void doResponse(HttpServletResponse response, ISorResponse sorResponse) {
        OutputStream out;
        try {
            out = response.getOutputStream();
            response.setContentType(PropertiesManager.getInstance().getResponseContentTypeXml());
            byte[] responseByteArray = sorResponse.getByteArray();
            response.setContentLength(responseByteArray.length);
            out.write(responseByteArray);
            out.close();
        }
        catch (Exception e) {
            log.error("Error on sending the response! " + e.getMessage());
        }
    }

    /**
     * Init-Method to initialise the PropertiesManager and the SORManager
     */
    @Override
    public void init() {
        // get ServletContext
        ServletContext context = getServletContext();

        String basepath = context.getRealPath("/");
        InputStream configStream = context.getResourceAsStream(getInitParameter(CONFIG_FILE_INIT_PARAMETER));

        // initialize property manager
        PropertiesManager.getInstance(configStream, basepath);

        // initialize phenomenon manager
        try {
            PhenomenonManager.getInstance();
        }
        catch (OwsExceptionReport e) {
            log.error("Could not initialize PhenomenonManager", e);
            throw new RuntimeException("Could not initialize PhenomenonManager! Service is not ready for use.");
        }

        log.info("*** INITIALIZED SOR ***");
    }

}