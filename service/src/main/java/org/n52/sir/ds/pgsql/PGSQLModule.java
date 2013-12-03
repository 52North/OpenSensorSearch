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

import java.io.IOException;
import java.util.Properties;

import org.n52.oss.config.ConfigModule;
import org.n52.sir.ds.IConnectToCatalogDAO;
import org.n52.sir.ds.IDAOFactory;
import org.n52.sir.ds.IDescribeSensorDAO;
import org.n52.sir.ds.IGetCapabilitiesDAO;
import org.n52.sir.ds.IGetSensorStatusDAO;
import org.n52.sir.ds.IHarvestServiceDAO;
import org.n52.sir.ds.IInsertSensorInfoDAO;
import org.n52.sir.ds.IInsertSensorStatusDAO;
import org.n52.sir.ds.ISearchSensorDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class PGSQLModule extends AbstractModule {

    private static Logger log = LoggerFactory.getLogger(PGSQLModule.class);

    @Override
    protected void configure() {
        try {
            Properties properties = new Properties();
            properties.load(ConfigModule.class.getResourceAsStream("/prop/db.properties"));
            Names.bindProperties(binder(), properties);

            log.debug("Loaded and bound properties:\n\t{}", properties);

            // leftover of old configuration: constants must be initialized
            PGDAOConstants.getInstance(properties);
        }
        catch (IOException e) {
            log.error("Could not load properties.", e);
        }

        bind(IDAOFactory.class).to(DAOFactory.class);
        bind(PGConnectionPool.class).toProvider(DAOFactory.class);

        bind(ISearchSensorDAO.class).annotatedWith(Names.named("full")).to(PGSQLSearchSensorDAO.class);
        bind(IGetCapabilitiesDAO.class).to(PGSQLGetCapabilitiesDAO.class);
        bind(IConnectToCatalogDAO.class).to(PGSQLConnectToCatalogDAO.class);
        bind(IInsertSensorInfoDAO.class).to(PGSQLInsertSensorInfoDAO.class);
        bind(IDescribeSensorDAO.class).to(PGSQLDescribeSensorDAO.class);
        bind(IGetSensorStatusDAO.class).to(PGSQLGetSensorStatusDAO.class);
        bind(IHarvestServiceDAO.class).to(PGSQLHarvestServiceDAO.class);
        bind(IInsertSensorStatusDAO.class).to(PGSQLInsertSensorStatusDAO.class);

        log.debug("Configured {}", this);
    }

}
