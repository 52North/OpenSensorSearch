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

import org.n52.sir.SirConfigurator;
import org.n52.sir.SirConstants;
import org.n52.sir.util.XmlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sir.x032.InsertSensorStatusResponseDocument;
import org.x52North.sir.x032.InsertSensorStatusResponseDocument.InsertSensorStatusResponse;

/**
 * @author Jan Schulte
 * 
 */
public class SirInsertSensorStatusResponse implements ISirResponse {

    private static Logger log = LoggerFactory.getLogger(SirInsertSensorStatusResponse.class);

    private String sensorIdInSir;

    private boolean statusUpdateSuccessful;

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.response.ISirResponse#getByteArray()
     */
    @Override
    public byte[] getByteArray() throws IOException, TransformerException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InsertSensorStatusResponseDocument insertSensStatRespDoc = parseToResponseDocument();
        insertSensStatRespDoc.save(baos, XmlTools.xmlOptionsForNamespaces());
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
     * @return the sensorIdInSir
     */
    public String getSensorIdInSir() {
        return this.sensorIdInSir;
    }

    /**
     * @return the statusUpdateSuccessful
     */
    public boolean isStatusUpdateSuccessful() {
        return this.statusUpdateSuccessful;
    }

    private InsertSensorStatusResponseDocument parseToResponseDocument() {
        InsertSensorStatusResponseDocument document = InsertSensorStatusResponseDocument.Factory.newInstance();
        InsertSensorStatusResponse insSensStatResp = document.addNewInsertSensorStatusResponse();

        // set sensorID in SIR
        insSensStatResp.setSensorIDInSIR(this.sensorIdInSir);

        XmlTools.addSirAndSensorMLSchemaLocation(insSensStatResp);

        if (SirConfigurator.getInstance().isValidateResponses()) {
            if ( !document.validate())
                log.warn("Service created invalid document!\n" + XmlTools.validateAndIterateErrors(document));
        }

        return document;
    }

    /**
     * @param sensorIdInSir
     *        the sensorIdInSir to set
     */
    public void setSensorIdInSir(String sensorIdInSir) {
        this.sensorIdInSir = sensorIdInSir;
    }

    /**
     * @param statusUpdateSuccessful
     *        the statusUpdateSuccessful to set
     */
    public void setStatusUpdateSuccessful(boolean statusUpdateSuccessful) {
        this.statusUpdateSuccessful = statusUpdateSuccessful;
    }

}
