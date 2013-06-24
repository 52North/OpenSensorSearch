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
package org.n52.sir;

import java.io.File;

import org.apache.xmlbeans.XmlObject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.sir.client.Client;
import org.x52North.sir.x032.GetSensorStatusRequestDocument;
import org.x52North.sir.x032.GetSensorStatusResponseDocument;

/**
 * 
 * @author Daniel Nüst
 * 
 */
public class GetSensorStatus extends SirTestCase {

    @Test
    public void testPostExampleSearchCriteria() throws Exception {
        File f = getPostExampleFile("GetSensorStatus_bySearchCriteria.xml");
        GetSensorStatusRequestDocument req = GetSensorStatusRequestDocument.Factory.parse(f);

        XmlObject response = c.xSendPostRequest(req);

        // parse and validate response
        GetSensorStatusResponseDocument responseDoc = GetSensorStatusResponseDocument.Factory.parse(response.getDomNode());

        assertTrue(responseDoc.validate());
    }

    @Test
    public void testPostExampleSensorID() throws Exception {
        File f = getPostExampleFile("GetSensorStatus_bySensorIDInSIR.xml");
        GetSensorStatusRequestDocument req = GetSensorStatusRequestDocument.Factory.parse(f);

        XmlObject response = c.xSendPostRequest(req);

        // parse and validate response
        GetSensorStatusResponseDocument responseDoc = GetSensorStatusResponseDocument.Factory.parse(response.getDomNode());

        assertTrue(responseDoc.validate());
    }

}