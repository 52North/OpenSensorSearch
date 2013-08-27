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
 * author Yakoub
 */

package org.n52.sir.IT;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import net.opengis.sensorML.x101.AbstractProcessType;
import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.http.HttpException;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.custommonkey.xmlunit.Diff;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.sir.client.Client;
import org.n52.sir.ows.OwsExceptionReport;
import org.x52North.sir.x032.DeleteSensorInfoRequestDocument;
import org.x52North.sir.x032.DescribeSensorRequestDocument;
import org.x52North.sir.x032.InsertSensorInfoRequestDocument;

public class DescribeSensorIT {

    private static Client client;
    private String sensorIDinSIR = "42";

    private AbstractProcessType expected;

    @BeforeClass
    public static void setUp() {
        client = GuiceUtil.configureSirClient();
    }

    @Before
    public void setup() throws OwsExceptionReport, XmlException, IOException, HttpException {
        File f = new File(ClassLoader.getSystemResource("Requests/InsertSensorInfo_newSensor.xml").getFile());

        InsertSensorInfoRequestDocument doc = InsertSensorInfoRequestDocument.Factory.parse(f);
        client.xSendPostRequest(doc);
        this.expected = doc.getInsertSensorInfoRequest().getInfoToBeInsertedArray()[0].getSensorDescription();
    }

    @Test
    public void describeSensorUsingDocument() throws Exception {

        DescribeSensorRequestDocument doc = DescribeSensorRequestDocument.Factory.newInstance();
        doc.addNewDescribeSensorRequest().setSensorIDInSIR(this.sensorIDinSIR);

        XmlObject response = null;

        response = client.xSendPostRequest(doc);
        System.out.println("response:" + response);
        // parse and validate response
        SensorMLDocument sml = SensorMLDocument.Factory.parse(response.getDomNode());
        boolean isValid = sml.validate();
        assertTrue("Not a valid sensorML returned", isValid);
    }

    @Test
    public void describeSensor() throws Exception {
        DescribeSensorRequestDocument doc = DescribeSensorRequestDocument.Factory.parse(new File(ClassLoader.getSystemResource("Requests/DescribeSensor.xml").getFile()));
        XmlObject response = null;

        response = client.xSendPostRequest(doc);
        // System.out.println("response:" + response);
        // parse and validate response
        SensorMLDocument actual = SensorMLDocument.Factory.parse(response.getDomNode());

        assertThat("Valid sensorML returned.", actual.validate(), is(true));

        Diff diff = new Diff(actual.toString(), this.expected.toString());
        assertThat("XML is similar.", diff.similar(), is(true));
        assertThat("XML is identical.", diff.identical(), is(true));
    }

    @After
    public void deleteInsertedSensor() throws Exception {
        // delete the added sensor to keep the system state unchanged
        DeleteSensorInfoRequestDocument doc = DeleteSensorInfoRequestDocument.Factory.parse(new File(ClassLoader.getSystemResource("Requests/DeleteSensorInfo.xml").getFile()));
        XmlObject response = client.xSendPostRequest(doc);
        boolean isValid = response.validate();
        // FIXME must check if the response actually reports that the sensor is deleted
        assertTrue("Warning:SensorId:42 has to be deleted", isValid);
    }
}
