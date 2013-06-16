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
package org.n52.sir.ds;

import java.net.URL;
import java.util.List;

import org.n52.sir.catalog.ICatalogConnection;
import org.n52.sir.ows.OwsExceptionReport;

/**
 * @author Jan Schulte
 * 
 */
public interface IConnectToCatalogDAO {

    /**
     * Returns a list of all catalogs that have a push interval
     * 
     * @return a list of all catalogs that have a push interval
     * @throws OwsExceptionReport
     */
    List<ICatalogConnection> getCatalogConnectionList() throws OwsExceptionReport;

    /**
     * checks in database if connection exists. If exists, the ConnectionID is returned, else null
     * 
     * @param url
     *        url to the catalog service
     * @param pushInterval
     *        update interval
     * @return Returns the catalog ID in SIR or null, if not exists
     */
    String getConnectionID(URL url, int pushInterval) throws OwsExceptionReport;

    /**
     * insert a connection to a catalog service
     * 
     * @param cswUrl
     *        url to the catalog service
     * @param pushInterval
     *        update interval
     * @return Returns the catalog ID in SIR
     * @throws OwsExceptionReport
     */
    String insertConnection(URL cswUrl, int pushInterval) throws OwsExceptionReport;

    /**
     * update the connection in database
     * 
     * @param cswUrl
     *        url to the catalog service
     * @param pushInterval
     *        update interval
     * @throws OwsExceptionReport
     */
    void updateConnection(URL cswUrl, int pushInterval) throws OwsExceptionReport;

}
