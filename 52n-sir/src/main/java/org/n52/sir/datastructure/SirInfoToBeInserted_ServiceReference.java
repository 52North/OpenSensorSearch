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

/**
 * @author Daniel Nüst
 * 
 */
public class SirInfoToBeInserted_ServiceReference extends SirInfoToBeInserted {

    /**
     * 
     */
    private SirSensorIDInSir sensorIDinSIR;

    /**
     * service association
     */
    private Collection<SirServiceReference> serviceReferences;

    /**
     * @return the sensorIDinSIR
     */
    public SirSensorIDInSir getSensorIDinSIR() {
        return this.sensorIDinSIR;
    }

    /**
     * @return the serviceReferences
     */
    public Collection<SirServiceReference> getServiceReferences() {
        return this.serviceReferences;
    }

    /**
     * @param sensorIDinSIR
     *        the sensorIDinSIR to set
     */
    public void setSensorIDinSIR(SirSensorIDInSir sensorIDinSIR) {
        this.sensorIDinSIR = sensorIDinSIR;
    }

    /**
     * @param serviceReferences
     *        the serviceReferences to set
     */
    public void setServiceReferences(Collection<SirServiceReference> serviceReferences) {
        this.serviceReferences = serviceReferences;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("InfoToBeInserted_ServiceReference:\nSensorIDinSIR:");
        sb.append(this.sensorIDinSIR);
        sb.append("\nServiceReferences: ");
        sb.append(this.serviceReferences);
        return sb.toString();
    }

}
