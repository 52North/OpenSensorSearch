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

package org.n52.sir.json;

import java.util.Collection;
import java.util.Date;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class SearchResultElement { // implements JsonSerializableWithType {

    private Date lastUpdate;

    private SensorDescription sensorDescription;

    private String sensorIdInSir;

    private Collection<ServiceReference> serviceReferences;

    private Collection<String> classifiers;

    private Collection<String> keywords;

    public Collection<String> getKeywords() {
        return this.keywords;
    }

    public void setKeywords(Collection<String> keywords) {
        this.keywords = keywords;
    }

    public Collection<String> getClassifiers() {
        return this.classifiers;
    }

    public void setClassifiers(Collection<String> classifiers) {
        this.classifiers = classifiers;
    }

    public Collection< ? extends Object> getIdentifiers() {
        return this.identifiers;
    }

    public void setIdentifiers(Collection< ? extends Object> identifiers) {
        this.identifiers = identifiers;
    }

    public Collection<String> getContacts() {
        return this.contacts;
    }

    public void setContacts(Collection<String> contacts) {
        this.contacts = contacts;
    }

    public Collection<String> getInputs() {
        return this.inputs;
    }

    public void setInputs(Collection<String> inputs) {
        this.inputs = inputs;
    }

    public Collection<String> getOutputs() {
        return this.outputs;
    }

    public void setOutputs(Collection<String> outputs) {
        this.outputs = outputs;
    }

    private Collection< ? extends Object> identifiers;
    private Collection<String> contacts;
    private Collection<String> inputs;
    private Collection<String> outputs;

    public Date getBeginDate() {
        return this.beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    private Date beginDate;

    private Date endDate;

    public SearchResultElement() {
        // empty constructor for deserialization
    }

    /**
     * @return the lastUpdate
     */
    public Date getLastUpdate() {
        return this.lastUpdate;
    }

    /**
     * @return the sensorDescription
     */
    public SensorDescription getSensorDescription() {
        return this.sensorDescription;
    }

    /**
     * @return the sensorIdInSir
     */
    public String getSensorIdInSir() {
        return this.sensorIdInSir;
    }

    /**
     * @return the serviceDescriptions
     */
    public Collection<ServiceReference> getServiceReferences() {
        return this.serviceReferences;
    }

    /**
     * @param lastUpdate
     *        the lastUpdate to set
     */
    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * @param sensorDescription
     *        the sensorDescription to set
     */
    public void setSensorDescription(SensorDescription sensorDescription) {
        this.sensorDescription = sensorDescription;
    }

    /**
     * @param sensorIdInSir
     *        the sensorIdInSir to set
     */
    public void setSensorIdInSir(String sensorIdInSir) {
        this.sensorIdInSir = sensorIdInSir;
    }

    /**
     * @param serviceDescriptions
     *        the serviceDescriptions to set
     */
    public void setServiceReferences(Collection<ServiceReference> serviceReferences) {
        this.serviceReferences = serviceReferences;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SearchResultElement: ");
        sb.append(", SensorID in SIR: " + this.sensorIdInSir);
        sb.append(", ServiceDescription: " + this.serviceReferences);
        sb.append(", Last update: " + this.lastUpdate);
        return sb.toString();
    }

    // @Override
    // public void serialize(JsonGenerator arg0, SerializerProvider arg1) throws
    // IOException,
    // JsonProcessingException {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // @Override
    // public void serializeWithType(JsonGenerator arg0, SerializerProvider
    // arg1, TypeSerializer arg2) throws
    // IOException,
    // JsonProcessingException {
    // // TODO Auto-generated method stub
    //
    // }
}
