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

import java.util.Collection;

import org.n52.sir.datastructure.SirSensorIdentification;
import org.n52.sir.datastructure.SirStatus;

/**
 * @author Jan Schulte
 * 
 */
public class SirInsertSensorStatusRequest extends AbstractSirRequest {

    /**
     * the sensor identification
     */
    private SirSensorIdentification sensIdent;

    /**
     * status of the sensor
     */
    private Collection<SirStatus> status;

    /**
     * @return the sensIdent
     */
    public SirSensorIdentification getSensIdent() {
        return this.sensIdent;
    }

    /**
     * @return the status
     */
    public Collection<SirStatus> getStatus() {
        return this.status;
    }

    /**
     * @param sensIdent
     *        the sensIdent to set
     */
    public void setSensIdent(SirSensorIdentification sensIdent) {
        this.sensIdent = sensIdent;
    }

    /**
     * @param status
     *        the status to set
     */
    public void setStatus(Collection<SirStatus> status) {
        this.status = status;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Info to be inserted: ");
        sb.append("Identification: " + this.sensIdent);
        sb.append(" Status: " + this.status);
        return sb.toString();
    }
}
