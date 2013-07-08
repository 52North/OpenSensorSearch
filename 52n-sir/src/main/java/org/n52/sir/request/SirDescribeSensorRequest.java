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

import org.n52.sir.SirConstants;
import org.n52.sir.SirConstants.Operations;

/**
 * Internal request to response with a sensorML by given sensor ID in Sir
 * 
 * @author Jan Schulte
 * 
 */
public class SirDescribeSensorRequest extends AbstractSirRequest {

    /**
     * sensor ID in the SIR
     */
    private String sensorIdInSir;

    /**
     * @return the sensorIdInSir
     */
    public String getSensorIdInSir() {
        return this.sensorIdInSir;
    }

    /**
     * @param sensorIdInSir
     *        the sensorIdInSir to set
     */
    public void setSensorIdInSir(String sensorIdInSir) {
        this.sensorIdInSir = sensorIdInSir;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("DescribeSensor: ");
        sb.append("SensorID in SIR: " + this.sensorIdInSir);
        return sb.toString();
    }

    @Override
    public Operations getOperation() {
        return SirConstants.Operations.DescribeSensor;
    }

}