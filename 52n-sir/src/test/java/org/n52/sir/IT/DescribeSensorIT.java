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

/**
 * author Yakoub
 */

package org.n52.sir.IT;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import javax.servlet.UnavailableException;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.http.HttpException;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.n52.sir.client.Client;
import org.n52.sir.ows.OwsExceptionReport;
import org.x52North.sir.x032.DeleteSensorInfoRequestDocument;
import org.x52North.sir.x032.DescribeSensorRequestDocument;
import org.x52North.sir.x032.InsertSensorInfoRequestDocument;

public class DescribeSensorIT {

    private String sensorIDinSIR = "42";

    @Before
    public void setup() throws UnavailableException, OwsExceptionReport, XmlException, IOException, HttpException {

        /*
         * To make it self consistent I will add the sensor testsensor.xml before doing testing
         */
        File f = new File(ClassLoader.getSystemResource("Requests/InsertSensorInfo_newSensor.xml").getFile());

        InsertSensorInfoRequestDocument doc = InsertSensorInfoRequestDocument.Factory.parse(f);
        Client.xSendPostRequest(doc);
        // The file has the sensor with Id:42

    }

    @Test
    public void describeSensorUsingDocument() throws Exception {

        DescribeSensorRequestDocument doc = DescribeSensorRequestDocument.Factory.newInstance();
        doc.addNewDescribeSensorRequest().setSensorIDInSIR(sensorIDinSIR);

        XmlObject response = null;

        response = Client.xSendPostRequest(doc);
        // parse and validate response
        SensorMLDocument sml = SensorMLDocument.Factory.parse(response.getDomNode());
        boolean isValid = sml.validate();
        assertTrue("Not a valid sensorML returned", isValid);
    }

    @Test
    public void describeSensor() throws Exception {

        DescribeSensorRequestDocument doc = DescribeSensorRequestDocument.Factory.parse(new File(ClassLoader.getSystemResource("Requests/DescribeSensor.xml").getFile()));
        XmlObject response = null;

        response = Client.xSendPostRequest(doc);
        // parse and validate response
        SensorMLDocument sml = SensorMLDocument.Factory.parse(response.getDomNode());
        boolean isValid = sml.validate();
        assertTrue("Not a valid sensorML returned", isValid);
    }

    @After
    public void deleteInsertedSensor() throws Exception {
        // delete the added sensor to keep the system state unchanged
        DeleteSensorInfoRequestDocument doc = DeleteSensorInfoRequestDocument.Factory.parse(new File(ClassLoader.getSystemResource("Requests/DeleteSensorInfo.xml").getFile()));
        XmlObject response = Client.xSendPostRequest(doc);
        boolean isValid = response.validate();
        assertTrue("Warning:SensorId:42 has to be deleted", isValid);
    }
}
