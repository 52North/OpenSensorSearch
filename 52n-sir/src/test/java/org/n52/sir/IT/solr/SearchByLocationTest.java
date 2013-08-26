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
 * @author Yakoub
 */

package org.n52.sir.IT.solr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.xmlbeans.XmlException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.datastructure.SirSensor;
import org.n52.sir.datastructure.detailed.SirDetailedSensorDescription;
import org.n52.sir.ds.solr.SOLRInsertSensorInfoDAO;
import org.n52.sir.ds.solr.SOLRSearchSensorDAO;
import org.n52.sir.ds.solr.SolrConnection;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.sml.SensorMLDecoder;

public class SearchByLocationTest {

    @Before
    public void insertSensor() throws XmlException, IOException, OwsExceptionReport {
        /*
         * Insert testSensor for search
         */
    	String basePath = (this.getClass().getResource("/Requests").getFile());
		File sensor_file = new File(basePath+"/testSensor.xml");
		
        SensorMLDocument doc = SensorMLDocument.Factory.parse(sensor_file);
        SirSensor sensor = SensorMLDecoder.decode(doc);

        /*
         * Inserts this sensor
         */
        // probably this will take some configuration - haven't decided yet.
        SOLRInsertSensorInfoDAO dao = new SOLRInsertSensorInfoDAO();
        dao.insertSensor(sensor);
    }

    @Test
    public void searchByLocation() {
        //
        SOLRSearchSensorDAO searchDAO = new SOLRSearchSensorDAO();
        /*
         * Prepare the list of keywords
         */
        String lat = "1.5";
        String lng = "3.49";
        Collection<SirSearchResultElement> results = searchDAO.searchSensorByLocation(lat, lng, 10);

        assertNotNull(results);
        assertEquals(results.size(), 1);

        Iterator<SirSearchResultElement> iter = results.iterator();
        SirSearchResultElement result = iter.next();
        // SensorML is stored in the sensor description value
        SirDetailedSensorDescription description = (SirDetailedSensorDescription) result.getSensorDescription();
        assertNotNull(description);
    //    assertTrue("urn:ogc:object:feature:testsensor".equals(description.getId()));
    }

    /*
     * Searches for a sensor but not in the range covered , should return 0
     */
    @Test
    public void searchByLocationNotInRange() {
        //
        SOLRSearchSensorDAO searchDAO = new SOLRSearchSensorDAO();
        /*
         * Prepare the list of keywords
         */
        String lat = "1";
        String lng = "3";
        Collection<SirSearchResultElement> results = searchDAO.searchSensorByLocation(lat, lng, 10);

        assertNotNull(results);
        assertEquals(results.size(), 0);

    }
    /**TODO LET the delete delete only by the given id not all  
     * 
     */
    
    @After
    public void deleteSensor() throws SolrServerException, IOException{
        new SolrConnection().deleteByQuery("");
        
    }
}
