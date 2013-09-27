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
package org.n52.sir.client;

import java.util.Collection;

import org.n52.sir.SirConfigurator;
import org.n52.sir.catalog.ICatalogStatusHandler;

/**
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class CatalogPushInfoBean extends TestClientBean {

    private ICatalogStatusHandler catalogStatusHandler;

    /**
     * 
     */
    public CatalogPushInfoBean() {
        this.catalogStatusHandler = SirConfigurator.getInstance().getCatalogStatusHandler();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.client.beans.AbstractBean#buildRequest()
     */
    @Override
    public void buildRequest() {
        throw new UnsupportedOperationException("Method not implemented!");
    }

    /**
     * 
     * @return
     */
    public String getInfoString() {
        StringBuilder sb = new StringBuilder();
        Collection<String> currentList = this.catalogStatusHandler.getRuntimeInfo();
        for (String s : currentList) {
            sb.append(s);
            sb.append("\n\n");
        }
        return sb.toString();
    }

    /**
     * 
     * @return
     */
    public int getMaxEventsCount() {
        return this.catalogStatusHandler.getMaximumInfolistSize();
    }

}
