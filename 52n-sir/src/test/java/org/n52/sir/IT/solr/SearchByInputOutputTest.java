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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

public class SearchByInputOutputTest {
	
	@Before
	public void insertSensor() throws XmlException, IOException,
			OwsExceptionReport {
		String basePath = (this.getClass().getResource("/Requests").getFile());
		File sensor_file = new File(basePath+"/testSensor.xml");
		SensorMLDocument doc = SensorMLDocument.Factory.parse(sensor_file);
		SirSensor sensor = SensorMLDecoder.decode(doc);
		SOLRInsertSensorInfoDAO dao = new SOLRInsertSensorInfoDAO();
		dao.insertSensor(sensor);
	}

	@Test
	public void searchByInput() {
		SOLRSearchSensorDAO searchDAO = new SOLRSearchSensorDAO();
		String input = "precipitation";
		Collection<SirSearchResultElement> results = searchDAO
				.searchByInput(input);

		assertNotNull(results);
		Iterator<SirSearchResultElement> iter = results.iterator();
		ArrayList<String> resultsInputs = new ArrayList<>();
		while (iter.hasNext()) {
			SirSearchResultElement element = iter.next();
			resultsInputs.addAll(((SirDetailedSensorDescription) element
					.getSensorDescription()).getInputs());
		}
		if (resultsInputs.size() > 0){
			Iterator<String> it = resultsInputs.iterator();
			int index=-1;
			while(it.hasNext())
				if(it.next().indexOf(input)>=0){
					index=0;
					break;
				}
			
			assertFalse(index == -1);
		}

	}
	@Test
	public void searchByOutput() {
		SOLRSearchSensorDAO searchDAO = new SOLRSearchSensorDAO();
		String input = "precipitation";
		Collection<SirSearchResultElement> results = searchDAO
				.searchByOutput(input);

		assertNotNull(results);
		Iterator<SirSearchResultElement> iter = results.iterator();
		ArrayList<String> resultsOutputs = new ArrayList<>();
		while (iter.hasNext()) {
			SirSearchResultElement element = iter.next();
			resultsOutputs.addAll(((SirDetailedSensorDescription) element
					.getSensorDescription()).getOutputs());
		}
		if (resultsOutputs.size() > 0){
			Iterator<String> it = resultsOutputs.iterator();
			int index=-1;
			while(it.hasNext())
				if(it.next().indexOf(input)>=0){
					index=0;
					break;
				}
			
			assertFalse(index == -1);
		}

	}
	
	@After
	public void deleteSensor() throws SolrServerException, IOException {
		new SolrConnection().deleteByQuery("");
	}
	
}