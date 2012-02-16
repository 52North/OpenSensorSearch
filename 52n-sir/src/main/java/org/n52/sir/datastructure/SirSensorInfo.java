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
     * @return the sensorMl
     */
    public XmlObject getSensorMl() {
        return this.sensorMl;
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
