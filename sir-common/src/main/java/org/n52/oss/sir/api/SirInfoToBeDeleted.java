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
 * @author Daniel Nüst
 * 
 */
public class SirInfoToBeDeleted {

    private boolean deleteSensor;

    private SirSensorIdentification sensorIdentification;

    private SirServiceInfo serviceInfo;

    /**
     * 
     */
    public SirInfoToBeDeleted() {
        //
    }

    /**
     * @param sensorIdentification
     * @param deleteSensor
     * @param serviceInfo
     */
    public SirInfoToBeDeleted(SirSensorIdentification sensorIdentification,
                              boolean deleteSensor,
                              SirServiceInfo serviceInfo) {
        this.sensorIdentification = sensorIdentification;
        this.deleteSensor = deleteSensor;
        this.serviceInfo = serviceInfo;
    }

    /**
     * @return the sensorIdentification
     */
    public SirSensorIdentification getSensorIdentification() {
        return this.sensorIdentification;
    }

    /**
     * @return the serviceInfo
     */
    public SirServiceInfo getServiceInfo() {
        return this.serviceInfo;
    }

    /**
     * @return the deleteSensor
     */
    public boolean isDeleteSensor() {
        return this.deleteSensor;
    }

    /**
     * @param deleteSensor
     *        the deleteSensor to set
     */
    public void setDeleteSensor(boolean deleteSensor) {
        this.deleteSensor = deleteSensor;
    }

    /**
     * @param sensorIdentification
     *        the sensorIdentification to set
     */
    public void setSensorIdentification(SirSensorIdentification sensorIdentification) {
        this.sensorIdentification = sensorIdentification;
    }

    /**
     * @param serviceInfo
     *        the serviceInfo to set
     */
    public void setServiceInfo(SirServiceInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

}
