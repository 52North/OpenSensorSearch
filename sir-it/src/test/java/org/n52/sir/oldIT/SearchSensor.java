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
import org.x52North.sir.x032.SearchSensorRequestDocument;
import org.x52North.sir.x032.SearchSensorResponseDocument;

/**
 * 
 * @author Daniel Nüst
 * 
 */
public class SearchSensor extends SirTest {

    @Test
    public void testPostExampleByID() throws Exception {
        File f = getPostExampleFile("SearchSensor_bySensorIDInSIR.xml");
        SearchSensorRequestDocument req = SearchSensorRequestDocument.Factory.parse(f);

        XmlObject response = client.xSendPostRequest(req);

        // parse and validate response
        SearchSensorResponseDocument responseDoc = SearchSensorResponseDocument.Factory.parse(response.getDomNode());

        int send = req.getSearchSensorRequest().getSensorIdentificationArray().length;
        int inserted = responseDoc.getSearchSensorResponse().sizeOfSearchResultElementArray();

        assertEquals(send, inserted);
        assertTrue(responseDoc.validate());
    }

    @Test
    public void testPostExampleByDescr() throws Exception {
        File f = getPostExampleFile("SearchSensor_byServiceDescription.xml");
        SearchSensorRequestDocument req = SearchSensorRequestDocument.Factory.parse(f);

        XmlObject response = client.xSendPostRequest(req);

        // parse and validate response
        SearchSensorResponseDocument responseDoc = SearchSensorResponseDocument.Factory.parse(response.getDomNode());
        assertTrue(responseDoc.validate());
    }
}