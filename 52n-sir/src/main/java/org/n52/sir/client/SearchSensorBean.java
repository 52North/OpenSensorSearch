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

import java.util.ArrayList;
import java.util.List;

import net.opengis.gml.x32.TimeInstantType;
import net.opengis.gml.x32.TimePeriodType;
import net.opengis.gml.x32.TimePositionType;
import net.opengis.ows.x11.BoundingBoxType;
import net.opengis.swe.x101.UomPropertyType;

import org.n52.sir.SirConfigurator;
import org.n52.sir.SirConstants;
import org.n52.sir.datastructure.SirSearchCriteria_Phenomenon.SirMatchingType;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.util.Tools;
import org.n52.sir.util.XmlTools;

import de.uniMuenster.swsl.sir.SearchCriteriaDocument.SearchCriteria;
import de.uniMuenster.swsl.sir.SearchCriteriaDocument.SearchCriteria.Phenomenon;
import de.uniMuenster.swsl.sir.SearchCriteriaDocument.SearchCriteria.Phenomenon.SORParameters;
import de.uniMuenster.swsl.sir.SearchSensorRequestDocument;
import de.uniMuenster.swsl.sir.SearchSensorRequestDocument.SearchSensorRequest;
import de.uniMuenster.swsl.sir.SensorIdentificationDocument.SensorIdentification;
import de.uniMuenster.swsl.sir.ServiceCriteriaDocument.ServiceCriteria;
import de.uniMuenster.swsl.sir.ServiceReferenceDocument.ServiceReference;

/**
 * @author Jan Schulte
 * 
 */
public class SearchSensorBean extends AbstractBean {

    private String sensorIDInSIRValue = "";

    private String serviceURL = "";

    private String serviceType = "";

    private String serviceSpecificSensorID = "";

    private String serviceCriteriaURL = "";

    private String serviceCriteriaType = "";

    private String searchText = "";

    private String phenomenonName = "";

    private String sorUrl = "";

    private String sorMatchingType = "";

    private String sorSearchDepth = "";

    private String uom = "";

    private String lowerCorner = "";

    private String upperCorner = "";

    private String timePeriodStart = "";

    private String timePeriodEnd = "";

    private boolean simpleResponse = false;

    /**
     * @return the sensorIDInSIRValue
     */
    public String getSensorIDInSIRValue() {
        return this.sensorIDInSIRValue;
    }

    /**
     * @param sensorIDInSIRValue
     *        the sensorIDInSIRValue to set
     */
    public void setSensorIDInSIRValue(String sensorIDInSIRValue) {
        this.sensorIDInSIRValue = sensorIDInSIRValue;
    }

    /**
     * @return the serviceURL
     */
    public String getServiceURL() {
        return this.serviceURL;
    }

    /**
     * @param serviceURL
     *        the serviceURL to set
     */
    public void setServiceURL(String serviceURL) {
        this.serviceURL = serviceURL;
    }

    /**
     * @return the serviceType
     */
    public String getServiceType() {
        return this.serviceType;
    }

    /**
     * @param serviceType
     *        the serviceType to set
     */
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    /**
     * @return the serviceSpecificSensorID
     */
    public String getServiceSpecificSensorID() {
        return this.serviceSpecificSensorID;
    }

    /**
     * @param serviceSpecificSensorID
     *        the serviceSpecificSensorID to set
     */
    public void setServiceSpecificSensorID(String serviceSpecificSensorID) {
        this.serviceSpecificSensorID = serviceSpecificSensorID;
    }

    /**
     * @return the serviceCriteriaURL
     */
    public String getServiceCriteriaURL() {
        return this.serviceCriteriaURL;
    }

    /**
     * @param serviceCriteriaURL
     *        the serviceCriteriaURL to set
     */
    public void setServiceCriteriaURL(String serviceCriteriaURL) {
        this.serviceCriteriaURL = serviceCriteriaURL;
    }

    /**
     * @return the serviceCriteriaType
     */
    public String getServiceCriteriaType() {
        return this.serviceCriteriaType;
    }

    /**
     * @param serviceCriteriaType
     *        the serviceCriteriaType to set
     */
    public void setServiceCriteriaType(String serviceCriteriaType) {
        this.serviceCriteriaType = serviceCriteriaType;
    }

    /**
     * @return the searchText
     */
    public String getSearchText() {
        return this.searchText;
    }

    /**
     * @param searchText
     *        the searchText to set
     */
    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    /**
     * @return the phenomenon
     */
    public String getPhenomenonName() {
        return this.phenomenonName;
    }

