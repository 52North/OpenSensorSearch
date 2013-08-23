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

package org.n52.sir.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;

import net.opengis.ows.ExceptionReportDocument;
import net.opengis.ows.x11.SectionsType;

import org.apache.http.HttpException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sir.client.Client;
import org.n52.sir.datastructure.SirSearchCriteria_Phenomenon;
import org.n52.sir.datastructure.SirSearchCriteria_Phenomenon.SirMatchingType;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.ows.OwsExceptionReport.ExceptionCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sor.x031.CapabilitiesDocument;
import org.x52North.sor.x031.GetCapabilitiesDocument;
import org.x52North.sor.x031.GetCapabilitiesDocument.GetCapabilities;
import org.x52North.sor.x031.GetMatchingDefinitionsRequestDocument;
import org.x52North.sor.x031.GetMatchingDefinitionsRequestDocument.GetMatchingDefinitionsRequest;
import org.x52North.sor.x031.GetMatchingDefinitionsResponseDocument;

import com.google.inject.Inject;

/**
 * @author Daniel Nüst
 * 
 */
public class SORClient {

    private static Logger log = LoggerFactory.getLogger(SORClient.class);

    private static final String SECTION_ALL = "ALL";

    private static final String SOR_SERVICE = "SOR";

    private static final String VERSION = "0.3.0";

    @Inject
    Client client;

    /**
     * 
     * @param serviceVersion
     * @param inputURI
     * @param matchingTypeString
     * @param searchDepth
     * @param validateXml
     * @return
     * @throws OwsExceptionReport
     */
    private GetMatchingDefinitionsRequestDocument buildRequest(String serviceVersion,
                                                               String inputURI,
                                                               String matchingTypeString,
                                                               int searchDepth,
                                                               boolean validateXml) throws OwsExceptionReport {
        GetMatchingDefinitionsRequestDocument requestDoc = GetMatchingDefinitionsRequestDocument.Factory.newInstance();
        GetMatchingDefinitionsRequest request = requestDoc.addNewGetMatchingDefinitionsRequest();
        request.setService(SOR_SERVICE);
        request.setVersion(serviceVersion);

        // phenomenon
        request.setInputURI(inputURI);

        // matchingType
        request.setMatchingType(SirMatchingType.getSorMatchingType(SirMatchingType.getSirMatchingType(matchingTypeString)));

        // searchDepth
        request.setSearchDepth(searchDepth);

        if (validateXml) {
            if ( !requestDoc.validate()) {
                log.warn("Request is NOT valid, service may return error!\n"
                        + XmlTools.validateAndIterateErrors(requestDoc));
            }
        }

        return requestDoc;
    }

    public Collection<SirSearchCriteria_Phenomenon> getMatchingTypes(SirSearchCriteria_Phenomenon p, boolean validateXml) throws OwsExceptionReport {
        int searchDepth = p.getSearchDepth();
        SirMatchingType matchingType = p.getMatchingType();
        String phenomenonName = p.getPhenomenonName();
        String sorUrlString = p.getSorUrl();

        Collection<SirSearchCriteria_Phenomenon> matchedPhenomena = new ArrayList<>();

        URI sorUri;
        try {
            sorUri = new URI(sorUrlString);
        }
        catch (URISyntaxException e) {
            log.error("Invalid sorURL " + sorUrlString + ". Not processing further!");
            return matchedPhenomena;
        }

        // check supported version
        CapabilitiesDocument caps = requestCapabilities(sorUri, validateXml);
        String version = caps.getCapabilities().getVersion();
        if ( !VERSION.equals(version)) {
            log.error("SOR version " + version + " not supported (" + VERSION + ")");
            return matchedPhenomena;
        }

        matchedPhenomena = requestMatchingDefinitions(sorUri,
                                                      phenomenonName,
                                                      matchingType.name(),
                                                      searchDepth,
                                                      validateXml);

        return matchedPhenomena;
    }

    /**
     * 
     * @param matchingURIArray
     * @return
     */
    private Collection<SirSearchCriteria_Phenomenon> parseMatchingURIs(String[] matchingURIArray) {
        ArrayList<SirSearchCriteria_Phenomenon> phenomena = new ArrayList<>();

        for (String s : matchingURIArray) {
            SirSearchCriteria_Phenomenon newPhen = new SirSearchCriteria_Phenomenon(s);
            phenomena.add(newPhen);
        }

        return phenomena;
    }

