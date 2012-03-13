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

import java.util.Collection;

import org.apache.xmlbeans.XmlObject;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class SirInfoToBeInserted_SensorDescription extends SirInfoToBeInserted {

    /**
     * the sensor description
     */
    private XmlObject sensorDescription;

    /**
     * service association
     */
    private Collection<SirServiceReference> serviceReferences;

    /**
     * @return the sensorDescription
     */
    public XmlObject getSensorDescription() {
        return this.sensorDescription;
    }

    /**
     * @return the serviceInfos
     */
    public Collection<SirServiceReference> getServiceReferences() {
        return this.serviceReferences;
    }

    /**
     * @param sensorDescription
     *        the sensorDescription to set
     */
    public void setSensorDescription(XmlObject sensorDescription) {
        this.sensorDescription = sensorDescription;
    }

    /**
     * @param serviceRefs
     *        the serviceInfos to set
     */
    public void setServiceReferences(Collection<SirServiceReference> serviceRefs) {
        this.serviceReferences = serviceRefs;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("InfoToBeInserted_SensorDescription:\n");
        sb.append(this.sensorDescription);
        sb.append("\nServiceReferences: ");
        sb.append(this.serviceReferences);
        return sb.toString();
    }

}
