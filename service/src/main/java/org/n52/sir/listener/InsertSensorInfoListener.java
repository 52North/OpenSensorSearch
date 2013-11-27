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

import org.n52.oss.id.IdentifierGenerator;
import org.n52.oss.sir.SirConstants;
import org.n52.oss.sir.api.InternalSensorID;
import org.n52.oss.sir.api.SirInfoToBeInserted;
import org.n52.oss.sir.api.SirInfoToBeInserted_SensorDescription;
import org.n52.oss.sir.api.SirInfoToBeInserted_ServiceReference;
import org.n52.oss.sir.api.SirSensor;
import org.n52.oss.sir.api.SirServiceReference;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.SirConfigurator;
import org.n52.sir.ds.IDAOFactory;
import org.n52.sir.ds.IInsertSensorInfoDAO;
import org.n52.sir.ds.solr.SOLRInsertSensorInfoDAO;
import org.n52.sir.request.AbstractSirRequest;
import org.n52.sir.request.SirInsertSensorInfoRequest;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.n52.sir.response.SirInsertSensorInfoResponse;
import org.n52.sir.sml.SensorMLDecoder;
import org.n52.sir.xml.IProfileValidator;
import org.n52.sir.xml.IValidatorFactory;
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

    private IValidatorFactory validatorFactory;

    private IdentifierGenerator identifierGenerator;

    private SOLRInsertSensorInfoDAO anotherInsSensInfoDao;

    @Inject
    public InsertSensorInfoListener(IdentifierGenerator idGen,
                                    SirConfigurator configurator,
                                    SOLRInsertSensorInfoDAO anotherInsSensorInfoDao) {
        IDAOFactory factory = configurator.getInstance().getFactory();
        this.validatorFactory = configurator.getInstance().getValidatorFactory();
        this.identifierGenerator = idGen;
        this.anotherInsSensInfoDao = anotherInsSensorInfoDao;

        try {
            this.insSensInfoDao = factory.insertSensorInfoDAO();
        }
        catch (OwsExceptionReport se) {
            log.error("Error while creating the insertSensorInfoDAO", se);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.ISirRequestListener#getOperationName()
     */
    @Override
    public String getOperationName() {
        return InsertSensorInfoListener.OPERATION_NAME;
    }

    /**
     * @param response
     * @param insertedSensors
     * @param sensorInfo
     * @param serviceInfos
     * @param sensor
     * @return
     * @throws OwsExceptionReport
     * @throws IOException
     */
    private void insertSensor(SirInsertSensorInfoResponse response,
                              Collection<SirServiceReference> serviceRefs,
                              SirSensor sensor) throws OwsExceptionReport, IOException {

        String id = this.identifierGenerator.generate();
        sensor.setInternalSensorId(id);
        log.debug("InsertSensor called, generated new ID {}", id);

        if (sensor.getSensorMLDocument() != null) {
            IProfileValidator profileValidator = this.validatorFactory.getSensorMLProfile4DiscoveryValidator();
            boolean isValid = profileValidator.validate(sensor.getSensorMLDocument());
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
                log.error("SensorML is not profile conform: {}",
                          String.valueOf(profileValidator.getValidationFailuresAsString()));

            profileValidator = null;
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

    /**
     * 
     * @param response
     * @param newReference
     * @throws OwsExceptionReport
     */
    private void insertServiceReferences(SirInsertSensorInfoResponse response,
                                         SirInfoToBeInserted_ServiceReference newReference) throws OwsExceptionReport {
        Collection<SirServiceReference> referenceArray = newReference.getServiceReferences();
        for (SirServiceReference sirServiceReference : referenceArray) {
            String id = this.insSensInfoDao.addNewReference(newReference.getID(), sirServiceReference);
            log.debug("Inserted service reference for sensor {}: {}", id, sirServiceReference.getService());

            response.setNumberOfNewServiceReferences(response.getNumberOfNewServiceReferences() + 1);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.n52.sir.ISirRequestListener#receiveRequest(org.n52.sir.request. AbstractSirRequest)
     */
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
