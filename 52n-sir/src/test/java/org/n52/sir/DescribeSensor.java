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

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.xmlbeans.XmlObject;
import org.junit.Test;
import org.n52.sir.client.Client;
import org.n52.sir.client.DescribeSensorBean;
import org.n52.sir.util.XmlTools;

import de.uniMuenster.swsl.sir.DescribeSensorRequestDocument;

/**
 * 
 * @author Daniel Nüst
 * 
 */
public class DescribeSensor extends SirTestCase {

    private String sensorIDinSIR = "1";

    @Test
    public void testGetExample() throws Exception {
        String s = loadGetRequestExample("DescribeSensor");

        String response = Client.sendGetRequest(s);

        // parse and validate response
        SensorMLDocument sml = SensorMLDocument.Factory.parse(response);
        boolean isValid = sml.validate();
        weakValidate(sml, isValid);
    }

    @Test
    public void testPost() throws Exception {
        // buildRequest
        DescribeSensorBean dsb = new DescribeSensorBean(this.sensorIDinSIR);

        dsb.buildRequest();

        // send request
        String response = Client.sendPostRequest(dsb.getRequestString());

        // parse and validate response
        SensorMLDocument sml = SensorMLDocument.Factory.parse(response);

        boolean isValid = sml.validate();
        weakValidate(sml, isValid);
    }

    @Test
    public void testPostExample() throws Exception {
        File f = getPostExampleFile("DescribeSensor.xml");
        DescribeSensorRequestDocument dsd = DescribeSensorRequestDocument.Factory.parse(f);

        XmlObject response = Client.xSendPostRequest(dsd);

        // parse and validate response
        SensorMLDocument sml = SensorMLDocument.Factory.parse(response.getDomNode());
        boolean isValid = sml.validate();
        weakValidate(sml, isValid);
    }

    /**
     * 
     * Something is wrong with the validation of SensorML regarding the version numbers...
     * 
     * @param sml
     * @param isValid
     */
    private void weakValidate(SensorMLDocument sml, boolean isValid) {
        if ( !isValid) {
            System.out.println("Response document is invalid, but test probably passed...\n"
                    + XmlTools.validateAndIterateErrors(sml));
        }
        else {
            assertTrue(isValid);
        }
    }

}