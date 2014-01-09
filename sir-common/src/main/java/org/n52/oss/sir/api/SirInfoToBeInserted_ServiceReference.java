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

/**
 * @author Daniel Nüst
 * 
 */
public class SirInfoToBeInserted_ServiceReference extends SirInfoToBeInserted {

    private InternalSensorID id;

    private Collection<SirServiceReference> serviceReferences;

    public InternalSensorID getID() {
        return this.id;
    }

    public Collection<SirServiceReference> getServiceReferences() {
        return this.serviceReferences;
    }

    public void setID(InternalSensorID id) {
        this.id = id;
    }

    public void setServiceReferences(Collection<SirServiceReference> serviceReferences) {
        this.serviceReferences = serviceReferences;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("InfoToBeInserted_ServiceReference:\nSensorID:");
        sb.append(this.id);
        sb.append("\nServiceReferences: ");
        sb.append(this.serviceReferences);
        return sb.toString();
    }

}
