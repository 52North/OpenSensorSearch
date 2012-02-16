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

import de.uniMuenster.swsl.sir.InsertSensorInfoRequestDocument;
import de.uniMuenster.swsl.sir.InsertSensorInfoRequestDocument.InsertSensorInfoRequest.InfoToBeInserted;
import de.uniMuenster.swsl.sir.InsertSensorInfoResponseDocument;

/**
 * 
 * @author Daniel Nüst
 * 
 */
public class InsertSensorInfo extends SirTestCase {

    @Test
    public void testPostExampleNewSensor() throws Exception {
        File f = getPostExampleFile("InsertSensorInfo_newSensor.xml");
        InsertSensorInfoRequestDocument isird = InsertSensorInfoRequestDocument.Factory.parse(f);

        XmlObject response = Client.xSendPostRequest(isird);

        // parse and validate response
        InsertSensorInfoResponseDocument responseDoc = InsertSensorInfoResponseDocument.Factory.parse(response.getDomNode());

        int send = isird.getInsertSensorInfoRequest().getInfoToBeInsertedArray().length;
        int inserted = responseDoc.getInsertSensorInfoResponse().getNumberOfInsertedSensors();

        SirTestCase.insertedSensorId = responseDoc.getInsertSensorInfoResponse().getInsertedSensors().getSensorIDInSIRArray(0);

        assertEquals(send, inserted);
        assertTrue(responseDoc.validate());
    }

    @Test
    public void testPostExampleAddReference() throws Exception {
        File f = getPostExampleFile("InsertSensorInfo_addReference.xml");
        InsertSensorInfoRequestDocument isird = InsertSensorInfoRequestDocument.Factory.parse(f);

        XmlObject response = Client.xSendPostRequest(isird);

        // parse and validate response
        InsertSensorInfoResponseDocument responseDoc = InsertSensorInfoResponseDocument.Factory.parse(response.getDomNode());
        assertTrue(responseDoc.validate());

        int inserted = responseDoc.getInsertSensorInfoResponse().getNumberOfInsertedServiceReferences();
        assertEquals(countServiceReferences(isird), inserted);
    }

    private int countServiceReferences(InsertSensorInfoRequestDocument isird) {
        int referenceCount = 0;

        InfoToBeInserted[] infoArray = isird.getInsertSensorInfoRequest().getInfoToBeInsertedArray();
        for (InfoToBeInserted infoToBeInserted : infoArray) {
            referenceCount += infoToBeInserted.getServiceReferenceArray().length;
        }

        return referenceCount;
    }

}