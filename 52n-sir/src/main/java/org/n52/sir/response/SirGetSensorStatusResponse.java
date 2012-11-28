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
import java.util.Collection;

import javax.xml.transform.TransformerException;

import net.opengis.gml.x32.TimeInstantType;
import net.opengis.gml.x32.TimePositionType;
import net.opengis.swe.x101.UomPropertyType;

import org.n52.sir.SirConfigurator;
import org.n52.sir.SirConstants;
import org.n52.sir.datastructure.SirStatusDescription;
import org.n52.sir.util.GMLDateParser;
import org.n52.sir.util.XmlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sir.x032.GetSensorStatusResponseDocument;
import org.x52North.sir.x032.GetSensorStatusResponseDocument.GetSensorStatusResponse;
import org.x52North.sir.x032.StatusDescriptionDocument.StatusDescription;
import org.x52North.sir.x032.StatusDocument.Status;

/**
 * Internal response to get the sensor status
 * 
 * was orginally opengis.gml.x32.TimeInstantType
 * 
 * @author Jan Schulte
 * 
 */
public class SirGetSensorStatusResponse implements ISirResponse {

    private static Logger log = LoggerFactory.getLogger(SirGetSensorStatusResponse.class);

    /**
     * the sensor status descriptions
     */
    private Collection<SirStatusDescription> statusDescs;

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.response.ISirResponse#getByteArray()
     */
    @Override
    public byte[] getByteArray() throws IOException, TransformerException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GetSensorStatusResponseDocument getSensStatRespDoc = parseToResponseDocument();
        getSensStatRespDoc.save(baos, XmlTools.xmlOptionsForNamespaces());
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
     * @return the statusDescs
     */
    public Collection<SirStatusDescription> getStatusDescs() {
        return this.statusDescs;
    }

    private GetSensorStatusResponseDocument parseToResponseDocument() {
        GetSensorStatusResponseDocument document = GetSensorStatusResponseDocument.Factory.newInstance();
        GetSensorStatusResponse getSensStatResp = document.addNewGetSensorStatusResponse();

        for (SirStatusDescription statDesc : this.statusDescs) {
            StatusDescription statusDescription = getSensStatResp.addNewStatusDescription();
            statusDescription.setSensorIDInSIR(statDesc.getSensorIdInSir());
            Status status = statusDescription.addNewStatus();

            // property name
            status.setPropertyName(statDesc.getStatus().getPropertyName());

            // property value
            status.setPropertyValue(statDesc.getStatus().getPropertyValue());

            // uom
            if ( !statDesc.getStatus().getUom().isEmpty()) {
                UomPropertyType uom = UomPropertyType.Factory.newInstance();
                uom.setCode(statDesc.getStatus().getUom());
                status.setUom(uom);
            }

            TimeInstantType timeInstantType = TimeInstantType.Factory.newInstance();
            TimePositionType timePosition = timeInstantType.addNewTimePosition();
            timePosition.setStringValue(GMLDateParser.getInstance().parseDate(statDesc.getStatus().getTimestamp()));
            timeInstantType.setId(XmlTools.generateGmlID(timeInstantType));

            status.setTimestamp(timeInstantType);
        }

        XmlTools.addSirAndSensorMLSchemaLocation(getSensStatResp);

        if (SirConfigurator.getInstance().isValidateResponses()) {
            if ( !document.validate())
                log.warn("Service created invalid document!\n" + XmlTools.validateAndIterateErrors(document));
        }

        return document;
    }

    /**
     * @param statusDescs
     *        the statusDescs to set
     */
    public void setStatusDescs(Collection<SirStatusDescription> statusDescs) {
        this.statusDescs = statusDescs;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SirGetSensorStatusResponse: ");
        sb.append("StatusDescriptions: " + this.statusDescs);
        return sb.toString();
    }

}
