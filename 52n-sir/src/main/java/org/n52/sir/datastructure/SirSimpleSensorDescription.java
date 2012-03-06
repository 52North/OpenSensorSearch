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
package org.n52.sir.datastructure;

/**
 * @author Daniel Nüst
 * 
 */
public class SirSimpleSensorDescription extends SirSensorDescription {

    private String sensorDescriptionURL;

    private String descriptionText;
    
    private SirBoundingBox boundingBox;

    public String getSensorDescriptionURL() {
        return this.sensorDescriptionURL;
    }

    public String getDescriptionText() {
        return this.descriptionText;
    }

    public void setSensorDescriptionURL(String sensorDescriptionURL) {
        this.sensorDescriptionURL = sensorDescriptionURL;
    }

    public void setDescriptionText(String descriptionText) {
        this.descriptionText = descriptionText;
    }

    public SirBoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    public void setBoundingBox(SirBoundingBox boundingBox) {
        this.boundingBox = boundingBox;
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
