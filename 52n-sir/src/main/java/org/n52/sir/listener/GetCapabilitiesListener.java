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

package org.n52.sir.listener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

import org.n52.sir.SirConfigurator.Section;
import org.n52.sir.SirConstants;
import org.n52.sir.ds.IDAOFactory;
import org.n52.sir.ds.IGetCapabilitiesDAO;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.request.AbstractSirRequest;
import org.n52.sir.request.SirGetCapabilitiesRequest;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.n52.sir.response.SirGetCapabilitiesResponse;
import org.n52.sir.util.GMLDateParser;
import org.n52.sir.util.ListenersTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class GetCapabilitiesListener implements ISirRequestListener {

    /**
     * the logger, used to log exceptions and additionally information
     */
    private static Logger log = LoggerFactory.getLogger(GetCapabilitiesListener.class);

    /**
     * Name of the operation the listener implements
     */
    private static final String OPERATION_NAME = SirConstants.Operations.GetCapabilities.name();

    /**
     * the data access object for the getCapabilities operation
     */
    private IGetCapabilitiesDAO capDao;

    @Inject
    public GetCapabilitiesListener(IDAOFactory daoFactory) throws OwsExceptionReport {
        try {
            this.capDao = daoFactory.getCapabilitiesDAO();
        }
        catch (OwsExceptionReport se) {
            log.error("Error while creating the getCapabilitiesDAO", se);
            throw se;
        }
    }

    private void checkAcceptedVersions(String[] versions) throws OwsExceptionReport {
        // String serviceVersion = SirConfigurator.getInstance().getServiceVersion();
        for (String version : versions) {
            ListenersTools.checkVersionParameter(version);

            // if (version.equals(serviceVersion)) {
            // return;
            // }
        }
    }

    private ArrayList<Section> checkSections(String[] sections) throws OwsExceptionReport {
        ArrayList<Section> responseSection = new ArrayList<>();
        for (String section : sections) {
            if (section.equalsIgnoreCase(Section.Contents.name())) {
                responseSection.add(Section.Contents);
            }
            else if (section.equalsIgnoreCase(Section.OperationsMetadata.name())) {
                responseSection.add(Section.OperationsMetadata);
            }
            else if (section.equalsIgnoreCase(Section.ServiceIdentification.name())) {
                responseSection.add(Section.ServiceIdentification);
            }
            else if (section.equalsIgnoreCase(Section.ServiceProvider.name())) {
                responseSection.add(Section.ServiceProvider);
            }
            else if (section.equalsIgnoreCase(Section.All.name())) {
                responseSection.add(Section.All);
            }
            else {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(ExceptionCode.InvalidParameterValue,
                                     null,
                                     "The parameter 'Sections' has a wrong value: '" + section
                                             + "'. Please use only this values: "
                                             + Section.ServiceIdentification.name() + ", "
                                             + Section.ServiceProvider.name() + ", "
                                             + Section.OperationsMetadata.name() + ", " + Section.Contents.name()
                                             + ", " + Section.All.name());
                log.error("The sections parameter is incorrect.", se);
                throw se;
            }
        }
        return responseSection;
    }

    private boolean checkUpdateSequenceEquals(String updateSequence) throws OwsExceptionReport {
        if (updateSequence != null && !updateSequence.equals("") && !updateSequence.equals("NOT_SET")) {

            try {
                Calendar usDate = GMLDateParser.getInstance().parseString(updateSequence);
                // Calendar sorUpdateSequence =
                // GMLDateParser.getInstance().parseString(this.configurator.getUpdateSequence());
                Calendar sorUpdateSequence = GMLDateParser.getInstance().parseString(null); // FIXME once the
                                                                                            // configuration
                                                                                            // works
                if (usDate.equals(sorUpdateSequence)) {
                    return true;
                }
                else if (usDate.after(sorUpdateSequence)) {
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(ExceptionCode.InvalidUpdateSequence, null, "The parameter 'updateSequence'"
                            + " is wrong. The Value should be a date in gml-format and could not be after '"
                            + GMLDateParser.getInstance().parseDate(sorUpdateSequence));
                    log.error("The update Sequence parameter is wrong!", se);
                    throw se;
                }
            }
            catch (ParseException pe) {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidUpdateSequence,
                                     null,
                                     "The value of parameter update sequence has to be a date in GML format like this '"
                                             + GMLDateParser.getInstance().parseDate(Calendar.getInstance())
                                             + "'! Your requested value was: '" + updateSequence + "'");
                log.error("The date of the update sequence could not be parsed!", pe);
                throw se;
            }
        }
        return false;
    }

    @Override
    public String getOperationName() {
        return GetCapabilitiesListener.OPERATION_NAME;
    }

    @Override
    public ISirResponse receiveRequest(AbstractSirRequest request) {
        try {
            SirGetCapabilitiesRequest sirRequest = (SirGetCapabilitiesRequest) request;
            SirGetCapabilitiesResponse response = new SirGetCapabilitiesResponse();

            // check service
            ListenersTools.checkServiceParameter(sirRequest.getService());

            // check acceptVersions
            if (sirRequest.getAcceptVersions() != null) {
                checkAcceptedVersions(sirRequest.getAcceptVersions());
            }
            // check sections
            if (sirRequest.getSections() != null) {
                response.setSections(checkSections(sirRequest.getSections()));
            }
            else {
                ArrayList<Section> temp = new ArrayList<>();
                temp.add(Section.All);
                response.setSections(temp);
            }

            // check updateSequence
            if (sirRequest.getUpdateSequence() != null) {
                if (checkUpdateSequenceEquals(sirRequest.getUpdateSequence())) {
                    return new SirGetCapabilitiesResponse();
                }
            }

            // TODO check AcceptFormats (Not supported now)

            // set harvested Services
            response.setServices(this.capDao.getServices());

            // set catalog connections
            response.setCatalogConnection(this.capDao.getCatalogConnections());

            return response;
        }
        catch (OwsExceptionReport se) {
            return new ExceptionResponse(se.getDocument());
        }
    }
}