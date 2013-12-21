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
import org.n52.sor.client.DeleteDefinitionBean;
import org.x52North.sor.DeleteDefinitionRequestDocument;
import org.x52North.sor.DeleteDefinitionResponseDocument;

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
        String path = PropertiesManager.getInstance().getTestRequestPath() + "DeleteDefinitionRequest.xml";
        DeleteDefinitionRequestDocument ddrd = DeleteDefinitionRequestDocument.Factory.parse(new File(path));

        XmlObject response = Client.xSendPostRequest(ddrd);

        doAssert(response.xmlText());
    }

}