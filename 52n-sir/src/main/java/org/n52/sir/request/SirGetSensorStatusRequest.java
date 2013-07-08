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

import org.n52.sir.SirConstants;
import org.n52.sir.SirConstants.Operations;
import org.n52.sir.datastructure.SirPropertyFilter;
import org.n52.sir.datastructure.SirSearchCriteria;
import org.n52.sir.datastructure.SirSensorIdentification;

/**
 * Internal request to get sensor status
 * 
 * @author Jan Schulte
 * 
 */
public class SirGetSensorStatusRequest extends AbstractSirRequest {

    private Collection<SirPropertyFilter> propertyFilter;

    private SirSearchCriteria searchCriteria;

    private Collection<SirSensorIdentification> sensIdent;

    /**
     * @return the propertyFilter
     */
    public Collection<SirPropertyFilter> getPropertyFilter() {
        return this.propertyFilter;
    }

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
     * @param propertyFilter
     *        the propertyFilter to set
     */
    public void setPropertyFilter(Collection<SirPropertyFilter> propertyFilter) {
        this.propertyFilter = propertyFilter;
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

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("GetSensorStatusRequest: ");
        sb.append("SensorIdentifications: " + this.sensIdent);
        sb.append(" SearchCriteria: " + this.searchCriteria);
        sb.append(" PropertyFilter: " + this.propertyFilter);
        return sb.toString();
    }

    @Override
    public Operations getOperation() {
        return SirConstants.Operations.GetSensorStatus;
    }
}
