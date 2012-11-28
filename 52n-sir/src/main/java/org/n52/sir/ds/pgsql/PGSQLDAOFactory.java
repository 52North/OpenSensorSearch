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

package org.n52.sir.ds.pgsql;

import java.util.Properties;

import org.n52.sir.ds.ICatalogStatusHandlerDAO;
import org.n52.sir.ds.IConnectToCatalogDAO;
import org.n52.sir.ds.IDAOFactory;
import org.n52.sir.ds.IDescribeSensorDAO;
import org.n52.sir.ds.IDisconnectFromCatalogDAO;
import org.n52.sir.ds.IGetAllServicesDAO;
import org.n52.sir.ds.IGetSensorStatusDAO;
import org.n52.sir.ds.IHarvestServiceDAO;
import org.n52.sir.ds.IInsertSensorInfoDAO;
import org.n52.sir.ds.IInsertSensorStatusDAO;
import org.n52.sir.ds.ISearchSensorDAO;
import org.n52.sir.ows.OwsExceptionReport;

/**
 * DAO factory for PostgreSQL
 * 
 * @author Jan Schulte
 * 
 */
public class PGSQLDAOFactory implements IDAOFactory {

    /**
     * ConnectionPool, which contains connections to the DB
     */
    private PGConnectionPool cpool;

    /**
     * constructor
     * 
     * @param daoProps
     *        Properties of the Dao config file
     * @throws OwsExceptionReport
     */
    public PGSQLDAOFactory(Properties daoProps) throws OwsExceptionReport {

        // initializeDAOConstants;
        PGDAOConstants.getInstance(daoProps);

        String connection = PGDAOConstants.connectionString;
        String user = PGDAOConstants.user;
        String password = PGDAOConstants.password;
        String driver = PGDAOConstants.driver;
        int initcon = PGDAOConstants.initcon;
        int maxcon = PGDAOConstants.maxcon;
        // initialize PGConnectionPool
        this.cpool = new PGConnectionPool(connection, user, password, driver, initcon, maxcon);
    }

    @Override
    public ICatalogStatusHandlerDAO catalogStatusHandlerDAO() {
        return new PGSQLCatalogStatusHandlerDAO(this.cpool);
    }

    @Override
    public IConnectToCatalogDAO connectToCatalogDAO() throws OwsExceptionReport {
        return new PGSQLConnectToCatalogDAO(this.cpool);
    }

    @Override
    public IDescribeSensorDAO describeSensorDAO() throws OwsExceptionReport {
        return new PGSQLDescribeSensorDAO(this.cpool);
    }

    @Override
    public IDisconnectFromCatalogDAO disconnectFromCatalogDAO() throws OwsExceptionReport {
        return new PGSQLDisconnetFromCatalogDAO(this.cpool);
    }

    @Override
    public IGetAllServicesDAO getAllServicesDAO() throws OwsExceptionReport {
        return new PGSQLGetAllServicesDAO(this.cpool);
    }

    @Override
    public PGSQLGetCapabilitiesDAO getCapabilitiesDAO() {
        return new PGSQLGetCapabilitiesDAO(this.cpool);
    }

    @Override
    public IGetSensorStatusDAO getSensorStatusDAO() throws OwsExceptionReport {
        return new PGSQLGetSensorStatusDAO(this.cpool);
    }

    @Override
    public IHarvestServiceDAO harvestServiceDAO() throws OwsExceptionReport {
        return new PGSQLHarvestServiceDAO(this.cpool);
    }

    @Override
    public IInsertSensorInfoDAO insertSensorInfoDAO() throws OwsExceptionReport {
        return new PGSQLInsertSensorInfoDAO(this.cpool);
    }

    @Override
    public IInsertSensorStatusDAO insertSensorStatusDAO() throws OwsExceptionReport {
        return new PGSQLInsertSensorStatusDAO(this.cpool);
    }

    @Override
    public ISearchSensorDAO searchSensorDAO() throws OwsExceptionReport {
        return new PGSQLSearchSensorDAO(this.cpool);
    }

}