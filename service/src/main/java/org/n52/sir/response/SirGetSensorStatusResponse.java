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

import java.util.Collection;

import net.opengis.gml.x32.TimeInstantType;
import net.opengis.gml.x32.TimePositionType;
import net.opengis.swe.x101.UomPropertyType;

import org.n52.oss.sir.api.SirStatusDescription;
import org.n52.oss.util.XmlTools;
import org.n52.sir.SirConfigurator;
import org.n52.sir.util.GMLDateParser;
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
public class SirGetSensorStatusResponse extends AbstractXmlResponse {

    private static Logger log = LoggerFactory.getLogger(SirGetSensorStatusResponse.class);

    /**
     * the sensor status descriptions
     */
    private Collection<SirStatusDescription> statusDescs;

    @Override
    public GetSensorStatusResponseDocument createXml() {
        GetSensorStatusResponseDocument document = GetSensorStatusResponseDocument.Factory.newInstance();
        GetSensorStatusResponse getSensStatResp = document.addNewGetSensorStatusResponse();

        for (SirStatusDescription statDesc : this.statusDescs) {
            StatusDescription statusDescription = getSensStatResp.addNewStatusDescription();
            statusDescription.setSensorIDInSIR(statDesc.getSensorId());
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
     * @return the statusDescs
     */
    public Collection<SirStatusDescription> getStatusDescs() {
        return this.statusDescs;
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