    /**
     * @param phenomenon
     *        the phenomenon to set
     */
    public void setPhenomenonName(String phenomenonName) {
        this.phenomenonName = phenomenonName;
    }

    /**
     * @return the sorUrl
     */
    public String getSorUrl() {
        return this.sorUrl;
    }

    /**
     * @return the sorMatchingType
     */
    public String getSorMatchingType() {
        return this.sorMatchingType;
    }

    /**
     * @return the sorSearchDepth
     */
    public String getSorSearchDepth() {
        return this.sorSearchDepth;
    }

    /**
     * @param sorUrl
     *        the sorUrl to set
     */
    public void setSorUrl(String sorUrl) {
        this.sorUrl = sorUrl;
    }

    /**
     * @param sorMatchingType
     *        the sorMatchingType to set
     */
    public void setSorMatchingType(String sorMatchingType) {
        this.sorMatchingType = sorMatchingType;
    }

    /**
     * @param sorSearchDepth
     *        the sorSearchDepth to set
     */
    public void setSorSearchDepth(String sorSearchDepth) {
        this.sorSearchDepth = sorSearchDepth;
    }

    /**
     * @return the uom
     */
    public String getUom() {
        return this.uom;
    }

    /**
     * @param uom
     *        the uom to set
     */
    public void setUom(String uom) {
        this.uom = uom;
    }

    /**
     * @return the lowerCorner
     */
    public String getLowerCorner() {
        return this.lowerCorner;
    }

    /**
     * @param lowerCorner
     *        the lowerCorner to set
     */
    public void setLowerCorner(String lowerCorner) {
        this.lowerCorner = lowerCorner;
    }

    /**
     * @return the upperCorner
     */
    public String getUpperCorner() {
        return this.upperCorner;
    }

    /**
     * @param upperCorner
     *        the upperCorner to set
     */
    public void setUpperCorner(String upperCorner) {
        this.upperCorner = upperCorner;
    }

    /**
     * @return the timePeriodStart
     */
    public String getTimePeriodStart() {
        return this.timePeriodStart;
    }

    /**
     * @param timePeriodStart
     *        the timePeriodStart to set
     */
    public void setTimePeriodStart(String timePeriodStart) {
        this.timePeriodStart = timePeriodStart;
    }

    /**
     * @return the timePeriodEnd
     */
    public String getTimePeriodEnd() {
        return this.timePeriodEnd;
    }

    /**
     * @param timePeriodEnd
     *        the timePeriodEnd to set
     */
    public void setTimePeriodEnd(String timePeriodEnd) {
        this.timePeriodEnd = timePeriodEnd;
    }

    /**
     * @return the simpleResponse
     */
    public boolean isSimpleResponse() {
        return this.simpleResponse;
    }

    /**
     * @param simpleResponse
     *        the simpleResponse to set
     */
    public void setSimpleResponse(boolean simpleResponse) {
        this.simpleResponse = simpleResponse;
    }

