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
public class SirSimpleSensorDescription extends SirSensorDescription {

    private SirBoundingBox boundingBox;

    private String descriptionText;

    private String sensorDescriptionURL;

    public SirSimpleSensorDescription() {
        //
    }

    public SirSimpleSensorDescription(SirBoundingBox boundingBox, String descriptionText, String sensorDescriptionURL) {
        super();
        this.boundingBox = boundingBox;
        this.descriptionText = descriptionText;
        this.sensorDescriptionURL = sensorDescriptionURL;
    }

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
        StringBuilder builder = new StringBuilder();
        builder.append("SirSimpleSensorDescription [boundingBox=");
        builder.append(this.boundingBox);
        builder.append(", descriptionText=");
        builder.append(this.descriptionText);
        builder.append(", sensorDescriptionURL=");
        builder.append(this.sensorDescriptionURL);
        builder.append("]");
        return builder.toString();
    }

}
