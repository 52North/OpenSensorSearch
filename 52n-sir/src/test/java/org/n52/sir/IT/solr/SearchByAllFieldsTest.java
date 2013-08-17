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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.xmlbeans.XmlException;
import org.junit.After;
import org.junit.Test;
import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.datastructure.SirSensor;
import org.n52.sir.datastructure.detailed.SirDetailedSensorDescription;
import org.n52.sir.ds.solr.SOLRInsertSensorInfoDAO;
import org.n52.sir.ds.solr.SOLRSearchSensorDAO;
import org.n52.sir.ds.solr.SolrConnection;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.sml.SensorMLDecoder;

public class SearchByAllFieldsTest {

	
	@Test
	public void searchByAllFields() throws XmlException, IOException, OwsExceptionReport {
		String basePath = (this.getClass().getResource("/Requests").getFile());
		File sensor_file = new File(basePath+"/testSensor.xml");
		SensorMLDocument doc = SensorMLDocument.Factory.parse(sensor_file);
		SirSensor sensor = SensorMLDecoder.decode(doc);
		SOLRInsertSensorInfoDAO dao = new SOLRInsertSensorInfoDAO();
		String id = dao.insertSensor(sensor);
		SOLRSearchSensorDAO searchDAO = new SOLRSearchSensorDAO();
		Collection<SirSearchResultElement> results = searchDAO
				.searchByAll("precipitation+keyword",null,null,null,null,null,null);
		Iterator<SirSearchResultElement> resultsIterator = results.iterator();
		boolean found = false;
		while(resultsIterator.hasNext()){
			SirDetailedSensorDescription description = (SirDetailedSensorDescription)resultsIterator.next().getSensorDescription();
			if(description.getId().equals(id)){
				found=true;
				break;
			}
		}
		assertTrue(found);
		
	}

	@After
	public void deleteSensor() throws SolrServerException, IOException {
		new SolrConnection().deleteByQuery("");
	}
	
}