    /**
     * 
     * @return
     */
    public SirMatchingType[] getMatchingTypes() {
        return SirMatchingType.values();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.client.beans.AbstractBean#buildRequest()
     */
    @Override
    public void buildRequest() {
        this.responseString = "";

        SearchSensorRequestDocument requestDoc = SearchSensorRequestDocument.Factory.newInstance();
        SearchSensorRequest request = requestDoc.addNewSearchSensorRequest();
        request.setService(SirConstants.SERVICE_NAME);
        request.setVersion(SirConfigurator.getInstance().getServiceVersionEnum());

        SensorIdentification sensIdent = null;

        if ( !this.sensorIDInSIRValue.isEmpty() || !this.serviceURL.isEmpty()) {
            sensIdent = request.addNewSensorIdentification();

            // sensorIDInSIR
            if ( !this.sensorIDInSIRValue.isEmpty()) {
                sensIdent.setSensorIDInSIR(this.sensorIDInSIRValue);
            }

            // serviceDescription
            else if ( !this.serviceURL.isEmpty() && !this.serviceType.isEmpty()
                    && !this.serviceSpecificSensorID.isEmpty()) {
                ServiceReference servRef = sensIdent.addNewServiceReference();
                // serviceURL
                servRef.setServiceURL(this.serviceURL);
                // serviceType
                servRef.setServiceType(this.serviceType);
                // serviceSpecificSensorID
                servRef.setServiceSpecificSensorID(this.serviceSpecificSensorID);
            }
        }

        // searchCriteria
        if (Tools.atLeastOneIsNotEmpty(new String[] {this.serviceCriteriaType,
                                                     this.serviceCriteriaURL,
                                                     this.searchText,
                                                     this.phenomenonName,
                                                     this.uom,
                                                     this.upperCorner,
                                                     this.lowerCorner,
                                                     this.timePeriodEnd,
                                                     this.timePeriodEnd})) {
            SearchCriteria searchCriteria = request.addNewSearchCriteria();

            // serviceCriteria
            if ( !this.serviceCriteriaType.isEmpty() && !this.serviceCriteriaURL.isEmpty()) {
                ServiceCriteria servCriteria = searchCriteria.addNewServiceCriteria();
                // serviceURL
                if ( !this.serviceCriteriaURL.isEmpty()) {
                    servCriteria.setServiceURL(this.serviceCriteriaURL);
                }
                // serviceType
                if ( !this.serviceCriteriaType.isEmpty()) {
                    servCriteria.setServiceType(this.serviceCriteriaType);
                }
            }

            // searchText
            if ( !this.searchText.isEmpty()) {
                searchCriteria.setSearchTextArray(this.searchText.split(";"));
            }

            // phenomenon
            if ( !this.phenomenonName.isEmpty()) {
                Phenomenon phen = searchCriteria.addNewPhenomenon();
                phen.setPhenomenonName(this.phenomenonName);

                if (Tools.noneEmpty(new String[] {this.sorMatchingType, this.sorSearchDepth, this.sorUrl})) {
                    SORParameters sorParams = phen.addNewSORParameters();
                    try {
                        sorParams.setMatchingType(SirMatchingType.getSirMatchingType(this.sorMatchingType).getSchemaMatchingType());
                    }
                    catch (OwsExceptionReport e) {
                        this.requestString = e.getDocument().xmlText();
                        return;
                    }
                    sorParams.setSORURL(this.sorUrl);
                    sorParams.setSearchDepth(Integer.parseInt(this.sorSearchDepth));
                }

            }

            // uom
            if ( !this.uom.isEmpty()) {
                String[] uomArray = this.uom.split(";");
                for (String uomCode : uomArray) {
                    UomPropertyType uomPropertyType = searchCriteria.addNewUom();
                    uomPropertyType.setCode(uomCode);
                }
            }

            // bounding box
            if ( !this.lowerCorner.isEmpty() && !this.upperCorner.isEmpty()) {
                BoundingBoxType boundingBox = searchCriteria.addNewBoundingBox();
                // lower corner
                List<Double> loco = new ArrayList<Double>();
                loco.add(new Double(this.lowerCorner.substring(0, this.lowerCorner.indexOf(" "))));
                loco.add(new Double(this.lowerCorner.substring(this.lowerCorner.indexOf(" ") + 1,
                                                               this.lowerCorner.length())));
                boundingBox.setLowerCorner(loco);
                // upper corner
                List<Double> upco = new ArrayList<Double>();
                upco.add(new Double(this.upperCorner.substring(0, this.upperCorner.indexOf(" "))));
                upco.add(new Double(this.upperCorner.substring(this.upperCorner.indexOf(" ") + 1,
                                                               this.upperCorner.length())));
                boundingBox.setUpperCorner(upco);
            }

            // time
            if (this.timePeriodStart.isEmpty() && !this.timePeriodEnd.isEmpty()) {
                this.requestString = "Please check the start time!";
                return;
            }

            // time instant type
            if ( !this.timePeriodStart.isEmpty() && this.timePeriodEnd.isEmpty()) {
                TimeInstantType timeInstantType = TimeInstantType.Factory.newInstance();
                TimePositionType timePosition = timeInstantType.addNewTimePosition();
                timePosition.setStringValue(this.timePeriodStart);
                searchCriteria.setTime(timeInstantType);
            }

            // time period
            if ( !this.timePeriodStart.isEmpty() && !this.timePeriodEnd.isEmpty()) {
                TimePeriodType timePeriodType = TimePeriodType.Factory.newInstance();
                TimePositionType beginPosition = timePeriodType.addNewBeginPosition();
                TimePositionType endPosition = timePeriodType.addNewEndPosition();
                beginPosition.setStringValue(this.timePeriodStart);
                endPosition.setStringValue(this.timePeriodEnd);
                searchCriteria.setTime(timePeriodType);
            }

        }

        // simpleResponse
        request.setSimpleResponse(this.simpleResponse);

        XmlTools.addSirAndSensorMLSchemaLocation(request);

        if ( !requestDoc.validate(XmlTools.xmlOptionsForNamespaces()))
            this.requestString = XmlTools.validateAndIterateErrors(requestDoc);
        else
            this.requestString = requestDoc.xmlText(XmlTools.xmlOptionsForNamespaces());

        this.requestString = requestDoc.toString();
    }

}
