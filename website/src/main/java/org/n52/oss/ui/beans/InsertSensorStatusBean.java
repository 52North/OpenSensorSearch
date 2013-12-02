/**
 * ﻿Copyright (C) 2013 52°North Initiative for Geospatial Open Source Software GmbH
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
package org.n52.oss.ui.beans;

import net.opengis.gml.x32.TimeInstantType;
import net.opengis.gml.x32.TimePositionType;
import net.opengis.swe.x101.UomPropertyType;

import org.n52.oss.util.XmlTools;
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

    private String sensorIdValue = "";

    private String timestamp = "";

    private String uom = "";

    @Override
    public void buildRequest() {
        this.responseString = "";

        InsertSensorStatusRequestDocument requestDoc = InsertSensorStatusRequestDocument.Factory.newInstance();
        InsertSensorStatusRequest request = requestDoc.addNewInsertSensorStatusRequest();
        request.setService(ClientConstants.SERVICE_NAME);
        request.setVersion(ClientConstants.getServiceVersionEnum());

        StatusDescription statusDesc = request.addNewStatusDescription();

        if ( !this.sensorIdValue.isEmpty()) {
            statusDesc.setSensorIDInSIR(this.sensorIdValue);
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

    public String getPropertyName() {
        return this.propertyName;
    }

    public String getPropertyValue() {
        return this.propertyValue;
    }

    public String getSensorIdValue() {
        return this.sensorIdValue;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public String getUom() {
        return this.uom;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public void setSensorIdValue(String sensorIdValue) {
        this.sensorIdValue = sensorIdValue;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

}
