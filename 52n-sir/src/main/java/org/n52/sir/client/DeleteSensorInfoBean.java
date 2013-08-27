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

import org.n52.sir.SirConfigurator;
import org.n52.sir.SirConstants;
import org.n52.sir.util.XmlTools;
import org.x52North.sir.x032.DeleteSensorInfoRequestDocument;
import org.x52North.sir.x032.DeleteSensorInfoRequestDocument.DeleteSensorInfoRequest;
import org.x52North.sir.x032.DeleteSensorInfoRequestDocument.DeleteSensorInfoRequest.InfoToBeDeleted;
import org.x52North.sir.x032.SensorIdentificationDocument.SensorIdentification;
import org.x52North.sir.x032.ServiceReferenceDocument.ServiceReference;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class DeleteSensorInfoBean extends TestClientBean {

    private String deleteRefSensorID = "";

    private String deleteRefType = "";

    private String deleteRefURL = "";

    private boolean deleteSensor = false;

    private String sensorIDinSIR = "";

    private String serviceSpecificSensorID = "";

    private String serviceType = "";

    private String serviceURL = "";

    @Override
    public void buildRequest() {
        DeleteSensorInfoRequestDocument requestDoc = DeleteSensorInfoRequestDocument.Factory.newInstance(XmlTools.xmlOptionsForNamespaces());
        DeleteSensorInfoRequest request = requestDoc.addNewDeleteSensorInfoRequest();
        request.setService(SirConstants.SERVICE_NAME);
        request.setVersion(SirConfigurator.getInstance().getServiceVersionEnum());

        // InfoToBeInserted
        InfoToBeDeleted infoToBeDeleted = request.addNewInfoToBeDeleted();
        SensorIdentification sensorIdentification = infoToBeDeleted.addNewSensorIdentification();

        if ( !this.sensorIDinSIR.isEmpty()) {
            sensorIdentification.setSensorIDInSIR(this.sensorIDinSIR);
        }

        // ServiceReference
        else if ( !this.serviceType.isEmpty() && !this.serviceType.isEmpty() && !this.serviceSpecificSensorID.isEmpty()) {
            ServiceReference serviceRef = sensorIdentification.addNewServiceReference();
            serviceRef.setServiceURL(this.serviceURL);
            serviceRef.setServiceType(this.serviceType);
            serviceRef.setServiceSpecificSensorID(this.serviceSpecificSensorID);
        }

        else {
            this.requestString = "Some kind of sensor identification is required!";
        }

        // delete sensor
        if (this.deleteSensor) {
            infoToBeDeleted.setDeleteSensor(this.deleteSensor);
        }
        // delete service reference
        else if ( !this.sensorIDinSIR.isEmpty()) {
            if ( !this.deleteRefURL.isEmpty() && !this.deleteRefType.isEmpty() && !this.deleteRefSensorID.isEmpty()) {
                ServiceReference serviceRef = infoToBeDeleted.addNewServiceInfo().addNewServiceReference();
                serviceRef.setServiceURL(this.deleteRefURL);
                serviceRef.setServiceType(this.deleteRefType);
                serviceRef.setServiceSpecificSensorID(this.deleteRefSensorID);
            }
        }

        XmlTools.addSirAndSensorMLSchemaLocation(request);

        if ( !requestDoc.validate(XmlTools.xmlOptionsForNamespaces()))
            this.requestString = XmlTools.validateAndIterateErrors(requestDoc);
        else
            this.requestString = requestDoc.xmlText(XmlTools.xmlOptionsForNamespaces());
    }

    /**
     * @return the deleteRefSensorID
     */
    public String getDeleteRefSensorID() {
        return this.deleteRefSensorID;
    }

    /**
     * @return the deleteRefType
     */
    public String getDeleteRefType() {
        return this.deleteRefType;
    }

    /**
     * @return the deleteRefURL
     */
    public String getDeleteRefURL() {
        return this.deleteRefURL;
    }

    /**
     * @return the sensorIDinSIR
     */
    public String getSensorIDinSIR() {
        return this.sensorIDinSIR;
    }

    /**
     * @return the serviceInfosServiceSpecificSensorID
     */
    public String getServiceSpecificSensorID() {
        return this.serviceSpecificSensorID;
    }

    /**
     * @return the serviceInfosServiceType
     */
    public String getServiceType() {
        return this.serviceType;
    }

    /**
     * @return the serviceInfosServiceURL
     */
    public String getServiceURL() {
        return this.serviceURL;
    }

    /**
     * @return the deleteSensor
     */
    public boolean isDeleteSensor() {
        return this.deleteSensor;
    }

    /**
     * @param deleteRefSensorID
     *        the deleteRefSensorID to set
     */
    public void setDeleteRefSensorID(String deleteRefSensorID) {
        this.deleteRefSensorID = deleteRefSensorID;
    }

    /**
     * @param deleteRefType
     *        the deleteRefType to set
     */
    public void setDeleteRefType(String deleteRefType) {
        this.deleteRefType = deleteRefType;
    }

    /**
     * @param deleteRefURL
     *        the deleteRefURL to set
     */
    public void setDeleteRefURL(String deleteRefURL) {
        this.deleteRefURL = deleteRefURL;
    }

    /**
     * @param deleteSensor
     *        the deleteSensor to set
     */
    public void setDeleteSensor(boolean deleteSensor) {
        this.deleteSensor = deleteSensor;
    }

    /**
     * @param sensorIDinSIR
     *        the sensorIDinSIR to set
     */
    public void setSensorIDinSIR(String sensorIDinSIR) {
        this.sensorIDinSIR = sensorIDinSIR;
    }

    /**
     * @param serviceInfosServiceSpecificSensorID
     *        the serviceInfosServiceSpecificSensorID to set
     */
    public void setServiceSpecificSensorID(String serviceInfosServiceSpecificSensorID) {
        this.serviceSpecificSensorID = serviceInfosServiceSpecificSensorID;
    }

    /**
     * @param serviceInfosServiceType
     *        the serviceInfosServiceType to set
     */
    public void setServiceType(String serviceInfosServiceType) {
        this.serviceType = serviceInfosServiceType;
    }

    /**
     * @param serviceInfosServiceURL
     *        the serviceInfosServiceURL to set
     */
    public void setServiceURL(String serviceInfosServiceURL) {
        this.serviceURL = serviceInfosServiceURL;
    }

}
