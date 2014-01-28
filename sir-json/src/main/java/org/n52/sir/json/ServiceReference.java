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
package org.n52.sir.json;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Daniel Nüst
 * 
 */
@XmlRootElement
public class ServiceReference { // extends SirSensorIdentification {

    private Service service;

    private String serviceSpecificSensorId;

    public ServiceReference() {
        // empty constructor for deserialization
    }

    public ServiceReference(Service service, String serviceSpecificSensorId) {
        this.service = service;
        this.serviceSpecificSensorId = serviceSpecificSensorId;
    }

    @XmlElement
    public String getServiceType() {
        return this.service.getType();
    }

    @XmlElement
    public String getServiceUrl() {
        return this.service.getUrl();
    }

    @XmlElement
    public String getServiceSpecificSensorId() {
        return this.serviceSpecificSensorId;
    }

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
