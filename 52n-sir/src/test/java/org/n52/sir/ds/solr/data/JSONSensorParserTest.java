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
package org.n52.sir.ds.solr.data;
/**
 * @author Yakoub
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.n52.sir.datastructure.SirSensor;
import org.n52.sir.datastructure.SirTimePeriod;
import org.n52.sir.ds.solr.SOLRInsertSensorInfoDAO;
import org.n52.sir.ds.solr.SolrConnection;
import org.n52.sir.ds.solr.data.ds.JSONSensor;
import org.n52.sir.ds.solr.data.ds.JSONSensorsCollection;
import org.n52.sir.ows.OwsExceptionReport;

import com.google.gson.Gson;

public class JSONSensorParserTest {
	
	@Test
	public void parseJsonSensorsAndInsert() throws IOException, OwsExceptionReport {
		/*
		File sensor_file = new File(ClassLoader.getSystemResource(
				"data/randomSensors.json").getFile());
		Gson gson = new Gson();
		StringBuilder builder = new StringBuilder();
		String s;
		BufferedReader reader = new BufferedReader(
				(new FileReader(sensor_file)));
		while ((s = reader.readLine()) != null)
			builder.append(s);
		JSONSensorsCollection collection = gson
				.fromJson(builder.toString(), JSONSensorsCollection.class);
		Iterator<JSONSensor> sensors = collection.sensors.iterator();
		while(sensors.hasNext()){
			SirSensor sensor = new SirSensor();
			JSONSensor jsensor = sensors.next();
			sensor.setKeywords(jsensor.keywords);
			SirTimePeriod period = new SirTimePeriod();
			DateTime begin = DateTime.parse(jsensor.beginPosition);
			DateTime end = DateTime.parse(jsensor.endPosition);
			//fix because we need start < end , and the data is randomly generated
			if(begin.getMillis() > end.getMillis()){
				DateTime temp = begin;
				end = begin;
				begin=temp;
			}
			period.setStartTime(begin.toDate());
			period.setEndTime(end.toDate());
			sensor.setTimePeriod(period);
			sensor.setIdentificationsList(jsensor.Identifiers);
			List<String> contacts = new ArrayList<String>();
			contacts.add(jsensor.contacts);
			sensor.setContacts(contacts);
			SOLRInsertSensorInfoDAO dao = new SOLRInsertSensorInfoDAO();
			dao.insertSensor(sensor);
		}
	*/
	}
	
}
