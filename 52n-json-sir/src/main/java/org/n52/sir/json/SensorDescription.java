/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */

package org.n52.sir.json;

/**
 * @author Daniel Nüst
 * 
 */
public class SensorDescription {

    private BoundingBox boundingBox;

    private String text;

    private String url;

    public SensorDescription() {
        // empty constructor to allow deserialization
    }

    public SensorDescription(String url, String text, BoundingBox boundingBox) {
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
