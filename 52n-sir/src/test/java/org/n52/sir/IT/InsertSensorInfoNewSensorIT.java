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
import java.io.IOException;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.http.HttpException;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.sir.client.Client;
import org.n52.sir.ows.OwsExceptionReport;
import org.x52North.sir.x032.InsertSensorInfoRequestDocument;
import org.x52North.sir.x032.InsertSensorInfoResponseDocument;

public class InsertSensorInfoNewSensorIT {

    private static Client client;

    @BeforeClass
    public static void setUp() {
        client = GuiceUtil.configureSirClient();
    }

    @Test
    public void insertNewSensor() throws XmlException, IOException, OwsExceptionReport, HttpException {
        File sensor = new File(ClassLoader.getSystemResource("AirBase-test.xml").getFile());
        SensorMLDocument DOC = SensorMLDocument.Factory.parse(sensor);
        InsertSensorInfoRequestDocument req = InsertSensorInfoRequestDocument.Factory.newInstance();
        req.addNewInsertSensorInfoRequest().addNewInfoToBeInserted().setSensorDescription(DOC.getSensorML().getMemberArray(0).getProcess());
        XmlObject res = client.xSendPostRequest(req);
        InsertSensorInfoResponseDocument resp = InsertSensorInfoResponseDocument.Factory.parse(res.getDomNode());

        System.out.println(resp);
        assertNotEquals("Failed to insert sensor", resp.getInsertSensorInfoResponse().getNumberOfInsertedSensors(), 0);
    }

    @Test
    public void insertSensorDirectly() throws XmlException, IOException, OwsExceptionReport, HttpException {
        File f = new File(ClassLoader.getSystemResource("Requests/verifiedNewSensor.xml").getFile());
        SensorMLDocument DOC = SensorMLDocument.Factory.parse(f);
        InsertSensorInfoRequestDocument req = InsertSensorInfoRequestDocument.Factory.newInstance();
        req.addNewInsertSensorInfoRequest().addNewInfoToBeInserted().setSensorDescription(DOC.getSensorML().getMemberArray()[0].getProcess());
        XmlObject res = client.xSendPostRequest(req);
        InsertSensorInfoResponseDocument resp = InsertSensorInfoResponseDocument.Factory.parse(res.getDomNode());

        assertNotEquals("Failed to insert sensor", resp.getInsertSensorInfoResponse().getNumberOfInsertedSensors(), 0);
    }

    @Test
    public void insertSampleSensor() throws XmlException, IOException, OwsExceptionReport, HttpException {
        File sensor = new File(ClassLoader.getSystemResource("Requests/testSensor.xml").getFile());
        SensorMLDocument DOC = SensorMLDocument.Factory.parse(sensor);

        InsertSensorInfoRequestDocument req = InsertSensorInfoRequestDocument.Factory.newInstance();
        req.addNewInsertSensorInfoRequest().addNewInfoToBeInserted().setSensorDescription(DOC.getSensorML().getMemberArray(0).getProcess());
        XmlObject res = client.xSendPostRequest(req);
        InsertSensorInfoResponseDocument resp = InsertSensorInfoResponseDocument.Factory.parse(res.getDomNode());

        assertNotEquals("Failed to insert sensor", resp.getInsertSensorInfoResponse().getNumberOfInsertedSensors(), 0);
    }

    @After
    public void cleanUp() {
        /*
         * TODO delete the sensor here
         */
    }

}
