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

package org.n52.sir.datastructure.detailed;

import java.util.Collection;
import java.util.Date;

import org.n52.sir.datastructure.SirSensorDescription;

public class SirDetailedSensorDescription extends SirSensorDescription {
    
    private String id;
    private Collection<String> keywords;
    private Date begineDate;
    private String location;

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    private String description;
    private double bbox_x;

    public double getbbox_x() {
        return this.bbox_x;
    }

    public void setbbox_x(double bbox_x) {
        this.bbox_x = bbox_x;
    }

    public double getbbox_y() {
        return this.bbox_y;
    }

    public void setbbox_y(double bbox_y) {
        this.bbox_y = bbox_y;
    }

    private double bbox_y;

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getBegineDate() {
        return this.begineDate;
    }

    public void setBegineDate(Date begineDate) {
        this.begineDate = begineDate;
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    private Date endDate;
    private Collection<String> classifiers;
    private Collection<String> identifiers;
    private Collection<String> contacts;
    private Collection<String> inputs;
    private Collection<String> outputs;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Collection<String> getKeywords() {
        return this.keywords;
    }

    public void setKeywords(Collection<String> keywords) {
        this.keywords = keywords;
    }

    public SirDetailedSensorDescription() {
        //
    }

    public void setClassifiers(Collection<String> classifiers) {
        this.classifiers = classifiers;
    }

    public Collection<String> getClassifiers() {
        return this.classifiers;
    }

    public Collection< ? extends Object> getIdentifiers() {
        return this.identifiers;
    }

    public void setIdentifiers(Collection<String> identifiers) {
        this.identifiers = identifiers;
    }

    public void setContacts(Collection<String> contacts) {
        this.contacts = contacts;
    }

    public Collection<String> getContacts() {
        return this.contacts;

    }

    public void setInputs(Collection<String> inputs) {
        this.inputs = inputs;
    }

    public Collection<String> getInputs() {
        return this.inputs;
    }

    public void setOutputs(Collection<String> outputs) {
        this.outputs = outputs;
    }

    public Collection<String> getOutputs() {
        return this.outputs;
    }

}
