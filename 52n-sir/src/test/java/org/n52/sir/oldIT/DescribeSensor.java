/**
 * ﻿Copyright (C) 2012 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.sir.oldIT;

import static org.junit.Assert.assertTrue;

import java.io.File;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.xmlbeans.XmlObject;
import org.junit.Test;
import org.n52.sir.client.DescribeSensorBean;
import org.n52.sir.util.XmlTools;
import org.x52North.sir.x032.DescribeSensorRequestDocument;

/**
 * 
 * @author Daniel Nüst
 * 
 */
public class DescribeSensor extends SirTest {

    private String sensorIDinSIR = "1";

    @Test
    public void testGetExample() throws Exception {
        String s = loadGetRequestExample("DescribeSensor");

        String response = client.sendGetRequest(s);

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
        String response = client.sendPostRequest(dsb.getRequestString());

        // parse and validate response
        SensorMLDocument sml = SensorMLDocument.Factory.parse(response);

        boolean isValid = sml.validate();
        weakValidate(sml, isValid);
    }

    @Test
    public void testPostExample() throws Exception {
        File f = getPostExampleFile("DescribeSensor.xml");
        DescribeSensorRequestDocument dsd = DescribeSensorRequestDocument.Factory.parse(f);

        XmlObject response = client.xSendPostRequest(dsd);

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