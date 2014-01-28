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

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Test;
import org.n52.sor.client.Client;
import org.n52.sor.client.DeleteDefinitionBean;
import org.x52North.sor.x031.DeleteDefinitionRequestDocument;
import org.x52North.sor.x031.DeleteDefinitionResponseDocument;

/**
 * 
 * @author Daniel Nüst
 * 
 */
public class DeleteDefinition extends SorTestCase {

    private String definitionURI = "urn:ogc:def:phenomenon:OGC::SunshineDuration2";

    /*
     * No GET for DeleteDefinition!
     */

    /**
     * @param response
     * @throws XmlException
     */
    private void doAssert(String response) throws XmlException {
        // parse and validate response
        DeleteDefinitionResponseDocument ddrd = DeleteDefinitionResponseDocument.Factory.parse(response);
        assertTrue(ddrd.validate());
    }

    @Test
    public void testPost() throws Exception {
        // buildRequest
        DeleteDefinitionBean ddb = new DeleteDefinitionBean();
        ddb.setDefinitionURI(this.definitionURI);

        ddb.buildRequest();

        // send request
        String response = Client.sendPostRequest(ddb.getRequestString());

        doAssert(response);
    }

    @Test
    public void testPostExample() throws XmlException, IOException {
        String path = "requests/DeleteDefinitionRequest.xml";
        DeleteDefinitionRequestDocument ddrd = DeleteDefinitionRequestDocument.Factory.parse(new File(path));

        XmlObject response = Client.xSendPostRequest(ddrd);

        doAssert(response.xmlText());
    }

}