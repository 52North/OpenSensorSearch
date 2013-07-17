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
package org.n52.sir.decode.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import net.opengis.gml.x32.TimeInstantType;
import net.opengis.gml.x32.TimePeriodType;
import net.opengis.swe.x101.UomPropertyType;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sir.SirConstants;
import org.n52.sir.datastructure.SirBoundingBox;
import org.n52.sir.datastructure.SirConstraint;
import org.n52.sir.datastructure.SirConstraint.ConsType;
import org.n52.sir.datastructure.SirDescriptionToBeUpdated;
import org.n52.sir.datastructure.SirInfoToBeDeleted;
import org.n52.sir.datastructure.SirInfoToBeInserted;
import org.n52.sir.datastructure.SirInfoToBeInserted_SensorDescription;
import org.n52.sir.datastructure.SirInfoToBeInserted_ServiceReference;
import org.n52.sir.datastructure.SirPropertyConstraint;
import org.n52.sir.datastructure.SirPropertyFilter;
import org.n52.sir.datastructure.SirSearchCriteria;
import org.n52.sir.datastructure.SirSearchCriteria_Phenomenon;
import org.n52.sir.datastructure.SirSensorIDInSir;
import org.n52.sir.datastructure.SirSensorIdentification;
import org.n52.sir.datastructure.SirService;
import org.n52.sir.datastructure.SirServiceInfo;
import org.n52.sir.datastructure.SirServiceReference;
import org.n52.sir.datastructure.SirStatus;
import org.n52.sir.decode.IHttpPostRequestDecoder;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.request.AbstractSirRequest;
import org.n52.sir.request.SirConnectToCatalogRequest;
import org.n52.sir.request.SirDeleteSensorInfoRequest;
import org.n52.sir.request.SirDescribeSensorRequest;
import org.n52.sir.request.SirDisconnectFromCatalogRequest;
import org.n52.sir.request.SirGetCapabilitiesRequest;
import org.n52.sir.request.SirGetSensorStatusRequest;
import org.n52.sir.request.SirHarvestServiceRequest;
import org.n52.sir.request.SirInsertSensorInfoRequest;
import org.n52.sir.request.SirInsertSensorStatusRequest;
import org.n52.sir.request.SirSearchSensorRequest;
import org.n52.sir.request.SirSubscriptionRequest;
import org.n52.sir.request.SirUpdateSensorDescriptionRequest;
import org.n52.sir.util.GMLDateParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sir.x032.CancelSensorStatusSubscriptionRequestDocument;
import org.x52North.sir.x032.ConnectToCatalogRequestDocument;
import org.x52North.sir.x032.DeleteSensorInfoRequestDocument;
import org.x52North.sir.x032.DeleteSensorInfoRequestDocument.DeleteSensorInfoRequest.InfoToBeDeleted;
import org.x52North.sir.x032.DeleteSensorInfoRequestDocument.DeleteSensorInfoRequest.InfoToBeDeleted.ServiceInfo;
import org.x52North.sir.x032.DescribeSensorRequestDocument;
import org.x52North.sir.x032.DisconnectFromCatalogRequestDocument;
import org.x52North.sir.x032.GetCapabilitiesDocument;
import org.x52North.sir.x032.GetCapabilitiesDocument.GetCapabilities;
import org.x52North.sir.x032.GetSensorStatusRequestDocument;
import org.x52North.sir.x032.HarvestServiceRequestDocument;
import org.x52North.sir.x032.HarvestServiceRequestDocument.HarvestServiceRequest;
import org.x52North.sir.x032.InsertSensorInfoRequestDocument;
import org.x52North.sir.x032.InsertSensorInfoRequestDocument.InsertSensorInfoRequest.InfoToBeInserted;
import org.x52North.sir.x032.InsertSensorStatusRequestDocument;
import org.x52North.sir.x032.PropertyFilterDocument.PropertyFilter;
import org.x52North.sir.x032.RenewSensorStatusSubscriptionRequestDocument;
import org.x52North.sir.x032.SearchCriteriaDocument.SearchCriteria;
import org.x52North.sir.x032.SearchCriteriaDocument.SearchCriteria.Phenomenon;
import org.x52North.sir.x032.SearchSensorRequestDocument;
import org.x52North.sir.x032.SearchSensorRequestDocument.SearchSensorRequest;
import org.x52North.sir.x032.SensorIdentificationDocument.SensorIdentification;
import org.x52North.sir.x032.ServiceCriteriaDocument.ServiceCriteria;
import org.x52North.sir.x032.ServiceReferenceDocument.ServiceReference;
import org.x52North.sir.x032.StatusDocument.Status;
import org.x52North.sir.x032.SubscribeSensorStatusRequestDocument;
import org.x52North.sir.x032.UpdateSensorDescriptionRequestDocument;
import org.x52North.sir.x032.UpdateSensorDescriptionRequestDocument.UpdateSensorDescriptionRequest.SensorDescriptionToBeUpdated;
import org.x52North.sir.x032.VersionAttribute.Version.Enum;

public class HttpPostRequestDecoder implements IHttpPostRequestDecoder {

    private static Logger log = LoggerFactory.getLogger(HttpPostRequestDecoder.class);

