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

import org.n52.oss.sir.api.SirSearchCriteria;
import org.n52.oss.sir.api.SirSearchResultElement;
import org.n52.oss.sir.api.SirServiceReference;
import org.n52.oss.sir.ows.OwsExceptionReport;

/**
 * Interface for the specific DAOFactories, offers methods to create the matching DAOs for the searchSensor
 * operation
 * 
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public interface ISearchSensorDAO {

    /**
     * Get all available sensors.
     * 
     * @return Returns the sensors
     * @throws OwsExceptionReport
     */
    public Collection<SirSearchResultElement> getAllSensors(boolean simpleReponse) throws OwsExceptionReport;

    /**
     * Search a sensor by given internal sensor identification and returns the sensor
     * 
     * @param sensorId
     *        the identifier provided by this service
     * @param b
     * @return Returns the Sensor
     * @throws OwsExceptionReport
     */
    public SirSearchResultElement getSensorBySensorID(String sensorId, boolean simpleReponse) throws OwsExceptionReport;

    /**
     * Search a sensor by given service description and returns the sensor
     * 
     * @param servDesc
     *        the service description
     * @return Returns the sensor
     * @throws OwsExceptionReport
     */
    public SirSearchResultElement getSensorByServiceDescription(SirServiceReference servDesc, boolean simpleReponse) throws OwsExceptionReport;

    /**
     * Search sensors by a given searchCriteria and returns a collection of SearchResultElements
     * 
     * @param searchCriteria
     *        the searchCriteria
     * @return Returns a Collection of SearchResultElements
     * @throws OwsExceptionReport
     */
    public Collection<SirSearchResultElement> searchSensor(SirSearchCriteria searchCriteria, boolean simpleReponse) throws OwsExceptionReport;

    /**
     * 
     * @return a list of all the sensor ids
     * @throws OwsExceptionReport
     */
    public Collection<String> getAllSensorIds() throws OwsExceptionReport;

}
