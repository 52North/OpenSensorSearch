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

import net.opengis.gml.x32.TimeInstantType;
import net.opengis.gml.x32.TimePositionType;
import net.opengis.swe.x101.UomPropertyType;

import org.n52.sir.SirConfigurator;
import org.n52.sir.SirConstants;
import org.n52.sir.util.XmlTools;
import org.x52North.sir.x032.InsertSensorStatusRequestDocument;
import org.x52North.sir.x032.InsertSensorStatusRequestDocument.InsertSensorStatusRequest;
import org.x52North.sir.x032.StatusDescriptionDocument.StatusDescription;
import org.x52North.sir.x032.StatusDocument.Status;

/**
 * @author Jan Schulte
 * 
 */
public class InsertSensorStatusBean extends TestClientBean {

    private static final String TIME_STAMP_ID = "status_time_stamp";

    private String propertyName = "";

    private String propertyValue = "";

    private String sensorIDInSIRValue = "";

    private String timestamp = "";

    private String uom = "";

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.client.beans.AbstractBean#buildRequest()
     */
    @Override
    public void buildRequest() {
        this.responseString = "";

        InsertSensorStatusRequestDocument requestDoc = InsertSensorStatusRequestDocument.Factory.newInstance();
        InsertSensorStatusRequest request = requestDoc.addNewInsertSensorStatusRequest();
        request.setService(SirConstants.SERVICE_NAME);
        request.setVersion(SirConfigurator.getInstance().getServiceVersionEnum());

        StatusDescription statusDesc = request.addNewStatusDescription();

        // sensorIDInSIR
        if ( !this.sensorIDInSIRValue.isEmpty()) {
            statusDesc.setSensorIDInSIR(this.sensorIDInSIRValue);
        }

        // status
        Status status = statusDesc.addNewStatus();
        status.setPropertyValue(this.propertyValue);
        status.setPropertyName(this.propertyName);

        // uom
        if ( !this.uom.isEmpty()) {
            UomPropertyType propertyType = UomPropertyType.Factory.newInstance();
            propertyType.setCode(this.uom);
            status.setUom(propertyType);
        }

        // timestamp
        if ( !this.timestamp.isEmpty()) {
            TimeInstantType timeInstantType = TimeInstantType.Factory.newInstance();
            timeInstantType.setId(TIME_STAMP_ID);
            TimePositionType timePosition = timeInstantType.addNewTimePosition();
            timePosition.setStringValue(this.timestamp);
            status.setTimestamp(timeInstantType);
        }

        XmlTools.addSirAndSensorMLSchemaLocation(request);

        if (requestDoc.validate())
            this.requestString = requestDoc.xmlText(XmlTools.xmlOptionsForNamespaces());
        else
            this.requestString = XmlTools.validateAndIterateErrors(requestDoc);
    }

    /**
     * @return the propertyName
     */
    public String getPropertyName() {
        return this.propertyName;
    }

    /**
     * @return the propertyValue
     */
    public String getPropertyValue() {
        return this.propertyValue;
    }

    /**
     * @return the sensorIDInSIRValue
     */
    public String getSensorIDInSIRValue() {
        return this.sensorIDInSIRValue;
    }

    /**
     * @return the timestamp
     */
    public String getTimestamp() {
        return this.timestamp;
    }

    /**
     * @return the uom
     */
    public String getUom() {
        return this.uom;
    }

    /**
     * @param propertyName
     *        the propertyName to set
     */
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * @param propertyValue
     *        the propertyValue to set
     */
    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    /**
     * @param sensorIDInSIRValue
     *        the sensorIDInSIRValue to set
     */
    public void setSensorIDInSIRValue(String sensorIDInSIRValue) {
        this.sensorIDInSIRValue = sensorIDInSIRValue;
    }

    /**
     * @param timestamp
     *        the timestamp to set
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @param uom
     *        the uom to set
     */
    public void setUom(String uom) {
        this.uom = uom;
    }

}
