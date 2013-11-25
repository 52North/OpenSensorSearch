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

import java.util.Collection;

import org.n52.oss.sir.api.SirService;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.catalog.ICatalogConnection;

/**
 * interface for the specific DAOFactories, offers methods to create the matching DAOs for the getCapabilities
 * operation
 * 
 * @author Jan Schulte
 * 
 */
public interface IGetCapabilitiesDAO {

    /**
     * @return Returns all connections from SIR to catalogs
     * @throws OwsExceptionReport
     */
    public Collection<ICatalogConnection> getCatalogConnections() throws OwsExceptionReport;

    /**
     * 
     * @return
     * @throws OwsExceptionReport
     */
    public long getPhenomenonCount() throws OwsExceptionReport;

    /**
     * @return Returns all phenomenon URNs in the SIR
     * @throws OwsExceptionReport
     */
    public Collection<String> getPhenomenonURNs() throws OwsExceptionReport;

    /**
     * 
     * @return
     * @throws OwsExceptionReport
     */
    public long getSensorCount() throws OwsExceptionReport;

    /**
     * 
     * @return
     * @throws OwsExceptionReport
     */
    public long getServiceCount() throws OwsExceptionReport;

    /**
     * @return Returns all services in the SIR
     * @throws OwsExceptionReport
     */
    public Collection<SirService> getServices() throws OwsExceptionReport;
}