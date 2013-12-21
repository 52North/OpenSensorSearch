/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */

package org.n52.sor;

import java.io.File;
import java.io.IOException;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Test;
import org.n52.sor.client.Client;
import org.n52.sor.client.GetCapabilitiesBean;
import org.x52North.sor.CapabilitiesDocument;
import org.x52North.sor.GetCapabilitiesDocument;

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
        String path = PropertiesManager.getInstance().getTestRequestPath() + "GetCapabilitiesRequest.xml";
        GetCapabilitiesDocument gcd = GetCapabilitiesDocument.Factory.parse(new File(path));

        XmlObject response = Client.xSendPostRequest(gcd);

        // parse and validate response
        CapabilitiesDocument cd = CapabilitiesDocument.Factory.parse(response.getDomNode());
        assertTrue(cd.validate());
    }

}