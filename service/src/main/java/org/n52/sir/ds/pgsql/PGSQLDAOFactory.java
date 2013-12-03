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

import org.n52.sir.ds.IDAOFactory;
import org.n52.sir.ds.IInsertHarvestScriptDAO;
import org.n52.sir.ds.IInsertRemoteHarvestServer;
import org.n52.sir.ds.IUserAccountDAO;

/**
 * DAO factory for PostgreSQL
 * 
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class PGSQLDAOFactory implements IDAOFactory {

    protected PGConnectionPool cpool;

    public PGSQLDAOFactory(String connectionString, String user, String password, String driver, int initcon, int maxcon) {
        this.cpool = new PGConnectionPool(connectionString, user, password, driver, initcon, maxcon);
    }

    @Override
    public IInsertHarvestScriptDAO insertHarvestScriptDAO() {
        return new PGSQLInsertHarvestScriptDAO(this.cpool);
    }

    @Override
    public IInsertRemoteHarvestServer insertRemoteHarvestSensor() {
        return new PGSQLInsertRemoteHarvestServer(this.cpool);
    }

    @Override
    public IUserAccountDAO userAccountDAO() {
        return new PGSQLUserAccountDAO(this.cpool);
    }

}