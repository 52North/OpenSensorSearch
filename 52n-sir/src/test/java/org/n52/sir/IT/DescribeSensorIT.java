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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.naming.OperationNotSupportedException;
import javax.servlet.UnavailableException;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.http.HttpException;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.sir.Util;
import org.n52.sir.client.Client;
import org.n52.sir.ows.OwsExceptionReport;
import org.x52North.sir.x032.DeleteSensorInfoRequestDocument;
import org.x52North.sir.x032.DescribeSensorRequestDocument;
import org.x52North.sir.x032.InsertSensorInfoRequestDocument;

public class DescribeSensorIT {

    private String sensorIDinSIR = "42";

    private static Client c = null;

    @BeforeClass
    public static void setUpClient() throws MalformedURLException {
        c = new Client(Util.getServiceURL());
    }

    @Before
    public void setup() throws UnavailableException,
            OwsExceptionReport,
            XmlException,
            IOException,
            HttpException,
            OperationNotSupportedException {

        /*
         * To make it self consistent I will add the sensor testsensor.xml before doing testing
         */
        File f = new File(ClassLoader.getSystemResource("Requests/InsertSensorInfo_newSensor.xml").getFile());

        InsertSensorInfoRequestDocument doc = InsertSensorInfoRequestDocument.Factory.parse(f);
        c.xSendPostRequest(doc);
        // The file has the sensor with Id:42
    }

    @Test
    public void describeSensorUsingDocument() throws Exception {

        DescribeSensorRequestDocument doc = DescribeSensorRequestDocument.Factory.newInstance();
        doc.addNewDescribeSensorRequest().setSensorIDInSIR(sensorIDinSIR);

        XmlObject response = null;

        response = c.xSendPostRequest(doc);
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

        response = c.xSendPostRequest(doc);
        System.out.println("response:" + response);
        // parse and validate response
        SensorMLDocument sml = SensorMLDocument.Factory.parse(response.getDomNode());
        boolean isValid = sml.validate();
        assertTrue("Not a valid sensorML returned", isValid);
    }

    @After
    public void deleteInsertedSensor() throws Exception {
        // delete the added sensor to keep the system state unchanged
        DeleteSensorInfoRequestDocument doc = DeleteSensorInfoRequestDocument.Factory.parse(new File(ClassLoader.getSystemResource("Requests/DeleteSensorInfo.xml").getFile()));
        XmlObject response = c.xSendPostRequest(doc);
        boolean isValid = response.validate();
        assertTrue("Warning:SensorId:42 has to be deleted", isValid);
    }
}
