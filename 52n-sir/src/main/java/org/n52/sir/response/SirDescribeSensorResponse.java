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
package org.n52.sir.response;

import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.sensorML.x101.SystemType;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.XmlAnyTypeImpl;
import org.n52.sir.SirConfigurator;
import org.n52.sir.util.Tools;
import org.n52.sir.util.XmlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schulte
 * 
 */
public class SirDescribeSensorResponse extends AbstractXmlResponse {

    private static Logger log = LoggerFactory.getLogger(SirDescribeSensorResponse.class);

    /**
     * String of the requested sensorMLDocument
     */
    private XmlObject sensorML;

    @Override
    public SensorMLDocument createXml() {
        SensorMLDocument document = null;

        if (this.sensorML instanceof SystemType) {
            log.debug("Have SystemType in response, wrapping in SensorMLDocument.");

            document = Tools.wrapSystemTypeInSensorMLDocument((SystemType) this.sensorML);
        }
        else if (this.sensorML instanceof SensorMLDocument) {
            log.debug("Returning SensorMLDocument from database.");

            document = (SensorMLDocument) this.sensorML;
        }
        else if (this.sensorML instanceof XmlAnyTypeImpl) {
            log.debug("Have XmlAnyTypeImpl, trying to parse.");
            // try parsing
            try {
                document = SensorMLDocument.Factory.parse(this.sensorML.getDomNode());
            }
            catch (XmlException e) {
                throw new UnsupportedOperationException("Sensor description was XmlAnyType but could not be parsed!");
            }
        }
        else {
            throw new UnsupportedOperationException("Sensor description was not a SystemType nor a SensorMLDocument with a System as the first member - case not implemented!");
        }

        // add schema location for validation
        XmlCursor cursor = document.newCursor();
        cursor.toFirstChild();
        cursor.setAttributeText(XmlTools.SCHEMA_LOCATION_ATTRIBUTE_QNAME, XmlTools.getSensorMLSchemaLocation());
        cursor.dispose();

        if (SirConfigurator.getInstance().isValidateResponses()) {
            if ( !document.validate())
                log.warn("Service created invalid document!\n" + XmlTools.validateAndIterateErrors(document));
        }

        return document;
    }

    /**
     * @return the sensorML
     */
    public XmlObject getSensorML() {
        return this.sensorML;
    }

    /**
     * @param sensorML
     *        the sensorML to set
     */
    public void setSensorML(XmlObject sensorML) {
        this.sensorML = sensorML;
    }

}
