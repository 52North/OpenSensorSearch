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
import java.util.Arrays;
import java.util.Collection;

import javax.xml.transform.TransformerException;

import org.n52.sir.SirConfigurator;
import org.n52.sir.SirConstants;
import org.n52.sir.util.XmlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniMuenster.swsl.sir.UpdateSensorDescriptionResponseDocument;
import de.uniMuenster.swsl.sir.UpdateSensorDescriptionResponseDocument.UpdateSensorDescriptionResponse;
import de.uniMuenster.swsl.sir.UpdateSensorDescriptionResponseDocument.UpdateSensorDescriptionResponse.UpdatedSensors;

/**
 * @author Daniel Nüst
 * 
 */
public class SirUpdateSensorDescriptionResponse implements ISirResponse {

    private static Logger log = LoggerFactory.getLogger(SirUpdateSensorDescriptionResponse.class);

    private int numberOfUpdatedSensorDescriptions;

    private Collection<String> updatedSensors;

    /**
     * @return the numberOfNewSensors
     */
    public int getNumberOfUpdatedSensorDescriptions() {
        return this.numberOfUpdatedSensorDescriptions;
    }

    /**
     * @param numberOfNewSensors
     *        the numberOfNewSensors to set
     */
    public void setNumberOfUpdatedSensorDescriptions(int numberOfNewSensors) {
        this.numberOfUpdatedSensorDescriptions = numberOfNewSensors;
    }

    /**
     * @return the insertedSensors
     */
    public Collection<String> getUpdatedSensors() {
        return this.updatedSensors;
    }

    /**
     * @param insertedSensors
     *        the insertedSensors to set
     */
    public void setUpdatedSensors(Collection<String> updatedSensors) {
        this.updatedSensors = updatedSensors;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SirUpdateSensorDescriptionResponse: ");
        sb.append("\n#Sensors: " + this.numberOfUpdatedSensorDescriptions);
        sb.append("\nUpdated Sensors: " + Arrays.toString(this.updatedSensors.toArray()));
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
        UpdateSensorDescriptionResponseDocument updSensDescrResp = parseToResponseDocument();
        updSensDescrResp.save(baos, XmlTools.xmlOptionsForNamespaces());
        byte[] bytes = baos.toByteArray();
        return bytes;
    }

    private UpdateSensorDescriptionResponseDocument parseToResponseDocument() {
        UpdateSensorDescriptionResponseDocument document = UpdateSensorDescriptionResponseDocument.Factory.newInstance();
        UpdateSensorDescriptionResponse updSensDescrResp = document.addNewUpdateSensorDescriptionResponse();

        updSensDescrResp.setNumberOfUpdatedSensors(this.numberOfUpdatedSensorDescriptions);
        UpdatedSensors updatedSensorsXB = updSensDescrResp.addNewUpdatedSensors();
        for (String inSens : this.updatedSensors) {
            updatedSensorsXB.addSensorIDInSIR(inSens);
        }

        XmlTools.addSirAndSensorMLSchemaLocation(updSensDescrResp);

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
