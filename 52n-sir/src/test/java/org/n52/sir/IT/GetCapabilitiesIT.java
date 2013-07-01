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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.naming.OperationNotSupportedException;

import org.apache.http.HttpException;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.sir.Util;
import org.n52.sir.client.Client;
import org.n52.sir.ows.OwsExceptionReport;
import org.x52North.sir.x032.CapabilitiesDocument;
import org.x52North.sir.x032.GetCapabilitiesDocument;

public class GetCapabilitiesIT {

    private static Client c = null;

    @BeforeClass
    public static void setUpClient() throws MalformedURLException {
        URL url = Util.getSIREndpointForIT();
        c = new Client(url);
    }

    @Test
    public void getCapabilitiesGET() throws XmlException,
            UnsupportedEncodingException,
            OperationNotSupportedException,
            HttpException,
            IOException,
            OwsExceptionReport {
        StringBuilder request = new StringBuilder();
        request.append(c.getURL());
        request.append("?");
        request.append("request=GetCapabilities&service=SIR");

        XmlObject response = c.xSendGetRequest(request.toString());
        CapabilitiesDocument resp_doc = CapabilitiesDocument.Factory.parse(response.getDomNode());
        assertTrue("Invalid XML", resp_doc.validate());
    }

//    @Test
    public void getCapabilitesPOST() throws IOException,
            OwsExceptionReport,
            HttpException,
            XmlException,
            OperationNotSupportedException {

        File f = new File(GetCapabilitiesIT.class.getResource("/Requests/GetCapabilities.xml").getFile());

        GetCapabilitiesDocument doc = GetCapabilitiesDocument.Factory.parse(f);
        XmlObject response = c.xSendPostRequest(doc);
        System.out.println(response.xmlText());

        CapabilitiesDocument resp_doc = CapabilitiesDocument.Factory.parse(response.getDomNode());
        assertTrue("Invalid XML", resp_doc.validate());

    }
}
