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
package org.n52.sor.request;

import org.n52.sor.ISorRequest;

/**
 * @created 15-Okt-2008 16:25:09
 * @author Jan Schulte
 * @version 1.0
 */
public class SorGetCapabilitiesRequest implements ISorRequest {

    private String service;

    private String updateSequence;

    private String[] acceptVersions;

    private String[] sections;

    private String[] acceptFormats;

    /**
     * @return the acceptFormats
     */
    public String[] getAcceptFormats() {
        return this.acceptFormats;
    }

    /**
     * @return the acceptVersions
     */
    public String[] getAcceptVersions() {
        return this.acceptVersions;
    }

    /**
     * @return the sections
     */
    public String[] getSections() {
        return this.sections;
    }

    /**
     * @return the service
     */
    public String getService() {
        return this.service;
    }

    /**
     * @return the updateSequence
     */
    public String getUpdateSequence() {
        return this.updateSequence;
    }

    /**
     * @param acceptFormats
     *        the acceptFormats to set
     */
    public void setAcceptFormats(String[] acceptFormats) {
        this.acceptFormats = acceptFormats;
    }

    /**
     * @param acceptVersions
     *        the acceptVersions to set
     */
    public void setAcceptVersions(String[] acceptVersions) {
        this.acceptVersions = acceptVersions;
    }

    /**
     * @param sections
     *        the sections to set
     */
    public void setSections(String[] sections) {
        this.sections = sections;
    }

    /**
     * @param service
     *        the service to set
     */
    public void setService(String service) {
        this.service = service;
    }

    /**
     * @param updateSequence
     *        the updateSequence to set
     */
    public void setUpdateSequence(String updateSequence) {
        this.updateSequence = updateSequence;
    }

    @Override
    public String toString() {
        return "GetCapabilities request!";
    }

}