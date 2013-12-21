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