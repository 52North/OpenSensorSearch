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
 * 
 * @author Daniel Nüst
 * 
 */
public class ServiceReference { // extends SirSensorIdentification {

    private Service service;

    private String serviceSpecificSensorId;

    public ServiceReference() {
        // empty constructor for deserialization
    }

    public ServiceReference(Service service, String serviceSpecificSensorId) {
        this.service = service;
        this.serviceSpecificSensorId = serviceSpecificSensorId;
    }

    public Service getService() {
        return this.service;
    }

    public String getServiceSpecificSensorId() {
        return this.serviceSpecificSensorId;
    }

    public void setService(Service service) {
        this.service = service;
    }

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
