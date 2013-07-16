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

package org.n52.sir.IT;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Before;
import org.junit.Test;
import org.n52.sir.client.Client;
import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.json.MapperFactory;
import org.n52.sir.json.SearchResult;
import org.n52.sir.json.SearchResultElement;
import org.n52.sir.ows.OwsExceptionReport;
import org.x52North.sir.x032.InsertSensorInfoRequestDocument;
import org.x52North.sir.x032.InsertSensorInfoResponseDocument;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.co.datumedge.hamcrest.json.SameJSONAs;

/*
 * TODO Add all OpenSourceOutputFormats IT to a single OpenSourceIT file
 */

public class OpenSearchIT {

	public void insertSensor(String sensorDest) throws XmlException,
			IOException, OwsExceptionReport, HttpException {
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
		XmlObject res = Client.xSendPostRequest(req);

		InsertSensorInfoResponseDocument resp = InsertSensorInfoResponseDocument.Factory
				.parse(res.getDomNode());

		assertNotEquals(
				"Failed to insert sensor",
				resp.getInsertSensorInfoResponse().getNumberOfInsertedSensors() == 0);

		System.out
				.println("Loaded a sensor , encoded and inserted successfully");

	}

	@Before
	public void insertSensors() throws XmlException, IOException,
			OwsExceptionReport, HttpException {

		insertSensor("Requests/testsensor.xml");
		// insertSensor("Requests/Sensors/testSensor02.xml");

	}

	public String buildQuery(String q, String format) {
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

	private String sendRequest(String query) throws ClientProtocolException,
			IOException {
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

	private String readResource(String s) throws IOException {
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
	public void testRSSResponseFromOpenSearch() throws IOException,
			SAXException {
		String realResult = readResource("Requests/Sensors/testSensor01Result.RSS");
		String responseResult = sendRequest(buildQuery(
				"urn:ogc:object:feature:Sensor:EEA:airbase:4.0:DEBB059", "rss"));

		assertXMLEqual(realResult, responseResult);
	}

	@Test
	public void testXMLResponseFromOpenSearch() throws IOException,
			SAXException {
		String realResult = readResource("Requests/Sensors/testSensor01Result.XML");
		String responseResult = sendRequest(buildQuery(
				"urn:ogc:object:feature:Sensor:EEA:airbase:4.0:DEBB059", "xml"));

		assertXMLEqual(realResult, responseResult);
	}

	@Test
	public void testJSONResponseFromOpenSearch() throws IOException,
			SAXException {
		String realResult = readResource("Requests/Sensors/jsonSensor.json");

		ObjectMapper mapper = MapperFactory.getMapper();

		SearchResult realResultObj = mapper.readValue(realResult,
				SearchResult.class);

		// Get the sensor
		Collection<SearchResultElement> sensors = realResultObj.getResults();

		SearchResultElement sensorJson = sensors.iterator().next();

		String sensorJsonStr = mapper.writeValueAsString(sensorJson);

		String responseResult = sendRequest(buildQuery("test", "json"));

		SearchResult result = mapper.readValue(responseResult,
				SearchResult.class);
		Collection<SearchResultElement> results = result.getResults();
		assertTrue(results.size() > 0);

		Iterator<SearchResultElement> it = results.iterator();

		while (it.hasNext()) {
			SearchResultElement elem = it.next();
			String resultsensor = mapper.writeValueAsString(elem);
			if (elem.getSensorIdInSir() == sensorJson.getSensorIdInSir())
				assertThat(resultsensor, SameJSONAs.sameJSONAs(sensorJsonStr)
						.allowingExtraUnexpectedFields()
						.allowingAnyArrayOrdering());

		}

	}

	@Test
	public void testKMLResponseFromOpenSearch() throws IOException,
			SAXException {
		String realResult = readResource("Requests/Sensors/testSensor01Result.kml");
		String responseResult = sendRequest(buildQuery(
				"urn:ogc:object:feature:Sensor:EEA:airbase:4.0:DEBB059", "kml"));
		assertXMLEqual(realResult, responseResult);
	}

}