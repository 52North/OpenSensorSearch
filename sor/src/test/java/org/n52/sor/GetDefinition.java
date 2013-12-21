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

import net.opengis.swe.x101.PhenomenonDocument;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Test;
import org.n52.sor.client.Client;
import org.n52.sor.client.GetDefinitionBean;
import org.x52North.sor.GetDefinitionRequestDocument;

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
        String path = PropertiesManager.getInstance().getTestRequestPath() + "GetDefinitionRequest.xml";
        GetDefinitionRequestDocument gcd = GetDefinitionRequestDocument.Factory.parse(new File(path));

        XmlObject response = Client.xSendPostRequest(gcd);

        doAssert(response.xmlText());
    }

}