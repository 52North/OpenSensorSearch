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

import java.io.IOException;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Test;
import org.n52.sor.client.Client;
import org.n52.sor.client.GetCapabilitiesBean;
import org.x52North.sor.x031.CapabilitiesDocument;
import org.x52North.sor.x031.GetCapabilitiesDocument;

/**
 * 
 * @author Daniel Nüst
 * 
 */
public class GetCapabilities extends SorTestCase {

    private static final String SERVICE_NAME = "SOR";

    private String updateSequence = "";

    private String acceptVersions = "";

    private boolean serviceIdentification = false;

    private boolean serviceProvider = false;

    private boolean operationsMetadata = false;

    private boolean contents = false;

    private boolean all = true;

    @Test
    public void testGetExample() throws Exception {
        String s = loadGetRequestExample("GetCapabilities");

        String response = Client.sendGetRequest(s);

        // parse and validate response
        CapabilitiesDocument cd = CapabilitiesDocument.Factory.parse(response);
        assertTrue(cd.validate());
    }

    @Test
    public void testPost() throws Exception {
        // buildRequest
        GetCapabilitiesBean gcb = new GetCapabilitiesBean(SERVICE_NAME,
                                                          this.updateSequence,
                                                          this.acceptVersions,
                                                          this.serviceIdentification,
                                                          this.serviceProvider,
                                                          this.operationsMetadata,
                                                          this.contents,
                                                          this.all);
        gcb.buildRequest();

        // send request
        String response = Client.sendPostRequest(gcb.getRequestString());

        // parse and validate response
        CapabilitiesDocument cd = CapabilitiesDocument.Factory.parse(response);
        assertTrue(cd.validate());
    }

    @Test
    public void testPostExample() throws XmlException, IOException {
        String path = "requests/GetCapabilitiesRequest.xml";
        GetCapabilitiesDocument gcd = GetCapabilitiesDocument.Factory.parse(getClass().getResourceAsStream(path));

        XmlObject response = Client.xSendPostRequest(gcd);

        // parse and validate response
        CapabilitiesDocument cd = CapabilitiesDocument.Factory.parse(response.getDomNode());
        assertTrue(cd.validate());
    }

}