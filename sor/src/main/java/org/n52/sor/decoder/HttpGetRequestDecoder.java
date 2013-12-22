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
package org.n52.sor.decoder;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.n52.sor.OwsExceptionReport;
import org.n52.sor.PropertiesManager;
import org.n52.sor.SorConstants;
import org.n52.sor.request.ISorRequest;
import org.n52.sor.request.ISorRequest.SorMatchingType;
import org.n52.sor.request.SorGetCapabilitiesRequest;
import org.n52.sor.request.SorGetDefinitionRequest;
import org.n52.sor.request.SorGetDefinitionURIsRequest;
import org.n52.sor.request.SorGetMatchingDefinitionsRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @created 15-Okt-2008 16:25:09
 * @author Jan Schulte
 * @version 1.0
 */
public class HttpGetRequestDecoder implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -6394050632543917701L;
    
    private static Logger log = LoggerFactory.getLogger(HttpGetRequestDecoder.class);

    private ISorRequest decodeGetCapabilities(String[] params) throws OwsExceptionReport {
        SorGetCapabilitiesRequest sorRequest = new SorGetCapabilitiesRequest();
        boolean serviceCheck = true;

        for (int i = 0; i < params.length; i++) {
            String[] nameAndValue = params[i].split("=");
            // service
            if ( ! ( (nameAndValue[0].equalsIgnoreCase(SorConstants.GetCapGetParams.SERVICE.name())) && (nameAndValue[1].equalsIgnoreCase(PropertiesManager.getInstance().getService())))) {
                serviceCheck = true;
            }
            // AcceptVersions
            if (nameAndValue[0].equalsIgnoreCase(SorConstants.GetCapGetParams.ACCEPTVERSIONS.name())) {
                sorRequest.setAcceptVersions(nameAndValue[1].split(","));
                List<String> l = Arrays.asList(sorRequest.getAcceptVersions());
                if ( !l.contains(PropertiesManager.getInstance().getServiceVersion())) {
                    log.error("Invalid request: version is not supported!");
                    throw new OwsExceptionReport(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                                 "AcceptVersions",
                                                 "None of accepted versions is not supported!");
                }
            }
            // Sections
            if (nameAndValue[0].equalsIgnoreCase(SorConstants.GetCapGetParams.SECTIONS.name())) {
                sorRequest.setSections(nameAndValue[1].split(","));
            }
            // updateSequence
            if (nameAndValue[0].equalsIgnoreCase(SorConstants.GetCapGetParams.UPDATESEQUENCE.name())) {
                sorRequest.setUpdateSequence(nameAndValue[1]);
            }
            // AcceptFormats
            if (nameAndValue[0].equalsIgnoreCase(SorConstants.GetCapGetParams.ACCEPTFORMATS.name())) {
                sorRequest.setAcceptFormats(nameAndValue[1].split(","));
            }
        }

        if (serviceCheck)
            return sorRequest;
        return errorServiceParameter();
    }

    private ISorRequest decodeGetDefinition(String[] params) throws OwsExceptionReport {
        boolean service = false;
        boolean version = false;
        String inputURI = null;
        for (int i = 0; i < params.length; i++) {
            String[] nameAndValue = params[i].split("=");
            // service
            if ( (nameAndValue[0].equalsIgnoreCase(SorConstants.GetDefParams.SERVICE.name()))
                    && (nameAndValue[1].equalsIgnoreCase(PropertiesManager.getInstance().getService()))) {
                service = true;
            }
            // version
            else if ( (nameAndValue[0].equalsIgnoreCase(SorConstants.GetDefParams.VERSION.name()))
                    && (nameAndValue[1].equalsIgnoreCase(PropertiesManager.getInstance().getServiceVersion()))) {
                version = true;
            }
            // inputURI
            else if ( (nameAndValue[0].equalsIgnoreCase(SorConstants.GetDefParams.INPUTURI.name()))) {
                inputURI = nameAndValue[1];
            }
        }
        if (service) {
            if (version) {
                if (inputURI != null) {
                    return new SorGetDefinitionRequest(inputURI);
                }
                return errorInputURIParameter();
            }
            return errorVersionParameter();
        }
        return errorServiceParameter();
    }

    private ISorRequest decodeGetDefinitionURIs(String[] params) throws OwsExceptionReport {
        boolean service = false;
        boolean version = false;
        int maxNumberOfResults = Integer.MAX_VALUE;
        int startResultElement = Integer.MAX_VALUE;
        String searchString = null;
        for (int i = 0; i < params.length; i++) {
            String[] nameAndValue = params[i].split("=");
            // service
            if ( (nameAndValue[0].equalsIgnoreCase(SorConstants.GetDefURIsParams.SERVICE.name()))
                    && (nameAndValue[1].equalsIgnoreCase(PropertiesManager.getInstance().getService()))) {
                service = true;
            }
            // version
            if ( (nameAndValue[0].equalsIgnoreCase(SorConstants.GetDefURIsParams.VERSION.name()))
                    && (nameAndValue[1].equalsIgnoreCase(PropertiesManager.getInstance().getServiceVersion()))) {
                version = true;
            }
            // maxNumberOfResults
            if (nameAndValue[0].equalsIgnoreCase(SorConstants.GetDefURIsParams.MAXNUMBEROFORESULTS.name())) {
                maxNumberOfResults = Integer.parseInt(nameAndValue[1]);
            }
            // startResultElement
            if (nameAndValue[0].equalsIgnoreCase(SorConstants.GetDefURIsParams.STARTRESULTELEMENT.name())) {
                startResultElement = Integer.parseInt(nameAndValue[1]);
            }
            // searchString
            if (nameAndValue[0].equalsIgnoreCase(SorConstants.GetDefURIsParams.SEARCHSTRING.name())) {
                searchString = nameAndValue[1];
            }
        }
        if (version) {
            if (service) {
                SorGetDefinitionURIsRequest request = new SorGetDefinitionURIsRequest();
                if (maxNumberOfResults != Integer.MAX_VALUE) {
                    request.setMaxNumberOfResults(maxNumberOfResults);
                }
                if (startResultElement != Integer.MAX_VALUE) {
                    request.setStartResultElement(startResultElement);
                }
                if (searchString != null) {
                    request.setSearchString(searchString);
                }
                return request;
            }
            return errorServiceParameter();
        }
        return errorVersionParameter();
    }

    private ISorRequest decodeGetMatchingDefinitions(String[] params) throws OwsExceptionReport {
        boolean service = false;
        boolean version = false;
        String inputURI = null;
        SorMatchingType matchingType = null;
        String searchDepth = null;

        for (int i = 0; i < params.length; i++) {
            String[] nameAndValue = params[i].split("=");
            // service
            if ( (nameAndValue[0].equalsIgnoreCase(SorConstants.GetMatchDefParams.SERVICE.name()))
                    && (nameAndValue[1].equalsIgnoreCase(PropertiesManager.getInstance().getService()))) {
                service = true;
            }
            // version
            else if ( (nameAndValue[0].equalsIgnoreCase(SorConstants.GetMatchDefParams.VERSION.name()))
                    && (nameAndValue[1].equalsIgnoreCase(PropertiesManager.getInstance().getServiceVersion()))) {
                version = true;
            }
            // inputURI
            else if ( (nameAndValue[0].equalsIgnoreCase(SorConstants.GetMatchDefParams.INPUTURI.name()))) {
                inputURI = nameAndValue[1];
            }
            // matchingURI
            else if (nameAndValue[0].equalsIgnoreCase(SorConstants.GetMatchDefParams.MATCHINGTYPE.name())) {
                matchingType = SorMatchingType.getSorMatchingType(nameAndValue[1]);
            }
            // searchDepth
            else if ( (nameAndValue[0].equalsIgnoreCase(SorConstants.GetMatchDefParams.SEARCHDEPTH.name()))) {
                searchDepth = nameAndValue[1];
            }
        }

        if (service) {
            if (version) {
                if (inputURI != null) {
                    if (matchingType != null) {
                        SorGetMatchingDefinitionsRequest request = new SorGetMatchingDefinitionsRequest();
                        request.setInputURI(inputURI);
                        request.setMatchingType(matchingType);
                        if (searchDepth != null) {
                            request.setSearchDepth(searchDepth);
                        }
                        return request;
                    }
                    log.error("Invalid request: matchingType parameter is missing or wrong!");
                    throw new OwsExceptionReport(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                                 "MatchingType",
                                                 "'matchingType' parameter is missing or wrong!");

                }
                return errorInputURIParameter();
            }
            return errorVersionParameter();
        }
        return errorServiceParameter();
    }

    /**
     * @throws OwsExceptionReport
     */
    private ISorRequest errorInputURIParameter() throws OwsExceptionReport {
        log.error("Invalid request: input URI parameter is missing!");
        throw new OwsExceptionReport(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                     "InputURI",
                                     "'inputURI' parameter is missing or wrong! Use the request GetDefinitionURIs to request available resources.");
    }

    /**
     * @throws OwsExceptionReport
     */
    private ISorRequest errorServiceParameter() throws OwsExceptionReport {
        log.error("Invalid request: service parameter is missing or wrong!");
        throw new OwsExceptionReport(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                     "service",
                                     "'service' parameter is missing or wrong! Required to be "
                                             + PropertiesManager.getInstance().getService());
    }

    /**
     * @throws OwsExceptionReport
     */
    private ISorRequest errorVersionParameter() throws OwsExceptionReport {
        log.error("Invalid request: version parameter is missing or wrong!");
        throw new OwsExceptionReport(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                     "version",
                                     "'version' parameter is missing or wrong! This service's version is "
                                             + PropertiesManager.getInstance().getServiceVersion());
    }

    /**
     * This methode manages all incoming get requests and parses to a ISorRequest.
     * 
     * @param inputString
     *        The get request as string
     * @return The detected SOR request
     * @throws OwsExceptionReport
     */
    public ISorRequest receiveRequest(String inputString) throws OwsExceptionReport {

        if (inputString == null || inputString.isEmpty()) {
            log.error("Invalid request: no request parameters given!");
            throw new OwsExceptionReport(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                         null,
                                         "Parameter 'request' is missing!");
        }

        String[] params = inputString.split("&");

        for (int i = 0; i < params.length; i++) {
            String[] nameAndValue = params[i].split("=");
            // GetCapabilitiesRequest
            if ( (nameAndValue[0].equalsIgnoreCase(SorConstants.GETREQUESTPARAM))
                    && (nameAndValue[1].equalsIgnoreCase(SorConstants.Operations.GetCapabilities.name()))) {
                return decodeGetCapabilities(params);
            }
            // GetDefinitionUINsRequest
            if ( (nameAndValue[0].equalsIgnoreCase(SorConstants.GETREQUESTPARAM))
                    && (nameAndValue[1].equalsIgnoreCase(SorConstants.Operations.GetDefinitionURIs.name()))) {
                return decodeGetDefinitionURIs(params);
            }
            // GetDefinitionRequest
            if ( (nameAndValue[0].equalsIgnoreCase(SorConstants.GETREQUESTPARAM))
                    && (nameAndValue[1].equalsIgnoreCase(SorConstants.Operations.GetDefinition.name()))) {
                return decodeGetDefinition(params);
            }
            // GetMatchingDefinitionsRequest
            if ( (nameAndValue[0].equalsIgnoreCase(SorConstants.GETREQUESTPARAM))
                    && (nameAndValue[1].equalsIgnoreCase(SorConstants.Operations.GetMatchingDefinitions.name()))) {
                return decodeGetMatchingDefinitions(params);
            }
        }

        log.error("Invalid request: request parameter is missing or unknown!");
        throw new OwsExceptionReport(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                     "request",
                                     "'request' parameter is missing or unknown!");
    }
}