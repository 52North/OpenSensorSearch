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

package org.n52.oss.guice;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import javax.servlet.UnavailableException;

import org.n52.oss.sir.SirConfig;
import org.n52.sir.SirConfigurator;
import org.n52.sir.ds.IDAOFactory;
import org.n52.sir.ds.pgsql.PGSQLDAOFactory;
import org.n52.sir.ows.OwsExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class ConfigModule extends AbstractModule {

    private static Logger log = LoggerFactory.getLogger(ConfigModule.class);

    private static final String PROPERTIES_FILE_NAME = "/sir.properties";

    private static final String DB_PROPERTIES_FILE_NAME = "/db.properties";

    @Override
    protected void configure() {
        log.debug("Configuring...");

        bind(IDAOFactory.class).to(PGSQLDAOFactory.class);

        Properties props = new Properties();
        try (InputStream in = ConfigModule.class.getResourceAsStream(PROPERTIES_FILE_NAME)) {
            props.load(in);
        }
        catch (IOException e) {
            log.error("Could not load properties", e);
        }
        Properties dbProps = new Properties();
        try (InputStream in = ConfigModule.class.getResourceAsStream(DB_PROPERTIES_FILE_NAME)) {
            dbProps.load(in);
        }
        catch (IOException e) {
            log.error("Could not load properties", e);
        }

        Names.bindProperties(binder(), dbProps);
        Names.bindProperties(binder(), props);

        log.trace("Bound properties: {}", Arrays.deepToString(props.entrySet().toArray()));
        log.trace("Bound db properties: {}", Arrays.deepToString(dbProps.entrySet().toArray()));
        
        // // FIXME workaround for slow transition of config, to be removed.
        try {
            SirConfig conf = SirConfigurator.getInstance(props, dbProps);
            log.trace("CREATED {}", conf);
        }
        catch (UnavailableException | OwsExceptionReport e) {
            log.error("Could not instantiate old style properties.", e);
        }
    }

}
