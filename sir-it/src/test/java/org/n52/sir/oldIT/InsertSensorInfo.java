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
package org.n52.sir.oldIT;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.xmlbeans.XmlObject;
import org.junit.Test;
import org.x52North.sir.x032.InsertSensorInfoRequestDocument;
import org.x52North.sir.x032.InsertSensorInfoRequestDocument.InsertSensorInfoRequest.InfoToBeInserted;
import org.x52North.sir.x032.InsertSensorInfoResponseDocument;

/**
 * 
 * @author Daniel Nüst
 * 
 */
public class InsertSensorInfo extends SirTest {

    @Test
    public void testPostExampleNewSensor() throws Exception {
        File f = getPostExampleFile("InsertSensorInfo_newSensor.xml");
        InsertSensorInfoRequestDocument isird = InsertSensorInfoRequestDocument.Factory.parse(f);

        XmlObject response = client.xSendPostRequest(isird);

        // parse and validate response
        InsertSensorInfoResponseDocument responseDoc = InsertSensorInfoResponseDocument.Factory.parse(response.getDomNode());

        int send = isird.getInsertSensorInfoRequest().getInfoToBeInsertedArray().length;
        int inserted = responseDoc.getInsertSensorInfoResponse().getNumberOfInsertedSensors();

        SirTest.insertedSensorId = responseDoc.getInsertSensorInfoResponse().getInsertedSensors().getSensorIDInSIRArray(0);

        assertEquals(send, inserted);
        assertTrue(responseDoc.validate());
    }

    @Test
    public void testPostExampleAddReference() throws Exception {
        File f = getPostExampleFile("InsertSensorInfo_addReference.xml");
        InsertSensorInfoRequestDocument isird = InsertSensorInfoRequestDocument.Factory.parse(f);

        XmlObject response = client.xSendPostRequest(isird);

        // parse and validate response
        InsertSensorInfoResponseDocument responseDoc = InsertSensorInfoResponseDocument.Factory.parse(response.getDomNode());
        assertTrue(responseDoc.validate());

        int inserted = responseDoc.getInsertSensorInfoResponse().getNumberOfInsertedServiceReferences();
        assertEquals(countServiceReferences(isird), inserted);
    }

    private int countServiceReferences(InsertSensorInfoRequestDocument isird) {
        int referenceCount = 0;

        InfoToBeInserted[] infoArray = isird.getInsertSensorInfoRequest().getInfoToBeInsertedArray();
        for (InfoToBeInserted infoToBeInserted : infoArray) {
            referenceCount += infoToBeInserted.getServiceReferenceArray().length;
        }

        return referenceCount;
    }

}