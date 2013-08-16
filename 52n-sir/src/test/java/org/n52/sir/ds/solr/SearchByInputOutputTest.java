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
 * @author Yakoub
 */

package org.n52.sir.ds.solr;

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
		ArrayList<String> resultsInputs = new ArrayList<String>();
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
		ArrayList<String> resultsOutputs = new ArrayList<String>();
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