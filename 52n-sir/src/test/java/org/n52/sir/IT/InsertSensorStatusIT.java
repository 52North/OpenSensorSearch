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
 * @author Yakoub
 */

package org.n52.sir.IT;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Test;
import org.n52.sir.client.Client;
import org.n52.sir.ows.OwsExceptionReport;
import org.x52North.sir.x032.InsertSensorStatusRequestDocument;
import org.x52North.sir.x032.InsertSensorStatusResponseDocument;

public class InsertSensorStatusIT {

    @Test
    public void insertSensorStatus() throws XmlException, IOException, OwsExceptionReport, HttpException {
        XmlObject res = null;
        /*
         * Create a sensor insert request from sensorFile
         */
        File sensor_status = new File(ClassLoader.getSystemResource("Requests/InsertSensorStatus.xml").getFile());
        InsertSensorStatusRequestDocument req = InsertSensorStatusRequestDocument.Factory.parse(sensor_status);

        res = Client.xSendPostRequest(req);

        InsertSensorStatusResponseDocument res_doc = InsertSensorStatusResponseDocument.Factory.parse(res.getDomNode());

        assertTrue("StatusId not equal",
                   res_doc.getInsertSensorStatusResponse().getSensorIDInSIR().equals(req.getInsertSensorStatusRequest().getStatusDescription().getSensorIDInSIR()));

    }

}
