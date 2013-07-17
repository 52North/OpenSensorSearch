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

/**
 * @author Daniel Nüst
 * 
 */
public class SirSimpleSensorDescription extends SirSensorDescription {

    private SirBoundingBox boundingBox;

    private String descriptionText;

    private String sensorDescriptionURL;

    public SirBoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    public String getDescriptionText() {
        return this.descriptionText;
    }

    public String getSensorDescriptionURL() {
        return this.sensorDescriptionURL;
    }

    public void setBoundingBox(SirBoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    public void setDescriptionText(String descriptionText) {
        this.descriptionText = descriptionText;
    }

    public void setSensorDescriptionURL(String sensorDescriptionURL) {
        this.sensorDescriptionURL = sensorDescriptionURL;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SimpleSensorDescription [url: ");
        sb.append(this.sensorDescriptionURL);
        sb.append(", descriptionText: ");
        sb.append(this.descriptionText);
        return sb.toString();
    }

}
