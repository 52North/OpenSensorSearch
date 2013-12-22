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

import java.io.IOException;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Test;
import org.n52.sor.ISorRequest.SorMatchingType;
import org.n52.sor.client.Client;
import org.n52.sor.client.GetMatchingDefinitionsBean;
import org.x52North.sor.x031.GetMatchingDefinitionsRequestDocument;
import org.x52North.sor.x031.GetMatchingDefinitionsResponseDocument;

/**
 * 
 * @author Daniel Nüst
 * 
 */
public class GetMatchingDefinitions extends SorTestCase {

    private String testURI = "urn:ogc:def:phenomenon:OGC::WindChill";

    private SorMatchingType matchingType = SorMatchingType.SUPER_TYPE;

    private int searchDepth = 3;

    private void doAssert(String response) throws XmlException {
        // parse and validate response
        GetMatchingDefinitionsResponseDocument gmdrd = GetMatchingDefinitionsResponseDocument.Factory.parse(response);
        assertTrue(gmdrd.validate());
    }

    @Test
    public void testGetExample() throws Exception {
        String s = loadGetRequestExample("GetMatchingDefinitions");

        String response = Client.sendGetRequest(s);

        doAssert(response);
    }

    @Test
    public void testPost() throws Exception {
        // buildRequest
        GetMatchingDefinitionsBean gmdb = new GetMatchingDefinitionsBean();

        gmdb.setInputURI(this.testURI);
        gmdb.setMatchingTypeString(this.matchingType.toString());
        gmdb.setSearchDepth(this.searchDepth);

        gmdb.buildRequest();

        // send request
        String response = Client.sendPostRequest(gmdb.getRequestString());

        doAssert(response);
    }

    @Test
    public void testPostExample() throws XmlException, IOException {
        String path = "requests/GetMatchingDefinitionsRequest.xml";
        GetMatchingDefinitionsRequestDocument gcd = GetMatchingDefinitionsRequestDocument.Factory.parse(getClass().getResourceAsStream(path));

        XmlObject response = Client.xSendPostRequest(gcd);

        doAssert(response.xmlText());
    }

}