    private AbstractSirRequest decodeConnectToCatalogRequest(ConnectToCatalogRequestDocument conToCatDoc) throws OwsExceptionReport {
        SirConnectToCatalogRequest sirRequest = new SirConnectToCatalogRequest();
        // csw url
        if (conToCatDoc.getConnectToCatalogRequest().getCatalogURL() != null) {
            URL cswUrl;
            try {
                cswUrl = new URL(conToCatDoc.getConnectToCatalogRequest().getCatalogURL());
            }
            catch (MalformedURLException e) {
                OwsExceptionReport se = new OwsExceptionReport("Malformed URL - please check!", e);
                throw se;
            }
            sirRequest.setCswUrl(cswUrl);
        }
        else {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.InvalidParameterValue, null, "&lt;cswURL&gt; is missing");
            log.error("&lt;cswURL&gt; is missing!");
            throw se;
        }
        // push interval
        if (conToCatDoc.getConnectToCatalogRequest().isSetPushIntervalSeconds()) {
            if (conToCatDoc.getConnectToCatalogRequest().getPushIntervalSeconds() > 0) {
                sirRequest.setPushInterval(conToCatDoc.getConnectToCatalogRequest().getPushIntervalSeconds());
            }
            else {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(ExceptionCode.InvalidParameterValue,
                                     null,
                                     "The update interval must be a positive integer.");
                log.error("The update interval is not positive!");
                throw se;
            }
        }
        return sirRequest;
    }

    private AbstractSirRequest decodeDeleteSensorInfoRequest(DeleteSensorInfoRequestDocument delSensInfoDoc) throws OwsExceptionReport {
        SirDeleteSensorInfoRequest sirRequest = new SirDeleteSensorInfoRequest();
        Collection<SirInfoToBeDeleted> deleteInfos = new ArrayList<SirInfoToBeDeleted>();

        InfoToBeDeleted[] infos = delSensInfoDoc.getDeleteSensorInfoRequest().getInfoToBeDeletedArray();

        for (InfoToBeDeleted infoToBeDeleted : infos) {
            SirInfoToBeDeleted itbd = new SirInfoToBeDeleted();
            itbd.setSensorIdentification(decodeSensorIdentification(infoToBeDeleted.getSensorIdentification()));

            if (infoToBeDeleted.isSetDeleteSensor()) {
                itbd.setDeleteSensor(infoToBeDeleted.getDeleteSensor());
            }

            if (infoToBeDeleted.isSetServiceInfo()) {
                itbd.setServiceInfo(decodeServiceInfo(infoToBeDeleted.getServiceInfo()));
            }

            deleteInfos.add(itbd);
        }

        sirRequest.setInfoToBeDeleted(deleteInfos);

        return sirRequest;
    }

    /**
     * 
     * @param descSensDoc
     * @return
     * @throws OwsExceptionReport
     */
    private AbstractSirRequest decodeDescribeSensorRequest(DescribeSensorRequestDocument descSensDoc) throws OwsExceptionReport {
        SirDescribeSensorRequest sirRequest = new SirDescribeSensorRequest();

        if (descSensDoc.getDescribeSensorRequest().getSensorIDInSIR() != null) {
            sirRequest.setSensorIdInSir(descSensDoc.getDescribeSensorRequest().getSensorIDInSIR());
        }
        else {
            log.error("&lt;sensorIDInSIR&gt; is missing!");
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.InvalidRequest,
                                 "HttpPostRequestDecoder.decodeDescribeSensorRequest()",
                                 "&lt;sensorIDInSIR&gt; is missing!");
            throw se;
        }
        return sirRequest;
    }

    private AbstractSirRequest decodeDisconnectFromCatalogRequest(DisconnectFromCatalogRequestDocument disconFromCatDoc) {
        SirDisconnectFromCatalogRequest sirRequest = new SirDisconnectFromCatalogRequest();
        if (disconFromCatDoc.getDisconnectFromCatalogRequest().getCatalogURL() != null) {
            sirRequest.setCswURL(disconFromCatDoc.getDisconnectFromCatalogRequest().getCatalogURL());
        }
        else {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.InvalidParameterValue, null, "&lt;cswURL&gt; is missing");
            log.error("&lt;cswURL&gt; is missing!");
        }
        return sirRequest;
    }

    /**
     * 
     * @param getCapDoc
     * @return
     */
    private AbstractSirRequest decodeGetCapabilities(GetCapabilitiesDocument getCapDoc) {
        SirGetCapabilitiesRequest sirRequest = new SirGetCapabilitiesRequest();
        GetCapabilities getCaps = getCapDoc.getGetCapabilities();
        // test
        // getCaps.addNewAcceptVersions();

        // service
        sirRequest.setService(getCaps.getService());

        // acceptVersions
        if (getCaps.isSetAcceptVersions()) {
            sirRequest.setAcceptVersions(getCaps.getAcceptVersions().getVersionArray());
        }

        // sections
        if (getCaps.isSetSections()) {
            sirRequest.setSections(getCaps.getSections().getSectionArray());
        }

        // updateSequence
        if (getCaps.isSetUpdateSequence()) {
            sirRequest.setUpdateSequence(getCaps.getUpdateSequence());
        }

        // acceptFormats
        if (getCaps.isSetAcceptFormats()) {
            sirRequest.setAcceptFormats(getCaps.getAcceptFormats().getOutputFormatArray());
        }
        return sirRequest;
    }

    private AbstractSirRequest decodeGetSensorStatusRequest(GetSensorStatusRequestDocument getSensStatDoc) throws OwsExceptionReport {
        SirGetSensorStatusRequest sirRequest = new SirGetSensorStatusRequest();
        // sensor identification
        if (getSensStatDoc.getGetSensorStatusRequest().getSensorIdentificationArray().length != 0) {
            ArrayList<SirSensorIdentification> sensIdents = new ArrayList<SirSensorIdentification>();
            SensorIdentification[] sensorIdentifications = getSensStatDoc.getGetSensorStatusRequest().getSensorIdentificationArray();
            for (SensorIdentification sensIdent : sensorIdentifications) {
                sensIdents.add(decodeSensorIdentification(sensIdent));
            }
            sirRequest.setSensIdent(sensIdents);
        }
        else if (getSensStatDoc.getGetSensorStatusRequest().getSearchCriteria() != null) {
            // search criteria
            sirRequest.setSearchCriteria(decodeSearchCriteria(getSensStatDoc.getGetSensorStatusRequest().getSearchCriteria()));
            // return sirRequest;
        }
        else {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.InvalidParameterValue,
                                 null,
                                 "&lt;sensorIdentification&gt; or &lt;searchCriteria&gt; is missing");
            log.error("&lt;sensorIdentification&gt; or &lt;searchCriteria&gt; is missing!");
        }
        // property filter
        if (getSensStatDoc.getGetSensorStatusRequest().getPropertyFilterArray().length != 0) {
            ArrayList<SirPropertyFilter> propFilters = new ArrayList<SirPropertyFilter>();
            for (PropertyFilter propFilt : getSensStatDoc.getGetSensorStatusRequest().getPropertyFilterArray()) {
                propFilters.add(decodePropertyFilter(propFilt));
            }
            sirRequest.setPropertyFilter(propFilters);
        }

        return sirRequest;
    }

    /**
     * 
     * @param harvServDoc
     * @return
     * @throws OwsExceptionReport
     */
    private AbstractSirRequest decodeHarvestServiceRequest(HarvestServiceRequestDocument harvServDoc) throws OwsExceptionReport {
        SirHarvestServiceRequest sirRequest = new SirHarvestServiceRequest();

        HarvestServiceRequest harvServ = harvServDoc.getHarvestServiceRequest();

        // set service url
        if (harvServ.getServiceURL() != null) {
            sirRequest.setServiceUrl(harvServ.getServiceURL());
        }
        else {
            log.error("&lt;serviceURL&gt; is missing!");
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.InvalidRequest,
                                 "HttpPostRequestDecoder.decodeHarvestService()",
                                 "&lt;serviceURL&gt; is Missing!");
            throw se;
        }

        // set service type
        if (harvServ.getServiceType() != null) {
            sirRequest.setServiceType(harvServ.getServiceType());
        }
        else {
            log.error("&lt;serviceType&gt; is missing!");
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.InvalidRequest,
                                 "HttpPostRequestDecoder.decodeHarvestServiceRequest()",
                                 "&lt;serviceType&gt; is missing!");
            throw se;
        }

        return sirRequest;
    }

    /**
     * 
     * @param insSensInfoDoc
     * @return
     * @throws OwsExceptionReport
     */
    private AbstractSirRequest decodeInsertSensorInfoRequest(InsertSensorInfoRequestDocument insSensInfoDoc) throws OwsExceptionReport {
        SirInsertSensorInfoRequest sirRequest = new SirInsertSensorInfoRequest();
        ArrayList<SirInfoToBeInserted> infoToBeInserteds = new ArrayList<SirInfoToBeInserted>();
        if (insSensInfoDoc.getInsertSensorInfoRequest().getInfoToBeInsertedArray().length != 0) {
            InfoToBeInserted[] infoArray = insSensInfoDoc.getInsertSensorInfoRequest().getInfoToBeInsertedArray();

            for (InfoToBeInserted infoToInsert : infoArray) {

                // add new sensor
                if (infoToInsert.isSetSensorDescription()) {
                    SirInfoToBeInserted_SensorDescription newSensorInfo = new SirInfoToBeInserted_SensorDescription();
                    newSensorInfo.setSensorDescription(infoToInsert.getSensorDescription());

                    // optional service references
                    ServiceReference[] servRefs = infoToInsert.getServiceReferenceArray();
                    if (servRefs.length != 0) {
                        ArrayList<SirServiceReference> sirServiceRefs = new ArrayList<SirServiceReference>();
                        for (ServiceReference servRef : servRefs) {
                            sirServiceRefs.add(decodeServiceReference(servRef));
                        }
                        newSensorInfo.setServiceReferences(sirServiceRefs);
                    }

                    infoToBeInserteds.add(newSensorInfo);
                }

                // add service reference with ID
                else if (infoToInsert.isSetSensorIDInSIR()) {
                    SirInfoToBeInserted_ServiceReference newServiceReference = new SirInfoToBeInserted_ServiceReference();
                    newServiceReference.setSensorIDinSIR(new SirSensorIDInSir(infoToInsert.getSensorIDInSIR()));

                    // service references
                    ServiceReference[] servRefs = infoToInsert.getServiceReferenceArray();
                    if (servRefs.length != 0) {
                        ArrayList<SirServiceReference> sirServiceRefs = new ArrayList<SirServiceReference>();
                        for (ServiceReference servRef : servRefs) {
                            sirServiceRefs.add(decodeServiceReference(servRef));
                        }
                        newServiceReference.setServiceReferences(sirServiceRefs);
                    }

                    infoToBeInserteds.add(newServiceReference);
                }

                else {
                    String errMsg = "&lt;SensorDescription&gt; or &lt;SensorIDInSIR&gt; with service references is missing!";
                    log.error(errMsg);
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue, null, errMsg);
                    throw se;
                }
            }
        }
        else {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 null,
                                 "&lt;infoToBeInserted&gt; is missing!");
            log.error("&lt;infoToBeInserted&gt; is missing!");
            throw se;
        }
        sirRequest.setInfoToBeInserted(infoToBeInserteds);
        return sirRequest;
    }

    /**
     * 
     * @param reqDoc
     * @return
     * @throws OwsExceptionReport
     */
    private AbstractSirRequest decodeInsertSensorStatusRequest(InsertSensorStatusRequestDocument reqDoc) throws OwsExceptionReport {
        SirInsertSensorStatusRequest sirRequest = new SirInsertSensorStatusRequest();

        // check StatusDescription
        if (reqDoc.getInsertSensorStatusRequest().getStatusDescription() == null) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 null,
                                 "&lt;StatusDescription&gt; is missing!");
            log.error("&lt;StatusDescription&gt; is missing!");
            throw se;
        }

        // check and set SensorIDInSIR
        if (reqDoc.getInsertSensorStatusRequest().getStatusDescription().getSensorIDInSIR() == null) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 null,
                                 "&lt;SensorIDInSIR&gt; is missing!");
            log.error("&lt;SensorIDInSIR&gt; is missing!");
            throw se;
        }
        sirRequest.setSensIdent(new SirSensorIDInSir(reqDoc.getInsertSensorStatusRequest().getStatusDescription().getSensorIDInSIR()));

        // set status
        if (reqDoc.getInsertSensorStatusRequest().getStatusDescription().getStatusArray().length == 0) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.InvalidParameterValue, null, "&lt;status&gt; is missing!");
            log.error("&lt;status&gt; is missing!");
            throw se;
        }

        Status[] status = reqDoc.getInsertSensorStatusRequest().getStatusDescription().getStatusArray();
        ArrayList<SirStatus> sirStatus = new ArrayList<SirStatus>();
        for (Status stat : status) {
            SirStatus sirStat = new SirStatus();
            // property name
            if (stat.getPropertyName() != null) {
                sirStat.setPropertyName(stat.getPropertyName());
            }
            else {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(ExceptionCode.InvalidParameterValue, null, "&lt;propertyName&gt; is missing!");
                log.error("&lt;propertyName&gt; is missing!");
                throw se;
            }
            // property value
            if (stat.getPropertyValue() != null) {
                sirStat.setPropertyValue(decodePropertyValue(stat.getPropertyValue().toString()));
            }
            else {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(ExceptionCode.InvalidParameterValue, null, "&lt;propertyValue&gt; is missing!");
                log.error("&lt;propertyValue&gt; is missing!");
                throw se;
            }

            // uom (optional)
            if (stat.isSetUom()) {
                sirStat.setUom(stat.getUom().getCode());
            }
            else {
                sirStat.setUom("");
            }

            // timestamp (optional)
            Calendar cal = null;
            if (stat.isSetTimestamp()) {
                cal = decodeTimeInstantType(stat.getTimestamp());
            }
            else {
                cal = Calendar.getInstance();
                cal.setTime(new Date());
            }
            sirStat.setTimestamp(cal);

            sirStatus.add(sirStat);
        }

        sirRequest.setStatus(sirStatus);

        return sirRequest;
    }

    private SirPropertyFilter decodePropertyFilter(PropertyFilter propertyFilter) throws OwsExceptionReport {
        SirPropertyFilter sirPropertyFilter = new SirPropertyFilter();

        if (propertyFilter.getPropertyName() != null) {
            // propertyName
            sirPropertyFilter.setPropertyName(propertyFilter.getPropertyName());
            if (propertyFilter.isSetPropertyConstraint()) {
                // propertyConstraint
                SirPropertyConstraint sirPropertyConstraint = new SirPropertyConstraint();
                sirPropertyFilter.setPropConst(sirPropertyConstraint);
                // constraint
                SirConstraint sirConstraint = new SirConstraint();
                sirPropertyConstraint.setConstraint(sirConstraint);
                if (propertyFilter.getPropertyConstraint().getConstraint() == null) {
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(ExceptionCode.InvalidRequest,
                                         "HttpPostRequestDecoder",
                                         "Constraint is missing!");
                    log.error("&lt;constraint&gt; is missing!");
                    throw se;
                }
                // isEqualTo
                if (propertyFilter.getPropertyConstraint().getConstraint().isSetIsEqualTo()) {
                    sirConstraint.setConsType(ConsType.isEqualTo);
                    String value = (String) propertyFilter.getPropertyConstraint().getConstraint().getIsEqualTo();
                    sirConstraint.setValueString(value);
                    // try {
                    // sirConstraint.setValueDouble(new Double(value));
                    // } catch (NumberFormatException nfe) {
                    // if (value.equals("true") || value.equals("false")) {
                    // sirConstraint.setValueBoolean(new Boolean(value));
                    // } else {
                    // sirConstraint.setValueString(value);
                    // }
                    // }
                }
                // isNotEqualTo
                if (propertyFilter.getPropertyConstraint().getConstraint().isSetIsNotEqualTo()) {
                    sirConstraint.setConsType(ConsType.isNotEqualTo);
                    String value = (String) propertyFilter.getPropertyConstraint().getConstraint().getIsNotEqualTo();
                    sirConstraint.setValueString(value);
                    // try {
                    // sirConstraint.setValueDouble(new Double(value));
                    // } catch (NumberFormatException nfe) {
                    // if (value.equals("true") || value.equals("false")) {
                    // sirConstraint.setValueBoolean(new Boolean(value));
                    // } else {
                    // sirConstraint.setValueString(value);
                    // }
                    // }
                }
                // isGreaterThan
                if (propertyFilter.getPropertyConstraint().getConstraint().isSetIsGreaterThan()) {
                    sirConstraint.setConsType(ConsType.isGreaterThan);
                    sirConstraint.setValueDouble(propertyFilter.getPropertyConstraint().getConstraint().getIsGreaterThan());
                }
                // isLessThan
                if (propertyFilter.getPropertyConstraint().getConstraint().isSetIsLessThan()) {
                    sirConstraint.setConsType(ConsType.isLessThan);
                    sirConstraint.setValueDouble(propertyFilter.getPropertyConstraint().getConstraint().getIsLessThan());
                }
                // isGreaterThanOrEqualThanTo
                if (propertyFilter.getPropertyConstraint().getConstraint().isSetIsGreaterThanOrEqualTo()) {
                    sirConstraint.setConsType(ConsType.isGreaterThanOrEqualTo);
                    sirConstraint.setValueDouble(propertyFilter.getPropertyConstraint().getConstraint().getIsGreaterThanOrEqualTo());
                }
                // isLessThanOrEqualThanTo
                if (propertyFilter.getPropertyConstraint().getConstraint().isSetIsLessThanOrEqualTo()) {
                    sirConstraint.setConsType(ConsType.isLessThanOrEqualTo);
                    sirConstraint.setValueDouble(propertyFilter.getPropertyConstraint().getConstraint().getIsLessThanOrEqualTo());
                }
                // isBetween
                if (propertyFilter.getPropertyConstraint().getConstraint().isSetIsBetween()) {
                    sirConstraint.setConsType(ConsType.isBetween);
                    sirConstraint.setLowerBoundary(propertyFilter.getPropertyConstraint().getConstraint().getIsBetween().getLowerBoundary());
                    sirConstraint.setUpperBoundary(propertyFilter.getPropertyConstraint().getConstraint().getIsBetween().getUpperBoundary());
                }
                // uom
                if (propertyFilter.getPropertyConstraint().getUom() != null) {
                    sirPropertyConstraint.setUom(propertyFilter.getPropertyConstraint().getUom().getCode());
                }
                if (sirPropertyFilter.getPropConst().getConstraint().getConsType() == null) {
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(ExceptionCode.InvalidRequest, "HttpPostRequestDecoder", "Unknown constraint!");
                    log.error("Unknown constraint!");
                    throw se;
                }
            }
        }
        else {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.InvalidRequest,
                                 "HttpPostRequestDecoder",
                                 "&lt;propertyName&gt; is missing!");
            log.error("&lt;propertyName&gt; is missing!");
            throw se;
        }
        return sirPropertyFilter;
    }

    /**
     * 
     * @param string
     * @return
     */
    private Object decodePropertyValue(String string) {
        try {
            return Integer.valueOf(string);
        }
        catch (NumberFormatException e) {
            // cannot parse as integer, carrying on...
        }
        try {
            return Double.valueOf(string);
        }
        catch (NumberFormatException e) {
            // cannot parse as double, carrying on...
        }
        if (string.equalsIgnoreCase("true") || string.equalsIgnoreCase("false")) {
            return Boolean.valueOf(string);
        }
        // return unchanged
        return string;
    }

    private SirSearchCriteria decodeSearchCriteria(SearchCriteria searchCrit) throws OwsExceptionReport {
        // searchCriteria
        SirSearchCriteria sirSearchCriteria = new SirSearchCriteria();

        // serviceCriteria
        ArrayList<SirService> servCrits = new ArrayList<SirService>();
        for (ServiceCriteria servCrit : searchCrit.getServiceCriteriaArray()) {
            servCrits.add(new SirService(servCrit.getServiceURL(), servCrit.getServiceType()));
        }
        sirSearchCriteria.setServiceCriteria(servCrits);

        // searchText
        ArrayList<String> searchText = new ArrayList<String>();
        for (String text : searchCrit.getSearchTextArray()) {
            searchText.add(text);
        }
        sirSearchCriteria.setSearchText(searchText);

        // phenomena
        if (searchCrit.getPhenomenonArray().length > 0) {
            Phenomenon[] phenomena = searchCrit.getPhenomenonArray();

            for (Phenomenon phenomenon : phenomena) {
                sirSearchCriteria.addPhenomenon(new SirSearchCriteria_Phenomenon(phenomenon));
            }
        }

        // uom
        ArrayList<String> uoms = new ArrayList<String>();
        for (UomPropertyType uom : searchCrit.getUomArray()) {
            uoms.add(uom.getCode());
        }
        sirSearchCriteria.setUom(uoms);

        // bounding Box
        if (searchCrit.isSetBoundingBox()) {
            // coordinate order: lat lon
            double south = Double.parseDouble(searchCrit.getBoundingBox().getLowerCorner().get(0).toString());
            double east = Double.parseDouble(searchCrit.getBoundingBox().getUpperCorner().get(1).toString());
            double north = Double.parseDouble(searchCrit.getBoundingBox().getUpperCorner().get(0).toString());
            double west = Double.parseDouble(searchCrit.getBoundingBox().getLowerCorner().get(1).toString());
            sirSearchCriteria.setBoundingBox(new SirBoundingBox(east, south, west, north));
        }

        // time
        // check time
        if (searchCrit.isSetTime()) {
            // check if it is timeInstantType
            if (searchCrit.getTime() instanceof TimeInstantType) {
                sirSearchCriteria.setStart(decodeTimeInstantType((TimeInstantType) searchCrit.getTime()));
            }

            // check it it is timePeriodType
            if (searchCrit.getTime() instanceof TimePeriodType) {
                TimePeriodType timePeriod = (TimePeriodType) searchCrit.getTime();
                Calendar start;
                try {
                    start = GMLDateParser.getInstance().parseString(timePeriod.getBeginPosition().getStringValue());
                    sirSearchCriteria.setStart(start);
                }
                catch (ParseException e) {
                    log.error("&lt;TimePeriodType&gt; &lt;BeginPosition&gt; has wrong format!");
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(ExceptionCode.InvalidRequest,
                                         "HttpPostRequestDecoder.decodeDescribeSensorRequest()",
                                         "&lt;TimePeriodType&gt; &lt;BeginPosition&gt; has wrong format, please use "
                                                 + GMLDateParser.getInstance().parseDate(Calendar.getInstance()) + "!");
                    throw se;
                }
                try {
                    Calendar end = GMLDateParser.getInstance().parseString(timePeriod.getEndPosition().getStringValue());
                    sirSearchCriteria.setEnd(end);
                }
                catch (ParseException e) {
                    log.error("&lt;TimePeriodType&gt; &lt;EndPosition&gt; has wrong format!");
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(ExceptionCode.InvalidRequest,
                                         "HttpPostRequestDecoder.decodeDescribeSensorRequest()",
                                         "&lt;TimePeriodType&gt; &lt;EndPosition&gt; has wrong format, please use "
                                                 + GMLDateParser.getInstance().parseDate(Calendar.getInstance()) + "!");
                    throw se;
                }
                log.info("TimePeriodType");
            }
            // check if it is _TimeGeometricPrimitive not implemented
        }

        // try {
        // if (searchCrit.isSetTimeperiod()) {
        // if (searchCrit.getTimeperiod().getStart() == null) {
        // log.error("&lt;starttime&gt; is missing!");
        // OwsExceptionReport se = new OwsExceptionReport();
        // se.addCodedException(ExceptionCode.InvalidRequest,
        // "HttpPostRequestDecoder.decodeSearchCriteria",
        // "&lt;starttime&gt; is missing!");
        // throw se;
        // }
        // else if (searchCrit.getTimeperiod().getEnd() == null) {
        // log.error("&lt;endtime&gt; is missing!");
        // OwsExceptionReport se = new OwsExceptionReport();
        // se.addCodedException(ExceptionCode.InvalidRequest,
        // "HttpPostRequestDecoder.decodeSearchCriteria",
        // "&lt;endtime&gt; is missing!");
        // throw se;
        // }
        // else {
        // sirSearchCriteria.setStart(searchCrit.getTimeperiod().getStart());
        // sirSearchCriteria.setEnd(searchCrit.getTimeperiod().getEnd());
        // }
        // }
        // }
        // catch (XmlValueOutOfRangeException e) {
        // log.error("Wrong date: " + e.getMessage());
        // OwsExceptionReport se = new OwsExceptionReport();
        // se.addCodedException(ExceptionCode.InvalidRequest,
        // "HttpPostRequestDecoder.decodeSearchCriteria",
        // "Invalid date value. Use date in this form: 2008-01-27T12:11:00.943Z !");
        // throw se;
        // }

        return sirSearchCriteria;
    }

    private AbstractSirRequest decodeSearchSensorRequest(SearchSensorRequestDocument searchSensDoc) throws OwsExceptionReport {
        SirSearchSensorRequest sirRequest = new SirSearchSensorRequest();
        SearchSensorRequest request = searchSensDoc.getSearchSensorRequest();
        Enum version = request.getVersion();

        if (version == null) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 null,
                                 "Version is null! The request must contain this attribute including the namespace, e.g. 'sir:version'.");
            log.error("Version is null in search sensor doc {}", searchSensDoc);
            throw se;
        }
        sirRequest.setVersion(version.toString());

        // sensorIdentification
        if (searchSensDoc.getSearchSensorRequest().getSensorIdentificationArray().length != 0) {
            ArrayList<SirSensorIdentification> sensIdents = new ArrayList<SirSensorIdentification>();

            for (SensorIdentification sensIdent : searchSensDoc.getSearchSensorRequest().getSensorIdentificationArray()) {
                sensIdents.add(decodeSensorIdentification(sensIdent));
            }

            sirRequest.setSensIdent(sensIdents);
        }
        else if (searchSensDoc.getSearchSensorRequest().getSearchCriteria() != null) {
            // search criteria
            sirRequest.setSearchCriteria(decodeSearchCriteria(searchSensDoc.getSearchSensorRequest().getSearchCriteria()));

        }
        else {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 null,
                                 "&lt;sensorIdentification&gt; or &lt;searchCriteria&gt; is missing!");
            log.error("&lt;sensorIdentification&gt; or &lt;searchCriteria&gt; is missing!");
            throw se;
        }
        // simpleResponse
        sirRequest.setSimpleResponse(searchSensDoc.getSearchSensorRequest().getSimpleResponse());

        return sirRequest;
    }

    /**
     * 
     * @param sensIdent
     * @return
     * @throws OwsExceptionReport
     */
    private SirSensorIdentification decodeSensorIdentification(SensorIdentification sensIdent) throws OwsExceptionReport {
        if (sensIdent.getSensorIDInSIR() != null || sensIdent.getServiceReference() != null) {
            // sensorID in SIR
            if (sensIdent.getSensorIDInSIR() != null) {
                return new SirSensorIDInSir(sensIdent.getSensorIDInSIR());
            }
            // serviceDescription
            if (sensIdent.getServiceReference() != null) {
                return decodeServiceReference(sensIdent.getServiceReference());
            }
        }
        else {
            log.error("&lt;sensorIDInSIR&gt; or &lt;serviceDescription&gt; is missing!");
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.InvalidRequest,
                                 "HttpPostRequestDecoder.decodeSensorIdentification()",
                                 "&lt;sensorIDInSIR&gt; or &lt;serviceDescription&gt; is missing!");
            throw se;
        }
        return null;
    }

    private SirServiceInfo decodeServiceInfo(ServiceInfo serviceInfo) {
        ServiceReference[] serviceReferenceArray = serviceInfo.getServiceReferenceArray();
        Collection<SirServiceReference> decodedReferences = new ArrayList<SirServiceReference>();

        for (ServiceReference serviceReference : serviceReferenceArray) {
            SirServiceReference newRef = new SirServiceReference(new SirService(serviceReference.getServiceURL(),
                                                                                serviceReference.getServiceType()),
                                                                 serviceReference.getServiceSpecificSensorID());
            decodedReferences.add(newRef);
        }

        SirServiceInfo decodedInfo = new SirServiceInfo(decodedReferences);

        return decodedInfo;
    }

    /**
     * 
     * @param servRef
     * @return
     * @throws OwsExceptionReport
     */
    private SirServiceReference decodeServiceReference(ServiceReference servRef) throws OwsExceptionReport {
        if (servRef.getServiceURL() == null) {
            log.error("&lt;ServiceUrl&gt; is missing!");
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.InvalidRequest,
                                 "HttpPostRequestDecoder.decodeServiceReference()",
                                 "&lt;ServiceUrl&gt; is missing!");
            throw se;
        }
        else if (servRef.getServiceType() == null) {
            log.error("&lt;ServiceType&gt; is missing!");
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.InvalidRequest,
                                 "HttpPostRequestDecoder.decodeServiceReference()",
                                 "&lt;ServiceType&gt; is missing!");
            throw se;
        }
        else if (servRef.getServiceSpecificSensorID() == null) {
            log.error("&gt;ServiceSpecificSensorID&gt; is missing!");
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.InvalidRequest,
                                 "HttpPostRequestDecoder.decodeServiceReference()",
                                 "&lt;ServiceSpecificSensorID&gt; ID is missing!");
            throw se;
        }
        else {
            return new SirServiceReference(new SirService(servRef.getServiceURL(), servRef.getServiceType()),
                                           servRef.getServiceSpecificSensorID());

        }
    }

    /**
     * 
     * @param timestamp
     * @return
     * @throws OwsExceptionReport
     */
    private Calendar decodeTimeInstantType(TimeInstantType timestamp) throws OwsExceptionReport {
        try {
            return GMLDateParser.getInstance().parseString(timestamp.getTimePosition().getStringValue());
        }
        catch (ParseException pe) {
            log.error("&lt;TimeInstantType&gt; has wrong format!");
            log.error(Calendar.getInstance().toString());

            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.InvalidRequest,
                                 "HttpPostRequestDecoder.decodeDescribeSensorRequest()",
                                 "&lt;TimeInstantType&gt; has a wrong format, please use "
                                         + GMLDateParser.getInstance().parseDate(Calendar.getInstance()) + "!");
            throw se;
        }
    }

    private AbstractSirRequest decodeUpdateSensorDescriptionRequest(UpdateSensorDescriptionRequestDocument updSensDescrDoc) throws OwsExceptionReport {
        SirUpdateSensorDescriptionRequest sirRequest = new SirUpdateSensorDescriptionRequest();
        Collection<SirDescriptionToBeUpdated> descriptionsToBeUpdated = new ArrayList<SirDescriptionToBeUpdated>();

        SensorDescriptionToBeUpdated[] sensors = updSensDescrDoc.getUpdateSensorDescriptionRequest().getSensorDescriptionToBeUpdatedArray();

        for (SensorDescriptionToBeUpdated sensorDescriptionToBeUpdated : sensors) {
            SirDescriptionToBeUpdated sdtbu = new SirDescriptionToBeUpdated();
            sdtbu.setSensorIdentification(decodeSensorIdentification(sensorDescriptionToBeUpdated.getSensorIdentification()));
            sdtbu.setSensorDescription(sensorDescriptionToBeUpdated.getSensorDescription());

            descriptionsToBeUpdated.add(sdtbu);
        }

        sirRequest.setDescriptionToBeUpdated(descriptionsToBeUpdated);

        return sirRequest;
    }

    @Override
    public AbstractSirRequest receiveRequest(String docString) throws OwsExceptionReport {
        AbstractSirRequest request = null;

        XmlObject doc = null;

        try {
            doc = XmlObject.Factory.parse(docString);
        }
        catch (XmlException e) {
            log.error("Error while parsing xml request!");
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                 null,
                                 "Error while parsing the post request: " + e.getMessage());
            throw se;
        }

        // getCapabilitiesRequest
        if (doc instanceof GetCapabilitiesDocument) {
            if (log.isDebugEnabled())
                log.debug("Post getCapabilities request");
            GetCapabilitiesDocument getCapDoc = (GetCapabilitiesDocument) doc;
            request = decodeGetCapabilities(getCapDoc);
        }

        // harvestServiceRequest
        else if (doc instanceof HarvestServiceRequestDocument) {
            if (log.isDebugEnabled())
                log.debug("Post harvestService request");
            HarvestServiceRequestDocument harvServDoc = (HarvestServiceRequestDocument) doc;
            request = decodeHarvestServiceRequest(harvServDoc);
        }

        // describeSensorRequest
        else if (doc instanceof DescribeSensorRequestDocument) {
            if (log.isDebugEnabled())
                log.debug("Post describeSensor request");
            DescribeSensorRequestDocument descSensDoc = (DescribeSensorRequestDocument) doc;
            request = decodeDescribeSensorRequest(descSensDoc);
        }

        // insertSensorStatusRequest
        else if (doc instanceof InsertSensorStatusRequestDocument) {
            if (log.isDebugEnabled())
                log.debug("Post insertSensorStatus request");
            InsertSensorStatusRequestDocument insSensStatDoc = (InsertSensorStatusRequestDocument) doc;
            request = decodeInsertSensorStatusRequest(insSensStatDoc);
        }

        // insertSensorInfoRequest
        else if (doc instanceof InsertSensorInfoRequestDocument) {
            if (log.isDebugEnabled())
                log.debug("Post insertSensorInfo request");
            InsertSensorInfoRequestDocument insSensInfoDoc = (InsertSensorInfoRequestDocument) doc;
            request = decodeInsertSensorInfoRequest(insSensInfoDoc);
        }

        // deleteSensorInfoRequest
        else if (doc instanceof DeleteSensorInfoRequestDocument) {
            if (log.isDebugEnabled())
                log.debug("Post deleteSensorInfo request");
            DeleteSensorInfoRequestDocument delSensInfoDoc = (DeleteSensorInfoRequestDocument) doc;
            request = decodeDeleteSensorInfoRequest(delSensInfoDoc);
        }

        // updateSensorDescriptionRequest
        else if (doc instanceof UpdateSensorDescriptionRequestDocument) {
            if (log.isDebugEnabled())
                log.debug("Post updateSensorDescription request");
            UpdateSensorDescriptionRequestDocument updSensDescrDoc = (UpdateSensorDescriptionRequestDocument) doc;
            request = decodeUpdateSensorDescriptionRequest(updSensDescrDoc);
        }

        // searchSensorRequest
        else if (doc instanceof SearchSensorRequestDocument) {
            if (log.isDebugEnabled())
                log.debug("Post searchSensor request");
            SearchSensorRequestDocument searchSensDoc = (SearchSensorRequestDocument) doc;
            request = decodeSearchSensorRequest(searchSensDoc);
        }

        // getSensorStatusRequest
        else if (doc instanceof GetSensorStatusRequestDocument) {
            if (log.isDebugEnabled())
                log.debug("Post getSensorStatus request");
            GetSensorStatusRequestDocument getSensStatDoc = (GetSensorStatusRequestDocument) doc;
            request = decodeGetSensorStatusRequest(getSensStatDoc);
        }

        // connectToCatalogRequest
        else if (doc instanceof ConnectToCatalogRequestDocument) {
            if (log.isDebugEnabled())
                log.debug("Post connectToCatalog request");
            ConnectToCatalogRequestDocument conToCatDoc = (ConnectToCatalogRequestDocument) doc;
            request = decodeConnectToCatalogRequest(conToCatDoc);
        }

        // disconnectFromCatalogRequest
        else if (doc instanceof DisconnectFromCatalogRequestDocument) {
            if (log.isDebugEnabled())
                log.debug("Post disconnectFromCatalog request");
            DisconnectFromCatalogRequestDocument disconFromCatDoc = (DisconnectFromCatalogRequestDocument) doc;
            request = decodeDisconnectFromCatalogRequest(disconFromCatDoc);
        }

        // not implemented: status subscription handling:
        else if (doc instanceof SubscribeSensorStatusRequestDocument) {
            if (log.isDebugEnabled())
                log.debug("Post subscribeSensorStatus request");
            request = new SirSubscriptionRequest(SirConstants.Operations.SubscribeSensorStatus.name());
        }
        else if (doc instanceof RenewSensorStatusSubscriptionRequestDocument) {
            if (log.isDebugEnabled())
                log.debug("Post renewSensorStatusSubscription request");
            request = new SirSubscriptionRequest(SirConstants.Operations.RenewSensorStatusSubscription.name());
        }
        else if (doc instanceof CancelSensorStatusSubscriptionRequestDocument) {
            if (log.isDebugEnabled())
                log.debug("Post cancelSensorStatusSubscription request");
            request = new SirSubscriptionRequest(SirConstants.Operations.CancelSensorStatusSubscription.name());
        }

        if (request == null) {
            log.error("Invalid Request!");
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidRequest, null, "The request is invalid! ");
            throw se;
        }
        return request;
    }
}