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

import java.util.ArrayList;
import java.util.List;

import net.opengis.gml.x32.TimeInstantType;
import net.opengis.gml.x32.TimePeriodType;
import net.opengis.gml.x32.TimePositionType;
import net.opengis.ows.x11.BoundingBoxType;
import net.opengis.swe.x101.UomPropertyType;

import org.n52.sir.SirConfigurator;
import org.n52.sir.SirConstants;
import org.n52.sir.datastructure.SirSearchCriteria_Phenomenon;
import org.n52.sir.datastructure.SirSearchCriteria_Phenomenon.SirMatchingType;
import org.n52.sir.util.Tools;
import org.n52.sir.util.XmlTools;
import org.x52North.sir.x032.ConstraintDocument.Constraint;
import org.x52North.sir.x032.ConstraintDocument.Constraint.IsBetween;
import org.x52North.sir.x032.GetSensorStatusRequestDocument;
import org.x52North.sir.x032.GetSensorStatusRequestDocument.GetSensorStatusRequest;
import org.x52North.sir.x032.PropertyFilterDocument.PropertyFilter;
import org.x52North.sir.x032.PropertyFilterDocument.PropertyFilter.PropertyConstraint;
import org.x52North.sir.x032.SearchCriteriaDocument.SearchCriteria;
import org.x52North.sir.x032.SensorIdentificationDocument.SensorIdentification;
import org.x52North.sir.x032.ServiceCriteriaDocument.ServiceCriteria;
import org.x52North.sir.x032.ServiceReferenceDocument.ServiceReference;

/**
 * @author Jan Schulte
 * 
 */
public class GetSensorStatusBean extends TestClientBean {

    private String isBetweenLowerBoundary = "";

    private String isBetweenUpperBoundary = "";

    private String isEqualTo = "";

    private String isGreaterThan = "";

    private String isGreaterThanOrEqualTo = "";

    private String isLessThan = "";

    private String isLessThanOrEqualTo = "";

    private String isNotEqualTo = "";

    private String lowerCorner = "";

    private String phenomenonName = "";

    private String propertyName = "";

    private String searchText = "";

    private String sensorIDInSIR = "";

    private String serviceCriteriaType = "";

    private String serviceCriteriaURL = "";

    private String serviceSpecificSensorID = "";

    private String serviceType = "";

    private String serviceURL = "";

    private int sorParamDepth = 1;

    private String sorParamMatchingTypeString;

    private String sorParamURL = "";

    private String timePeriodEnd = "";

    private String timePeriodStart = "";

    private String uom = "";

    private String uomConstraint = "";

