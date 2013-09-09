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
import java.util.Iterator;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
@XmlRootElement
@JsonInclude(Include.NON_NULL)
public class SearchResultElement {

    private Date lastUpdate;

    private SimpleSensorDescription sensorDescription;

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

    public Collection<String> getIdentifiers() {
        return this.identifiers;
    }

    public void setIdentifiers(Collection<String> identifiers) {
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

    private Collection<String> identifiers;
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
    public SimpleSensorDescription getSensorDescription() {
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
    public void setSensorDescription(SimpleSensorDescription sensorDescription) {
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
        final int maxLen = 3;
        StringBuilder builder = new StringBuilder();
        builder.append("SearchResultElement [");
        if (this.lastUpdate != null) {
            builder.append("lastUpdate=");
            builder.append(this.lastUpdate);
            builder.append(", ");
        }
        if (this.sensorDescription != null) {
            builder.append("sensorDescription=");
            builder.append(this.sensorDescription);
            builder.append(", ");
        }
        if (this.sensorIdInSir != null) {
            builder.append("sensorIdInSir=");
            builder.append(this.sensorIdInSir);
            builder.append(", ");
        }
        if (this.serviceReferences != null) {
            builder.append("serviceReferences=");
            builder.append(toString(this.serviceReferences, maxLen));
            builder.append(", ");
        }
        if (this.classifiers != null) {
            builder.append("classifiers=");
            builder.append(toString(this.classifiers, maxLen));
            builder.append(", ");
        }
        if (this.keywords != null) {
            builder.append("keywords=");
            builder.append(toString(this.keywords, maxLen));
            builder.append(", ");
        }
        if (this.identifiers != null) {
            builder.append("identifiers=");
            builder.append(toString(this.identifiers, maxLen));
            builder.append(", ");
        }
        if (this.contacts != null) {
            builder.append("contacts=");
            builder.append(toString(this.contacts, maxLen));
            builder.append(", ");
        }
        if (this.inputs != null) {
            builder.append("inputs=");
            builder.append(toString(this.inputs, maxLen));
            builder.append(", ");
        }
        if (this.outputs != null) {
            builder.append("outputs=");
            builder.append(toString(this.outputs, maxLen));
            builder.append(", ");
        }
        if (this.beginDate != null) {
            builder.append("beginDate=");
            builder.append(this.beginDate);
            builder.append(", ");
        }
        if (this.endDate != null) {
            builder.append("endDate=");
            builder.append(this.endDate);
        }
        builder.append("]");
        return builder.toString();
    }

    private String toString(Collection< ? > collection, int maxLen) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        int i = 0;
        for (Iterator< ? > iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
            if (i > 0)
                builder.append(", ");
            builder.append(iterator.next());
        }
        builder.append("]");
        return builder.toString();
    }

}
