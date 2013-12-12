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

import java.net.URL;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Set;

import org.n52.oss.sir.Client;
import org.n52.oss.sir.api.SirSensor;
import org.n52.sir.ds.IHarvestServiceDAO;
import org.n52.sir.ds.IInsertSensorInfoDAO;
import org.n52.sir.ds.ISearchSensorDAO;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.n52.sir.response.SirHarvestServiceResponse;
import org.n52.sir.xml.IProfileValidator;
import org.n52.sir.xml.IProfileValidator.ValidatableFormatAndProfile;
import org.n52.sir.xml.ValidatorModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * 
 * Works for http://p5.neracoos.org/IOOSCatalog/sos_platforms.xml
 * 
 * @author Daniel Nüst
 * 
 */
public abstract class FileHarvester extends Harvester {

    private static final Logger log = LoggerFactory.getLogger(FileHarvester.class);

    @Inject
    public FileHarvester(IHarvestServiceDAO harvServDao, IInsertSensorInfoDAO insertDao, @Named(ISearchSensorDAO.FULL)
    ISearchSensorDAO searchDao, Client client, Set<IProfileValidator> validators) {
        super(harvServDao,
              insertDao,
              searchDao,
              client,
              ValidatorModule.getFirstMatchFor(validators, ValidatableFormatAndProfile.SML_DISCOVERY));

        log.info("NEW {}", this);
    }

    @Override
    public ISirResponse call() throws Exception {
        ISirResponse r = null;

        try {
            log.info("Starting Harvesting of File " + this.request.getServiceUrl());

            SirHarvestServiceResponse response = new SirHarvestServiceResponse();
            // set service type in response
            response.setServiceType(this.request.getServiceType());
            // set service URL in response
            response.setServiceUrl(this.request.getServiceUrl());

            // this.reader = XMLInputFactory.newFactory().createXMLEventReader(new
            // FileReader(catalogFile));

            URL fileURL = new URL(this.request.getServiceUrl());
            // this.catalogXml = XmlObject.Factory.parse(fileURL);

            // event based parsing:
            XMLReader myReader = XMLReaderFactory.createXMLReader();

            // get the handler from implementing classes
            ContentHandler handler = getHandler();

            myReader.setContentHandler(handler);
            myReader.parse(new InputSource(fileURL.openStream()));

            // TODO add harvested catalog to database (with harvesting intervall) >> harvesting history in DB

            // change update sequence
            // SirConfigurator.getInstance().newUpdateSequence();

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
            log.error("Error harvesting file at {}", this.request.getServiceUrl(), e);
            r = new ExceptionResponse(e);
        }

        return r;
    }

    /**
     * 
     * @return
     */
    protected abstract ContentHandler getHandler();

}