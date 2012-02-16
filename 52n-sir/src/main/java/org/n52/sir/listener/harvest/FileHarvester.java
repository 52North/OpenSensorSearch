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

import java.net.URL;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.n52.sir.SirConfigurator;
import org.n52.sir.datastructure.SirSensor;
import org.n52.sir.ds.IHarvestServiceDAO;
import org.n52.sir.ows.OwsExceptionReport;
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