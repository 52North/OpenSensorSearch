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
 * @author Daniel Nüst
 * 
 */
public class SirDescriptionToBeUpdated {

    private XmlObject sensorDescription;

    private SirSensorIdentification sensorIdentification;

    /**
     * @return the sensorDescription
     */
    public XmlObject getSensorDescription() {
        return this.sensorDescription;
    }

    /**
     * @return the sensorIdentification
     */
    public SirSensorIdentification getSensorIdentification() {
        return this.sensorIdentification;
    }

    /**
     * @param sensorDescription
     *        the sensorDescription to set
     */
    public void setSensorDescription(XmlObject sensorDescription) {
        this.sensorDescription = sensorDescription;
    }

    /**
     * @param sensorIdentification
     *        the sensorIdentification to set
     */
    public void setSensorIdentification(SirSensorIdentification sensorIdentification) {
        this.sensorIdentification = sensorIdentification;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SirDescriptionToBeUpdated: ");
        sb.append("\nSensorIdentification: ");
        sb.append(this.sensorIdentification);
        sb.append("\nSensorDescription: ");
        sb.append(this.sensorDescription);
        return sb.toString();
    }

}
