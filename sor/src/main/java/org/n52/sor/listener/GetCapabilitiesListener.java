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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.n52.sor.ISorRequest;
import org.n52.sor.ISorResponse;
import org.n52.sor.OwsExceptionReport;
import org.n52.sor.OwsExceptionReport.ExceptionCode;
import org.n52.sor.PhenomenonManager;
import org.n52.sor.PropertiesManager;
import org.n52.sor.request.SorGetCapabilitiesRequest;
import org.n52.sor.response.CapabilitiesSection;
import org.n52.sor.response.SorExceptionReportResponse;
import org.n52.sor.response.SorGetCapabilitiesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetCapabilitiesListener implements IRequestListener {

    private static Logger log = LoggerFactory.getLogger(PhenomenonManager.class);

    private void checkAcceptedVersions(String[] versions) throws OwsExceptionReport {
        String serviceVersion = PropertiesManager.getInstance().getServiceVersion();
        for (String version : versions) {
            if (version.equals(serviceVersion)) {
                return;
            }
        }

        OwsExceptionReport se = new OwsExceptionReport(ExceptionCode.VersionNegotiationFailed,
                                                       null,
                                                       "The parameter 'AcceptVersions' does not contain the version of this SOR: '"
                                                               + serviceVersion + "'");
        log.error("The accepted versions parameter is incorrect.", se);
        throw se;
    }

    private ArrayList<CapabilitiesSection> checkSections(String[] sections) throws OwsExceptionReport {
        ArrayList<CapabilitiesSection> responseSection = new ArrayList<>();
        for (String section : sections) {
            if (section.equalsIgnoreCase(CapabilitiesSection.CONTENTS.name())) {
                responseSection.add(CapabilitiesSection.CONTENTS);
            }
            else if (section.equalsIgnoreCase(CapabilitiesSection.OPERATIONSMETADATA.name())) {
                responseSection.add(CapabilitiesSection.OPERATIONSMETADATA);
            }
            else if (section.equalsIgnoreCase(CapabilitiesSection.SERVICEIDENTIFICATION.name())) {
                responseSection.add(CapabilitiesSection.SERVICEIDENTIFICATION);
            }
            else if (section.equalsIgnoreCase(CapabilitiesSection.SERVICEPROVIDER.name())) {
                responseSection.add(CapabilitiesSection.SERVICEPROVIDER);
            }
            else if (section.equalsIgnoreCase(CapabilitiesSection.ALL.name())) {
                responseSection.add(CapabilitiesSection.ALL);
            }
            else {
                OwsExceptionReport se = new OwsExceptionReport(ExceptionCode.InvalidParameterValue,
                                                               null,
                                                               "The parameter 'Sections' has a wrong value: '"
                                                                       + section
                                                                       + "'. Please use only (one of, or a combination of) these values: "
                                                                       + CapabilitiesSection.SERVICEIDENTIFICATION.name() + ", "
                                                                       + CapabilitiesSection.SERVICEPROVIDER.name() + ", "
                                                                       + CapabilitiesSection.OPERATIONSMETADATA.name() + ", "
                                                                       + CapabilitiesSection.CONTENTS.name() + ", "
                                                                       + CapabilitiesSection.ALL.name());
                log.error("The sections parameter is incorrect.", se);
                throw se;
            }
        }
        return responseSection;
    }

    /**
     * 
     * @param updateSequence
     * @return
     * @throws OwsExceptionReport
     */
    private boolean checkUpdateSequenceEquals(String updateSequence) throws OwsExceptionReport {
        SimpleDateFormat gmlSdf = new SimpleDateFormat(PropertiesManager.getInstance().getGmlDateFormat());
        if (updateSequence != null && !updateSequence.isEmpty() && !updateSequence.equals("NOT_SET")) {

            try {
                Date usDate = gmlSdf.parse(updateSequence);
                Date sorUpdateSequence = gmlSdf.parse(PropertiesManager.getInstance().getUpdateSequence());
                if (usDate.equals(sorUpdateSequence)) {
                    return true;
                }
                else if (usDate.after(sorUpdateSequence)) {
                    OwsExceptionReport se = new OwsExceptionReport(ExceptionCode.InvalidUpdateSequence,
                                                                   null,
                                                                   "The parameter 'updateSequence'"
                                                                           + " is wrong. The Value should be a date in gml-format and could not be after '"
                                                                           + gmlSdf.format(sorUpdateSequence));
                    log.error("The update Sequence parameter is wrong!", se);
                    throw se;
                }
            }
            catch (ParseException pe) {
                OwsExceptionReport se = new OwsExceptionReport(OwsExceptionReport.ExceptionCode.InvalidUpdateSequence,
                                                               null,
                                                               "The value of parameter update sequence has to be a date in the following GML format: '"
                                                                       + gmlSdf.format(new Date())
                                                                       + "'. The requested value was: '"
                                                                       + updateSequence + "' and could not be parsed!");
                log.error("The date of the update sequence could not be parsed!", pe);
                throw se;
            }
        }
        return false;
    }

    /*
     * @see ISorProcess#receiveRequest(ISorRequest)
     */
    @Override
    public ISorResponse receiveRequest(ISorRequest request) {
        SorGetCapabilitiesResponse response = new SorGetCapabilitiesResponse();
        try {
            // GetCapabilitiesRequests
            SorGetCapabilitiesRequest sorRequest = (SorGetCapabilitiesRequest) request;

            // check acceptVersions
            if (sorRequest.getAcceptVersions() != null) {
                checkAcceptedVersions(sorRequest.getAcceptVersions());
            }

            // check Sections
            if (sorRequest.getSections() != null) {
                response.setSections(checkSections(sorRequest.getSections()));
            }
            else {
                ArrayList<CapabilitiesSection> temp = new ArrayList<>();
                temp.add(CapabilitiesSection.ALL);
                response.setSections(temp);
            }

            // AcceptFormats (Not supported yet)
            if (sorRequest.getAcceptFormats() != null) {
                log.warn("AcceptFormats not implemented yet, values are ignored by service.");
            }

            // check updateSequence
            if (sorRequest.getUpdateSequence() != null) {
                if (checkUpdateSequenceEquals(sorRequest.getUpdateSequence())) {
                    return new SorGetCapabilitiesResponse();
                }
            }
        }
        catch (OwsExceptionReport se) {
            return new SorExceptionReportResponse(se.getDocument());
        }
        return response;
    }
}