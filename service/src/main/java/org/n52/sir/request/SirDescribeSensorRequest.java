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
package org.n52.sir.request;

/**
 * Internal request to response with a sensorML by given sensor ID in Sir
 * 
 * @author Jan Schulte
 * 
 */
public class SirDescribeSensorRequest extends AbstractSirRequest {

    private String sensorId;

    public String getSensorId() {
        return this.sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SirDescribeSensorRequest [");
        if (this.sensorId != null) {
            builder.append("sensorId=");
            builder.append(this.sensorId);
        }
        builder.append("]");
        return builder.toString();
    }

}