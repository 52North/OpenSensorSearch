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

import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.net.MalformedURLException;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.xmlbeans.XmlObject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.sir.Util;
import org.n52.sir.client.Client;
import org.x52North.sir.x032.InsertSensorInfoRequestDocument;
import org.x52North.sir.x032.InsertSensorInfoResponseDocument;

public class InsertSensorInfoNewSensorIT {

    private static Client c = null;
    
    @BeforeClass
    public static void setUpClient() throws MalformedURLException {
        c  = new Client(Util.getServiceURIforIT());
    }

	@Test
	public void insertSampleSensor() throws Exception {

		/*
		 * Create a sensor insert request from sensorFile
		 */
		File sensor = new File(ClassLoader.getSystemResource(
				"Requests/testSensor.xml").getFile());
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
				resp.getInsertSensorInfoResponse().getNumberOfInsertedSensors() , 0);

		System.out
				.println("Loaded a sensor , encoded and inserted successfully");
		
		/*
		 * TODO delete the sensor here
		 */

	}

	@Test
	public void insertSensorDirectly() throws Exception {
		File f = new File(ClassLoader.getSystemResource(
				"Requests/InsertSensorInfo_newSensor.xml").getFile());

		InsertSensorInfoRequestDocument doc = InsertSensorInfoRequestDocument.Factory
				.parse(f);

		XmlObject response = c.xSendPostRequest(doc);

		InsertSensorInfoResponseDocument responseDoc = InsertSensorInfoResponseDocument.Factory
				.parse(response.getDomNode());
		// Check the number of inserted sensor
		int number = (responseDoc.getInsertSensorInfoResponse()
				.getNumberOfInsertedSensors());
		assertNotEquals("Failed to insert sensor", number, 0);
	}

}
