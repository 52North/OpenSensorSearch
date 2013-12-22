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
package org.n52.sor.listener;

import java.util.Collection;

import org.n52.sor.OwsExceptionReport;
import org.n52.sor.PhenomenonManager;
import org.n52.sor.PropertiesManager;
import org.n52.sor.reasoner.IReasoner;
import org.n52.sor.reasoner.JenaReasoner.MatchingCode;
import org.n52.sor.request.ISorRequest;
import org.n52.sor.request.ISorRequest.SorMatchingType;
import org.n52.sor.request.SorGetMatchingDefinitionsRequest;
import org.n52.sor.response.ISorResponse;
import org.n52.sor.response.SorGetMatchingDefinitionsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetMatchingDefinitionsListener implements IRequestListener {

    private static Logger log = LoggerFactory.getLogger(GetMatchingDefinitionsListener.class);

    /*
     * @see ISorProcess#receiveRequest(ISorRequest)
     */
    @Override
    public ISorResponse receiveRequest(ISorRequest request) throws OwsExceptionReport {
        SorGetMatchingDefinitionsRequest getMatchDefReq = (SorGetMatchingDefinitionsRequest) request;

        SorGetMatchingDefinitionsResponse getMatchDefResp = new SorGetMatchingDefinitionsResponse();

        // get ontology
        String ontologyURL = PhenomenonManager.getInstance().getOntology(getMatchDefReq.getInputURI());
        if (ontologyURL == null) {
            throw new OwsExceptionReport(OwsExceptionReport.ExceptionCode.OptionNotSupported,
                                         null,
                                         "The URL to the Ontology is missing in the database! Matching to related phenomena is not possible for this InputURI.");
        }
        log.debug("ontologyURL: " + ontologyURL);

        // get matching code
        SorMatchingType code = getMatchDefReq.getMatchingType();
        if (code == null) {
            throw new OwsExceptionReport(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                         null,
                                         "The matching code is missing!");
        }
        log.debug("code: " + code);

        // get depth of the search
        int searchDepth = Integer.parseInt(getMatchDefReq.getSearchDepth());
        if (searchDepth < 1) {
            throw new OwsExceptionReport(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                         null,
                                         "The searchDepth parameter should be greater than 1!");
        }
        log.debug("Search depth: " + searchDepth);

        // get matching urls
        IReasoner rsnr = PropertiesManager.getInstance().getReasonerFactory().getReasoner();
        MatchingCode matchCode = code.getJenaMatchingCode();

        Collection<String> list = rsnr.getMatchingURLs(ontologyURL, matchCode, searchDepth);
        log.debug("Got " + list.size() + " possible matche(s): " + list);

        // get equivalent uris
        list = PhenomenonManager.getInstance().getEquivalentURI(list);
        log.debug("Matching URIs: " + list);

        getMatchDefResp.setMatchingURI(list);
        return getMatchDefResp;
    }

}