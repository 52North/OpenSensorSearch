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

package org.n52.sir.IT;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import net.opengis.ows.ExceptionReportDocument;
import net.opengis.ows.impl.ExceptionReportDocumentImpl;
import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.http.HttpException;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.oss.util.GuiceUtil;
import org.n52.sir.client.Client;
import org.n52.sir.ows.OwsExceptionReport;
import org.x52North.sir.x032.DeleteSensorInfoRequestDocument;
import org.x52North.sir.x032.DeleteSensorInfoRequestDocument.DeleteSensorInfoRequest.InfoToBeDeleted;
import org.x52North.sir.x032.DeleteSensorInfoResponseDocument;
import org.x52North.sir.x032.InsertSensorInfoRequestDocument;
import org.x52North.sir.x032.InsertSensorInfoResponseDocument;

/**
 * 
 * @author Yakoub
 * 
 */
public class DeleteSensorInfoIT {

    private static Client client;
    private static String sensorID;
    private static File testSensorInfo;

    @BeforeClass
    public static void setUp() {
        client = GuiceUtil.configureSirClient();
        testSensorInfo = new File(ClassLoader.getSystemResource("Requests/sir/InsertSensorInfo_newSensor.xml").getFile());
    }

    @Before
    public void insertSensor() throws IOException, OwsExceptionReport, HttpException, XmlException {

        InsertSensorInfoRequestDocument doc = InsertSensorInfoRequestDocument.Factory.parse(testSensorInfo);
        XmlObject response = client.xSendPostRequest(doc);
        InsertSensorInfoResponseDocument isird = InsertSensorInfoResponseDocument.Factory.parse(response.xmlText());
        sensorID = isird.getInsertSensorInfoResponse().getInsertedSensors().getSensorIDInSIRArray(0);

        response = client.xSendGetRequest("request=DescribeSensor&service=SIR&sensorIDinSIR=" + sensorID);
        SensorMLDocument sensorDocument = SensorMLDocument.Factory.parse(response.xmlText());
        assertThat("test DescribeSensor-request returns valid document.", sensorDocument.validate(), is(equalTo(true)));
    }

    @After
    public void sensorMustBeGone() throws UnsupportedEncodingException,
            HttpException,
            IOException,
            OwsExceptionReport,
            XmlException {
        XmlObject response = client.xSendGetRequest("request=DescribeSensor&service=SIR&sensorIDinSIR=" + sensorID);

        assertThat("response is an exception",
                   response.getClass().toString(),
                   is(equalTo(ExceptionReportDocumentImpl.class.toString())));
        ExceptionReportDocument report = ExceptionReportDocument.Factory.parse(response.xmlText());
        assertThat("exception is valid", report.validate(), is(equalTo(true)));
        assertThat("exception text contains id",
                   report.getExceptionReport().getExceptionArray(0).getExceptionTextArray(0),
                   containsString(sensorID));
    }

    @Test
    public void deleteReference() throws Exception {
        File f = new File(ClassLoader.getSystemResource("Requests/DeleteSensorInfo_deleteReference.xml").getFile());
        DeleteSensorInfoRequestDocument req = DeleteSensorInfoRequestDocument.Factory.parse(f);
        XmlObject response = client.xSendPostRequest(req);

        DeleteSensorInfoResponseDocument responseDoc = DeleteSensorInfoResponseDocument.Factory.parse(response.getDomNode());

        assertTrue(responseDoc.validate());

        // FIXME test must check whether the reference was actually deleted
    }

    @Test
    public void deleteSensor() throws Exception {
        File f = new File(ClassLoader.getSystemResource("Requests/sir/DeleteSensorInfo.xml").getFile());
        DeleteSensorInfoRequestDocument req = DeleteSensorInfoRequestDocument.Factory.parse(f);
        req.getDeleteSensorInfoRequest().getInfoToBeDeletedArray(0).getSensorIdentification().setSensorIDInSIR(sensorID);

        XmlObject response = client.xSendPostRequest(req);

        DeleteSensorInfoResponseDocument responseDoc = DeleteSensorInfoResponseDocument.Factory.parse(response.getDomNode());
        assertTrue(responseDoc.validate());

        assertThat("one sensor was deleted.",
                   responseDoc.getDeleteSensorInfoResponse().getNumberOfDeletedSensors(),
                   is(equalTo(1)));
        assertThat("no reference was deleted.",
                   responseDoc.getDeleteSensorInfoResponse().getNumberOfDeletedServiceReferences(),
                   is(equalTo(0)));
    }

    private int countServiceReferences(DeleteSensorInfoRequestDocument isird) {
        int referenceCount = 0;

        InfoToBeDeleted[] infoArray = isird.getDeleteSensorInfoRequest().getInfoToBeDeletedArray();
        for (InfoToBeDeleted infoToBeDel : infoArray) {
            referenceCount += infoToBeDel.getServiceInfo().getServiceReferenceArray().length;
        }

        return referenceCount;
    }

}