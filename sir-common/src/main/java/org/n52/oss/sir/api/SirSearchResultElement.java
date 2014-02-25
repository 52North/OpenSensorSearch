/**
 * Copyright 2013 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.oss.sir.api;

import java.util.Collection;
import java.util.Date;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class SirSearchResultElement {

    private Date lastUpdate;

    private SirSensorDescription sensorDescription;

    private String sensorId;

    private Collection<SirServiceReference> serviceReferences;

    public SirSearchResultElement() {
        // empty constructor for deserialization
    }

    public Date getLastUpdate() {
        return this.lastUpdate;
    }

    public SirSensorDescription getSensorDescription() {
        return this.sensorDescription;
    }

    public String getSensorId() {
        return this.sensorId;
    }

    public Collection<SirServiceReference> getServiceReferences() {
        return this.serviceReferences;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void setSensorDescription(SirSensorDescription sensorDescription) {
        this.sensorDescription = sensorDescription;
    }

    public void setSensorId(String id) {
        this.sensorId = id;
    }

    public void setServiceReferences(Collection<SirServiceReference> serviceReferences) {
        this.serviceReferences = serviceReferences;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SirSearchResultElement [");
        if (this.sensorId != null) {
            builder.append("sensorId=");
            builder.append(this.sensorId);
            builder.append(", ");
        }
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
        if (this.serviceReferences != null) {
            builder.append("serviceReferences=");
            builder.append(this.serviceReferences);
        }
        builder.append("]");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( (this.sensorId == null) ? 0 : this.sensorId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SirSearchResultElement other = (SirSearchResultElement) obj;
        if (this.sensorId == null) {
            if (other.sensorId != null)
                return false;
        }
        else if ( !this.sensorId.equals(other.sensorId))
            return false;
        return true;
    }

}
