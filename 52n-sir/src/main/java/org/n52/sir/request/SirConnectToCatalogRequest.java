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
package org.n52.sir.request;

import java.net.URL;

import org.n52.sir.SirConstants;
import org.n52.sir.SirConstants.Operations;
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

    @Override
    public Operations getOperation() {
        return SirConstants.Operations.ConnectToCatalog;
    }
}
