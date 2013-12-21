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

import java.io.Serializable;

import org.n52.sor.OwsExceptionReport.ExceptionCode;
import org.n52.sor.decoder.HttpGetRequestDecoder;
import org.n52.sor.decoder.HttpPostRequestDecoder;
import org.n52.sor.listener.DeleteDefinitionListener;
import org.n52.sor.listener.GetCapabilitiesListener;
import org.n52.sor.listener.GetDefinitionListener;
import org.n52.sor.listener.GetDefinitionURIsListener;
import org.n52.sor.listener.GetMatchingDefinitionsListener;
import org.n52.sor.listener.InsertDefinitionListener;
import org.n52.sor.request.ISorRequest;
import org.n52.sor.request.SorDeleteDefinitionRequest;
import org.n52.sor.request.SorGetCapabilitiesRequest;
import org.n52.sor.request.SorGetDefinitionRequest;
import org.n52.sor.request.SorGetDefinitionURIsRequest;
import org.n52.sor.request.SorGetMatchingDefinitionsRequest;
import org.n52.sor.request.SorInsertDefinitionRequest;
import org.n52.sor.response.ISorResponse;
import org.n52.sor.response.SorExceptionReportResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The RequestOperator gets all requests and sends them to the HttpPostRequestDecoder when it's a POST-request
 * and to the HttpGetRequestDecoder when it's a GET-request. The decoded requests are then dispatched to the
 * according listeners and the reponse is returned.
 * 
 * @created 15-Okt-2008 16:25:09
 * @author Jan Schulte, Daniel Nüst
 * @version 1.0
 */
public class RequestOperator implements Serializable {

    /**
     * referenced by SOS which extends HttpServlet and is serializable
     */
    private static final long serialVersionUID = -1585907171058336256L;

    private static Logger log = LoggerFactory.getLogger(RequestOperator.class);

    /**
     * Decode an incoming post request string to a SorRequest
     */
    private HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder();

    /**
     * Decode an incoming get request string to a SorRequest
     */
    private HttpGetRequestDecoder getDecoder = new HttpGetRequestDecoder();

    /**
     * This method gets a String as GET-request input, sends it to the decoder and after that starts the
     * relevant process and pass back a response.
     * 
     * @param inputString
     *        Request as input string
     */
    public ISorResponse doGetOperation(String inputString) {

        ISorRequest request = null;
        try {
            request = this.getDecoder.receiveRequest(inputString);
        }
        catch (OwsExceptionReport e) {
            return new SorExceptionReportResponse(e.getDocument());
        }

        log.info("Get request document: " + request.toString());

        return getResponse(request);
    }

    /**
     * This method gets a String as POST-request input, sends it to the decoder and after that starts the
     * relevant process and pass back a response.
     * 
     * @param inputString
     *        Request as input string
     */
    public ISorResponse doPostOperation(String inputString) {
        ISorRequest request = null;
        try {
            request = this.postDecoder.receiveRequest(inputString);
        }
        catch (OwsExceptionReport e) {
            return new SorExceptionReportResponse(e.getDocument());
        }

        log.info("Post request document: " + request.toString());

        return getResponse(request);
    }

    /**
     * This method gets a ISorRequest starts the appropriate process and returns the ISorResponse
     * 
     * @param request
     *        The ISorRequest
     * @return The ISorResponse
     */
    private ISorResponse getResponse(ISorRequest request) {
        ISorResponse response = null;

        try {

            // GetCapabilitiesRequest
            if (request instanceof SorGetCapabilitiesRequest) {
                response = new GetCapabilitiesListener().receiveRequest(request);
            }

            // GetDefinitionRequest
            else if (request instanceof SorGetDefinitionRequest) {
                response = new GetDefinitionListener().receiveRequest(request);
            }

            // GetDefinitionURIsRequest
            else if (request instanceof SorGetDefinitionURIsRequest) {
                response = new GetDefinitionURIsListener().receiveRequest(request);
            }

            // GetMatchingDefinitionsRequest
            else if (request instanceof SorGetMatchingDefinitionsRequest) {
                response = new GetMatchingDefinitionsListener().receiveRequest(request);
            }

            // InsertDefinitionRequest
            else if (request instanceof SorInsertDefinitionRequest) {
                response = new InsertDefinitionListener().receiveRequest(request);
            }

            // DeleteDefinitionRequest
            else if (request instanceof SorDeleteDefinitionRequest) {
                response = new DeleteDefinitionListener().receiveRequest(request);
            }

            // invalid request
            else {
                throw new OwsExceptionReport(ExceptionCode.OperationNotSupported, "/", "The request "
                        + request.getClass() + "is not supported!");
            }

        }
        catch (OwsExceptionReport e) {
            return new SorExceptionReportResponse(e.getDocument());
        }

        return response;
    }
}