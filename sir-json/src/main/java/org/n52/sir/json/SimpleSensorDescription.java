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

package org.n52.sir.json;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Daniel Nüst
 * 
 */
@XmlRootElement
public class SimpleSensorDescription {

    private BoundingBox boundingBox;

    private String text;

    private String url;

    public SimpleSensorDescription() {
        // empty constructor to allow deserialization
    }

    public SimpleSensorDescription(String url, String text, BoundingBox boundingBox) {
        this.url = url;
        this.text = text;
        this.boundingBox = boundingBox;
    }

    /**
     * @return the boundingBox
     */
    public BoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    /**
     * @return the descriptionText
     */
    public String getText() {
        return this.text;
    }

    /**
     * @return the sensorDescriptionURL
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * @param boundingBox
     *        the boundingBox to set
     */
    public void setBoundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    /**
     * @param descriptionText
     *        the descriptionText to set
     */
    public void setText(String descriptionText) {
        this.text = descriptionText;
    }

    /**
     * @param sensorDescriptionURL
     *        the sensorDescriptionURL to set
     */
    public void setUrl(String sensorDescriptionURL) {
        this.url = sensorDescriptionURL;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SimpleSensorDescription [url: ");
        sb.append(this.url);
        sb.append(", descriptionText: ");
        sb.append(this.text);
        return sb.toString();
    }

}
