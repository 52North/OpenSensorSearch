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
import org.n52.sor.client.GetMatchingDefinitionsBean;
import org.n52.sor.request.ISorRequest.SorMatchingType;
import org.x52North.sor.GetMatchingDefinitionsRequestDocument;
import org.x52North.sor.GetMatchingDefinitionsResponseDocument;

/**
 * 
 * @author Daniel Nüst
 * 
 */
public class GetMatchingDefinitions extends SorTestCase {

    private String testURI = "urn:ogc:def:phenomenon:OGC::WindChill";

    private SorMatchingType matchingType = SorMatchingType.SUPER_TYPE;

    private int searchDepth = 3;

    /**
     * @param response
     * @throws XmlException
     */
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
        String path = PropertiesManager.getInstance().getTestRequestPath() + "GetMatchingDefinitionsRequest.xml";
        GetMatchingDefinitionsRequestDocument gcd = GetMatchingDefinitionsRequestDocument.Factory.parse(new File(path));

        XmlObject response = Client.xSendPostRequest(gcd);

        doAssert(response.xmlText());
    }

}