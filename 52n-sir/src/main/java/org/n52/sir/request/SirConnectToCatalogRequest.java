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

import java.net.URL;

import org.n52.sir.catalog.ICatalogConnection;

/**
 * Internal request to connect to a CSW catalog service
 * 
 * @author Jan Schulte
 * 
 */
public class SirConnectToCatalogRequest extends AbstractSirRequest {

    /**
     * url of the catalog service
     */
    private URL cswUrl;

    /**
     * update interval in seconds. Default value is @see {@link ICatalogConnection#NO_PUSH_INTERVAL}.
     */
    private int pushInterval = ICatalogConnection.NO_PUSH_INTERVAL;

    /**
     * @return the cswUrl
     */
    public URL getCswUrl() {
        return this.cswUrl;
    }

    /**
     * @return the pushInterval
     */
    public int getPushInterval() {
        return this.pushInterval;
    }

    /**
     * @param cswUrl
     *        the cswUrl to set
     */
    public void setCswUrl(URL cswUrl) {
        this.cswUrl = cswUrl;
    }

    /**
     * @param pushInterval
     *        the pushInterval to set
     */
    public void setPushInterval(int pushInterval) {
        this.pushInterval = pushInterval;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("ConnectToCatalogRequest: ");
        sb.append("CswURL: " + this.cswUrl);
        sb.append(", PushInterval: " + this.pushInterval);
        return sb.toString();
    }
}
