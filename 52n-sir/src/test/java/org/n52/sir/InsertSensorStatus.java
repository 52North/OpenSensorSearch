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
import org.junit.Test;
import org.n52.sir.client.Client;
import org.x52North.sir.x032.InsertSensorStatusRequestDocument;
import org.x52North.sir.x032.InsertSensorStatusResponseDocument;

/**
 * 
 * @author Daniel Nüst
 * 
 */
public class InsertSensorStatus extends SirTestCase {

    @Test
    public void testPostExample() throws Exception {
        File f = getPostExampleFile("InsertSensorStatus.xml");
        InsertSensorStatusRequestDocument req = InsertSensorStatusRequestDocument.Factory.parse(f);

        XmlObject response = c.xSendPostRequest(req);

        // parse and validate response
        InsertSensorStatusResponseDocument responseDoc = InsertSensorStatusResponseDocument.Factory.parse(response.getDomNode());

        assertEquals(req.getInsertSensorStatusRequest().getStatusDescription().getSensorIDInSIR(),
                     responseDoc.getInsertSensorStatusResponse().getSensorIDInSIR());
        assertTrue(responseDoc.validate());
    }

}