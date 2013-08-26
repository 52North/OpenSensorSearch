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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.sir.client.Client;
import org.n52.sir.client.ConnectToCatalogBean;
import org.n52.sir.client.DisconnectFromCatalogBean;
import org.x52North.sir.x032.DisconnectFromCatalogRequestDocument;
import org.x52North.sir.x032.DisconnectFromCatalogResponseDocument;

/**
 * 
 * @author Yakoub
 * 
 */
public class DisconnectFromCatalogIT {

    private String catalogURL = "http://localhost:8080/ergorr/webservice";

    private static Client client;

    @BeforeClass
    public static void setUp() {
        client = GuiceUtil.configureSirClient();
    }
    
    @Before
    private void connectCatalog(String url) throws Exception {
        int pushInterval = 3600; // needs to be with repetition, otherwise not
                                 // saved in database for removal.
        ConnectToCatalogBean ctcb = new ConnectToCatalogBean(url, pushInterval);
        ctcb.buildRequest();
        client.sendPostRequest(ctcb.getRequestString());
    }
    
    @Test
    public void fromBean() throws Exception {
        // buildRequest
        DisconnectFromCatalogBean dfcb = new DisconnectFromCatalogBean(this.catalogURL);
        dfcb.buildRequest();

        // send request
        String response = client.sendPostRequest(dfcb.getRequestString());

        // parse and validate response
        DisconnectFromCatalogResponseDocument responseDoc = DisconnectFromCatalogResponseDocument.Factory.parse(response);
        assertTrue(responseDoc.validate());

        assertEquals(this.catalogURL, responseDoc.getDisconnectFromCatalogResponse().getCatalogURL());
    }

    @Test
    public void fromFile() throws Exception {
        File f = new File(ClassLoader.getSystemResource("Requests/ConnectToCatalog.xml").getFile());
        DisconnectFromCatalogRequestDocument dfcrd = DisconnectFromCatalogRequestDocument.Factory.parse(f);

        String sentUrl = dfcrd.getDisconnectFromCatalogRequest().getCatalogURL();
        XmlObject response = client.xSendPostRequest(dfcrd);

        // parse and validate response
        DisconnectFromCatalogResponseDocument responseDoc = DisconnectFromCatalogResponseDocument.Factory.parse(response.getDomNode());
        assertTrue(responseDoc.validate());

        assertEquals(sentUrl, responseDoc.getDisconnectFromCatalogResponse().getCatalogURL());
    }

}