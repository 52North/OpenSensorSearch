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
import org.n52.sor.client.GetDefinitionURIsBean;
import org.x52North.sor.x031.GetDefinitionURIsRequestDocument;
import org.x52North.sor.x031.GetDefinitionURIsResponseDocument;

/**
 * 
 * @author Daniel Nüst
 * 
 */
public class GetDefinitionURIs extends SorTestCase {

    /**
     * @param response
     * @throws XmlException
     */
    private void doAssert(String response) throws XmlException {
        // parse and validate response
        GetDefinitionURIsResponseDocument gdurd = GetDefinitionURIsResponseDocument.Factory.parse(response);
        assertTrue(gdurd.validate());
    }

    @Test
    public void testGetExample() throws Exception {
        String s = loadGetRequestExample("GetDefinitionURIs");

        String response = Client.sendGetRequest(s);

        doAssert(response);
    }

    /**
     * 
     * Send without parameters.
     * 
     * @throws Exception
     */
    @Test
    public void testPost() throws Exception {
        // buildRequest
        GetDefinitionURIsBean gdub = new GetDefinitionURIsBean();
        gdub.buildRequest();

        // send request
        String response = Client.sendPostRequest(gdub.getRequestString());

        doAssert(response);
    }

    @Test
    public void testPostExample() throws XmlException, IOException {
        String path = PropertiesManager.getInstance().getTestRequestPath() + "GetDefinitionURIsRequest.xml";
        GetDefinitionURIsRequestDocument gcd = GetDefinitionURIsRequestDocument.Factory.parse(new File(path));

        XmlObject response = Client.xSendPostRequest(gcd);

        doAssert(response.xmlText());
    }

}