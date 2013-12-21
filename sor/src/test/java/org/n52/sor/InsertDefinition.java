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
import org.n52.sor.client.InsertDefinitionBean;
import org.x52North.sor.InsertDefinitionRequestDocument;
import org.x52North.sor.InsertDefinitionResponseDocument;

/**
 * @author Daniel Nüst
 * 
 */
public class InsertDefinition extends SorTestCase {

    private static final String ID_PLACEHOLDER = "@REPLACE_ID@";
    private String phenomenon = "<swe:Phenomenon gml:id=\"test_Phenonemon\" xsi:schemaLocation=\"http://www.opengis.net/swe/1.0/gml32 http://giv-genesis.uni-muenster.de/schemas/sweCommon/1.0.1_gml32/phenomenon.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:gml=\"http://www.opengis.net/gml/3.2\" xmlns:swe=\"http://www.opengis.net/swe/1.0/gml32\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"><gml:description>Phenomenon inserted for testing</gml:description><gml:identifier codeSpace=\"urn:x-ogc:tc:arch:doc-rp(05-010)\">urn:test: "
            + ID_PLACEHOLDER + "</gml:identifier><gml:name>Test 123</gml:name></swe:Phenomenon>";

    /*
     * No GET for DeleteDefinition!
     */

    /**
     * @param response
     * @throws XmlException
     */
    private void doAssert(String response) throws XmlException {
        // parse and validate response
        InsertDefinitionResponseDocument idrd = InsertDefinitionResponseDocument.Factory.parse(response);
        assertTrue(idrd.validate());
    }

    @Test
    public void testPost() throws Exception {
        this.phenomenon = this.phenomenon.replace(ID_PLACEHOLDER, Long.toString(System.currentTimeMillis()));

        // buildRequest
        InsertDefinitionBean idb = new InsertDefinitionBean();
        idb.setPhenomenon(this.phenomenon);

        idb.buildRequest();

        // send request
        String response = Client.sendPostRequest(idb.getRequestString());

        doAssert(response);
    }

    @Test
    public void testPostExample() throws XmlException, IOException {
        String path = PropertiesManager.getInstance().getTestRequestPath() + "InsertDefinitionRequest.xml";
        InsertDefinitionRequestDocument ddrd = InsertDefinitionRequestDocument.Factory.parse(new File(path));

        XmlObject response = Client.xSendPostRequest(ddrd);

        doAssert(response.xmlText());
    }

}