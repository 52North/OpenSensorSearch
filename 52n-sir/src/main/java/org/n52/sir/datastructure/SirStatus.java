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

import java.util.Calendar;

/**
 * @author Jan Schulte
 * 
 */
public class SirStatus {

    private String propertyName;

    private Object propertyValue;

    private Calendar timestamp;

    private String uom;

    /**
     * @return the propertyName
     */
    public String getPropertyName() {
        return this.propertyName;
    }

    /**
     * @return the propertyValue
     */
    public Object getPropertyValue() {
        return this.propertyValue;
    }

    /**
     * @return the timestamp
     */
    public Calendar getTimestamp() {
        return this.timestamp;
    }

    /**
     * @return the uom
     */
    public String getUom() {
        return this.uom;
    }

    /**
     * @param propertyName
     *        the propertyName to set
     */
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * @param propertyValue
     *        the propertyValue to set
     */
    public void setPropertyValue(Object propertyValue) {
        this.propertyValue = propertyValue;
    }

    /**
     * @param timestamp
     *        the timestamp to set
     */
    public void setTimestamp(Calendar timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @param uom
     *        the uom to set
     */
    public void setUom(String uom) {
        this.uom = uom;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Status: ");
        sb.append("Property Name: " + this.propertyName);
        sb.append(" Property Value: " + this.propertyValue);
        sb.append(" Uom: " + this.uom);
        sb.append(" Timestamp: " + this.timestamp);
        return sb.toString();
    }

}
