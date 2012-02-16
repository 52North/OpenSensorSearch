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

package org.n52.sir.response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.transform.TransformerException;

import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.sensorML.x101.SystemType;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.XmlAnyTypeImpl;
import org.n52.sir.SirConfigurator;
import org.n52.sir.SirConstants;
import org.n52.sir.util.Tools;
import org.n52.sir.util.XmlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schulte
 * 
 */
public class SirDescribeSensorResponse implements ISirResponse {

    private static Logger log = LoggerFactory.getLogger(SirDescribeSensorResponse.class);

    /**
     * String of the requested sensorMLDocument
     */
    private XmlObject sensorML;

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

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.response.ISirResponse#getByteArray()
     */
    @Override
    public byte[] getByteArray() throws IOException, TransformerException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SensorMLDocument process = getDocument();
        process.save(baos, XmlTools.xmlOptionsForNamespaces());
        byte[] bytes = baos.toByteArray();
        return bytes;
    }

    private SensorMLDocument getDocument() {
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

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.response.ISirResponse#getContentLength()
     */
    @Override
    public int getContentLength() throws IOException, TransformerException {
        return getByteArray().length;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.response.ISirResponse#getContentType()
     */
    @Override
    public String getContentType() {
        return SirConstants.CONTENT_TYPE_XML;
    }

}
