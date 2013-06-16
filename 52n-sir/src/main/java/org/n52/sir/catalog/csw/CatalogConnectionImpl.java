/**
 * ﻿Copyright (C) 2012 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
