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
import org.n52.sir.util.Tools;
import org.n52.sir.util.XmlTools;

import de.uniMuenster.swsl.sir.ServiceReferenceDocument.ServiceReference;
import de.uniMuenster.swsl.sir.UpdateSensorDescriptionRequestDocument;
import de.uniMuenster.swsl.sir.UpdateSensorDescriptionRequestDocument.UpdateSensorDescriptionRequest;
import de.uniMuenster.swsl.sir.UpdateSensorDescriptionRequestDocument.UpdateSensorDescriptionRequest.SensorDescriptionToBeUpdated;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class UpdateSensorDescriptionBean extends AbstractBean {

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
            if (Tools.noneEmpty(new String[] {this.serviceType,
                                              this.serviceType,
                                              this.serviceSpecificSensorID})) {
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
