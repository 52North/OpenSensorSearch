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

package org.n52.sir.IT;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.xmlbeans.XmlObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.sir.Util;
import org.n52.sir.client.Client;
import org.x52North.sir.x032.InsertSensorInfoRequestDocument;
import org.x52North.sir.x032.InsertSensorInfoResponseDocument;
import org.xml.sax.SAXException;

import uk.co.datumedge.hamcrest.json.SameJSONAs;
/*
 * TODO Add all OpenSourceOutputFormats IT to a single OpenSourceIT file
 */

public class OpenSearchIT {
    
    private static Client c = null;
    
    @BeforeClass
    public static void setUpClient() throws MalformedURLException {
        c  = new Client(Util.getServiceURIforIT());
    }

	public void insertSensor(String sensorDest) throws Exception {
		/*
		 * Inserts a Sensor First
		 */
		File sensor = new File(ClassLoader.getSystemResource(sensorDest)
				.getFile());
		SensorMLDocument DOC = SensorMLDocument.Factory.parse(sensor);

		InsertSensorInfoRequestDocument req = InsertSensorInfoRequestDocument.Factory
				.newInstance();
		req.addNewInsertSensorInfoRequest()
				.addNewInfoToBeInserted()
				.setSensorDescription(
						DOC.getSensorML().getMemberArray(0).getProcess());
		XmlObject res = c.xSendPostRequest(req);

		InsertSensorInfoResponseDocument resp = InsertSensorInfoResponseDocument.Factory
				.parse(res.getDomNode());

		assertNotEquals(
				"Failed to insert sensor",
				resp.getInsertSensorInfoResponse().getNumberOfInsertedSensors() == 0);

		System.out
				.println("Loaded a sensor , encoded and inserted successfully");

	}

	@Before
	public void insertSensors() throws Exception {
		insertSensor("Requests/Sensors/testSensor01.xml");
		insertSensor("Requests/Sensors/testSensor02.xml");
	}
	public String buildQuery(String q,String format) {
		/*
		 * I'm sure that the server will be localhost port : 8080 If it's not
		 * installed on this the mvn verify will through a BindingException :
		 * Address already in use.
		 */
		StringBuilder query = new StringBuilder();
		query.append("http://localhost:8080/SIR");
		query.append("/search?q=");
		query.append(q);
		query.append("&httpAccept=application/");
		query.append("format");
		return query.toString();
	}
	
	private String sendRequest(String query) throws ClientProtocolException, IOException{
		HttpClient client = new DefaultHttpClient();
		/*
		 * I test using the unique ID of testSenosr01
		 */
		HttpGet get = new HttpGet(query);

		HttpResponse response = client.execute(get);
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				response.getEntity().getContent()));
		String s = "";
		while ((s = reader.readLine()) != null)
			builder.append(s);

		String responseString = builder.toString();
		reader.close();
		return responseString;
	}
	
	private String readResource(String s) throws IOException{
		File results = new File(ClassLoader.getSystemResource(
				"Requests/Sensors/testSensor01Result.RSS").getFile());
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(results));
		s = "";
		while ((s = reader.readLine()) != null)
			builder.append(s);
		String realResults = builder.toString();
		reader.close();
		return realResults;

	}
	
	@Test
	public void testRSSResponseFromOpenSearch() throws IOException, SAXException{
		String realResult = readResource("Requests/Sensors/testSensor01Result.RSS");
		String responseResult = sendRequest(buildQuery("urn:ogc:object:feature:Sensor:EEA:airbase:4.0:DEBB059", "rss"));
		
		assertXMLEqual(realResult,responseResult);
	}
	@Test
	public void testXMLResponseFromOpenSearch() throws IOException, SAXException{
		String realResult = readResource("Requests/Sensors/testSensor01Result.XML");
		String responseResult = sendRequest(buildQuery("urn:ogc:object:feature:Sensor:EEA:airbase:4.0:DEBB059", "xml"));
		
		assertXMLEqual(realResult,responseResult);
	}
	@Test
	public void testJSONResponseFromOpenSearch() throws IOException, SAXException{
		String realResult = readResource("Requests/Sensors/testSensor01Result.json");
		String responseResult = sendRequest(buildQuery("urn:ogc:object:feature:Sensor:EEA:airbase:4.0:DEBB059", "json"));
		assertThat(realResult, SameJSONAs.sameJSONAs(responseResult)
				.allowingExtraUnexpectedFields().allowingAnyArrayOrdering());
	}
	@Test
	public void testKMLResponseFromOpenSearch() throws IOException, SAXException{
		String realResult = readResource("Requests/Sensors/testSensor01Result.kml");
		String responseResult = sendRequest(buildQuery("urn:ogc:object:feature:Sensor:EEA:airbase:4.0:DEBB059", "kml"));
		assertXMLEqual(realResult,responseResult);
	}
	
	
	
	


}