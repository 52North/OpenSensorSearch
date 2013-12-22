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

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sor.ISorRequest;
import org.n52.sor.ISorRequest.SorMatchingType;
import org.n52.sor.OwsExceptionReport;
import org.n52.sor.PropertiesManager;
import org.n52.sor.request.SorDeleteDefinitionRequest;
import org.n52.sor.request.SorGetCapabilitiesRequest;
import org.n52.sor.request.SorGetDefinitionRequest;
import org.n52.sor.request.SorGetDefinitionURIsRequest;
import org.n52.sor.request.SorGetMatchingDefinitionsRequest;
import org.n52.sor.request.SorInsertDefinitionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sor.x031.DeleteDefinitionRequestDocument;
import org.x52North.sor.x031.DeleteDefinitionRequestDocument.DeleteDefinitionRequest;
import org.x52North.sor.x031.GetCapabilitiesDocument;
import org.x52North.sor.x031.GetCapabilitiesDocument.GetCapabilities;
import org.x52North.sor.x031.GetDefinitionRequestDocument;
import org.x52North.sor.x031.GetDefinitionRequestDocument.GetDefinitionRequest;
import org.x52North.sor.x031.GetDefinitionURIsRequestDocument;
import org.x52North.sor.x031.GetDefinitionURIsRequestDocument.GetDefinitionURIsRequest;
import org.x52North.sor.x031.GetMatchingDefinitionsRequestDocument;
import org.x52North.sor.x031.GetMatchingDefinitionsRequestDocument.GetMatchingDefinitionsRequest;
import org.x52North.sor.x031.InsertDefinitionRequestDocument;
import org.x52North.sor.x031.InsertDefinitionRequestDocument.InsertDefinitionRequest;

/**
 * @created 15-Okt-2008 16:25:09
 * @author Jan Schulte
 * @version 1.0
 */
