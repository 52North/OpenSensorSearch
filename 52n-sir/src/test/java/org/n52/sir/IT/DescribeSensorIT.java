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

import static org.hamcrest.Matchers.equalTo;
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
import org.custommonkey.xmlunit.XMLAssert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.oss.util.GuiceUtil;
import org.n52.sir.client.Client;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.util.XmlTools;
import org.x52North.sir.x032.DeleteSensorInfoRequestDocument;
import org.x52North.sir.x032.DeleteSensorInfoResponseDocument;
import org.x52North.sir.x032.DescribeSensorRequestDocument;
import org.x52North.sir.x032.InsertSensorInfoRequestDocument;
import org.x52North.sir.x032.InsertSensorInfoResponseDocument;
import org.xml.sax.SAXException;

public class DescribeSensorIT {

    private static Client client;

    private static AbstractProcessType expected;

    private static String sensorID;

    @BeforeClass
    public static void setup() throws OwsExceptionReport, XmlException, IOException, HttpException {
        client = GuiceUtil.configureSirClient();

        File f = new File(ClassLoader.getSystemResource("Requests/InsertSensorInfo_newSensor.xml").getFile());

        InsertSensorInfoRequestDocument doc = InsertSensorInfoRequestDocument.Factory.parse(f);
        XmlObject response = client.xSendPostRequest(doc);
        InsertSensorInfoResponseDocument isird = InsertSensorInfoResponseDocument.Factory.parse(response.xmlText());
        sensorID = isird.getInsertSensorInfoResponse().getInsertedSensors().getSensorIDInSIRArray(0);

        expected = doc.getInsertSensorInfoRequest().getInfoToBeInsertedArray()[0].getSensorDescription();
    }

    @AfterClass
    public static void deleteInsertedSensor() throws Exception {
        DeleteSensorInfoRequestDocument requestDocument = DeleteSensorInfoRequestDocument.Factory.newInstance();
        requestDocument.addNewDeleteSensorInfoRequest().addNewInfoToBeDeleted().addNewSensorIdentification().setSensorIDInSIR(sensorID);

        XmlObject response = client.xSendPostRequest(requestDocument);
        DeleteSensorInfoResponseDocument responseDoc = DeleteSensorInfoResponseDocument.Factory.parse(response.xmlText());

        boolean isValid = response.validate();
        assertTrue("valid response", isValid);

        assertThat("one sensor was deleted.",
                   responseDoc.getDeleteSensorInfoResponse().getNumberOfDeletedSensors(),
                   is(equalTo(1)));
        assertThat("correct sensor was deleted.",
                   responseDoc.getDeleteSensorInfoResponse().getDeletedSensors().getSensorIDInSIRArray(0),
                   is(equalTo(sensorID)));
    }

    @Test
    public void describeSensorWithTestFile() throws Exception {
        DescribeSensorRequestDocument doc = DescribeSensorRequestDocument.Factory.parse(new File(ClassLoader.getSystemResource("Requests/sir/DescribeSensor.xml").getFile()));
        doc.getDescribeSensorRequest().setSensorIDInSIR(sensorID);

        XmlObject response = client.xSendPostRequest(doc);
        SensorMLDocument actual = SensorMLDocument.Factory.parse(response.getDomNode());
        checkSensor(actual);
    }

    @Test
    public void getDescribeSensor() throws Exception {
        XmlObject response = client.xSendGetRequest("request=DescribeSensor&service=SIR&sensorIDinSIR=" + this.sensorID);

        SensorMLDocument actual = SensorMLDocument.Factory.parse(response.getDomNode());
        checkSensor(actual);
    }

    @Test
    public void postDescribeSensor() throws Exception {
        DescribeSensorRequestDocument doc = DescribeSensorRequestDocument.Factory.newInstance();
        doc.addNewDescribeSensorRequest().setSensorIDInSIR(sensorID);

        XmlObject response = client.xSendPostRequest(doc);
        SensorMLDocument actual = SensorMLDocument.Factory.parse(response.getDomNode());
        checkSensor(actual);
    }

    private void checkSensor(SensorMLDocument actual) throws SAXException, IOException {
        assertThat("Valid sensorML returned.", actual.validate(), is(true));

        Diff diff = new Diff(actual.toString(), expected.xmlText());
        System.out.println(diff);
        XMLAssert.assertXMLEqual("documents similar", diff, true);

        // assertThat("XML is similar.", diff.similar(), is(true));
        // assertThat("XML is identical.", diff.identical(), is(true));
    }

    /**
     * 
     * Something is wrong with the validation of SensorML regarding the version numbers...
     * 
     * @param sml
     * @param isValid
     */
    private void weakValidate(SensorMLDocument sml, boolean isValid) {
        if ( !isValid) {
            System.out.println("Response document is invalid, but test probably passed...\n"
                    + XmlTools.validateAndIterateErrors(sml));
        }
        else {
            assertTrue(isValid);
        }
    }
}
