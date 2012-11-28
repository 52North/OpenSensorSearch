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
