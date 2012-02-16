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

import de.uniMuenster.swsl.sir.DeleteSensorInfoResponseDocument;
import de.uniMuenster.swsl.sir.DeleteSensorInfoResponseDocument.DeleteSensorInfoResponse;
import de.uniMuenster.swsl.sir.DeleteSensorInfoResponseDocument.DeleteSensorInfoResponse.DeletedSensors;

/**
 * @author Daniel Nüst
 * 
 */
public class SirDeleteSensorInfoResponse implements ISirResponse {

    private static Logger log = LoggerFactory.getLogger(SirDeleteSensorInfoResponse.class);

    private int numberOfDeletedSensors = 0;

    private int numberOfDeletedServiceReferences = 0;

    private Collection<String> deletedSensors = new ArrayList<String>();

    /**
     * @return the numberOfNewSensors
     */
    public int getNumberOfDeletedSensors() {
        return this.numberOfDeletedSensors;
    }

    /**
     * @param numberOfDeletedSensors
     *        the numberOfDeletedSensors to set
     */
    public void setNumberOfDeletedSensors(int numberOfDeletedSensors) {
        this.numberOfDeletedSensors = numberOfDeletedSensors;
    }

    /**
     * @return the numberOfNewServiceReferences
     */
    public int getNumberOfDeletedServiceReferences() {
        return this.numberOfDeletedServiceReferences;
    }

    /**
     * @param numberOfDeletedServiceReferences
     *        the numberOfDeletedServiceReferences to set
     */
    public void setNumberOfDeletedServiceReferences(int numberOfDeletedServiceReferences) {
        this.numberOfDeletedServiceReferences = numberOfDeletedServiceReferences;
    }

    /**
     * @return the deletedSensors
     */
    public Collection<String> getDeletedSensors() {
        return this.deletedSensors;
    }

    /**
     * @param deletedSensors
     *        the deletedSensors to set
     */
    public void setDeletedSensors(Collection<String> insertedSensors) {
        this.deletedSensors = insertedSensors;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SirDeleteSensorInfoResponse: ");
        sb.append("\n# of deleted sensors: " + this.numberOfDeletedSensors);
        sb.append("\nDeleted service references: " + this.numberOfDeletedServiceReferences);
        sb.append("\nDeleted Sensors: " + Arrays.toString(this.deletedSensors.toArray()));
        return sb.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.response.ISirResponse#getByteArray()
     */
    @Override
    public byte[] getByteArray() throws IOException, TransformerException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DeleteSensorInfoResponseDocument resp = parseToResponseDocument();
        resp.save(baos, XmlTools.xmlOptionsForNamespaces());
        byte[] bytes = baos.toByteArray();
        return bytes;
    }

    private DeleteSensorInfoResponseDocument parseToResponseDocument() {
        DeleteSensorInfoResponseDocument document = DeleteSensorInfoResponseDocument.Factory.newInstance();
        DeleteSensorInfoResponse delteSensInfoResp = document.addNewDeleteSensorInfoResponse();

        delteSensInfoResp.setNumberOfDeletedSensors(this.numberOfDeletedSensors);
        delteSensInfoResp.setNumberOfDeletedServiceReferences(this.numberOfDeletedServiceReferences);
        DeletedSensors deletedSensor = delteSensInfoResp.addNewDeletedSensors();
        for (String inSens : this.deletedSensors) {
            deletedSensor.addSensorIDInSIR(inSens);
        }

        XmlTools.addSirAndSensorMLSchemaLocation(delteSensInfoResp);

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
