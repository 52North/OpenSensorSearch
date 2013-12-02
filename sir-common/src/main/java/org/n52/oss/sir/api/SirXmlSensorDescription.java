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

import org.apache.xmlbeans.XmlObject;

/**
 * @author Daniel Nüst
 * 
 */
public class SirXmlSensorDescription extends SirSensorDescription {

    XmlObject description;

    public SirXmlSensorDescription(XmlObject sensorDescription) {
        this.description = sensorDescription;
    }

    /**
     * @return the description
     */
    public XmlObject getDescription() {
        return this.description;
    }

    /**
     * @param description
     *        the description to set
     */
    public void setDescription(XmlObject description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "SirXmlSensorDescription" + this.description.xmlText().substring(0, 100);
    }
}
