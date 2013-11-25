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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.http.HttpException;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.util.GuiceUtil;
import org.x52North.sir.x032.DeleteSensorInfoRequestDocument;
import org.x52North.sir.x032.DeleteSensorInfoResponseDocument;
import org.x52North.sir.x032.InsertSensorInfoRequestDocument;
import org.x52North.sir.x032.InsertSensorInfoResponseDocument;

public class InsertSensorInfoNewSensorIT {

    private static Client client;

    @BeforeClass
    public static void setUp() {
        client = GuiceUtil.configureSirClient();
    }

    private String sensorId;

    @Test
    public void insertAirBaseSensor() throws XmlException, IOException, OwsExceptionReport, HttpException {
        File sensor = new File(ClassLoader.getSystemResource("Requests/sir/testSensor-AirBase.xml").getFile());
        SensorMLDocument DOC = SensorMLDocument.Factory.parse(sensor);
        InsertSensorInfoRequestDocument req = InsertSensorInfoRequestDocument.Factory.newInstance();
        req.addNewInsertSensorInfoRequest().addNewInfoToBeInserted().setSensorDescription(DOC.getSensorML().getMemberArray(0).getProcess());
        XmlObject res = client.xSendPostRequest(req);
        InsertSensorInfoResponseDocument resp = InsertSensorInfoResponseDocument.Factory.parse(res.getDomNode());

        assertThat("one sensor was inserted",
                   resp.getInsertSensorInfoResponse().getNumberOfInsertedSensors(),
                   is(equalTo(1)));
        assertThat("no service reference was inserted",
                   resp.getInsertSensorInfoResponse().getNumberOfInsertedServiceReferences(),
                   is(equalTo(0)));
        this.sensorId = resp.getInsertSensorInfoResponse().getInsertedSensors().getSensorIDInSIRArray(0);
    }

    @Test
    public void insertTestSensorFromFile1() throws XmlException, IOException, OwsExceptionReport, HttpException {
        File f = new File(ClassLoader.getSystemResource("Requests/sir/testSensor-1.xml").getFile());
        SensorMLDocument doc = SensorMLDocument.Factory.parse(f);
        InsertSensorInfoRequestDocument req = InsertSensorInfoRequestDocument.Factory.newInstance();
        req.addNewInsertSensorInfoRequest().addNewInfoToBeInserted().setSensorDescription(doc.getSensorML().getMemberArray()[0].getProcess());
        XmlObject res = client.xSendPostRequest(req);
        InsertSensorInfoResponseDocument resp = InsertSensorInfoResponseDocument.Factory.parse(res.getDomNode());

        assertThat("one sensor was inserted",
                   resp.getInsertSensorInfoResponse().getNumberOfInsertedSensors(),
                   is(equalTo(1)));
        assertThat("no service reference was inserted",
                   resp.getInsertSensorInfoResponse().getNumberOfInsertedServiceReferences(),
                   is(equalTo(0)));
        this.sensorId = resp.getInsertSensorInfoResponse().getInsertedSensors().getSensorIDInSIRArray(0);
    }

    @Test
    public void insertTestSensorFromFile() throws XmlException, IOException, OwsExceptionReport, HttpException {
        File sensor = new File(ClassLoader.getSystemResource("Requests/sir/testSensor.xml").getFile());
        SensorMLDocument DOC = SensorMLDocument.Factory.parse(sensor);

        InsertSensorInfoRequestDocument req = InsertSensorInfoRequestDocument.Factory.newInstance();
        req.addNewInsertSensorInfoRequest().addNewInfoToBeInserted().setSensorDescription(DOC.getSensorML().getMemberArray(0).getProcess());
        XmlObject res = client.xSendPostRequest(req);
        InsertSensorInfoResponseDocument resp = InsertSensorInfoResponseDocument.Factory.parse(res.getDomNode());

        assertThat("one sensor was inserted",
                   resp.getInsertSensorInfoResponse().getNumberOfInsertedSensors(),
                   is(equalTo(1)));
        assertThat("no service reference was inserted",
                   resp.getInsertSensorInfoResponse().getNumberOfInsertedServiceReferences(),
                   is(equalTo(0)));
        this.sensorId = resp.getInsertSensorInfoResponse().getInsertedSensors().getSensorIDInSIRArray(0);
    }

    @After
    public void cleanUp() throws IOException, OwsExceptionReport, HttpException, XmlException {
        DeleteSensorInfoRequestDocument req = DeleteSensorInfoRequestDocument.Factory.newInstance();
        req.addNewDeleteSensorInfoRequest().addNewInfoToBeDeleted().addNewSensorIdentification().setSensorIDInSIR(this.sensorId);

        XmlObject response = client.xSendPostRequest(req);
        DeleteSensorInfoResponseDocument responseDoc = DeleteSensorInfoResponseDocument.Factory.parse(response.getDomNode());
        assertTrue(responseDoc.validate());
    }

}
