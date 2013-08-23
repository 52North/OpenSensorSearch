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
import java.util.List;
import java.util.Map.Entry;

import net.opengis.sps.x10.CapabilitiesDocument;
import net.opengis.sps.x10.SensorOfferingType;

import org.apache.xmlbeans.XmlObject;
import org.n52.sir.SirConfigurator;
import org.n52.sir.client.Client;
import org.n52.sir.datastructure.SirSensor;
import org.n52.sir.ds.IHarvestServiceDAO;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.request.SirHarvestServiceRequest;
import org.n52.sir.response.ISirResponse;
import org.n52.sir.response.SirHarvestServiceResponse;
import org.n52.sir.util.Pair;
import org.n52.sir.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class SPSServiceHarvester extends Harvester {

    private static Logger log = LoggerFactory.getLogger(SPSServiceHarvester.class);

    private SirHarvestServiceRequest request;

    @Inject
    private Client client;

    /**
     * 
     * @param request
     * @param harvServDao
     * @throws OwsExceptionReport
     */
    public SPSServiceHarvester(SirHarvestServiceRequest request, IHarvestServiceDAO harvServDao) throws OwsExceptionReport {
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
        // request capabilities
        URI uri = Tools.url2Uri(this.request);
        XmlObject caps = this.client.requestCapabilities(this.request.getServiceType(), uri);

        CapabilitiesDocument.Capabilities spsCaps;
        if (caps instanceof CapabilitiesDocument) {
            net.opengis.sps.x10.CapabilitiesDocument doc = (net.opengis.sps.x10.CapabilitiesDocument) caps;
            spsCaps = doc.getCapabilities();
        }
        else {
            log.error("No capabilities document returned by service! Instead got:\n" + caps.xmlText());
            OwsExceptionReport e = new OwsExceptionReport();
            e.addCodedException(OwsExceptionReport.ExceptionCode.NoApplicableCode,
                                null,
                                "No capabilities document returned by service!");
            throw e;
        }

        // add service to database
        String serviceID = this.harvServDao.addService(this.request.getServiceUrl(),
                                                       this.request.getServiceType().toUpperCase());
        log.info("Added service to database with SERVICEID: " + serviceID);

        // change update sequence
        SirConfigurator.getInstance().newUpdateSequence();

        // search for possible sensors in getCapabilities response
        List<Pair<String, URI>> sensorDefinitions = new ArrayList<>();
        SensorOfferingType[] sensorOfferings = spsCaps.getContents().getSensorOfferingList().getSensorOfferingArray();

        for (SensorOfferingType currentOffering : sensorOfferings) {
            // try to create uri from it
            URI tempUri = URI.create(currentOffering.getSensorDefinition());
            String tempID = currentOffering.getSensorID();
            sensorDefinitions.add(new Pair<>(tempID, tempUri));
            if (log.isDebugEnabled())
                log.debug("Found sensor with ID " + tempID + " and description " + tempUri.toString());
        }

        SirHarvestServiceResponse response = new SirHarvestServiceResponse();
        response.setServiceType(this.request.getServiceType());
        response.setServiceUrl(this.request.getServiceUrl());

        // request and process sensor descriptions
        for (Pair<String, URI> current : sensorDefinitions) {
            processURI(this.request,
                       this.insertedSensors,
                       this.failedSensors,
                       this.updatedSensors,
                       current.getFirst(),
                       current.getSecond());
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
