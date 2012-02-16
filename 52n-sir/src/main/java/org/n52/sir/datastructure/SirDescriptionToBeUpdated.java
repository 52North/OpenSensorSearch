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
