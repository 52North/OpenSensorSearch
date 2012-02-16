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
package org.n52.sir.listener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

import org.n52.sir.SirConfigurator;
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

public class GetCapabilitiesListener implements ISirRequestListener {

    /**
     * the data access object for the getCapabilities operation
     */
    private IGetCapabilitiesDAO capDao;

    /**
     * the logger, used to log exceptions and additionally information
     */
    private static Logger log = LoggerFactory.getLogger(GetCapabilitiesListener.class);

    /**
     * Name of the operation the listener implements
     */
    private static final String OPERATION_NAME = SirConstants.Operations.GetCapabilities.name();

    public GetCapabilitiesListener() throws OwsExceptionReport {

        // get sir configurator
        SirConfigurator configurator = SirConfigurator.getInstance();

        // setting up DAOFactory
        IDAOFactory factory = configurator.getFactory();

        try {
            this.capDao = factory.getCapabilitiesDAO();
        }
        catch (OwsExceptionReport se) {
            log.error("Error while creating the getCapabilitiesDAO", se);
            throw se;
        }
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
                ArrayList<Section> temp = new ArrayList<Section>();
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

    private boolean checkUpdateSequenceEquals(String updateSequence) throws OwsExceptionReport {
        if (updateSequence != null && !updateSequence.equals("") && !updateSequence.equals("NOT_SET")) {

            try {
                Calendar usDate = GMLDateParser.getInstance().parseString(updateSequence);
                Calendar sorUpdateSequence = GMLDateParser.getInstance().parseString(SirConfigurator.getInstance().getUpdateSequence());
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

    private ArrayList<Section> checkSections(String[] sections) throws OwsExceptionReport {
        ArrayList<Section> responseSection = new ArrayList<Section>();
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

    private void checkAcceptedVersions(String[] versions) throws OwsExceptionReport {
        // String serviceVersion = SirConfigurator.getInstance().getServiceVersion();
        for (String version : versions) {
            ListenersTools.checkVersionParameter(version);

            // if (version.equals(serviceVersion)) {
            // return;
            // }
        }
    }
}