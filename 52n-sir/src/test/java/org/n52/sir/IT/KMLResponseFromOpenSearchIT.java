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

import static org.junit.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotEquals;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.n52.sir.SirConstants;
import org.n52.sir.client.Client;
import org.n52.sir.ows.OwsExceptionReport;
import org.x52North.sir.x032.InsertSensorInfoRequestDocument;
import org.x52North.sir.x032.InsertSensorInfoResponseDocument;

import uk.co.datumedge.hamcrest.json.SameJSONAs;

public class KMLResponseFromOpenSearchIT {

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
		/*
		 * insertSensor("Requests/testSensor.xml");
		 * insertSensor("Requests/testSensor.xml");
		 * insertSensor("Requests/testSensor.xml");
		 */
	}

	public String buildQuery(String q) {
		/*
		 * I'm sure that the server will be localhost port : 8080 If it's not
		 * installed on this the mvn verify will through a BindingException :
		 * Address already in use.
		 */
		StringBuilder query = new StringBuilder();
		query.append("http://localhost:8080/" + SirConstants.SERVICE_NAME);
		query.append("/search?q=");
		query.append(q);
		query.append("&httpAccept=application/json");
		return query.toString();
	}

	@Test
	public void testJSONOutputOpenSearch() throws ClientProtocolException,
			IOException, JSONException {
		/*
		 * Here I'm keeping record of the keywords of each file to search for
		 * them
		 */

		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(buildQuery("berlin"));

		HttpResponse response = client.execute(get);
		/*
		 * TODO Prepare a list of three json sensors and a unique distinct
		 * keywords for each for reliable results
		 */
		File sensor = new File(ClassLoader.getSystemResource(
				"Requests/testSensor.kml").getFile());
		assertEquals(response.getStatusLine().getStatusCode(), 200);
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				response.getEntity().getContent()));
		String s = "";
		while ((s = reader.readLine()) != null)
			builder.append(s);

		String responseKML = builder.toString();

		builder = new StringBuilder();
		reader.close();
		reader = new BufferedReader(new FileReader(sensor));
		s = "";
		while ((s = reader.readLine()) != null)
			builder.append(s);

		String sensorKML = builder.toString();
		System.out.println(sensorKML);
		System.out.println(responseKML);

		/*
		 * Do a test using XMLUnit
		 */
	}

}