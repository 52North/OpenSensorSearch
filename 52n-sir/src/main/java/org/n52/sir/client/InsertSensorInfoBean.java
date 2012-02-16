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
package org.n52.sir.client;

import net.opengis.sensorML.x101.AbstractProcessType;
import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.sensorML.x101.SensorMLDocument.SensorML.Member;
import net.opengis.sensorML.x101.SystemType;

import org.apache.xmlbeans.XmlException;
import org.n52.sir.SirConfigurator;
import org.n52.sir.SirConstants;
import org.n52.sir.util.XmlTools;

import de.uniMuenster.swsl.sir.InsertSensorInfoRequestDocument;
import de.uniMuenster.swsl.sir.InsertSensorInfoRequestDocument.InsertSensorInfoRequest;
import de.uniMuenster.swsl.sir.InsertSensorInfoRequestDocument.InsertSensorInfoRequest.InfoToBeInserted;
import de.uniMuenster.swsl.sir.ServiceReferenceDocument.ServiceReference;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class InsertSensorInfoBean extends AbstractBean {

    private String sensorDescription = "";

    private String serviceInfosServiceURL = "";

    private String serviceInfosServiceType = "";

    private String serviceInfosServiceSpecificSensorID = "";

    private String addRefURL = "";

    private String addRefType = "";

    private String addRefSensorID = "";

    private String sensorIDinSIR = "";

    /**
     * @return the sensorDescription
     */
    public String getSensorDescription() {
        return this.sensorDescription;
    }

    /**
     * @param sensorDescription
     *        the sensorDescription to set
     */
    public void setSensorDescription(String sensorDescription) {
        this.sensorDescription = sensorDescription;
    }

    /**
     * @return the sensorIDinSIR
     */
    public String getSensorIDinSIR() {
        return this.sensorIDinSIR;
    }

    /**
     * @param sensorIDinSIR
     *        the sensorIDinSIR to set
     */
    public void setSensorIDinSIR(String sensorIDinSIR) {
        this.sensorIDinSIR = sensorIDinSIR;
    }
    
    /**
     * @return the serviceInfosServiceURL
     */
    public String getServiceInfosServiceURL() {
        return this.serviceInfosServiceURL;
    }

    /**
     * @return the serviceInfosServiceType
     */
    public String getServiceInfosServiceType() {
        return this.serviceInfosServiceType;
    }

    /**
     * @return the serviceInfosServiceSpecificSensorID
     */
    public String getServiceInfosServiceSpecificSensorID() {
        return this.serviceInfosServiceSpecificSensorID;
    }

    /**
     * @return the addRefURL
     */
    public String getAddRefURL() {
        return this.addRefURL;
    }

    /**
     * @return the addRefType
     */
    public String getAddRefType() {
        return this.addRefType;
    }

    /**
     * @return the addRefSensorID
     */
    public String getAddRefSensorID() {
        return this.addRefSensorID;
    }

    /**
     * @param serviceInfosServiceURL the serviceInfosServiceURL to set
     */
    public void setServiceInfosServiceURL(String serviceInfosServiceURL) {
        this.serviceInfosServiceURL = serviceInfosServiceURL;
    }

    /**
     * @param serviceInfosServiceType the serviceInfosServiceType to set
     */
    public void setServiceInfosServiceType(String serviceInfosServiceType) {
        this.serviceInfosServiceType = serviceInfosServiceType;
    }

    /**
     * @param serviceInfosServiceSpecificSensorID the serviceInfosServiceSpecificSensorID to set
     */
    public void setServiceInfosServiceSpecificSensorID(String serviceInfosServiceSpecificSensorID) {
        this.serviceInfosServiceSpecificSensorID = serviceInfosServiceSpecificSensorID;
    }

    /**
     * @param addRefURL the addRefURL to set
     */
    public void setAddRefURL(String addRefURL) {
        this.addRefURL = addRefURL;
    }

    /**
     * @param addRefType the addRefType to set
     */
    public void setAddRefType(String addRefType) {
        this.addRefType = addRefType;
    }

    /**
     * @param addRefSensorID the addRefSensorID to set
     */
    public void setAddRefSensorID(String addRefSensorID) {
        this.addRefSensorID = addRefSensorID;
    }

    @Override
    public void buildRequest() {
        InsertSensorInfoRequestDocument requestDoc = InsertSensorInfoRequestDocument.Factory.newInstance(XmlTools.xmlOptionsForNamespaces());
        InsertSensorInfoRequest request = requestDoc.addNewInsertSensorInfoRequest();
        request.setService(SirConstants.SERVICE_NAME);
        request.setVersion(SirConfigurator.getInstance().getServiceVersionEnum());

        // InfoToBeInserted
        InfoToBeInserted infoToBeInserted = request.addNewInfoToBeInserted();

        // SensorDescription with optional service reference
        if ( !this.sensorDescription.isEmpty()) {
            this.sensorDescription = this.sensorDescription.trim();

            try {
                AbstractProcessType sensorDescr = infoToBeInserted.addNewSensorDescription();
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

            // ServiceReference
            if ( !this.serviceInfosServiceType.isEmpty() && !this.serviceInfosServiceType.isEmpty()
                    && !this.serviceInfosServiceSpecificSensorID.isEmpty()) {
                ServiceReference serviceRef = infoToBeInserted.addNewServiceReference();
                // serviceURL
                serviceRef.setServiceURL(this.serviceInfosServiceURL);
                // serviceType
                serviceRef.setServiceType(this.serviceInfosServiceType);
                // serviceSpecificSensorID
                serviceRef.setServiceSpecificSensorID(this.serviceInfosServiceSpecificSensorID);
            }
        }

        // sensor id and service reference
        else if ( !this.sensorIDinSIR.isEmpty()) {
            infoToBeInserted.setSensorIDInSIR(this.sensorIDinSIR);

            if ( !this.addRefURL.isEmpty() && !this.addRefType.isEmpty() && !this.addRefSensorID.isEmpty()) {
                ServiceReference serviceRef = infoToBeInserted.addNewServiceReference();
                // serviceURL
                serviceRef.setServiceURL(this.addRefURL);
                // serviceType
                serviceRef.setServiceType(this.addRefType);
                // serviceSpecificSensorID
                serviceRef.setServiceSpecificSensorID(this.addRefSensorID);
            }
        }

        XmlTools.addSirAndSensorMLSchemaLocation(request);

        if ( !requestDoc.validate(XmlTools.xmlOptionsForNamespaces()))
            this.requestString = XmlTools.validateAndIterateErrors(requestDoc);
        else
            this.requestString = requestDoc.xmlText(XmlTools.xmlOptionsForNamespaces());
    }

}
