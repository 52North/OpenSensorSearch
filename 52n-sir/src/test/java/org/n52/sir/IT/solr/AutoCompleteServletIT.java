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
package org.n52.sir.IT.solr;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.xmlbeans.XmlException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.n52.sir.ds.solr.SOLRInsertSensorInfoDAO;
import org.n52.sir.ds.solr.SolrConnection;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.sml.SensorMLDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yakoub
 */
public class AutoCompleteServletIT {
	private static Logger log = LoggerFactory
			.getLogger(AutoCompleteServletIT.class);

	@Before
	public  void insertSensor() throws OwsExceptionReport, XmlException, IOException {
		File sensor_status = new File(ClassLoader.getSystemResource(
				"Requests/testsensor.xml").getFile());
		SensorMLDocument doc = SensorMLDocument.Factory.parse(sensor_status);
		
		SOLRInsertSensorInfoDAO dao = new SOLRInsertSensorInfoDAO();
		String insertSensor = dao.insertSensor(SensorMLDecoder.decode(doc));
		log.debug("inserted test sensor: {}", insertSensor);
	}
	
	@Test
	public  void testServlet() throws ClientProtocolException, IOException {
		org.apache.http.client.HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet("http://localhost:8080/OpenSensorSearch/autocomplete?text=te");
		
		HttpResponse response = client.execute(get);
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String s ;
		while((s=reader.readLine())!=null)
			builder.append(s);
		
		log.debug(builder.toString());
		
	}
	@After
	public  void deleteTestSensor() throws SolrServerException, IOException{
		new  SolrConnection().deleteByQuery("");
	}
}