    /**
     * 
     * @param sorUri
     * @param validateXml
     * @return
     * @throws OwsExceptionReport
     */
    private CapabilitiesDocument requestCapabilities(URI sorUri, boolean validateXml) throws OwsExceptionReport {
        GetCapabilitiesDocument getCapRequestDoc = GetCapabilitiesDocument.Factory.newInstance();
        GetCapabilities request = getCapRequestDoc.addNewGetCapabilities();

        // service
        request.setService(SOR_SERVICE);
        SectionsType sections = request.addNewSections();
        sections.addSection(SECTION_ALL);

        if (validateXml) {
            if ( !getCapRequestDoc.validate()) {
                log.warn("Request is NOT valid, service may return error!\n"
                        + XmlTools.validateAndIterateErrors(getCapRequestDoc));
            }
        }

        XmlObject response;
        try {
            response = this.client.xSendPostRequest(getCapRequestDoc, sorUri);

            if (response instanceof CapabilitiesDocument) {
                CapabilitiesDocument caps = (CapabilitiesDocument) response;
                return caps;
            }
            throw new OwsExceptionReport(ExceptionCode.NoApplicableCode,
                                         "SorURL",
                                         "Could not request capabilities from given url " + sorUri.toString());
        }
        catch (UnsupportedEncodingException e) {
            throw new OwsExceptionReport(ExceptionCode.NoApplicableCode,
                                         "SorURL",
                                         "Could not request capabilities from given url " + sorUri.toString()
                                                 + " because of\n\n" + e);
        }
        catch (HttpException e) {
            throw new OwsExceptionReport(ExceptionCode.NoApplicableCode,
                                         "SorURL",
                                         "Could not request capabilities from given url " + sorUri.toString()
                                                 + " because of\n\n" + e);
        }
        catch (IOException e) {
            throw new OwsExceptionReport(ExceptionCode.NoApplicableCode,
                                         "SorURL",
                                         "Could not request capabilities from given url " + sorUri.toString()
                                                 + " because of\n\n" + e);
        }
    }

    /**
     * 
     * @param serviceURI
     * @param phenomenon
     * @param matchingType
     * @param searchDepth
     * @param validateXml
     * @return
     * @throws OwsExceptionReport
     */
    private Collection<SirSearchCriteria_Phenomenon> requestMatchingDefinitions(URI serviceURI,
                                                                                String phenomenon,
                                                                                String matchingType,
                                                                                int searchDepth,
                                                                                boolean validateXml) throws OwsExceptionReport {

        GetMatchingDefinitionsRequestDocument getDefRequest = buildRequest(VERSION,
                                                                           phenomenon,
                                                                           matchingType,
                                                                           searchDepth,
                                                                           validateXml);
        try {
            XmlObject response = this.client.xSendPostRequest(getDefRequest, serviceURI);

            if (response instanceof GetMatchingDefinitionsResponseDocument) {
                GetMatchingDefinitionsResponseDocument getMatchinDefsRespDoc = (GetMatchingDefinitionsResponseDocument) response;

                String[] matchingURIArray = getMatchinDefsRespDoc.getGetMatchingDefinitionsResponse().getMatchingURIArray();

                return parseMatchingURIs(matchingURIArray);
            }
            if (response instanceof ExceptionReportDocument) {
                ExceptionReportDocument er = (ExceptionReportDocument) response;
                log.info("Received ExceptionReport, could be a sign for no matches found!\n" + er.xmlText());
                return new ArrayList<>();
            }
            log.error("Did not get GetMatchingDefinitionsResponseDocument, but \n" + response.xmlText());
            throw new OwsExceptionReport(ExceptionCode.NoApplicableCode,
                                         "SorURL",
                                         "Could not request matching types from given url " + serviceURI.toString()
                                                 + " because of\n\n" + response.xmlText());
        }
        catch (UnsupportedEncodingException e) {
            throw new OwsExceptionReport(ExceptionCode.NoApplicableCode,
                                         "SorURL",
                                         "Could not request matching types from given url " + serviceURI.toString()
                                                 + " because of\n\n" + e);
        }
        catch (HttpException e) {
            throw new OwsExceptionReport(ExceptionCode.NoApplicableCode,
                                         "SorURL",
                                         "Could not request matching types from given url " + serviceURI.toString()
                                                 + " because of\n\n" + e);
        }
        catch (IOException e) {
            throw new OwsExceptionReport(ExceptionCode.NoApplicableCode,
                                         "SorURL",
                                         "Could not request matching types from given url " + serviceURI.toString()
                                                 + " because of\n\n" + e);
        }
    }
}
