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

import org.apache.xmlbeans.XmlObject;

/**
 * @author Jan Schulte
 * 
 */
public class SirSensorInfo {

    /**
     * sensor to be deleted
     */
    private boolean delete;

    /**
     * sensorML document to be updated
     */
    private XmlObject sensorMl;

    /**
     * @return the sensorMl
     */
    public XmlObject getSensorMl() {
        return this.sensorMl;
    }

    /**
     * @return the delete
     */
    public boolean isDelete() {
        return this.delete;
    }

    /**
     * @param delete
     *        the delete to set
     */
    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    /**
     * @param sensorMl
     *        the sensorMl to set
     */
    public void setSensorMl(XmlObject sensorMl) {
        this.sensorMl = sensorMl;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SensorInfo: ");
        sb.append("delete: " + this.delete);
        sb.append(" sensorML: " + this.sensorMl.toString().subSequence(1, 10));
        return sb.toString();
    }

}
