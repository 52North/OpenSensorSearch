/**
 * Copyright 2013 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.sir.catalog;

import java.net.URL;


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

    @Override
    public URL getCatalogURL() {
        return this.catalogURL;
    }

    @Override
    public String getConnectionID() {
        return this.connectionID;
    }

    @Override
    public int getPushIntervalSeconds() {
        return this.pushIntervalSeconds;
    }

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
