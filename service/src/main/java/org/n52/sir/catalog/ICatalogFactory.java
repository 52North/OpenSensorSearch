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
package org.n52.sir.catalog;

import java.net.URL;

import org.n52.oss.sir.ows.OwsExceptionReport;

/**
 * 
 * Factory for objects that access catalogue services, {@link ICatalog}. The created catalogs use the url that
 * is available via {@link ICatalogFactory#getCatalogUrl()} as their endpoint.
 * 
 * The method {@link ICatalogFactory#getCatalogConnection(String, URL, int, String)} shall be used to create
 * {@link ICatalogConnection} instances that contain the neccessary parameters to create and save a connection
 * to a catalog.
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public interface ICatalogFactory {

    /**
     * 
     * @param url
     *        the endpoint the catalog must use
     * @return a new instance of an ICatalog
     * @throws OwsExceptionReport
     */
    public abstract ICatalog getCatalog(URL url) throws OwsExceptionReport;

    /**
     * 
     * Method creates objects that encapsulate all information that is needed to persitently save a connection
     * to a catalog.
     * 
     * @param connectionID
     * @param url
     * @param pushInterval
     * @param newConnectionStatus
     * @return
     */
    public abstract ICatalogConnection getCatalogConnection(String connectionID,
                                                            URL url,
                                                            int pushInterval,
                                                            String newConnectionStatus);

}
