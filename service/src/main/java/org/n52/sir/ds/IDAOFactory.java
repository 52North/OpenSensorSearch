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

import org.n52.oss.sir.ows.OwsExceptionReport;

/**
 * interface for the specific DAOFactories, offers methods to create the matching DAOs for the operations
 * 
 * @author Jan Schulte, Moh-Yakou, Daniel Nüst
 * 
 */
public interface IDAOFactory {

    ICatalogStatusHandlerDAO catalogStatusHandlerDAO();

    IConnectToCatalogDAO connectToCatalogDAO() throws OwsExceptionReport;

    IDescribeSensorDAO describeSensorDAO() throws OwsExceptionReport;

    IDisconnectFromCatalogDAO disconnectFromCatalogDAO() throws OwsExceptionReport;

    IGetAllServicesDAO getAllServicesDAO() throws OwsExceptionReport;

    IGetCapabilitiesDAO getCapabilitiesDAO() throws OwsExceptionReport;

    IGetSensorStatusDAO getSensorStatusDAO() throws OwsExceptionReport;

    IHarvestServiceDAO harvestServiceDAO() throws OwsExceptionReport;

    IInsertSensorInfoDAO insertSensorInfoDAO() throws OwsExceptionReport;

    IInsertSensorStatusDAO insertSensorStatusDAO() throws OwsExceptionReport;

    ISearchSensorDAO searchSensorDAO() throws OwsExceptionReport;
    
    IInsertHarvestScriptDAO insertHarvestScriptDAO() ;
    
    IInsertRemoteHarvestServer insertRemoteHarvestSensor();
    
    IUserAccountDAO userAccountDAO();
}