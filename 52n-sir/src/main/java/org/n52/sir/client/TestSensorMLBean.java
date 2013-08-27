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
package org.n52.sir.client;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.xmlbeans.XmlException;
import org.n52.sir.SirConfigurator;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.xml.IProfileValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class TestSensorMLBean extends TestClientBean {

    private static Logger log = LoggerFactory.getLogger(TestSensorMLBean.class);

    private String schematronLink;

    /**
     * 
     */
    public TestSensorMLBean() {
        this.schematronLink = SirConfigurator.getInstance().getSchemaDownloadLink();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.client.beans.AbstractBean#buildRequest()
     */
    @Override
    public void buildRequest() {
        // do nothing...
    }

    /**
     * 
     * @return The full path to the schematron file against which is tested.
     */
    public String getSchematronDownloadLink() {
        return this.schematronLink;
    }

    /**
     * 
     */
    public void requestTest() {
        this.responseString = "";

        IProfileValidator validator;
        try {
            validator = SirConfigurator.getInstance().getValidatorFactory().getSensorMLProfile4DiscoveryValidator();
        }
        catch (OwsExceptionReport e) {
            setResponseString(e.getDocument().xmlText());
            return;
        }

        SensorMLDocument sml = null;
        try {
            sml = SensorMLDocument.Factory.parse(getRequestString());
        }
        catch (XmlException e) {
            log.error("Error parsing input.", e);
            setResponseString("Exception:\n" + e.getMessage());
            setRequestString("");
            return;
        }

        boolean b = validator.validate(sml);
        if (b) {
            setResponseString("The document is conform!");
        }
        else {
            setResponseString(validator.getValidationFailuresAsString());
        }

        return;
    }

}
