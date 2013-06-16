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

import org.n52.sir.datastructure.SirPropertyFilter;
import org.n52.sir.datastructure.SirSearchCriteria;
import org.n52.sir.datastructure.SirSensorIDInSir;
import org.n52.sir.datastructure.SirServiceReference;
import org.n52.sir.datastructure.SirStatusDescription;
import org.n52.sir.ows.OwsExceptionReport;

/**
 * @author Jan Schulte
 * 
 */
public interface IGetSensorStatusDAO {

    /**
     * Search the status of sensors by a given search criteria and a filter, than returns status information
     * 
     * @param searchCriteria
     *        the search criteria
     * @param propertyFilter
     *        the filter for status filtering
     * @return Returns a collection of status descriptions
     * @throws OwsExceptionReport
     */
    public Collection<SirStatusDescription> getSensorStatusBySearchCriteria(SirSearchCriteria searchCriteria,
                                                                            Collection<SirPropertyFilter> propertyFilter) throws OwsExceptionReport;

    /**
     * Search the status of a sensor by a given sensorID and a filter, than returns a status information
     * 
     * @param sensorId
     *        the sensorId of the given sensor
     * @param propertyFilter
     *        the filter for status filtering
     * @return Returns a collection of status descriptions
     * @throws OwsExceptionReport
     */
    public Collection<SirStatusDescription> getSensorStatusBySensorID(SirSensorIDInSir sensorId,
                                                                      Collection<SirPropertyFilter> propertyFilter) throws OwsExceptionReport;

    /**
     * Search the status of a sensor by a given service description and a filter, than returns a status
     * information
     * 
     * @param servDesc
     *        the service description of the given sensor
     * @param propertyFilter
     *        the filter for status filtering
     * @return Returns a collection of status descriptions
     * @throws OwsExceptionReport
     */
    public Collection<SirStatusDescription> getSensorStatusByServiceDescription(SirServiceReference servDesc,
                                                                                Collection<SirPropertyFilter> propertyFilter) throws OwsExceptionReport;

}
