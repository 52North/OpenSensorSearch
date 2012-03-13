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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.xml.transform.TransformerException;

import org.n52.sir.SirConfigurator;
import org.n52.sir.SirConstants;
import org.n52.sir.util.XmlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniMuenster.swsl.sir.InsertSensorInfoResponseDocument;
import de.uniMuenster.swsl.sir.InsertSensorInfoResponseDocument.InsertSensorInfoResponse;
import de.uniMuenster.swsl.sir.InsertSensorInfoResponseDocument.InsertSensorInfoResponse.InsertedSensors;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class SirInsertSensorInfoResponse implements ISirResponse {

    private static Logger log = LoggerFactory.getLogger(SirInsertSensorInfoResponse.class);

    private Collection<String> insertedSensors = new ArrayList<String>();

    private int numberOfInsertedSensors = 0;

    private int numberOfInsertedServiceReferences = 0;

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.response.ISirResponse#getByteArray()
     */
    @Override
    public byte[] getByteArray() throws IOException, TransformerException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InsertSensorInfoResponseDocument insSensInfoResp = parseToResponseDocument();
        insSensInfoResp.save(baos, XmlTools.xmlOptionsForNamespaces());
        byte[] bytes = baos.toByteArray();
        return bytes;
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

    /**
     * @return the insertedSensors
     */
    public Collection<String> getInsertedSensors() {
        return this.insertedSensors;
    }

    /**
     * @return the numberOfNewSensors
     */
    public int getNumberOfNewSensors() {
        return this.numberOfInsertedSensors;
    }

    /**
     * @return the numberOfNewServiceReferences
     */
    public int getNumberOfNewServiceReferences() {
        return this.numberOfInsertedServiceReferences;
    }

    private InsertSensorInfoResponseDocument parseToResponseDocument() {
        InsertSensorInfoResponseDocument document = InsertSensorInfoResponseDocument.Factory.newInstance();
        InsertSensorInfoResponse insSensInfoResp = document.addNewInsertSensorInfoResponse();

        insSensInfoResp.setNumberOfInsertedSensors(this.numberOfInsertedSensors);
        insSensInfoResp.setNumberOfInsertedServiceReferences(this.numberOfInsertedServiceReferences);
        InsertedSensors insertedSensor = insSensInfoResp.addNewInsertedSensors();
        for (String inSens : this.insertedSensors) {
            insertedSensor.addSensorIDInSIR(inSens);
        }

        XmlTools.addSirAndSensorMLSchemaLocation(insSensInfoResp);

        if (SirConfigurator.getInstance().isValidateResponses()) {
            if ( !document.validate())
                log.warn("Service created invalid document!\n" + XmlTools.validateAndIterateErrors(document));
        }

        return document;
    }

    /**
     * @param insertedSensors
     *        the insertedSensors to set
     */
    public void setInsertedSensors(Collection<String> insertedSensors) {
        this.insertedSensors = insertedSensors;
    }

    /**
     * @param numberOfNewSensors
     *        the numberOfNewSensors to set
     */
    public void setNumberOfNewSensors(int numberOfNewSensors) {
        this.numberOfInsertedSensors = numberOfNewSensors;
    }

    /**
     * @param numberOfNewServiceReferences
     *        the numberOfNewServiceReferences to set
     */
    public void setNumberOfNewServiceReferences(int numberOfNewServiceReferences) {
        this.numberOfInsertedServiceReferences = numberOfNewServiceReferences;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SirInsertSensorInfoResponse: ");
        sb.append(" Inserted sensors: " + this.numberOfInsertedSensors);
        sb.append(" New service references: " + this.numberOfInsertedServiceReferences);
        sb.append(" Inserted Sensors: " + Arrays.toString(this.insertedSensors.toArray()));
        return sb.toString();
    }

}
