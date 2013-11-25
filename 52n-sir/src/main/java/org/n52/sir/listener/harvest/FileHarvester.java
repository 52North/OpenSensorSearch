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

import org.n52.oss.sir.api.SirSensor;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.SirConfigurator;
import org.n52.sir.ds.IHarvestServiceDAO;
import org.n52.sir.request.SirHarvestServiceRequest;
import org.n52.sir.response.ISirResponse;
import org.n52.sir.response.SirHarvestServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * 
 * Works for http://p5.neracoos.org/IOOSCatalog/sos_platforms.xml
 * 
 * @author Daniel Nüst
 * 
 */
public abstract class FileHarvester extends Harvester {

    private static final Logger log = LoggerFactory.getLogger(FileHarvester.class);

    protected SirHarvestServiceRequest request;

    /**
     * 
     * @param request
     * @param harvServDao
     * @throws OwsExceptionReport
     */
    public FileHarvester(SirHarvestServiceRequest request, IHarvestServiceDAO harvServDao) throws OwsExceptionReport {
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
        SirConfigurator.getInstance().newUpdateSequence();

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

    /**
     * 
     * @return
     */
    protected abstract ContentHandler getHandler();

}