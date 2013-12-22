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
/**
 * 
 */

package org.n52.sir.listener.harvest;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import net.opengis.ows.x11.ValueType;
import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.sos.x10.DescribeSensorDocument;
import net.opengis.sos.x10.DescribeSensorDocument.DescribeSensor;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.oss.sir.Client;
import org.n52.oss.sir.api.SirSearchResultElement;
import org.n52.oss.sir.api.SirSensor;
import org.n52.oss.sir.api.SirService;
import org.n52.oss.sir.api.SirServiceReference;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.ds.IHarvestServiceDAO;
import org.n52.sir.ds.IInsertSensorInfoDAO;
import org.n52.sir.ds.ISearchSensorDAO;
import org.n52.sir.request.SirHarvestServiceRequest;
import org.n52.sir.response.ISirResponse;
import org.n52.sir.response.SirHarvestServiceResponse;
import org.n52.sir.sml.SensorMLDecoder;
import org.n52.sir.xml.IProfileValidator;
import org.n52.sir.xml.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.name.Named;

/**
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public abstract class Harvester implements Callable<ISirResponse> {

    private static Logger log = LoggerFactory.getLogger(Harvester.class);

    protected HashMap<String, String> failedSensors;

    protected IHarvestServiceDAO harvServDao;

    protected ArrayList<SirSensor> insertedSensors;

    private IInsertSensorInfoDAO insertSensorDao;

    private ISearchSensorDAO searchSensorDao;

    protected ArrayList<SirSensor> updatedSensors;

    private Client client;

    protected SirHarvestServiceRequest request;

    private IProfileValidator validator;

    protected boolean validateResponses;

    public Harvester(IHarvestServiceDAO harvServDao, IInsertSensorInfoDAO insertDao, @Named(ISearchSensorDAO.FULL)
    ISearchSensorDAO searchDao, Client client, IProfileValidator validator, boolean validateResponses) {
        this.harvServDao = harvServDao;
        this.client = client;
        this.validator = validator;
        this.validateResponses = validateResponses;

        this.insertedSensors = new ArrayList<>();
        this.updatedSensors = new ArrayList<>();
        this.failedSensors = new HashMap<>();

        this.insertSensorDao = insertDao;
        this.searchSensorDao = searchDao;

        log.info("NEW {}", this);
    }

    public void setRequest(SirHarvestServiceRequest request) {
        this.request = request;
    }

    protected void processProcedure(SirHarvestServiceResponse response,
                                    Collection<SirSensor> insertedSensorsP,
                                    Collection<SirSensor> updatedSensorsP,
                                    Map<String, String> failedSensorsP,
                                    URI uri,
                                    String versionType,
                                    String serviceType,
                                    ValueType outputFormatType,
                                    ValueType procedureType) {
        log.info("Processing procedure: " + procedureType.getStringValue() + " for " + this.request.getServiceType()
                + " @ " + this.request.getServiceUrl());

        // increase number of found sensors in response
        response.setNumberOfFoundSensors(response.getNumberOfFoundSensors() + 1);

        String serviceSpecificSensorId = procedureType.getStringValue();

        // build describe sensor request for SOS
        DescribeSensorDocument descSensorDoc = DescribeSensorDocument.Factory.newInstance();
        DescribeSensor descSensor = descSensorDoc.addNewDescribeSensor();
        descSensor.setService(serviceType);
        descSensor.setVersion(versionType);
        descSensor.setProcedure(serviceSpecificSensorId);
        descSensor.setOutputFormat(outputFormatType.getStringValue());

        SensorMLDocument sensorMLDocument = null;
        try {
            XmlObject xmlResponse = this.client.xSendPostRequest(descSensorDoc, uri);
            sensorMLDocument = SensorMLDocument.Factory.parse(xmlResponse.getDomNode());

            processSensorMLDocument(this.request.getServiceUrl(),
                                    this.request.getServiceType(),
                                    insertedSensorsP,
                                    updatedSensorsP,
                                    sensorMLDocument,
                                    serviceSpecificSensorId);
        }
        catch (XmlException xmle) {
            log.error("Error on parsing SensorML document: " + xmle.getMessage());
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                 null,
                                 "Error on parsing SensorML document: " + xmle.getMessage());

            failedSensorsP.put(serviceSpecificSensorId, se.getMessage());
            response.setNumberOfFailedSensors(response.getNumberOfFailedSensors() + 1);
        }
        catch (IOException ioe) {
            String errMsg = "Error requesting SensorML document from " + this.request.getServiceType() + " @ "
                    + uri.toString() + " for sensor " + serviceSpecificSensorId + " : " + ioe.getMessage();
            log.error(errMsg);
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidRequest, null, errMsg);

            failedSensorsP.put(serviceSpecificSensorId, se.getMessage());
            response.setNumberOfFailedSensors(response.getNumberOfFailedSensors() + 1);
        }
        catch (OwsExceptionReport e) {
            String errMsg = "Error requesting SensorML document from " + this.request.getServiceType() + " @ "
                    + uri.toString() + " for sensor " + serviceSpecificSensorId + " : " + e.getMessage();
            log.error(errMsg);

            failedSensorsP.put(serviceSpecificSensorId, e.getMessage());
            response.setNumberOfFailedSensors(response.getNumberOfFailedSensors() + 1);
        }
    }

    protected void processSensorMLDocument(String serviceURL,
                                           String serviceType,
                                           Collection<SirSensor> insertedSensorsP,
                                           Collection<SirSensor> updatedSensorsP,
                                           SensorMLDocument sensorMLDocument,
                                           String serviceSpecificSensorId) throws OwsExceptionReport, IOException {
        // check SensorML for conformity with profile
        ValidationResult result = this.validator.validate(sensorMLDocument);
        boolean isValid = result.isValidated();
        if ( !isValid) {
            String errMsg = "Sensor metadata document of sensor " + serviceSpecificSensorId + " in " + serviceURL
                    + " is not conform with the required profile and cannot be harvested.\n\n"
                    + result.getValidationFailuresAsString();
            throw new OwsExceptionReport(ExceptionCode.InvalidParameterValue, "sensor description", errMsg);
        }
        
        log.info("Sensor description is conform with discovery profile!");

        // set sensor
        SensorMLDecoder decoder = new SensorMLDecoder();
        SirSensor sensor = decoder.decode(sensorMLDocument);

        // set last update time
        sensor.setLastUpdate(new Date());

        // set service description
        ArrayList<SirServiceReference> servDescs = new ArrayList<>();
        servDescs.add(new SirServiceReference(new SirService(serviceURL, serviceType), serviceSpecificSensorId));
        sensor.setServDescs(servDescs);

        // add sensor do database
        log.debug("Saving harvested sensor {} to DB.", serviceSpecificSensorId);

        SirSensor temporarySensor = this.harvServDao.insertSensor(sensor);

        if (temporarySensor.getInternalSensorID() != null) {
            // add to inserted sensor to response
            insertedSensorsP.add(temporarySensor);

            log.info("Inserted sensor " + temporarySensor);
        }
        else {
                log.debug("Could not insert sensor, trying to update: {}", temporarySensor);

            // could still be updateable
            SirService sirService = new SirService(serviceURL, serviceType);
            SirServiceReference serviceRef = new SirServiceReference(sirService, serviceSpecificSensorId);

            SirSearchResultElement searchResult = this.searchSensorDao.getSensorByServiceDescription(serviceRef, false);

            if (searchResult != null) {
                    log.debug("Found and will update sensor: {}", searchResult);

                sensor.setInternalSensorId(searchResult.getSensorId());
                String id = this.insertSensorDao.updateSensor(serviceRef, sensor);
                updatedSensorsP.add(sensor);

                log.info("Updated sensor with id " + id);
            }
            else {
                String errMsg = "Could not add sensor to database, because it could neither be inserted nor updated: "
                        + sensor
                        + "\nIf the sensor was inserted once without (or with a different) service reference, it is not updatable.";
                log.error(errMsg);
                throw new OwsExceptionReport(ExceptionCode.NoApplicableCode, "", errMsg);
            }
        }
    }

    protected void processURI(Collection<SirSensor> insertedSensorsP,
                              Map<String, String> failedSensorsP,
                              Collection<SirSensor> updatedSensorsP,
                              String sensorID,
                              URI sensorDefinition) {
        log.debug("Processing URI: {}", sensorDefinition);

        SensorMLDocument sensorMLDocument = null;
        try {
            // request sensor descriptions
            XmlObject xmlResponse = this.client.xSendGetRequest(sensorDefinition);

            if (xmlResponse instanceof SensorMLDocument) {
                sensorMLDocument = (SensorMLDocument) xmlResponse;

                processSensorMLDocument(this.request.getServiceUrl(),
                                        this.request.getServiceType(),
                                        insertedSensorsP,
                                        updatedSensorsP,
                                        sensorMLDocument,
                                        sensorID);
            }
            else {
                String errMsg = "Did not get SensorML document from " + this.request.getServiceType() + " @ "
                        + sensorDefinition.toString() + ": " + xmlResponse.xmlText();
                OwsExceptionReport e = new OwsExceptionReport(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                                              null,
                                                              errMsg);
                log.error("No SensorML", e);

                failedSensorsP.put(sensorID, e.getMessage());
            }

        }
        catch (OwsExceptionReport | IOException e) {
            log.error("Error processing SensorML Document", e);
            failedSensorsP.put(sensorID, e.getMessage());
        }
    }

}
