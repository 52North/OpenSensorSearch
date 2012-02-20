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
import net.opengis.ows.x11.VersionType;
import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.sos.x10.DescribeSensorDocument;
import net.opengis.sos.x10.DescribeSensorDocument.DescribeSensor;
import net.opengis.sos.x10.GetCapabilitiesDocument;
import net.opengis.sos.x10.GetCapabilitiesDocument.GetCapabilities;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sir.SirConfigurator;
import org.n52.sir.SirConstants;
import org.n52.sir.client.Client;
import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.datastructure.SirSensor;
import org.n52.sir.datastructure.SirService;
import org.n52.sir.datastructure.SirServiceReference;
import org.n52.sir.ds.IDAOFactory;
import org.n52.sir.ds.IHarvestServiceDAO;
import org.n52.sir.ds.IInsertSensorInfoDAO;
import org.n52.sir.ds.ISearchSensorDAO;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.request.SirHarvestServiceRequest;
import org.n52.sir.response.ISirResponse;
import org.n52.sir.response.SirHarvestServiceResponse;
import org.n52.sir.sml.SensorMLDecoder;
import org.n52.sir.xml.IProfileValidator;
import org.n52.sir.xml.IValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    protected IValidatorFactory validatorFactory;

    /**
     * @throws OwsExceptionReport
     * 
     */
    public Harvester(IHarvestServiceDAO harvServDao) throws OwsExceptionReport {
        this.harvServDao = harvServDao;

        this.insertedSensors = new ArrayList<SirSensor>();
        this.updatedSensors = new ArrayList<SirSensor>();
        this.failedSensors = new HashMap<String, String>();

        SirConfigurator configurator = SirConfigurator.getInstance();
        this.validatorFactory = configurator.getValidatorFactory();

        try {
            IDAOFactory factory = SirConfigurator.getInstance().getFactory();
            this.insertSensorDao = factory.insertSensorInfoDAO();
            this.searchSensorDao = factory.searchSensorDAO();
        }
        catch (OwsExceptionReport e) {
            log.error("Error while creating the harvestServiceDAO", e);
            throw e;
        }
    }

    /**
     * 
     * @param serviceType
     */
    private static String createGetCapabilities(String serviceType) {
        if (serviceType.equals(SirConstants.SOS_SERVICE_TYPE)) {
            GetCapabilitiesDocument gcdoc = GetCapabilitiesDocument.Factory.newInstance();
            GetCapabilities gc = gcdoc.addNewGetCapabilities();
            gc.setService(serviceType);
            VersionType version = gc.addNewAcceptVersions().addNewVersion();
            version.setStringValue(SirConstants.SOS_VERSION);
            return gcdoc.xmlText();
        }
        if (serviceType.equals(SirConstants.SPS_SERVICE_TYPE)) {
            net.opengis.sps.x10.GetCapabilitiesDocument gcdoc = net.opengis.sps.x10.GetCapabilitiesDocument.Factory.newInstance();
            net.opengis.sps.x10.GetCapabilitiesDocument.GetCapabilities gc = gcdoc.addNewGetCapabilities();
            gc.setService(serviceType);
            return gcdoc.xmlText();
        }

        throw new IllegalArgumentException("Service type not supported: " + serviceType);
    }

    /**
     * @param request
     * @param response
     * @param insertedSensors
     * @param failedSensors
     * @param updatedSensors
     * @param uri
     * @param versionType
     * @param serviceType
     * @param outputFormatType
     * @param procedureType
     * @throws OwsExceptionReport
     */
    protected void processProcedure(SirHarvestServiceRequest request,
                                    SirHarvestServiceResponse response,
                                    Collection<SirSensor> insertedSensorsP,
                                    Collection<SirSensor> updatedSensorsP,
                                    Map<String, String> failedSensorsP,
                                    URI uri,
                                    String versionType,
                                    String serviceType,
                                    ValueType outputFormatType,
                                    ValueType procedureType) {
        log.info("Processing procedure: " + procedureType.getStringValue() + " for " + request.getServiceType() + " @ "
                + request.getServiceUrl());

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
            XmlObject xmlResponse = Client.xSendPostRequest(descSensorDoc, uri);
            sensorMLDocument = SensorMLDocument.Factory.parse(xmlResponse.getDomNode());

            processSensorMLDocument(request.getServiceUrl(),
                                    request.getServiceType(),
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
            String errMsg = "Error requesting SensorML document from " + request.getServiceType() + " @ "
                    + uri.toString() + " for sensor " + serviceSpecificSensorId + " : " + ioe.getMessage();
            log.error(errMsg);
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidRequest, null, errMsg);

            failedSensorsP.put(serviceSpecificSensorId, se.getMessage());
            response.setNumberOfFailedSensors(response.getNumberOfFailedSensors() + 1);
        }
        catch (OwsExceptionReport e) {
            String errMsg = "Error requesting SensorML document from " + request.getServiceType() + " @ "
                    + uri.toString() + " for sensor " + serviceSpecificSensorId + " : " + e.getMessage();
            log.error(errMsg);

            failedSensorsP.put(serviceSpecificSensorId, e.getMessage());
            response.setNumberOfFailedSensors(response.getNumberOfFailedSensors() + 1);
        }
    }

    /**
     * 
     * @param serviceURL
     * @param serviceType
     * @param insertedSensors
     * @param updatedSensors
     * @param failedSensors
     * @param sensorMLDocument
     * @param serviceSpecificSensorId
     * @throws OwsExceptionReport
     */
    protected void processSensorMLDocument(String serviceURL,
                                           String serviceType,
                                           Collection<SirSensor> insertedSensorsP,
                                           Collection<SirSensor> updatedSensorsP,
                                           SensorMLDocument sensorMLDocument,
                                           String serviceSpecificSensorId) throws OwsExceptionReport {
        // check SensorML for conformity with profile
        IProfileValidator profileValidator = this.validatorFactory.getSensorMLProfile4DiscoveryValidator();
        boolean isValid = profileValidator.validate(sensorMLDocument);
        if ( !isValid) {
            String errMsg = "Sensor metadata document of sensor " + serviceSpecificSensorId + " in " + serviceURL
                    + " is not conform with the required profile and cannot be harvested.\n\n"
                    + String.valueOf(profileValidator.getValidationFailuresAsString());
            throw new OwsExceptionReport(ExceptionCode.InvalidParameterValue, "sensor description", errMsg);
        }
        profileValidator = null;
        log.info("Sensor description is conform with discovery profile!");

        // set sensor
        SirSensor sensor = SensorMLDecoder.decode(sensorMLDocument);

        // set last update time
        sensor.setLastUpdate(new Date());

        // set service description
        ArrayList<SirServiceReference> servDescs = new ArrayList<SirServiceReference>();
        servDescs.add(new SirServiceReference(new SirService(serviceURL, serviceType), serviceSpecificSensorId));
        sensor.setServDescs(servDescs);

        // add sensor do database
        if (log.isDebugEnabled())
            log.debug("Saving harvested sensor " + serviceSpecificSensorId + " to DB.");

        SirSensor temporarySensor = this.harvServDao.insertSensor(sensor);

        if (temporarySensor.getSensorIDInSIR() != null) {
            // add to inserted sensor to response
            insertedSensorsP.add(temporarySensor);

            log.info("Inserted sensor " + temporarySensor);
        }
        else {
            if (log.isDebugEnabled())
                log.debug("Could not insert sensor, trying to update..." + temporarySensor);

            // could still be updateable
            SirService sirService = new SirService(serviceURL, serviceType);
            SirServiceReference serviceRef = new SirServiceReference(sirService, serviceSpecificSensorId);

            SirSearchResultElement result = this.searchSensorDao.getSensorByServiceDescription(serviceRef, false);

            if (result != null) {
                if (log.isDebugEnabled())
                    log.debug("Found and will update sensor: " + result);

                sensor.setSensorIDInSIR(result.getSensorIdInSir());
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

    /**
     * 
     * @param request
     * @param response
     * @param insertedSensors
     * @param updatedSensors
     * @param failedSensors
     * @param currentUri
     * @throws OwsExceptionReport
     */
    protected void processURI(SirHarvestServiceRequest request,
                              Collection<SirSensor> insertedSensorsP,
                              Map<String, String> failedSensorsP,
                              Collection<SirSensor> updatedSensorsP,
                              String sensorID,
                              URI sensorDefinition) {
        if (log.isDebugEnabled())
            log.debug("Processing URI: " + sensorDefinition);

        SensorMLDocument sensorMLDocument = null;
        try {
            // request sensor descriptions
            XmlObject xmlResponse = Client.xSendGetRequest(sensorDefinition);

            if (xmlResponse instanceof SensorMLDocument) {
                sensorMLDocument = (SensorMLDocument) xmlResponse;

                processSensorMLDocument(request.getServiceUrl(),
                                        request.getServiceType(),
                                        insertedSensorsP,
                                        updatedSensorsP,
                                        sensorMLDocument,
                                        sensorID);
            }
            else {
                String errMsg = "Did not get SensorML document from " + request.getServiceType() + " @ "
                        + sensorDefinition.toString() + ": " + xmlResponse.xmlText();
                OwsExceptionReport e = new OwsExceptionReport(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                                              null,
                                                              errMsg);
                log.error("No SensorML", e);

                failedSensorsP.put(sensorID, e.getMessage());
            }

        }
        catch (OwsExceptionReport e) {
            log.error("OWS Report", e.getDocument());
            failedSensorsP.put(sensorID, e.getMessage());
        }
    }

    /**
     * @param request
     * @param response
     * @param uri
     * @return
     * @throws OwsExceptionReport
     */
    public static XmlObject requestCapabilities(String serviceType, URI uri) throws OwsExceptionReport {
        // create getCapabilities request
        String gcDoc = createGetCapabilities(serviceType);

        if (log.isDebugEnabled())
            log.debug("GetCapabilities to be send to " + serviceType + " @ " + uri.toString() + ": " + gcDoc);

        // send getCapabilities request
        XmlObject caps = null;
        XmlObject getCapXmlResponse = null;
        try {
            getCapXmlResponse = Client.xSendPostRequest(XmlObject.Factory.parse(gcDoc), uri);
            caps = XmlObject.Factory.parse(getCapXmlResponse.getDomNode());
        }
        catch (XmlException xmle) {
            String msg = "Error on parsing Capabilities document: " + xmle.getMessage()
                    + (getCapXmlResponse == null ? "" : "\n" + getCapXmlResponse.xmlText());
            log.warn(msg);
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidRequest, null, msg);
            throw se;
        }
        catch (IOException ioe) {
            String errMsg = "Error sending GetCapabilities to " + serviceType + " @ " + uri.toString() + " : "
                    + ioe.getMessage();
            log.warn(errMsg);
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidRequest, null, errMsg);
            throw se;
        }
        catch (Exception e) {
            String errMsg = "Error doing GetCapabilities to " + serviceType + " @ " + uri.toString() + " : "
                    + e.getMessage();
            log.warn(errMsg);
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidRequest, null, errMsg);
            throw se;
        }

        return caps;
    }

}
