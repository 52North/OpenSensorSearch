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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

public class DAOFactory extends PGSQLDAOFactory implements Provider<PGConnectionPool> {

    private static Logger log = LoggerFactory.getLogger(DAOFactory.class);

    @Inject
    public DAOFactory(@Named("oss.db.CONNECTIONSTRING")
    String connectionString, @Named("oss.db.USER")
    String user, @Named("oss.db.PASSWORD")
    String password, @Named("oss.db.DRIVER")
    String driver, @Named("oss.db.INITCON")
    int initcon, @Named("oss.db.MAXCON")
    int maxcon) {
        super(connectionString, user, password, driver, initcon, maxcon);
        log.debug("NEW {}", this);
    }

    @Override
    public PGConnectionPool get() {
        return this.cpool;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DAOFactory [cpool=");
        builder.append(this.cpool);
        builder.append("]");
        return builder.toString();
    }

}
