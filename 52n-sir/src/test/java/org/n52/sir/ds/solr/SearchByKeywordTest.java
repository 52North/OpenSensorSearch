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

package org.n52.sir.ds.solr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.xmlbeans.XmlException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.n52.sir.datastructure.SirSearchCriteria;
import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.datastructure.SirSensor;
import org.n52.sir.datastructure.detailed.SirDetailedSensorDescription;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.sml.SensorMLDecoder;

public class SearchByKeywordTest {
	
	public String id;

    @Test
    public void searchKeywords() throws OwsExceptionReport, XmlException, IOException {
    	//Inserts the sensor
    	String basePath = (this.getClass().getResource("/Requests").getFile());
		File sensor_file = new File(basePath+"/testSensor.xml");
		SensorMLDocument doc = SensorMLDocument.Factory.parse(sensor_file);
        SirSensor sensor = SensorMLDecoder.decode(doc);
        // probably this will take some configuration - haven't decided yet.
        SOLRInsertSensorInfoDAO dao = new SOLRInsertSensorInfoDAO();
        String id = dao.insertSensor(sensor);
        this.id=id;
    	
        SOLRSearchSensorDAO searchDAO = new SOLRSearchSensorDAO();
        SirSearchCriteria criteria = new SirSearchCriteria();
        /*
         * Prepare the list of keywords
         */
        List<String> keywords = new ArrayList<String>();
        keywords.add("testkeyword");
        keywords.add("test");
        keywords.add("another keyword");

        ArrayList<String> searchkeywords = new ArrayList<String>();
        searchkeywords.add("test");
        criteria.setSearchText(searchkeywords);

        Collection<SirSearchResultElement> results = searchDAO.searchSensor(criteria, true);

        assertNotNull(results);
        assertEquals(results.size(), 1);

        Iterator<SirSearchResultElement> iter = results.iterator();
        SirSearchResultElement result = iter.next();
        // SensorML is stored in the sensor description value
        SirDetailedSensorDescription description = (SirDetailedSensorDescription) result.getSensorDescription();
        assertNotNull(description);
        assertEquals(description.getKeywords().size(), keywords.size());
        for (String s : keywords)
            assertTrue(description.getKeywords().contains(s));
        //deletes the sensor
        

    }
    /**TODO LET the delete delete only by the given id not all  
     *
     */
   
    @After
    public void deleteSensor() throws SolrServerException, IOException{
        new SolrConnection().deleteByQuery("");   
    } 
}