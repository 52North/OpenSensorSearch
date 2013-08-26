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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.xmlbeans.XmlObject;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.sir.client.Client;
import org.n52.sir.client.ConnectToCatalogBean;
import org.x52North.sir.x032.ConnectToCatalogRequestDocument;
import org.x52North.sir.x032.ConnectToCatalogResponseDocument;

/**
 * 
 * @author Yakoub
 * 
 */
public class ConnectToCatalogIT {

    private static Client client;

    private String catalogURL = "http://localhost:8080/ergorr/webservice";

    private int pushInterval = 0;

    @BeforeClass
    public static void setUp() {
        client = GuiceUtil.configureSirClient();
    }

    @Test
    public void requestCreatedByBean() throws Exception {
        // buildRequest
        ConnectToCatalogBean ctcb = new ConnectToCatalogBean(this.catalogURL, this.pushInterval);
        ctcb.buildRequest();

        // send request
        String response = client.sendPostRequest(ctcb.getRequestString());

        ConnectToCatalogResponseDocument responseDoc = ConnectToCatalogResponseDocument.Factory.parse(response);
        assertTrue(responseDoc.validate());
        assertThat("catalog URLs match", responseDoc.getConnectToCatalogResponse().getCatalogURL(), is(this.catalogURL));
    }

    @Test
    public void requestFromFile() throws Exception {
        File f = new File(ClassLoader.getSystemResource("Requests/ConnectToCatalog.xml").getFile());
        ConnectToCatalogRequestDocument ctcrd = ConnectToCatalogRequestDocument.Factory.parse(f);

        XmlObject response = client.xSendPostRequest(ctcrd);

        ConnectToCatalogResponseDocument responseDoc = ConnectToCatalogResponseDocument.Factory.parse(response.getDomNode());
        assertTrue(responseDoc.validate());
        assertThat("catalog URLs match", responseDoc.getConnectToCatalogResponse().getCatalogURL(), is(this.catalogURL));
    }

    @After
    public void cleanUp() {
        // FIXME test must remove the catalog connections
    }

}