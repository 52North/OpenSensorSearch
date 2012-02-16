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
 * Represents an internal service description, which includes the service and a service specific sensor ID
 * 
 * @author Jan Schulte
 * 
 */
public class SirServiceReference extends SirSensorIdentification {

    private SirService service;

    private String serviceSpecificSensorId;

    public SirServiceReference() {
        //
    }

    public SirServiceReference(SirService service, String serviceSpecificSensorId) {
        this.service = service;
        this.serviceSpecificSensorId = serviceSpecificSensorId;
    }

    /**
     * @return the service
     */
    public SirService getService() {
        return this.service;
    }

    /**
     * @param service
     *        the service to set
     */
    public void setService(SirService service) {
        this.service = service;
    }

    /**
     * @return the serviceSpecificSensorId
     */
    public String getServiceSpecificSensorId() {
        return this.serviceSpecificSensorId;
    }

    /**
     * @param serviceSpecificSensorId
     *        the serviceSpecificSensorId to set
     */
    public void setServiceSpecificSensorId(String serviceSpecificSensorId) {
        this.serviceSpecificSensorId = serviceSpecificSensorId;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("ServiceReference: ");
        sb.append(this.service);
        sb.append(" Service specific ID: " + this.serviceSpecificSensorId);
        return sb.toString();
    }

}
