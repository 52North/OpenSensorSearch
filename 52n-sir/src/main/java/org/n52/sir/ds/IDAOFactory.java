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

import org.n52.sir.ows.OwsExceptionReport;

/**
 * interface for the specific DAOFactories, offers methods to create the matching DAOs for the operations
 * 
 * @author Jan Schulte
 * 
 */
public interface IDAOFactory {

    /**
     * @return Returns the CatalogStatusHandlerDAO
     * @throws OwsExceptionReport
     */
    ICatalogStatusHandlerDAO catalogStatusHandlerDAO();

    /**
     * @return Returns the ConncetToCatalogDAO
     * @throws OwsExceptionReport
     */
    IConnectToCatalogDAO connectToCatalogDAO() throws OwsExceptionReport;

    /**
     * @return Returns the DescribeSensorDAO
     * @throws OwsExceptionReport
     */
    IDescribeSensorDAO describeSensorDAO() throws OwsExceptionReport;

    /**
     * @return Returns the DisconnectFromCatalogDAO
     * @throws OwsExceptionReport
     */
    IDisconnectFromCatalogDAO disconnectFromCatalogDAO() throws OwsExceptionReport;

    /**
     * @return Returns the GetAllServicesDAO
     * @throws OwsExceptionReport
     */
    IGetAllServicesDAO getAllServicesDAO() throws OwsExceptionReport;

    /**
     * @return Returns the GetCapabilitiesDAO
     * @throws OwsExceptionReport
     */
    IGetCapabilitiesDAO getCapabilitiesDAO() throws OwsExceptionReport;

    /**
     * @return Returns the GetSensorStatusDAO
     * @throws OwsExceptionReport
     */
    IGetSensorStatusDAO getSensorStatusDAO() throws OwsExceptionReport;

    /**
     * @return Returns the HarvestServiceDAO
     * @throws OwsExceptionReport
     */
    IHarvestServiceDAO harvestServiceDAO() throws OwsExceptionReport;

    /**
     * @return Returns the InsertSensorInfoDAO
     * @throws OwsExceptionReport
     */
    IInsertSensorInfoDAO insertSensorInfoDAO() throws OwsExceptionReport;

    /**
     * @return Returns the InsertSensorStatusDAO
     * @throws OwsExceptionReport
     */
    IInsertSensorStatusDAO insertSensorStatusDAO() throws OwsExceptionReport;

    /**
     * @return Returns the SearchSensorDAO
     * @throws OwsExceptionReport
     */
    ISearchSensorDAO searchSensorDAO() throws OwsExceptionReport;
    
    IInsertHarvestScriptDAO insertHarvestScriptDAO() ;
    
    IInsertRemoteHarvestServer insertRemoteHarvestSensor();
    
    IUserAccountDAO userAccountDAO();
}