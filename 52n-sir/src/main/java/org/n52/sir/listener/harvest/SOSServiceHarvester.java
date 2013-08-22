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

import java.net.URI;
import java.util.ArrayList;
import java.util.Map.Entry;

import net.opengis.ows.x11.DomainType;
import net.opengis.ows.x11.OperationDocument.Operation;
import net.opengis.ows.x11.OperationsMetadataDocument.OperationsMetadata;
import net.opengis.ows.x11.ValueType;
import net.opengis.sos.x10.CapabilitiesDocument;
import net.opengis.sos.x10.CapabilitiesDocument.Capabilities;

import org.apache.xmlbeans.XmlObject;
import org.n52.sir.SirConfigurator;
import org.n52.sir.SirConstants;
import org.n52.sir.client.Client;
import org.n52.sir.datastructure.SirSensor;
import org.n52.sir.ds.IHarvestServiceDAO;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.request.SirHarvestServiceRequest;
import org.n52.sir.response.ISirResponse;
import org.n52.sir.response.SirHarvestServiceResponse;
import org.n52.sir.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class SOSServiceHarvester extends Harvester {

    private static final long HARVEST_SLEEP_MILLIS = 50;

    private static final Logger log = LoggerFactory.getLogger(SOSServiceHarvester.class);

    private static final String OUTPUT_FORMAT_PARAMETER_NAME = "outputFormat";

    private static final String PROCEDURE_PARAMETER_NAME = "procedure";

    private SirHarvestServiceRequest request;

    @Inject
    private Client client;

    /**
     * 
     * @param request
     * @param harvServDao
     * @throws OwsExceptionReport
     */
    public SOSServiceHarvester(SirHarvestServiceRequest request, IHarvestServiceDAO harvServDao) throws OwsExceptionReport {
        super(harvServDao);
        this.request = request;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.Callable#call()
     */
    @Override
    public ISirResponse call() throws Exception {
        SirHarvestServiceResponse response = new SirHarvestServiceResponse();
        // set service type in response
        response.setServiceType(this.request.getServiceType());
        // set service URL in response
        response.setServiceUrl(this.request.getServiceUrl());

        URI uri = Tools.url2Uri(this.request);

        // request capabilities
        XmlObject caps = this.client.requestCapabilities(this.request.getServiceType(), uri);
        Capabilities sosCaps;
        if (caps instanceof CapabilitiesDocument) {
            CapabilitiesDocument doc = (CapabilitiesDocument) caps;
            sosCaps = doc.getCapabilities();
        }
        else {
            log.error("No SOS capabilities document returned by service! Instead got:\n" + caps.xmlText());
            OwsExceptionReport e = new OwsExceptionReport(OwsExceptionReport.ExceptionCode.OperationNotSupported,
                                                          "root",
                                                          "No SOS capabilities document returned by service! "
                                                                  + caps.xmlText());
            throw e;
        }

        // add service to database
        String serviceID = this.harvServDao.addService(this.request.getServiceUrl(),
                                                       this.request.getServiceType().toUpperCase());
        log.info("Added service to database with SERVICEID: " + serviceID);

        // change update sequence
        SirConfigurator.getInstance().newUpdateSequence();

        // search for possible sensors in getCapabilities response
        // search the describeSensor operation
        OperationsMetadata opData = sosCaps.getOperationsMetadata();
        Operation[] operations = opData.getOperationArray();
        Operation describeSensorOp = null;
        for (Operation operation : operations) {
            if (operation.getName().equals(SirConstants.DESCRIBE_SENSOR_OPERATION_NAME)) {
                describeSensorOp = operation;
                break;
            }
        }

        if (describeSensorOp == null) {
            log.error("No DescribeSensor Operation found in Capabilities!");
            OwsExceptionReport e = new OwsExceptionReport();
            e.addCodedException(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                null,
                                "No DescribeSensor Operation found!");
            throw e;
        }

        // set up parameters for describe sensor request
        String version = sosCaps.getVersion();
        String service = SirConstants.SOS_SERVICE_TYPE;

        // search the parameters of the describe sensor operation
        DomainType[] parameters = describeSensorOp.getParameterArray();
        DomainType procedure = null;
        DomainType outputFormat = null;
        for (DomainType parameter : parameters) {
            if (parameter.getName().equals(OUTPUT_FORMAT_PARAMETER_NAME)) {
                outputFormat = parameter;
            }
            if (parameter.getName().equals(PROCEDURE_PARAMETER_NAME)) {
                procedure = parameter;
            }
        }

        if (procedure == null || version == null || service == null || outputFormat == null) {
            log.error("DescribeSensor parameters not sufficiently defined! Required: version, service, outputFormat, procedure.");
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                 null,
                                 "DescribeSensor parameters not sufficiently defined! Required: version, service, outputFormat, procedure.");
            throw se;
        }

        // procedure
        ValueType[] procedureTypes = procedure.getAllowedValues().getValueArray();
        ValueType outputFormatType = outputFormat.getAllowedValues().getValueArray(0);

        // check every found sensor (procedure)
        for (ValueType procedureType : procedureTypes) {
            processProcedure(this.request,
                             response,
                             this.insertedSensors,
                             this.updatedSensors,
                             this.failedSensors,
                             uri,
                             version,
                             service,
                             outputFormatType,
                             procedureType);

            if (log.isDebugEnabled())
                log.debug("Taking a " + HARVEST_SLEEP_MILLIS + " millis break from harvesting.");
            Thread.sleep(HARVEST_SLEEP_MILLIS);
        }

        response.setUpdatedSensors(this.updatedSensors);
        response.setNumberOfUpdatedSensors(this.updatedSensors.size());
        response.setDeletedSensors(new ArrayList<SirSensor>());
        response.setFailedSensors(this.failedSensors.keySet());
        for (Entry<String, String> failedSensor : this.failedSensors.entrySet()) {
            response.addFailureDescription(failedSensor.getKey(), failedSensor.getValue());
        }
        response.setNumberOfFailedSensors(this.failedSensors.size());
        response.setInsertedSensors(this.insertedSensors);
        response.setNumberOfInsertedSensors(this.insertedSensors.size());

        return response;
    }

}