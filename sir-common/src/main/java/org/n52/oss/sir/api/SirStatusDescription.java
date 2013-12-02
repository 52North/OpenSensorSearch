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
 * @author Jan Schulte
 * 
 */
public class SirStatusDescription {

    private String sensorId;

    private SirStatus status;

    public String getSensorId() {
        return this.sensorId;
    }

    public SirStatus getStatus() {
        return this.status;
    }

    public void setSensorId(String id) {
        this.sensorId = id;
    }

    public void setStatus(SirStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SensorID in SIR: " + this.sensorId);
        sb.append(" Status: " + this.status);
        return sb.toString();
    }

}
