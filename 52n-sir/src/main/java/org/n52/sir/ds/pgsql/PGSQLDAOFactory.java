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
import org.n52.sir.ds.IInsertHarvestScriptDAO;
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

	@Override
	public IInsertHarvestScriptDAO insertHarvestScriptDAO() {
		// TODO Auto-generated method stub
		return new PGSQLInsertHarvestScriptDAO(this.cpool);
	}

}