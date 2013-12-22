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
import java.util.Set;

import net.opengis.sps.x10.CapabilitiesDocument;
import net.opengis.sps.x10.SensorOfferingType;

import org.apache.xmlbeans.XmlObject;
import org.n52.oss.sir.Client;
import org.n52.oss.sir.SirClient;
import org.n52.oss.sir.api.SirSensor;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.ds.IHarvestServiceDAO;
import org.n52.sir.ds.IInsertSensorInfoDAO;
import org.n52.sir.ds.ISearchSensorDAO;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.n52.sir.response.SirHarvestServiceResponse;
import org.n52.sir.util.Pair;
import org.n52.sir.xml.IProfileValidator;
import org.n52.sir.xml.IProfileValidator.ValidatableFormatAndProfile;
import org.n52.sir.xml.ValidatorModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class SPSServiceHarvester extends Harvester {

    private static Logger log = LoggerFactory.getLogger(SPSServiceHarvester.class);

    private SirClient client;

    @Inject
    public SPSServiceHarvester(IHarvestServiceDAO harvServDao,
                               IInsertSensorInfoDAO insertDao,
                               @Named(ISearchSensorDAO.FULL)
                               ISearchSensorDAO searchDao,
                               Client client,
                               Set<IProfileValidator> validators,
                               @Named("oss.sir.responses.validate")
                               boolean validateResponses) {
        super(harvServDao,
              insertDao,
              searchDao,
              client,
              ValidatorModule.getFirstMatchFor(validators, ValidatableFormatAndProfile.SML_DISCOVERY),
              validateResponses);

        log.info("NEW {}", this);
    }

    @Override
    public ISirResponse call() throws Exception {
        ISirResponse r = null;

        try {
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
            // SirConfigurator.getInstance().newUpdateSequence();

            // search for possible sensors in getCapabilities response
            List<Pair<String, URI>> sensorDefinitions = new ArrayList<>();
            SensorOfferingType[] sensorOfferings = spsCaps.getContents().getSensorOfferingList().getSensorOfferingArray();

            for (SensorOfferingType currentOffering : sensorOfferings) {
                // try to create uri from it
                URI tempUri = URI.create(currentOffering.getSensorDefinition());
                String tempID = currentOffering.getSensorID();
                sensorDefinitions.add(new Pair<>(tempID, tempUri));
                log.debug("Found sensor with ID {} and description {}", tempID, tempUri.toString());
            }

            SirHarvestServiceResponse response = new SirHarvestServiceResponse(this.validateResponses);
            response.setServiceType(this.request.getServiceType());
            response.setServiceUrl(this.request.getServiceUrl());

            // request and process sensor descriptions
            for (Pair<String, URI> current : sensorDefinitions) {
                processURI(this.insertedSensors,
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
            r = response;
        }
        catch (Exception e) {
            log.error("Error harvesting SPS at {}", this.request.getServiceUrl(), e);
            r = new ExceptionResponse(e);
        }

        return r;
    }
}
