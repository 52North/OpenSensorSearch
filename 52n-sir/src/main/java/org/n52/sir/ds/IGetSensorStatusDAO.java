/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
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

}
