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

import java.util.Collection;

import org.n52.oss.sir.api.SirSearchCriteria;
import org.n52.oss.sir.api.SirSensorIdentification;

/**
 * Internal request to search sensors
 * 
 * @author Jan Schulte
 * 
 */
public class SirSearchSensorRequest extends AbstractSirRequest {

    /**
     * Search Criteria
     */
    private SirSearchCriteria searchCriteria;

    /**
     * Collection of sensor identifications
     */
    private Collection<SirSensorIdentification> sensIdent;

    /**
     * if a simple response is requested
     */
    private boolean simpleResponse = false;

    /**
     * @return the searchCriteria
     */
    public SirSearchCriteria getSearchCriteria() {
        return this.searchCriteria;
    }

    /**
     * @return the sensIdent
     */
    public Collection<SirSensorIdentification> getSensIdent() {
        return this.sensIdent;
    }

    /**
     * @return the simpleResponse
     */
    public boolean isSimpleResponse() {
        return this.simpleResponse;
    }

    /**
     * @param searchCriteria
     *        the searchCriteria to set
     */
    public void setSearchCriteria(SirSearchCriteria searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    /**
     * @param sensIdent
     *        the sensIdent to set
     */
    public void setSensIdent(Collection<SirSensorIdentification> sensIdent) {
        this.sensIdent = sensIdent;
    }

    /**
     * @param simpleResponse
     *        the simpleResponse to set
     */
    public void setSimpleResponse(boolean simpleResponse) {
        this.simpleResponse = simpleResponse;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SearchSensorRequest: ");
        sb.append(" " + this.sensIdent);
        sb.append(", SimpleResponse: " + this.simpleResponse);
        return sb.toString();
    }
}
