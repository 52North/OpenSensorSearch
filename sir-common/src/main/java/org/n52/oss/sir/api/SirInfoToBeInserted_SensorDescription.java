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
package org.n52.oss.sir.api;

import java.util.Collection;

import org.apache.xmlbeans.XmlObject;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class SirInfoToBeInserted_SensorDescription extends SirInfoToBeInserted {

    /**
     * the sensor description
     */
    private XmlObject sensorDescription;

    /**
     * service association
     */
    private Collection<SirServiceReference> serviceReferences;

    /**
     * @return the sensorDescription
     */
    public XmlObject getSensorDescription() {
        return this.sensorDescription;
    }

    /**
     * @return the serviceInfos
     */
    public Collection<SirServiceReference> getServiceReferences() {
        return this.serviceReferences;
    }

    /**
     * @param sensorDescription
     *        the sensorDescription to set
     */
    public void setSensorDescription(XmlObject sensorDescription) {
        this.sensorDescription = sensorDescription;
    }

    /**
     * @param serviceRefs
     *        the serviceInfos to set
     */
    public void setServiceReferences(Collection<SirServiceReference> serviceRefs) {
        this.serviceReferences = serviceRefs;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("InfoToBeInserted_SensorDescription:\n");
        sb.append(this.sensorDescription);
        sb.append("\nServiceReferences: ");
        sb.append(this.serviceReferences);
        return sb.toString();
    }

}
