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
import org.n52.sir.client.ConnectToCatalogBean;
import org.x52North.sir.x032.ConnectToCatalogRequestDocument;
import org.x52North.sir.x032.ConnectToCatalogResponseDocument;

/**
 * 
 * @author Daniel Nüst
 * 
 */
public class ConnectToCatalog extends SirTest {

    private String catalogURL = "http://localhost:8080/ergorr/webservice";

    private int pushInterval = 0;
    
    @Test
    public void testPost() throws Exception {
        // buildRequest
        ConnectToCatalogBean ctcb = new ConnectToCatalogBean(this.catalogURL, this.pushInterval);

        ctcb.buildRequest();

        // send request
        String response = client.sendPostRequest(ctcb.getRequestString());

        // parse and validate response
        ConnectToCatalogResponseDocument responseDoc = ConnectToCatalogResponseDocument.Factory.parse(response);
        assertTrue(responseDoc.validate());
    }

    @Test
    public void testPostExample() throws Exception {
        File f = new File(ConnectToCatalog.class.getResource("/Request/ConnectToCatalog.xml").toURI());
        ConnectToCatalogRequestDocument ctcrd = ConnectToCatalogRequestDocument.Factory.parse(f);

        XmlObject response = client.xSendPostRequest(ctcrd);

        // parse and validate response
        ConnectToCatalogResponseDocument responseDoc = ConnectToCatalogResponseDocument.Factory.parse(response.getDomNode());
        assertTrue(responseDoc.validate());

        assertEquals(this.catalogURL, responseDoc.getConnectToCatalogResponse().getCatalogURL());
    }

}