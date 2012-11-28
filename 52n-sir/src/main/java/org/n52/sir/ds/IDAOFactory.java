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
}