    private String upperCorner = "";

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.client.beans.AbstractBean#buildRequest()
     */
    @Override
    public void buildRequest() {
        this.responseString = "";

        GetSensorStatusRequestDocument requestDoc = GetSensorStatusRequestDocument.Factory.newInstance();
        GetSensorStatusRequest request = requestDoc.addNewGetSensorStatusRequest();
        request.setService(SirConstants.SERVICE_NAME);
        request.setVersion(SirConfigurator.getInstance().getServiceVersionEnum());

        boolean isUsingIdentification = isSensorIdentification();
        boolean isUsingSearch = isSearchCriteria();

        if (isUsingIdentification && isUsingSearch) {
            this.requestString = "Please choose sensor identification (x)or search criteria!";
            return;
        }

        if (isUsingIdentification) {
            SensorIdentification sensIdent = request.addNewSensorIdentification();

            // sensorIDInSIR
            if ( !this.sensorIDInSIR.isEmpty()) {
                sensIdent.setSensorIDInSIR(this.sensorIDInSIR);
            }

            // serviceDescription
            if (Tools.noneEmpty(new String[] {this.serviceURL, this.serviceType, this.serviceSpecificSensorID})) {
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
        if (isUsingSearch) {
            SearchCriteria searchCriteria = request.addNewSearchCriteria();

            // serviceCriteria
            if ( !this.serviceCriteriaURL.isEmpty() && !this.serviceCriteriaType.isEmpty()) {
                ServiceCriteria servCriteria = searchCriteria.addNewServiceCriteria();
                // serviceURL
                servCriteria.setServiceURL(this.serviceCriteriaURL);
                // serviceType
                servCriteria.setServiceType(this.serviceCriteriaType);
            }

            // searchText
            if ( !this.searchText.isEmpty()) {
                searchCriteria.setSearchTextArray(this.searchText.split(";"));
            }

            // phenomenon
            if ( !this.phenomenonName.isEmpty()) {
                SirSearchCriteria_Phenomenon p = new SirSearchCriteria_Phenomenon(this.phenomenonName);

                // useSOR
                if ( !this.sorParamURL.isEmpty()) {
                    p.setMatchingType(SirMatchingType.valueOf(this.sorParamMatchingTypeString));
                    p.setSearchDepth(this.sorParamDepth);
                    p.setSorUrl(this.sorParamURL);
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
            if (Tools.noneEmpty(new String[] {this.lowerCorner, this.upperCorner})) {
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

        // propertyFilter
        if ( !this.propertyName.isEmpty()) {
            PropertyFilter propFilt = request.addNewPropertyFilter();

            // propertyName
            propFilt.setPropertyName(this.propertyName);

            Constraint constraint;
            if ( !this.uomConstraint.isEmpty() || !this.isEqualTo.isEmpty() || !this.isNotEqualTo.isEmpty()
                    || !this.isGreaterThan.isEmpty() || !this.isLessThan.isEmpty()
                    || !this.isGreaterThanOrEqualTo.isEmpty() || !this.isLessThanOrEqualTo.isEmpty()
                    || !this.isBetweenLowerBoundary.isEmpty() || !this.isBetweenUpperBoundary.isEmpty()) {
                PropertyConstraint propConst = propFilt.addNewPropertyConstraint();

                // code
                if ( !this.uomConstraint.isEmpty()) {
                    UomPropertyType uomPropType = UomPropertyType.Factory.newInstance();
                    uomPropType.setCode(this.uomConstraint);
                    propConst.setUom(uomPropType);
                }

                // isEqualTo
                if ( !this.isEqualTo.isEmpty()) {
                    constraint = propConst.addNewConstraint();
                    constraint.setIsEqualTo(this.isEqualTo);
                }

                // isNotEqualTo
                if ( !this.isNotEqualTo.isEmpty()) {
                    constraint = propConst.addNewConstraint();
                    constraint.setIsNotEqualTo(this.isNotEqualTo);
                }

                // isGreaterThan
                if ( !this.isGreaterThan.isEmpty()) {
                    try {
                        constraint = propConst.addNewConstraint();
                        constraint.setIsGreaterThan(checkNumber(this.isGreaterThan));
                    }
                    catch (NumberFormatException e) {
                        this.requestString = "is greater than value must be a numerical value";
                        return;
                    }
                }

                // isLessThan
                if ( !this.isLessThan.isEmpty()) {
                    try {
                        constraint = propConst.addNewConstraint();
                        constraint.setIsLessThan(checkNumber(this.isLessThan));
                    }
                    catch (NumberFormatException e) {
                        this.requestString = "is less than value must be a numerical value";
                        return;
                    }
                }

                // isGreaterThanOrEqualTo
                if ( !this.isGreaterThanOrEqualTo.isEmpty()) {
                    try {
                        constraint = propConst.addNewConstraint();
                        constraint.setIsGreaterThanOrEqualTo(checkNumber(this.isGreaterThanOrEqualTo));
                    }
                    catch (NumberFormatException e) {
                        this.requestString = "is greater than or equal to value must be a numerical value";
                        return;
                    }
                }

                // isLessThanOrEqualTo
                if ( !this.isLessThanOrEqualTo.isEmpty()) {
                    try {
                        constraint = propConst.addNewConstraint();
                        constraint.setIsLessThanOrEqualTo(checkNumber(this.isLessThanOrEqualTo));
                    }
                    catch (NumberFormatException e) {
                        this.requestString = "is less than or equal to value must be a numerical value";
                        return;
                    }
                }

                // isBetween
                if ( !this.isBetweenLowerBoundary.isEmpty() && !this.isBetweenUpperBoundary.isEmpty()) {
                    try {
                        constraint = propConst.addNewConstraint();
                        IsBetween isBetween = constraint.addNewIsBetween();
                        isBetween.setLowerBoundary(checkNumber(this.isBetweenLowerBoundary));
                        isBetween.setUpperBoundary(checkNumber(this.isBetweenUpperBoundary));
                    }
                    catch (NumberFormatException e) {
                        this.requestString = "is between value must be a numerical value";
                        return;
                    }
                }
            }
        }

        if (requestDoc.validate())
            this.requestString = requestDoc.xmlText(XmlTools.xmlOptionsForNamespaces());
        else
            this.requestString = XmlTools.validateAndIterateErrors(requestDoc);
    }

    /**
     * 
     * @param string
     * @return
     * @throws NumberFormatException
     */
    private double checkNumber(String string) throws NumberFormatException {
        double number = Double.parseDouble(string);
        return number;
    }

    /**
     * @return the isBetweenLowerBoundary
     */
    public String getIsBetweenLowerBoundary() {
        return this.isBetweenLowerBoundary;
    }

    /**
     * @return the isBetweenUpperBoundary
     */
    public String getIsBetweenUpperBoundary() {
        return this.isBetweenUpperBoundary;
    }

    /**
     * @return the isEqualTo
     */
    public String getIsEqualTo() {
        return this.isEqualTo;
    }

    /**
     * @return the isGreaterThan
     */
    public String getIsGreaterThan() {
        return this.isGreaterThan;
    }

    /**
     * @return the isGreaterThanOrEqualTo
     */
    public String getIsGreaterThanOrEqualTo() {
        return this.isGreaterThanOrEqualTo;
    }

    /**
     * @return the isLessThan
     */
    public String getIsLessThan() {
        return this.isLessThan;
    }

    /**
     * @return the isLessThanOrEqualTo
     */
    public String getIsLessThanOrEqualTo() {
        return this.isLessThanOrEqualTo;
    }

    /**
     * @return the isNotEqualTo
     */
    public String getIsNotEqualTo() {
        return this.isNotEqualTo;
    }

    /**
     * @return the lowerCorner
     */
    public String getLowerCorner() {
        return this.lowerCorner;
    }

    /**
     * 
     * @return
     * @return
     */
    public SirMatchingType[] getMatchingTypes() {
        return SirMatchingType.values();
    }

    /**
     * @return the phenomenon
     */
    public String getPhenomenonName() {
        return this.phenomenonName;
    }

    /**
     * @return the propertyName
     */
    public String getPropertyName() {
        return this.propertyName;
    }

    /**
     * @return the searchText
     */
    public String getSearchText() {
        return this.searchText;
    }

    /**
     * @return the sensorIDInSIR
     */
    public String getSensorIDInSIR() {
        return this.sensorIDInSIR;
    }

    /**
     * @return the serviceCriteriaType
     */
    public String getServiceCriteriaType() {
        return this.serviceCriteriaType;
    }

    /**
     * @return the serviceCriteriaURL
     */
    public String getServiceCriteriaURL() {
        return this.serviceCriteriaURL;
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
     * @return the sorMatchingTypeString
     */
    public String getSorMatchingTypeString() {
        return this.sorParamMatchingTypeString;
    }

    /**
     * @return the sorParamDepth
     */
    public int getSorParamDepth() {
        return this.sorParamDepth;
    }

    /**
     * @return the sorParamURL
     */
    public String getSorParamURL() {
        return this.sorParamURL;
    }

    /**
     * @return the timePeriodEnd
     */
    public String getTimePeriodEnd() {
        return this.timePeriodEnd;
    }

    /**
     * @return the timePeriodStart
     */
    public String getTimePeriodStart() {
        return this.timePeriodStart;
    }

    /**
     * @return the uom
     */
    public String getUom() {
        return this.uom;
    }

    /**
     * @return the uomConstraint
     */
    public String getUomConstraint() {
        return this.uomConstraint;
    }

    /**
     * @return the upperCorner
     */
    public String getUpperCorner() {
        return this.upperCorner;
    }

    /**
     * 
     * @return
     */
    private boolean isSearchCriteria() {
        return Tools.atLeastOneIsNotEmpty(new String[] {this.serviceCriteriaURL,
                                                        this.searchText,
                                                        this.phenomenonName,
                                                        this.uom,
                                                        this.lowerCorner,
                                                        this.upperCorner,
                                                        this.timePeriodEnd,
                                                        this.timePeriodStart});
    }

    /**
     * 
     * @return
     */
    private boolean isSensorIdentification() {
        return Tools.atLeastOneIsNotEmpty(new String[] {this.sensorIDInSIR,
                                                        this.serviceURL,
                                                        this.serviceType,
                                                        this.serviceSpecificSensorID});
    }

    /**
     * @param isBetweenLowerBoundary
     *        the isBetweenLowerBoundary to set
     */
    public void setIsBetweenLowerBoundary(String isBetweenLowerBoundary) {
        this.isBetweenLowerBoundary = isBetweenLowerBoundary;
    }

    /**
     * @param isBetweenUpperBoundary
     *        the isBetweenUpperBoundary to set
     */
    public void setIsBetweenUpperBoundary(String isBetweenUpperBoundary) {
        this.isBetweenUpperBoundary = isBetweenUpperBoundary;
    }

    /**
     * @param isEqualTo
     *        the isEqualTo to set
     */
    public void setIsEqualTo(String isEqualTo) {
        this.isEqualTo = isEqualTo;
    }

    /**
     * @param isGreaterThan
     *        the isGreaterThan to set
     */
    public void setIsGreaterThan(String isGreaterThan) {
        this.isGreaterThan = isGreaterThan;
    }

    /**
     * @param isGreaterThanOrEqualTo
     *        the isGreaterThanOrEqualTo to set
     */
    public void setIsGreaterThanOrEqualTo(String isGreaterThanOrEqualTo) {
        this.isGreaterThanOrEqualTo = isGreaterThanOrEqualTo;
    }

    /**
     * @param isLessThan
     *        the isLessThan to set
     */
    public void setIsLessThan(String isLessThan) {
        this.isLessThan = isLessThan;
    }

    /**
     * @param isLessThanOrEqualTo
     *        the isLessThanOrEqualTo to set
     */
    public void setIsLessThanOrEqualTo(String isLessThanOrEqualTo) {
        this.isLessThanOrEqualTo = isLessThanOrEqualTo;
    }

    /**
     * @param isNotEqualTo
     *        the isNotEqualTo to set
     */
    public void setIsNotEqualTo(String isNotEqualTo) {
        this.isNotEqualTo = isNotEqualTo;
    }

    /**
     * @param lowerCorner
     *        the lowerCorner to set
     */
    public void setLowerCorner(String lowerCorner) {
        this.lowerCorner = lowerCorner;
    }

    /**
     * @param phenomenon
     *        the phenomenon to set
     */
    public void setPhenomenonName(String name) {
        this.phenomenonName = name;
    }

    /**
     * @param propertyName
     *        the propertyName to set
     */
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * @param searchText
     *        the searchText to set
     */
    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    /**
     * @param sensorIDInSIR
     *        the sensorIDInSIR to set
     */
    public void setSensorIDInSIR(String sensorIDInSIR) {
        this.sensorIDInSIR = sensorIDInSIR;
    }

    /**
     * @param serviceCriteriaType
     *        the serviceCriteriaType to set
     */
    public void setServiceCriteriaType(String serviceCriteriaType) {
        this.serviceCriteriaType = serviceCriteriaType;
    }

    /**
     * @param serviceCriteriaURL
     *        the serviceCriteriaURL to set
     */
    public void setServiceCriteriaURL(String serviceCriteriaURL) {
        this.serviceCriteriaURL = serviceCriteriaURL;
    }

    /**
     * @param serviceSpecificSensorID
     *        the serviceSpecificSensorID to set
     */
    public void setServiceSpecificSensorID(String serviceSpecificSensorID) {
        this.serviceSpecificSensorID = serviceSpecificSensorID;
    }

    /**
     * @param serviceType
     *        the serviceType to set
     */
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    /**
     * @param serviceURL
     *        the serviceURL to set
     */
    public void setServiceURL(String serviceURL) {
        this.serviceURL = serviceURL;
    }

    /**
     * @param sorMatchingTypeString
     *        the sorMatchingTypeString to set
     */
    public void setSorMatchingTypeString(String sorMatchingTypeString) {
        this.sorParamMatchingTypeString = sorMatchingTypeString;
    }

    /**
     * @param sorParamDepth
     *        the sorParamDepth to set
     */
    public void setSorParamDepth(int sorParamDepth) {
        this.sorParamDepth = sorParamDepth;
    }

    /**
     * @param sorParamURL
     *        the sorParamURL to set
     */
    public void setSorParamURL(String sorParamURL) {
        this.sorParamURL = sorParamURL;
    }

    /**
     * @param timePeriodEnd
     *        the timePeriodEnd to set
     */
    public void setTimePeriodEnd(String timePeriodEnd) {
        this.timePeriodEnd = timePeriodEnd;
    }

    /**
     * @param timePeriodStart
     *        the timePeriodStart to set
     */
    public void setTimePeriodStart(String timePeriodStart) {
        this.timePeriodStart = timePeriodStart;
    }

    /**
     * @param uom
     *        the uom to set
     */
    public void setUom(String uom) {
        this.uom = uom;
    }

    /**
     * @param uomConstraint
     *        the uomConstraint to set
     */
    public void setUomConstraint(String uomConstraint) {
        this.uomConstraint = uomConstraint;
    }

    /**
     * @param upperCorner
     *        the upperCorner to set
     */
    public void setUpperCorner(String upperCorner) {
        this.upperCorner = upperCorner;
    }

}
