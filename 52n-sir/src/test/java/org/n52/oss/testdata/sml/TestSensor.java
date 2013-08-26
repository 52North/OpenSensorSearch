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
package org.n52.oss.testdata.sml;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.opengis.sensorML.x101.ComponentsDocument.Components.ComponentList.Component;
import net.opengis.sensorML.x101.ContactDocument.Contact;
import net.opengis.sensorML.x101.SensorMLDocument;

/**
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class TestSensor {

    protected String gmlDescription;

    protected Collection<String> keywords;

    protected String uniqueId;

    protected String longName;

    protected String shortName;

    protected String intendedApplication;

    protected String sensorType;

    protected String validTimeBegin;

    protected String validTimeEnd;

    protected double[] bboxLowerCorner;

    protected double[] bboxUpperCorner;

    protected Contact contact;

    protected double[] latLonPosition;

    protected double altitude;

    protected Collection<Map<String, String>> interfaces;

    protected Collection<Map<String, String>> inputs;

    protected Collection<Map<String, String>> output;

    private Collection<Component> components;

    private String locationId;

    /**
     * @param gmlDescription
     * @param keywords
     * @param uniqueId
     * @param longName
     * @param shortName
     * @param intendedApplication
     * @param sensorType
     * @param validTimeBegin
     * @param validTimeEnd
     * @param bboxLowerCorner
     * @param bboxUpperCorner
     * @param contact
     * @param latLonPosition
     * @param altitude
     * @param interfaces
     * @param inputs
     * @param output
     * @param components
     */
    public TestSensor(String gmlDescription,
                      Collection<String> keywords,
                      String uniqueId,
                      String longName,
                      String shortName,
                      String intendedApplication,
                      String sensorType,
                      String validTimeBegin,
                      String validTimeEnd,
                      double[] bboxLowerCorner,
                      double[] bboxUpperCorner,
                      String locationId,
                      Contact contact,
                      double[] latLonPosition,
                      double altitude,
                      Collection<Map<String, String>> interfaces,
                      Collection<Map<String, String>> inputs,
                      Collection<Map<String, String>> output,
                      Collection<Component> components) {
        this.gmlDescription = gmlDescription;
        this.keywords = keywords;
        this.uniqueId = uniqueId;
        this.longName = longName;
        this.shortName = shortName;
        this.intendedApplication = intendedApplication;
        this.sensorType = sensorType;
        this.validTimeBegin = validTimeBegin;
        this.validTimeEnd = validTimeEnd;
        this.bboxLowerCorner = bboxLowerCorner;
        this.bboxUpperCorner = bboxUpperCorner;
        this.locationId = locationId;
        this.contact = contact;
        this.latLonPosition = latLonPosition;
        this.altitude = altitude;
        this.interfaces = interfaces;
        this.inputs = inputs;
        this.output = output;
        this.components = components;
    }

    /**
     * @return the locationId
     */
    public String getLocationId() {
        return this.locationId;
    }

    /**
     * @param locationId
     *        the locationId to set
     */
    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    /**
     * @return the gmlDescription
     */
    public String getGmlDescription() {
        return this.gmlDescription;
    }

    /**
     * @param gmlDescription
     *        the gmlDescription to set
     */
    public void setGmlDescription(String gmlDescription) {
        this.gmlDescription = gmlDescription;
    }

    /**
     * @return the keywords
     */
    public Collection<String> getKeywords() {
        return this.keywords;
    }

    /**
     * @param keywords
     *        the keywords to set
     */
    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    /**
     * @return the uniqueId
     */
    public String getUniqueId() {
        return this.uniqueId;
    }

    /**
     * @param uniqueId
     *        the uniqueId to set
     */
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    /**
     * @return the longName
     */
    public String getLongName() {
        return this.longName;
    }

    /**
     * @param longName
     *        the longName to set
     */
    public void setLongName(String longName) {
        this.longName = longName;
    }

    /**
     * @return the shortName
     */
    public String getShortName() {
        return this.shortName;
    }

    /**
     * @param shortName
     *        the shortName to set
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * @return the intendedApplication
     */
    public String getIntendedApplication() {
        return this.intendedApplication;
    }

    /**
     * @param intendedApplication
     *        the intendedApplication to set
     */
    public void setIntendedApplication(String intendedApplication) {
        this.intendedApplication = intendedApplication;
    }

    /**
     * @return the sensorType
     */
    public String getSensorType() {
        return this.sensorType;
    }

    /**
     * @param sensorType
     *        the sensorType to set
     */
    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    /**
     * @return the validTimeBegin
     */
    public String getValidTimeBegin() {
        return this.validTimeBegin;
    }

    /**
     * @param validTimeBegin
     *        the validTimeBegin to set
     */
    public void setValidTimeBegin(String validTimeBegin) {
        this.validTimeBegin = validTimeBegin;
    }

    /**
     * @return the validTimeEnd
     */
    public String getValidTimeEnd() {
        return this.validTimeEnd;
    }

    /**
     * @param validTimeEnd
     *        the validTimeEnd to set
     */
    public void setValidTimeEnd(String validTimeEnd) {
        this.validTimeEnd = validTimeEnd;
    }

    /**
     * @return the bboxLowerCorner
     */
    public double[] getBboxLowerCorner() {
        return this.bboxLowerCorner;
    }

    /**
     * @param bboxLowerCorner
     *        the bboxLowerCorner to set
     */
    public void setBboxLowerCorner(double[] bboxLowerCorner) {
        this.bboxLowerCorner = bboxLowerCorner;
    }

    /**
     * @return the bboxUpperCorner
     */
    public double[] getBboxUpperCorner() {
        return this.bboxUpperCorner;
    }

    /**
     * @param bboxUpperCorner
     *        the bboxUpperCorner to set
     */
    public void setBboxUpperCorner(double[] bboxUpperCorner) {
        this.bboxUpperCorner = bboxUpperCorner;
    }

    /**
     * @return the contact
     */
    public Contact getContact() {
        return this.contact;
    }

    /**
     * @param contact
     *        the contact to set
     */
    public void setContact(Contact contact) {
        this.contact = contact;
    }

    /**
     * @return the latLonPosition
     */
    public double[] getLatLonPosition() {
        return this.latLonPosition;
    }

    /**
     * @param latLonPosition
     *        the latLonPosition to set
     */
    public void setLatLonPosition(double[] latLonPosition) {
        this.latLonPosition = latLonPosition;
    }

    /**
     * @return the altitude
     */
    public double getAltitude() {
        return this.altitude;
    }

    /**
     * @param altitude
     *        the altitude to set
     */
    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    /**
     * @return the interfaces
     */
    public Collection<Map<String, String>> getInterfaces() {
        return this.interfaces;
    }

    /**
     * @param interfaces
     *        the interfaces to set
     */
    public void setInterfaces(Collection<Map<String, String>> interfaces) {
        this.interfaces = interfaces;
    }

    /**
     * @return the inputs
     */
    public Collection<Map<String, String>> getInputs() {
        return this.inputs;
    }

    /**
     * @param inputs
     *        the inputs to set
     */
    public void setInputs(Collection<Map<String, String>> inputs) {
        this.inputs = inputs;
    }

    /**
     * @return the output
     */
    public Collection<Map<String, String>> getOutput() {
        return this.output;
    }

    /**
     * @param output
     *        the output to set
     */
    public void setOutput(Collection<Map<String, String>> output) {
        this.output = output;
    }

    /**
     * @return the components
     */
    public Collection<Component> getComponents() {
        return this.components;
    }

    /**
     * @param components
     *        the components to set
     */
    public void setComponents(Collection<Component> components) {
        this.components = components;
    }

    /**
     * 
     * @return
     */
    public final SensorMLDocument getSensorMLDocument() {
        SensorMLEncoder encoder = SensorMLEncoderFactory.getFactory().getEncoder();
        return encoder.encode(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TestSensor [uniqueId: ");
        sb.append(this.uniqueId);
        sb.append(", keywords: ");
        sb.append(Arrays.toString(this.keywords.toArray()));
        sb.append(", gmlDescription: ");
        sb.append(this.gmlDescription);
        sb.append(", intendedApplication: ");
        sb.append(this.intendedApplication);
        sb.append(", sensorType: ");
        sb.append(this.sensorType);
        sb.append(", validTime (begin, end): (");
        sb.append(this.validTimeBegin);
        sb.append(", ");
        sb.append(this.validTimeEnd);
        sb.append(", latLonPosition, altitude: ");
        sb.append(Arrays.toString(this.latLonPosition));
        sb.append(", ");
        sb.append(this.altitude);
        sb.append(", bbox (upperCorner, lowerCorner): (");
        sb.append(Arrays.toString(this.bboxUpperCorner));
        sb.append("; ");
        sb.append(Arrays.toString(this.bboxLowerCorner));
        sb.append("]");
        return sb.toString();
    }

}
