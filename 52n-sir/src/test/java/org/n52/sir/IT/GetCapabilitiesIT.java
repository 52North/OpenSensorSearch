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
/**
 * @author Yakoub
 */

package org.n52.sir.IT;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.custommonkey.xmlunit.Diff;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.sir.client.Client;
import org.n52.sir.ows.OwsExceptionReport;
import org.x52North.sir.x032.CapabilitiesDocument;
import org.x52North.sir.x032.GetCapabilitiesDocument;
import org.xml.sax.SAXException;

public class GetCapabilitiesIT {

    static Client client;

    @BeforeClass
    public static void setUp() {
        client = GuiceUtil.configureSirClient();
    }

    @Test
    public void getCapabilites() throws IOException, OwsExceptionReport, HttpException, XmlException, SAXException {

        File f = new File(ClassLoader.getSystemResource("sir/requests/GetCapabilities.xml").getFile());

        GetCapabilitiesDocument doc = GetCapabilitiesDocument.Factory.parse(f);
        XmlObject response = client.xSendPostRequest(doc);
        CapabilitiesDocument actual = CapabilitiesDocument.Factory.parse(response.getDomNode());

        assertThat("Document is valid according to XMLBeans.", actual.validate(), is(true));

        f = new File(ClassLoader.getSystemResource("sir/responses/capabilities.xml").getFile());
        CapabilitiesDocument expected = CapabilitiesDocument.Factory.parse(f);

        Diff diff = new Diff(actual.getCapabilities().getOperationsMetadata().toString(),
                             expected.getCapabilities().getOperationsMetadata().toString());
        
        assertThat("XML is similar.", diff.similar(), is(true));
        assertThat("XML is identical.", diff.identical(), is(true));
    }
}