public class HttpPostRequestDecoder implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -2648124993455588059L;

    private static Logger log = LoggerFactory.getLogger(HttpPostRequestDecoder.class);

    private ISorRequest decodeDeleteDefinitionRequest(DeleteDefinitionRequestDocument delDefReqDoc) throws OwsExceptionReport {
        DeleteDefinitionRequest request = delDefReqDoc.getDeleteDefinitionRequest();
        SorDeleteDefinitionRequest sorRequest = new SorDeleteDefinitionRequest();
        // definitionURI
        if ( !request.getDefinitionURI().isEmpty()) {
            sorRequest.setDefinitionURI(request.getDefinitionURI());
        }
        else {
            throw new OwsExceptionReport(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                         null,
                                         "Request is invalid: DefinitionURI is missing or wrong!");
        }

        return sorRequest;
    }

    private ISorRequest decodeGetCapabilitiesRequest(GetCapabilitiesDocument getCaps) throws OwsExceptionReport {
        GetCapabilities request = getCaps.getGetCapabilities();
        SorGetCapabilitiesRequest sorRequest = new SorGetCapabilitiesRequest();
        // service
        if ( !request.getService().equalsIgnoreCase(PropertiesManager.getInstance().getService())) {
            log.error("Invalid request: service parameter is wrong!");
            throw new OwsExceptionReport(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                         null,
                                         "Your request was invalid: service parameter is wrong!");
        }
        // AcceptVersions
        // AcceptVersionsType avt = request.addNewAcceptVersions();
        // avt.addVersion("0.4.0");
        // System.out.println(request);
        if (request.isSetAcceptVersions()) {
            sorRequest.setAcceptVersions(request.getAcceptVersions().getVersionArray());
        }
        // Sections
        if (request.isSetSections()) {
            sorRequest.setSections(request.getSections().getSectionArray());
        }
        // updateSequence
        if (request.isSetUpdateSequence()) {
            sorRequest.setUpdateSequence(request.getUpdateSequence());
        }
        // AcceptFormats
        if (request.isSetAcceptFormats()) {
            sorRequest.setAcceptFormats(request.getAcceptFormats().getOutputFormatArray());
        }

        return sorRequest;
    }

    private ISorRequest decodeGetDefinitionRequest(GetDefinitionRequestDocument getDefReqDoc) throws OwsExceptionReport {
        GetDefinitionRequest request = getDefReqDoc.getGetDefinitionRequest();

        log.debug("Service: {}", request.getService());
        if ( !request.getService().equalsIgnoreCase(PropertiesManager.getInstance().getService())) {
            log.error("Invalid request: service parameter is wrong!");
            throw new OwsExceptionReport(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                         null,
                                         "Request is invalid: service parameter is wrong!");
        }

        log.debug("Version: {}", request.getVersion());
        if ( !request.getVersion().equalsIgnoreCase(PropertiesManager.getInstance().getServiceVersion())) {
            log.error("Invalid request: version parameter is wrong!");
            throw new OwsExceptionReport(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                         null,
                                         "Request is invalid: version parameter is wrong!");
        }
        if (request.getInputURI() == null || request.getInputURI().isEmpty()) {
            log.error("Invalid request: InputURI parameter is missing or wrong!");
            throw new OwsExceptionReport(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                         "InputURI",
                                         "Request is invalid: InputURI parameter is missing or wrong!");
        }
        return new SorGetDefinitionRequest(request.getInputURI());
    }

    private ISorRequest decodeGetDefinitionURIsRequest(GetDefinitionURIsRequestDocument getDefURIsReqDoc) throws OwsExceptionReport {
        GetDefinitionURIsRequest request = getDefURIsReqDoc.getGetDefinitionURIsRequest();
        SorGetDefinitionURIsRequest sorRequest = new SorGetDefinitionURIsRequest();
        // service
        if ( !request.getService().equalsIgnoreCase(PropertiesManager.getInstance().getService())) {
            log.error("Invalid request: service parameter is wrong!");
            throw new OwsExceptionReport(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                         null,
                                         "Your request was invalid: service parameter is wrong!");
        }
        // version
        if ( !request.getVersion().equalsIgnoreCase(PropertiesManager.getInstance().getServiceVersion())) {
            log.error("Invalid request: version parameter is wrong!");
            throw new OwsExceptionReport(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                         null,
                                         "Your request was invalid: version parameter is wrong!");
        }
        // maxNumberOfResults
        if (request.isSetMaxNumberOfResults()) {
            sorRequest.setMaxNumberOfResults(request.getMaxNumberOfResults());
        }
        // startResultElement
        if (request.isSetStartResultElement()) {
            sorRequest.setStartResultElement(request.getStartResultElement());
        }
        // searchString
        if (request.isSetSearchString()) {
            sorRequest.setSearchString(request.getSearchString());
        }
        return sorRequest;
    }

    private ISorRequest decodeGetMatchingDefinitionsRequest(GetMatchingDefinitionsRequestDocument getMatchDefReqDoc) throws OwsExceptionReport {
        GetMatchingDefinitionsRequest request = getMatchDefReqDoc.getGetMatchingDefinitionsRequest();
        SorGetMatchingDefinitionsRequest sorRequest = new SorGetMatchingDefinitionsRequest();
        // service
        if ( !request.getService().equalsIgnoreCase(PropertiesManager.getInstance().getService())) {
            log.error("Invalid request: service parameter is wrong!");
            throw new OwsExceptionReport(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                         null,
                                         "Your request was invalid: service parameter is wrong!");
        }
        // version
        if ( !request.getVersion().equalsIgnoreCase(PropertiesManager.getInstance().getServiceVersion())) {
            log.error("Invalid request: version parameter is wrong!");
            throw new OwsExceptionReport(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                         null,
                                         "Your request was invalid: version parameter is wrong!");
        }
        // inputURI
        if (request.getInputURI() == null || request.getInputURI().isEmpty()) {
            log.error("Invalid request: inputURI parameter is missing or wrong!");
            throw new OwsExceptionReport(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                         null,
                                         "Your request was invalid: inputURI parameter is missing or wrong!");
        }

        sorRequest.setInputURI(request.getInputURI());

        // matchingType
        log.debug("Matching Type: " + request.getMatchingType());
        sorRequest.setMatchingType(SorMatchingType.getSorMatchingType(request.getMatchingType()));

        // searchDepth
        log.debug("Search depth:" + request.xgetSearchDepth().xmlText());
        if (request.xgetMatchingType() != null) {
            sorRequest.setSearchDepth(String.valueOf(request.getSearchDepth()));
        }
        return sorRequest;
    }

    private ISorRequest decodeInsertDefinitionRequest(InsertDefinitionRequestDocument insDefReqDoc) throws OwsExceptionReport {
        InsertDefinitionRequest request = insDefReqDoc.getInsertDefinitionRequest();
        SorInsertDefinitionRequest sorRequest = new SorInsertDefinitionRequest();

        // DefinitionURI
        if ( !request.getPhenomenon().getId().isEmpty()) {
            sorRequest.setDefinitionURI(request.getPhenomenon().getId());
        }
        else {
            throw new OwsExceptionReport(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                         "gml:identifier",
                                         "Request is invalid: identifier is missing!");
        }
        // phenomenonType
        sorRequest.setPhenomenon(request.getPhenomenon());

        return sorRequest;
    }

    /**
     * This methode manages all incoming post requests and parses to a ISorRequest
     * 
     * @param inputString
     *        The post request string
     * @return The detected SOR request
     * @throws OwsExceptionReport
     */
    public ISorRequest receiveRequest(String inputString) throws OwsExceptionReport {
        XmlObject doc = null;

        try {
            doc = XmlObject.Factory.parse(inputString);
        }
        catch (XmlException e) {
            log.error("Error while parsing xml request");
        }

        if (doc != null) {
            log.info("Request type: " + doc.getClass());
        }

        if (doc instanceof GetCapabilitiesDocument) {
            log.debug("GetCapabilities starts!");
            GetCapabilitiesDocument getCaps = (GetCapabilitiesDocument) doc;
            return decodeGetCapabilitiesRequest(getCaps);
        }

        if (doc instanceof GetDefinitionURIsRequestDocument) {
            log.debug("GetDefinitionURIsRequest starts!");
            GetDefinitionURIsRequestDocument getDefURIsReqDoc = (GetDefinitionURIsRequestDocument) doc;
            return decodeGetDefinitionURIsRequest(getDefURIsReqDoc);
        }

        if (doc instanceof GetDefinitionRequestDocument) {
            log.debug("GetDefinitionRequest starts!");
            GetDefinitionRequestDocument getDefReqDoc = (GetDefinitionRequestDocument) doc;
            return decodeGetDefinitionRequest(getDefReqDoc);
        }

        if (doc instanceof GetMatchingDefinitionsRequestDocument) {
            log.debug("GetMatchingDefinitionsRequest starts!");
            GetMatchingDefinitionsRequestDocument getMatchDefReqDoc = (GetMatchingDefinitionsRequestDocument) doc;
            return decodeGetMatchingDefinitionsRequest(getMatchDefReqDoc);
        }

        if (doc instanceof InsertDefinitionRequestDocument) {
            log.debug("InsertDefinitionRequest starts!");
            InsertDefinitionRequestDocument insDefReqDoc = (InsertDefinitionRequestDocument) doc;
            return decodeInsertDefinitionRequest(insDefReqDoc);
        }

        if (doc instanceof DeleteDefinitionRequestDocument) {
            log.debug("DeleteDefinitionRequest starts!");
            DeleteDefinitionRequestDocument delDefReqDoc = (DeleteDefinitionRequestDocument) doc;
            return decodeDeleteDefinitionRequest(delDefReqDoc);
        }

        log.error("Invalid Request!");
        throw new OwsExceptionReport(OwsExceptionReport.ExceptionCode.InvalidRequest, null, "Your request was invalid!");
    }

}