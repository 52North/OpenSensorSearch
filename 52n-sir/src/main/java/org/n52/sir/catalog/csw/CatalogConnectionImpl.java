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

package org.n52.sir.catalog.csw;

import java.net.URL;

import org.n52.sir.catalog.ICatalogConnection;

/**
 * 
 * Class encapsulates all inormation that is required to start and also persistently store a connection to a
 * catalog.
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class CatalogConnectionImpl implements ICatalogConnection {

    private URL catalogURL;
    private String connectionID;
    private int pushIntervalSeconds;
    private String status;

    public CatalogConnectionImpl(String connectionID, URL url, int pushInterval, String connectionStatus) {
        this.connectionID = connectionID;
        this.catalogURL = url;
        this.pushIntervalSeconds = pushInterval;
        this.status = connectionStatus;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.catalog.ICatalogConnection#getCatalogURL()
     */
    @Override
    public URL getCatalogURL() {
        return this.catalogURL;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.catalog.ICatalogConnection#getConnectionID()
     */
    @Override
    public String getConnectionID() {
        return this.connectionID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.catalog.ICatalogConnection#getPushIntervalSeconds()
     */
    @Override
    public int getPushIntervalSeconds() {
        return this.pushIntervalSeconds;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.catalog.ICatalogConnection#getStatus()
     */
    @Override
    public String getStatus() {
        return this.status;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CatalocConnection [connectionID=");
        sb.append(this.connectionID);
        sb.append(", pushIntervalSeconds=");
        sb.append(this.pushIntervalSeconds);
        sb.append(", catalogURL=");
        sb.append(this.catalogURL);
        sb.append(", status=");
        sb.append(this.status);
        sb.append("]");
        return sb.toString();
    }

}
