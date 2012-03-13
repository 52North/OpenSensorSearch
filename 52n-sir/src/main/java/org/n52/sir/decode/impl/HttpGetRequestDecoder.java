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

package org.n52.sir.decode.impl;

import org.n52.sir.SirConstants;
import org.n52.sir.decode.IHttpGetRequestDecoder;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.request.AbstractSirRequest;
import org.n52.sir.request.SirDescribeSensorRequest;
import org.n52.sir.request.SirGetCapabilitiesRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schulte
 * 
 */
public class HttpGetRequestDecoder implements IHttpGetRequestDecoder {

    private static Logger log = LoggerFactory.getLogger(HttpGetRequestDecoder.class);

    private AbstractSirRequest decodeDescribeSensor(String[] params) throws OwsExceptionReport {
        try {
            SirDescribeSensorRequest request = new SirDescribeSensorRequest();
            for (String param : params) {
                String[] nameAndValue = param.split("=");
                if (nameAndValue[0].equalsIgnoreCase(SirConstants.GetDescSensorParams.SENSORIDINSIR.name())) {
                    request = new SirDescribeSensorRequest();
                    request.setSensorIdInSir(nameAndValue[1]);
                    return request;
                }
            }
            return request;
        }
        catch (Exception e) {
            log.error("Invalid DescribeSensor request!");
            OwsExceptionReport se = new OwsExceptionReport(OwsExceptionReport.ExceptionLevel.DetailedExceptions);
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                 "HttpGetRequestDecoder.receiveRequest()",
                                 "Invalid DescribeSensor request!");
            throw se;
        }
    }

    private AbstractSirRequest decodeGetCapabilities(String[] params) throws OwsExceptionReport {

        SirGetCapabilitiesRequest sirRequest = new SirGetCapabilitiesRequest();
        for (int i = 0; i < params.length; i++) {
            String[] nameAndValue = params[i].split("=");
            if (nameAndValue.length < 2) {
                log.error("Missing value for " + nameAndValue[0] + " parameter!");
                OwsExceptionReport se = new OwsExceptionReport(OwsExceptionReport.ExceptionLevel.DetailedExceptions);
                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                     "HttpGetRequestDecoder.receiveRequest()",
                                     "Missing value for " + nameAndValue[0] + " parameter!");
                throw se;
            }
            // service
            if ( (nameAndValue[0].equalsIgnoreCase(SirConstants.GetCapGetParams.SERVICE.name()))) {
                sirRequest.setService(nameAndValue[1]);
            }
            // AcceptVersions
            if (nameAndValue[0].equalsIgnoreCase(SirConstants.GetCapGetParams.ACCEPTVERSIONS.name())) {
                sirRequest.setAcceptVersions(nameAndValue[1].split(","));
            }
            // Sections
            if (nameAndValue[0].equalsIgnoreCase(SirConstants.GetCapGetParams.SECTIONS.name())) {
                sirRequest.setSections(nameAndValue[1].split(","));
            }
            // updateSequence
            if (nameAndValue[0].equalsIgnoreCase(SirConstants.GetCapGetParams.UPDATESEQUENCE.name())) {
                sirRequest.setUpdateSequence(nameAndValue[1]);
            }
            // AcceptFormats
            if (nameAndValue[0].equalsIgnoreCase(SirConstants.GetCapGetParams.ACCEPTFORMATS.name())) {
                sirRequest.setAcceptFormats(nameAndValue[1].split(","));
            }
        }
        return sirRequest;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.decode.IHttpGetRequestDecoder#receiveRequest(java.lang.String)
     */
    @Override
    public AbstractSirRequest receiveRequest(String queryString) throws OwsExceptionReport {
        // check queryString
        if ( ! (queryString != null && queryString.length() != 0)) {
            log.error("Invalid GET request!");
            OwsExceptionReport se = new OwsExceptionReport(OwsExceptionReport.ExceptionLevel.DetailedExceptions);
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                 "HttpGetRequestDecoder.receiveRequest()",
                                 "Invalid GET request!");
            throw se;
        }

        String[] params = queryString.split("&");

        // if less than 2 parameters, throw exception
        if (params.length < 2) {
            log.error("Invalid GET request! At least 2 parameters needed: {}", queryString);
            OwsExceptionReport se = new OwsExceptionReport(OwsExceptionReport.ExceptionLevel.DetailedExceptions);
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                 "HttpGetRequestDecoder.receiveRequest()",
                                 "Invalid GET request! At least 2 parameters needed!");
            throw se;
        }

        for (String param : params) {
            String[] nameAndValue = param.split("=");
            // check the request parameter
            if (nameAndValue[0].equalsIgnoreCase(SirConstants.GETREQUESTPARAM)) {

                // check request = GetCapabilities
                if (nameAndValue[1].equalsIgnoreCase(SirConstants.Operations.GetCapabilities.name())) {
                    if (log.isDebugEnabled())
                        log.debug("**** GetCapabilities");
                    return decodeGetCapabilities(params);
                }

                // check request = DescribeSensor
                if (nameAndValue[1].equalsIgnoreCase(SirConstants.Operations.DescribeSensor.name())) {
                    if (log.isDebugEnabled())
                        log.debug("**** DescribeSensor");
                    return decodeDescribeSensor(params);
                }
            }
        }

        log.error(SirConstants.GETREQUESTPARAM + " is needed: {}", queryString);
        OwsExceptionReport se = new OwsExceptionReport(OwsExceptionReport.ExceptionLevel.DetailedExceptions);
        se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidRequest,
                             "HttpGetRequestDecoder.receiveRequest()",
                             SirConstants.GETREQUESTPARAM + " is needed!");
        throw se;
    }
}