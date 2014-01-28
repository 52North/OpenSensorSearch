/**
 * Copyright 2013 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.sir.listener;

import java.util.ArrayList;
import java.util.Collection;

import org.n52.oss.sir.SirConstants;
import org.n52.oss.sir.api.SirInfoToBeDeleted;
import org.n52.oss.sir.api.SirSensorIdentification;
import org.n52.oss.sir.api.SirServiceReference;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.ds.IInsertSensorInfoDAO;
import org.n52.sir.request.AbstractSirRequest;
import org.n52.sir.request.SirDeleteSensorInfoRequest;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.n52.sir.response.SirDeleteSensorInfoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author Daniel Nüst
 * 
 */
public class DeleteSensorInfoListener implements ISirRequestListener {

    private static Logger log = LoggerFactory.getLogger(DeleteSensorInfoListener.class);

    private static final String OPERATION_NAME = SirConstants.Operations.DeleteSensorInfo.name();

    private IInsertSensorInfoDAO sensorInfoDao;

    @Inject
    public DeleteSensorInfoListener(IInsertSensorInfoDAO dao) {
        this.sensorInfoDao = dao;

        log.debug("NEW {}", this);
    }

    private void deleteSensor(SirDeleteSensorInfoResponse response,
                              SirSensorIdentification sensorIdent,
                              ArrayList<String> deletedSensors) throws OwsExceptionReport {
        if (sensorIdent != null) {
            String sID = this.sensorInfoDao.deleteSensor(sensorIdent);

            if (sID != null) {
                deletedSensors.add(sID);
                response.setNumberOfDeletedSensors(response.getNumberOfDeletedSensors() + 1);
                log.debug("Deleted Sensor: {}", sID);
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
        if (serviceReference != null) {
            String sID = this.sensorInfoDao.deleteReference(sensIdent, serviceReference);

            if (sID != null) {
                response.setNumberOfDeletedServiceReferences(response.getNumberOfDeletedServiceReferences() + 1);
                log.debug("Deleted ServiceReference for sensor {}" + sID);
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

    @Override
    public String getOperationName() {
        return DeleteSensorInfoListener.OPERATION_NAME;
    }

    @Override
    public ISirResponse receiveRequest(AbstractSirRequest request) {
        log.debug("** request: {}", request);

        SirDeleteSensorInfoRequest sirRequest = (SirDeleteSensorInfoRequest) request;
        SirDeleteSensorInfoResponse response = new SirDeleteSensorInfoResponse();

        ArrayList<String> deletedSensors = new ArrayList<>();

        try {
            for (SirInfoToBeDeleted intoToBeDeleted : sirRequest.getInfoToBeDeleted()) {
                SirSensorIdentification sensorIdent = intoToBeDeleted.getSensorIdentification();

                if (intoToBeDeleted.isDeleteSensor()) {
                    deleteSensor(response, sensorIdent, deletedSensors);
                }
                else if (intoToBeDeleted.getServiceInfo() != null) {
                    Collection<SirServiceReference> serviceReferences = intoToBeDeleted.getServiceInfo().getServiceReferences();

                    for (SirServiceReference serviceReference : serviceReferences) {
                        deleteServiceReference(response, serviceReference, sensorIdent);
                    }
                }
            }
        }
        catch (OwsExceptionReport e) {
            return new ExceptionResponse(e);
        }

        response.setDeletedSensors(deletedSensors);

        log.debug("** response: {}", response);
        return response;
    }

}
