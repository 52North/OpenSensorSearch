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
public class TestSensorMLBean extends AbstractBean {

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
