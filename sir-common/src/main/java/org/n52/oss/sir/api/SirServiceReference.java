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

/**
 * Represents an internal service description, which includes the service and a service specific sensor ID
 * 
 * @author Jan Schulte
 * 
 */
public class SirServiceReference extends SirSensorIdentification {

    private SirService service;

    private String serviceSpecificSensorId;

    public SirServiceReference() {
        //
    }

    public SirServiceReference(SirService service, String serviceSpecificSensorId) {
        this.service = service;
        this.serviceSpecificSensorId = serviceSpecificSensorId;
    }

    /**
     * @return the service
     */
    public SirService getService() {
        return this.service;
    }

    /**
     * @return the serviceSpecificSensorId
     */
    public String getServiceSpecificSensorId() {
        return this.serviceSpecificSensorId;
    }

    /**
     * @param service
     *        the service to set
     */
    public void setService(SirService service) {
        this.service = service;
    }

    /**
     * @param serviceSpecificSensorId
     *        the serviceSpecificSensorId to set
     */
    public void setServiceSpecificSensorId(String serviceSpecificSensorId) {
        this.serviceSpecificSensorId = serviceSpecificSensorId;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("ServiceReference: ");
        sb.append(this.service);
        sb.append(" Service specific ID: " + this.serviceSpecificSensorId);
        return sb.toString();
    }

}
