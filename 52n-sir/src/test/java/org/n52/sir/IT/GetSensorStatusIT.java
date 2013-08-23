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

import org.apache.xmlbeans.XmlObject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.sir.client.Client;
import org.x52North.sir.x032.GetSensorStatusRequestDocument;
import org.x52North.sir.x032.GetSensorStatusResponseDocument;

public class GetSensorStatusIT {

    private static Client client;

    @BeforeClass
    public static void setUp() {
        client = GuiceUtil.configureSirClient();
    }
    
    @Test
    public void getSensorStatus(String file) throws Exception {
        File f = new File( (ClassLoader.getSystemResource(file).getFile()));
        GetSensorStatusRequestDocument doc = GetSensorStatusRequestDocument.Factory.parse(f);
        XmlObject response = null;

        // try {

        response = client.xSendPostRequest(doc);
        // parse and validate response
        GetSensorStatusResponseDocument resp_doc = GetSensorStatusResponseDocument.Factory.parse(response.getDomNode());
        // validate the respo_doc

        assertTrue("Invalid  Sensor status", resp_doc.validate());

    }

    @Test
    public void getSensorStatusSearchCriteria() throws Exception {
        getSensorStatus("Requests/GetSensorStatus_bySearchCriteria.xml");
        // TODO implement test
    }

    @Test
    public void getSensorStatusSearchID() throws Exception {
        getSensorStatus("Requests/GetSensorStatus_bySensorIDInSIR");
        // TODO implement test
    }

    @Test
    public void getSensorStatusSearchServiceDescription() throws Exception {
        getSensorStatus("Requests/GetSensorStatus_byServiceDescription.xml");
        // TODO implement test
    }

}
