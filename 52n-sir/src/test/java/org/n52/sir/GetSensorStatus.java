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
package org.n52.sir;

import java.io.File;

import org.apache.xmlbeans.XmlObject;
import org.junit.Test;
import org.n52.sir.client.Client;

import de.uniMuenster.swsl.sir.GetSensorStatusRequestDocument;
import de.uniMuenster.swsl.sir.GetSensorStatusResponseDocument;

/**
 * 
 * @author Daniel Nüst
 * 
 */
public class GetSensorStatus extends SirTestCase {

    @Test
    public void testPostExampleSearchCriteria() throws Exception {
        File f = getPostExampleFile("GetSensorStatus_bySearchCriteria.xml");
        GetSensorStatusRequestDocument req = GetSensorStatusRequestDocument.Factory.parse(f);

        XmlObject response = Client.xSendPostRequest(req);

        // parse and validate response
        GetSensorStatusResponseDocument responseDoc = GetSensorStatusResponseDocument.Factory.parse(response.getDomNode());

        assertTrue(responseDoc.validate());
    }

    @Test
    public void testPostExampleSensorID() throws Exception {
        File f = getPostExampleFile("GetSensorStatus_bySensorIDInSIR.xml");
        GetSensorStatusRequestDocument req = GetSensorStatusRequestDocument.Factory.parse(f);

        XmlObject response = Client.xSendPostRequest(req);

        // parse and validate response
        GetSensorStatusResponseDocument responseDoc = GetSensorStatusResponseDocument.Factory.parse(response.getDomNode());

        assertTrue(responseDoc.validate());
    }

}