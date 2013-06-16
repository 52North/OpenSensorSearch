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
package org.n52.sir.datastructure;

import java.util.Collection;
import java.util.Date;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class SirSearchResultElement {

    private Date lastUpdate;

    private SirSensorDescription sensorDescription;

    private String sensorIdInSir;

    private Collection<SirServiceReference> serviceReferences;

    public SirSearchResultElement() {
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
    public SirSensorDescription getSensorDescription() {
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
    public Collection<SirServiceReference> getServiceReferences() {
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
    public void setSensorDescription(SirSensorDescription sensorDescription) {
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
    public void setServiceReferences(Collection<SirServiceReference> serviceReferences) {
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
}
