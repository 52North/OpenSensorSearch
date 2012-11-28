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

package org.n52.sir.request;

/**
 * Internal request to harvest a service
 * 
 * @author Jan Schulte
 * 
 */
public class SirHarvestServiceRequest extends AbstractSirRequest {

    /**
     * type of the service
     */
    private String serviceType;

    /**
     * url of the service
     */
    private String serviceUrl;

    /**
     * @return the serviceType
     */
    public String getServiceType() {
        return this.serviceType;
    }

    /**
     * @return the serviceUrl
     */
    public String getServiceUrl() {
        return this.serviceUrl;
    }

    /**
     * @param serviceType
     *        the serviceType to set
     */
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    /**
     * @param serviceUrl
     *        the serviceUrl to set
     */
    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("HarvestServiceRequest: ");
        sb.append("ServiceType: " + this.serviceType);
        sb.append(", ServiceURL: " + this.serviceUrl);
        return sb.toString();
    }

}
