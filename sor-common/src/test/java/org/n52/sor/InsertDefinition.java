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
package org.n52.sor;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Test;
import org.n52.sor.client.Client;
import org.n52.sor.client.InsertDefinitionBean;
import org.x52North.sor.x031.InsertDefinitionRequestDocument;
import org.x52North.sor.x031.InsertDefinitionResponseDocument;

/**
 * @author Daniel Nüst
 * 
 */
public class InsertDefinition extends SorTestCase {

    private static final String ID_PLACEHOLDER = "@REPLACE_ID@";
    private String phenomenon = "<swe:Phenomenon gml:id=\"test_Phenonemon\" xsi:schemaLocation=\"http://www.opengis.net/swe/1.0/gml32 http://giv-genesis.uni-muenster.de/schemas/sweCommon/1.0.1_gml32/phenomenon.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:gml=\"http://www.opengis.net/gml/3.2\" xmlns:swe=\"http://www.opengis.net/swe/1.0/gml32\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"><gml:description>Phenomenon inserted for testing</gml:description><gml:identifier codeSpace=\"urn:x-ogc:tc:arch:doc-rp(05-010)\">urn:test: "
            + ID_PLACEHOLDER + "</gml:identifier><gml:name>Test 123</gml:name></swe:Phenomenon>";

    private void doAssert(String response) throws XmlException {
        // parse and validate response
        InsertDefinitionResponseDocument idrd = InsertDefinitionResponseDocument.Factory.parse(response);
        assertTrue(idrd.validate());
    }

    @Test
    public void testPost() throws Exception {
        this.phenomenon = this.phenomenon.replace(ID_PLACEHOLDER, Long.toString(System.currentTimeMillis()));

        // buildRequest
        InsertDefinitionBean idb = new InsertDefinitionBean();
        idb.setPhenomenon(this.phenomenon);

        idb.buildRequest();

        // send request
        String response = Client.sendPostRequest(idb.getRequestString());

        doAssert(response);
    }

    @Test
    public void testPostExample() throws XmlException, IOException {
        String path = PropertiesManager.getInstance().getTestRequestPath() + "InsertDefinitionRequest.xml";
        InsertDefinitionRequestDocument ddrd = InsertDefinitionRequestDocument.Factory.parse(new File(path));

        XmlObject response = Client.xSendPostRequest(ddrd);

        doAssert(response.xmlText());
    }

}