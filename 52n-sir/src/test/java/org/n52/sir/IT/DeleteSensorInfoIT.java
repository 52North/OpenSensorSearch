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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.xmlbeans.XmlObject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.sir.client.Client;
import org.x52North.sir.x032.DeleteSensorInfoRequestDocument;
import org.x52North.sir.x032.DeleteSensorInfoRequestDocument.DeleteSensorInfoRequest.InfoToBeDeleted;
import org.x52North.sir.x032.DeleteSensorInfoResponseDocument;

/**
 * 
 * @author Yakoub
 * 
 */
public class DeleteSensorInfoIT {

    private static Client client;

    @BeforeClass
    public static void setUp() {
        client = GuiceUtil.configureSirClient();
    }
    
    @Test
    public void deleteReference() throws Exception {
        File f = new File(ClassLoader.getSystemResource("Requests/DeleteSensorInfo_deleteReference.xml").getFile());
        DeleteSensorInfoRequestDocument req = DeleteSensorInfoRequestDocument.Factory.parse(f);

        XmlObject response = client.xSendPostRequest(req);

        // parse and validate response
        DeleteSensorInfoResponseDocument responseDoc = DeleteSensorInfoResponseDocument.Factory.parse(response.getDomNode());

        assertTrue(responseDoc.validate());
        
        // FIXME test must check whether the reference was actually deleted
    }

    @Test
    public void deleteSensor() throws Exception {
        File f = new File(ClassLoader.getSystemResource("Requests/DeleteSensorInfo.xml").getFile());
        DeleteSensorInfoRequestDocument req = DeleteSensorInfoRequestDocument.Factory.parse(f);

        XmlObject response = client.xSendPostRequest(req);

        // parse and validate response
        DeleteSensorInfoResponseDocument responseDoc = DeleteSensorInfoResponseDocument.Factory.parse(response.getDomNode());
        assertTrue(responseDoc.validate());

        int deleted = responseDoc.getDeleteSensorInfoResponse().getNumberOfDeletedSensors();
        assertEquals(countServiceReferences(req), deleted);
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