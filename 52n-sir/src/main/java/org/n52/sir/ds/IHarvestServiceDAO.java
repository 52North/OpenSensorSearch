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

import org.n52.sir.datastructure.SirSensor;
import org.n52.sir.ows.OwsExceptionReport;

/**
 * interface for the specific DAOFactories, offers methods to create the matching DAOs for the harvestService
 * operation
 * 
 * @author Jan Schulte
 * 
 */
public interface IHarvestServiceDAO {

    /**
     * Inserts a service in the database
     * 
     * @param serviceUrl
     *        The Service Url
     * @param serviceType
     *        The Service type
     * @return Returns the ServiceID in the Database
     * @throws OwsExceptionReport
     */
    public String addService(String serviceUrl, String serviceType) throws OwsExceptionReport;

    /**
     * Inserts a sensor in the database
     * 
     * @param sensor
     *        the inserted sensor
     * @return Returns the SensorID in the Database
     * @throws OwsExceptionReport
     */
    public SirSensor insertSensor(SirSensor sensor) throws OwsExceptionReport;

}
