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
package org.n52.sir.request;

import org.n52.sir.SirConstants;
import org.n52.sir.SirConstants.Operations;

/**
 * class represents a GetCapabilities request and encapsulates the parameters
 * 
 * @author Jan Schulte
 * 
 */
public class SirGetCapabilitiesRequest extends AbstractSirRequest {

    private String[] acceptFormats;

    private String[] acceptVersions;

    private String[] sections;

    private String updateSequence;

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
     * @param updateSequence
     *        the updateSequence to set
     */
    public void setUpdateSequence(String updateSequence) {
        this.updateSequence = updateSequence;
    }

    @Override
    public Operations getOperation() {
        return SirConstants.Operations.GetCapabilities;
    }

}