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

import java.util.ArrayList;
import java.util.Collection;

import org.n52.sir.SirConfigurator;
import org.n52.sir.SirConstants;
import org.n52.sir.datastructure.SirInfoToBeDeleted;
import org.n52.sir.datastructure.SirSensorIdentification;
import org.n52.sir.datastructure.SirServiceReference;
import org.n52.sir.ds.IDAOFactory;
import org.n52.sir.ds.IInsertSensorInfoDAO;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.request.AbstractSirRequest;
import org.n52.sir.request.SirDeleteSensorInfoRequest;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.n52.sir.response.SirDeleteSensorInfoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Nüst
 * 
 */
public class DeleteSensorInfoListener implements ISirRequestListener {

    /**
     * the logger, used to log exceptions and additionally information
     */
    private static Logger log = LoggerFactory.getLogger(DeleteSensorInfoListener.class);

    private static final String OPERATION_NAME = SirConstants.Operations.DeleteSensorInfo.name();

    private IInsertSensorInfoDAO insertSensorInfoDAO;

    public DeleteSensorInfoListener() throws OwsExceptionReport {
        SirConfigurator configurator = SirConfigurator.getInstance();

        IDAOFactory factory = configurator.getFactory();

        this.insertSensorInfoDAO = factory.insertSensorInfoDAO();
    }

    /**
     * @param response
     * @param sensorIdent
     * @param deletedSensors
     * @throws OwsExceptionReport
     */
    private void deleteSensor(SirDeleteSensorInfoResponse response,
                              SirSensorIdentification sensorIdent,
                              ArrayList<String> deletedSensors) throws OwsExceptionReport {
        // check if ident is present, cannot delete otherwise
        if (sensorIdent != null) {
            // DELETE
            String sID = this.insertSensorInfoDAO.deleteSensor(sensorIdent);

            if (sID != null) {
                deletedSensors.add(sID);
                response.setNumberOfDeletedSensors(response.getNumberOfDeletedSensors() + 1);
                if (log.isDebugEnabled())
                    log.debug("Deleted Sensor! " + sID);
            }
        }
        else {
            OwsExceptionReport se = new OwsExceptionReport(OwsExceptionReport.ExceptionLevel.DetailedExceptions);
            se.addCodedException(OwsExceptionReport.ExceptionCode.MissingParameterValue,
                                 "DeleteSensorInfoListener.receiveRequest()",
                                 "Missing parameter: To delete a sensor, a sensorIdentification element is required!");
            throw se;
        }
    }

    /**
     * 
     * @param response
     * @param serviceReference
     * @param sensIdent
     * @throws OwsExceptionReport
     */
    private void deleteServiceReference(SirDeleteSensorInfoResponse response,
                                        SirServiceReference serviceReference,
                                        SirSensorIdentification sensIdent) throws OwsExceptionReport {
        // check if ident is present, cannot delete otherwise
        if (serviceReference != null) {
            // DELETE
            String sID = this.insertSensorInfoDAO.deleteReference(sensIdent, serviceReference);

            if (sID != null) {
                response.setNumberOfDeletedServiceReferences(response.getNumberOfDeletedServiceReferences() + 1);

                if (log.isDebugEnabled())
                    log.debug("Deleted ServiceReference! " + sID);
            }
        }
        else {
            OwsExceptionReport se = new OwsExceptionReport(OwsExceptionReport.ExceptionLevel.DetailedExceptions);
            se.addCodedException(OwsExceptionReport.ExceptionCode.MissingParameterValue,
                                 "DeleteSensorInfoListener.receiveRequest()",
                                 "Missing parameter: To delete a service reference, a ServiceReference element is required!");
            throw se;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.ISirRequestListener#getOperationName()
     */
    @Override
    public String getOperationName() {
        return DeleteSensorInfoListener.OPERATION_NAME;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.n52.sir.ISirRequestListener#receiveRequest(org.n52.sir.request. AbstractSirRequest)
     */
    @Override
    public ISirResponse receiveRequest(AbstractSirRequest request) {

        SirDeleteSensorInfoRequest sirRequest = (SirDeleteSensorInfoRequest) request;
        SirDeleteSensorInfoResponse response = new SirDeleteSensorInfoResponse();

        ArrayList<String> deletedSensors = new ArrayList<String>();

        try {
            for (SirInfoToBeDeleted intoToBeDeleted : sirRequest.getInfoToBeDeleted()) {
                SirSensorIdentification sensorIdent = intoToBeDeleted.getSensorIdentification();

                if (intoToBeDeleted.isDeleteSensor()) {
                    // DELETE sensor
                    deleteSensor(response, sensorIdent, deletedSensors);
                }
                else if (intoToBeDeleted.getServiceInfo() != null) {
                    Collection<SirServiceReference> serviceReferences = intoToBeDeleted.getServiceInfo().getServiceReferences();

                    for (SirServiceReference serviceReference : serviceReferences) {
                        // DELETE service reference
                        deleteServiceReference(response, serviceReference, sensorIdent);
                    }
                }
            }
        }
        catch (OwsExceptionReport e) {
            return new ExceptionResponse(e.getDocument());
        }

        response.setDeletedSensors(deletedSensors);

        return response;
    }

}
