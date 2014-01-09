/**
 * Copyright 2013 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
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

import net.opengis.swe.x101.PhenomenonDocument;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Test;
import org.n52.sor.client.Client;
import org.n52.sor.client.GetDefinitionBean;
import org.x52North.sor.x031.GetDefinitionRequestDocument;

/**
 * 
 * @author Daniel Nüst
 * 
 */
public class GetDefinition extends SorTestCase {

    private String testURI = "urn:x-ogc:def:phenomenon:OGC::Speed";

    /**
     * @param response
     * @throws XmlException
     */
    private void doAssert(String response) throws XmlException {
        // parse and validate response
        PhenomenonDocument p = PhenomenonDocument.Factory.parse(response);
        assertTrue(p.validate());
    }

    @Test
    public void testGetExample() throws Exception {
        String s = loadGetRequestExample("GetDefinition");

        String response = Client.sendGetRequest(s);

        doAssert(response);
    }

    @Test
    public void testPost() throws Exception {
        // buildRequest
        GetDefinitionBean gdb = new GetDefinitionBean();
        gdb.setInputURI(this.testURI);
        gdb.buildRequest();

        // send request
        String response = Client.sendPostRequest(gdb.getRequestString());

        doAssert(response);
    }

    @Test
    public void testPostExample() throws XmlException, IOException {
        String path = "requests/GetDefinitionRequest.xml";
        GetDefinitionRequestDocument gcd = GetDefinitionRequestDocument.Factory.parse(new File(path));

        XmlObject response = Client.xSendPostRequest(gcd);

        doAssert(response.xmlText());
    }

}