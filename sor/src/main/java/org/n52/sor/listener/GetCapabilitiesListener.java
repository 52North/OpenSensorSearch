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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.n52.sor.OwsExceptionReport;
import org.n52.sor.OwsExceptionReport.ExceptionCode;
import org.n52.sor.PhenomenonManager;
import org.n52.sor.PropertiesManager;
import org.n52.sor.request.ISorRequest;
import org.n52.sor.request.SorGetCapabilitiesRequest;
import org.n52.sor.response.ISorResponse;
import org.n52.sor.response.SorExceptionReportResponse;
import org.n52.sor.response.SorGetCapabilitiesResponse;
import org.n52.sor.response.SorGetCapabilitiesResponse.Section;
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

    private ArrayList<Section> checkSections(String[] sections) throws OwsExceptionReport {
        ArrayList<Section> responseSection = new ArrayList<>();
        for (String section : sections) {
            if (section.equalsIgnoreCase(Section.CONTENTS.name())) {
                responseSection.add(Section.CONTENTS);
            }
            else if (section.equalsIgnoreCase(Section.OPERATIONSMETADATA.name())) {
                responseSection.add(Section.OPERATIONSMETADATA);
            }
            else if (section.equalsIgnoreCase(Section.SERVICEIDENTIFICATION.name())) {
                responseSection.add(Section.SERVICEIDENTIFICATION);
            }
            else if (section.equalsIgnoreCase(Section.SERVICEPROVIDER.name())) {
                responseSection.add(Section.SERVICEPROVIDER);
            }
            else if (section.equalsIgnoreCase(Section.ALL.name())) {
                responseSection.add(Section.ALL);
            }
            else {
                OwsExceptionReport se = new OwsExceptionReport(ExceptionCode.InvalidParameterValue,
                                                               null,
                                                               "The parameter 'Sections' has a wrong value: '"
                                                                       + section
                                                                       + "'. Please use only (one of, or a combination of) these values: "
                                                                       + Section.SERVICEIDENTIFICATION.name() + ", "
                                                                       + Section.SERVICEPROVIDER.name() + ", "
                                                                       + Section.OPERATIONSMETADATA.name() + ", "
                                                                       + Section.CONTENTS.name() + ", "
                                                                       + Section.ALL.name());
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
                ArrayList<Section> temp = new ArrayList<>();
                temp.add(Section.ALL);
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