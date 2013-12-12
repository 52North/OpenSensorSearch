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

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import org.n52.oss.id.IdentifierGenerator;
import org.n52.oss.sir.SirConstants;
import org.n52.oss.sir.api.InternalSensorID;
import org.n52.oss.sir.api.SirInfoToBeInserted;
import org.n52.oss.sir.api.SirInfoToBeInserted_SensorDescription;
import org.n52.oss.sir.api.SirInfoToBeInserted_ServiceReference;
import org.n52.oss.sir.api.SirSensor;
import org.n52.oss.sir.api.SirServiceReference;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.ds.IInsertSensorInfoDAO;
import org.n52.sir.ds.solr.SOLRInsertSensorInfoDAO;
import org.n52.sir.request.AbstractSirRequest;
import org.n52.sir.request.SirInsertSensorInfoRequest;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.n52.sir.response.SirInsertSensorInfoResponse;
import org.n52.sir.sml.SensorMLDecoder;
import org.n52.sir.xml.IProfileValidator;
import org.n52.sir.xml.ValidationResult;
import org.n52.sir.xml.ValidatorModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author Jan Schulte
 * 
 */
public class InsertSensorInfoListener implements ISirRequestListener {

    private static Logger log = LoggerFactory.getLogger(InsertSensorInfoListener.class);

    private static final String OPERATION_NAME = SirConstants.Operations.InsertSensorInfo.name();

    private IInsertSensorInfoDAO insSensInfoDao;

    private IdentifierGenerator identifierGenerator;

    private SOLRInsertSensorInfoDAO anotherInsSensInfoDao;

    private IProfileValidator validator;

    @Inject
    public InsertSensorInfoListener(IdentifierGenerator idGen,
                                    IInsertSensorInfoDAO dao,
                                    SOLRInsertSensorInfoDAO anotherInsSensorInfoDao,
                                    Set<IProfileValidator> validators) {
        this.validator = ValidatorModule.getFirstMatchFor(validators,
                                                          IProfileValidator.ValidatableFormatAndProfile.SML_DISCOVERY);
        this.identifierGenerator = idGen;
        this.anotherInsSensInfoDao = anotherInsSensorInfoDao;

        this.insSensInfoDao = dao;

        log.info("NEW {}", this);
    }

    @Override
    public String getOperationName() {
        return InsertSensorInfoListener.OPERATION_NAME;
    }

    private void insertSensor(SirInsertSensorInfoResponse response,
                              Collection<SirServiceReference> serviceRefs,
                              SirSensor sensor) throws OwsExceptionReport, IOException {

        String id = this.identifierGenerator.generate();
        sensor.setInternalSensorId(id);
        log.debug("InsertSensor called, generated new ID {}", id);

        if (sensor.getSensorMLDocument() != null) {
            ValidationResult result = this.validator.validate(sensor.getSensorMLDocument());
            boolean isValid = result.isValidated();
            log.debug("The sensor is valid: {}", isValid);

            if (isValid) {

                // TODO Daniel: implement listener mechanism or event bus to support several databases
                // dynamically

                /*
                 * Inserts into solr
                 */
                String sensorIdInSolr = this.anotherInsSensInfoDao.insertSensor(sensor);
                log.debug("Inserted sensor in solr: " + sensorIdInSolr);
                if (sensorIdInSolr == null)
                    log.warn("Could not insert sensor to solr.");

                /*
                 * Insert to database
                 */
                String sensorIdInDB = this.insSensInfoDao.insertSensor(sensor);
                log.debug("Inserted sensor in database: " + sensorIdInDB);

                if (// sensorIdInSolr != null && sensorIdInSolr.equals(sensorIdInDB) &&
                sensorIdInDB != null) {
                    String internalSensorId = sensorIdInDB;
                    response.setNumberOfNewSensors(response.getNumberOfNewSensors() + 1);
                    response.getInsertedSensors().add(internalSensorId);
                    log.debug("Inserted Sensor und updated response for {}", internalSensorId);

                    if (serviceRefs != null) {
                        for (SirServiceReference servRef : serviceRefs) {
                            this.insSensInfoDao.addNewReference(new InternalSensorID(internalSensorId), servRef);
                            response.setNumberOfNewServiceReferences(response.getNumberOfNewServiceReferences() + 1);
                        }

                        log.debug("Inserted {} references for sensor {}: {}",
                                  serviceRefs.size(),
                                  internalSensorId,
                                  serviceRefs);
                    }
                }
                else
                    log.error("Could not insert sensor to databases, problem with ids: solr = {}, pg = {}",
                              sensorIdInSolr,
                              sensorIdInDB);

            }
            else
                log.error("SensorML is not profile conform: {}", result);
        }
        else {
            OwsExceptionReport se = new OwsExceptionReport(OwsExceptionReport.ExceptionLevel.DetailedExceptions);
            se.addCodedException(OwsExceptionReport.ExceptionCode.MissingParameterValue,
                                 "InsertSensorInfoListener.receiveRequest()",
                                 "Missing parameter: To insert a sensor, a sensorInfo element is required!");
            log.error("OWS:", se);
            throw se;
        }
    }

    private void insertServiceReferences(SirInsertSensorInfoResponse response,
                                         SirInfoToBeInserted_ServiceReference newReference) throws OwsExceptionReport {
        Collection<SirServiceReference> referenceArray = newReference.getServiceReferences();
        for (SirServiceReference sirServiceReference : referenceArray) {
            String id = this.insSensInfoDao.addNewReference(newReference.getID(), sirServiceReference);
            log.debug("Inserted service reference for sensor {}: {}", id, sirServiceReference.getService());

            response.setNumberOfNewServiceReferences(response.getNumberOfNewServiceReferences() + 1);
        }
    }

    @Override
    public ISirResponse receiveRequest(AbstractSirRequest request) {
        log.debug("** Receiving request: {}", request);

        SirInsertSensorInfoRequest sirRequest = (SirInsertSensorInfoRequest) request;
        SirInsertSensorInfoResponse response = new SirInsertSensorInfoResponse();
        SensorMLDecoder decoder = new SensorMLDecoder();

        try {
            for (SirInfoToBeInserted infoToBeInserted : sirRequest.getInfoToBeInserted()) {

                if (infoToBeInserted instanceof SirInfoToBeInserted_SensorDescription) {
                    SirInfoToBeInserted_SensorDescription newSensor = (SirInfoToBeInserted_SensorDescription) infoToBeInserted;
                    SirSensor sensor = decoder.decode(newSensor.getSensorDescription());

                    sensor.setLastUpdate(new Date());

                    Collection<SirServiceReference> serviceReferences = newSensor.getServiceReferences();

                    insertSensor(response, serviceReferences, sensor);
                }
                else if (infoToBeInserted instanceof SirInfoToBeInserted_ServiceReference) {
                    SirInfoToBeInserted_ServiceReference newReference = (SirInfoToBeInserted_ServiceReference) infoToBeInserted;

                    insertServiceReferences(response, newReference);
                }
            }
        }
        catch (OwsExceptionReport | IOException e) {
            return new ExceptionResponse(e);
        }

        log.debug("** Returning response: {}", response);

        return response;
    }

}
