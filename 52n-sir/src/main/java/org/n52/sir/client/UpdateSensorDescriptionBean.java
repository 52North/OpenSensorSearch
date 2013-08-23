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

import net.opengis.sensorML.x101.AbstractProcessType;
import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.sensorML.x101.SensorMLDocument.SensorML.Member;
import net.opengis.sensorML.x101.SystemType;

import org.apache.xmlbeans.XmlException;
import org.n52.sir.SirConfigurator;
import org.n52.sir.SirConstants;
import org.n52.sir.util.Tools;
import org.n52.sir.util.XmlTools;
import org.x52North.sir.x032.ServiceReferenceDocument.ServiceReference;
import org.x52North.sir.x032.UpdateSensorDescriptionRequestDocument;
import org.x52North.sir.x032.UpdateSensorDescriptionRequestDocument.UpdateSensorDescriptionRequest;
import org.x52North.sir.x032.UpdateSensorDescriptionRequestDocument.UpdateSensorDescriptionRequest.SensorDescriptionToBeUpdated;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class UpdateSensorDescriptionBean extends TestClientBean {

    private String sensorDescription = "";

    private String sensorIDinSIR = "";

    private String serviceSpecificSensorID = "";

    private String serviceType = "";

    private String serviceURL = "";

    @Override
    public void buildRequest() {
        UpdateSensorDescriptionRequestDocument requestDoc = UpdateSensorDescriptionRequestDocument.Factory.newInstance(XmlTools.xmlOptionsForNamespaces());
        UpdateSensorDescriptionRequest request = requestDoc.addNewUpdateSensorDescriptionRequest();
        request.setService(SirConstants.SERVICE_NAME);
        request.setVersion(SirConfigurator.getInstance().getServiceVersionEnum());

        // SensorDescriptionToBeUpdated
        SensorDescriptionToBeUpdated desrcToBeUpdated = request.addNewSensorDescriptionToBeUpdated();

        if ( !this.sensorDescription.isEmpty()) {
            this.sensorDescription = this.sensorDescription.trim();

            try {
                AbstractProcessType sensorDescr = desrcToBeUpdated.addNewSensorDescription();
                SystemType system = (SystemType) sensorDescr.changeType(SystemType.type);
                SensorMLDocument doc = SensorMLDocument.Factory.parse(this.sensorDescription);
                Member member = doc.getSensorML().getMemberArray(0);
                system.set(member.getProcess());
            }
            catch (XmlException e) {
                this.requestString = "Please check the sensor description, it must be a sml:SensorML document.";
                this.requestString += "\n\n" + e.getMessage();
                return;
            }
        }

        // sensor identification
        if ( !this.sensorIDinSIR.isEmpty()) {
            // by id
            desrcToBeUpdated.addNewSensorIdentification().setSensorIDInSIR(this.sensorIDinSIR);
        }
        else {
            // by service reference
            if (Tools.noneEmpty(new String[] {this.serviceType, this.serviceType, this.serviceSpecificSensorID})) {
                ServiceReference serviceRef = desrcToBeUpdated.addNewSensorIdentification().addNewServiceReference();
                // serviceURL
                serviceRef.setServiceURL(this.serviceURL);
                // serviceType
                serviceRef.setServiceType(this.serviceType);
                // serviceSpecificSensorID
                serviceRef.setServiceSpecificSensorID(this.serviceSpecificSensorID);
            }
        }

        XmlTools.addSirAndSensorMLSchemaLocation(request);

        if ( !requestDoc.validate(XmlTools.xmlOptionsForNamespaces()))
            this.requestString = XmlTools.validateAndIterateErrors(requestDoc);
        else
            this.requestString = requestDoc.xmlText(XmlTools.xmlOptionsForNamespaces());
    }

    /**
     * @return the sensorDescription
     */
    public String getSensorDescription() {
        return this.sensorDescription;
    }

    /**
     * @return the sensorIDinSIR
     */
    public String getSensorIDinSIR() {
        return this.sensorIDinSIR;
    }

    /**
     * @return the serviceSpecificSensorID
     */
    public String getServiceSpecificSensorID() {
        return this.serviceSpecificSensorID;
    }

    /**
     * @return the serviceType
     */
    public String getServiceType() {
        return this.serviceType;
    }

    /**
     * @return the serviceURL
     */
    public String getServiceURL() {
        return this.serviceURL;
    }

    /**
     * @param sensorDescription
     *        the sensorDescription to set
     */
    public void setSensorDescription(String sensorDescription) {
        this.sensorDescription = sensorDescription;
    }

    /**
     * @param sensorIDinSIR
     *        the sensorIDinSIR to set
     */
    public void setSensorIDinSIR(String sensorIDinSIR) {
        this.sensorIDinSIR = sensorIDinSIR;
    }

    /**
     * @param serviceSpecificSensorID
     *        the serviceSpecificSensorID to set
     */
    public void setServiceSpecificSensorID(String serviceInfosServiceSpecificSensorID) {
        this.serviceSpecificSensorID = serviceInfosServiceSpecificSensorID;
    }

    /**
     * @param serviceType
     *        the serviceType to set
     */
    public void setServiceType(String serviceInfosServiceType) {
        this.serviceType = serviceInfosServiceType;
    }

    /**
     * @param serviceURL
     *        the serviceURL to set
     */
    public void setServiceURL(String serviceInfosServiceURL) {
        this.serviceURL = serviceInfosServiceURL;
    }

}
