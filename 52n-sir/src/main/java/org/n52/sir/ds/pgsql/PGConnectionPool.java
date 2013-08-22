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

import java.sql.Connection;
import java.sql.SQLException;

import org.n52.sir.ds.AbstractConnectionPool;
import org.n52.sir.ows.OwsExceptionReport;

/**
 * Connection Pool for PostgreSQL databases. Class implements the abstract class ConnectionPool and implements
 * the getNewConnection method.
 * 
 * @author Jan Schulte
 * 
 */
public class PGConnectionPool extends AbstractConnectionPool {

    /**
     * constructor with necessary parameters as strings
     * 
     * @param connection
     *        connection url
     * @param user
     *        db username
     * @param password
     *        db password
     * @param driverName
     *        classname of the db driver
     * @param initConnections
     *        number of initial connections
     * @param maxConnections
     *        maximal number of connection pool failed
     */
    public PGConnectionPool(String connection,
                            String user,
                            String password,
                            String driverName,
                            int initConnections,
                            int maxConnections) {

        super(connection, user, password, driverName, initConnections, maxConnections);

        log.info(" ***** NEW connection pool: {} ****** ", this);
    }

    @Override
    public Connection getConnection() throws OwsExceptionReport {
        // pooled connection
        Connection conn;

        try {
            conn = this.dataSource.getConnection();
        }
        catch (SQLException sqle) {
            if (this.dataSource.getNumActive() == this.dataSource.getMaxActive()) {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.NoApplicableCode,
                                     "PGConnection.getConnection()",
                                     "All db connections are in use. Please try again later! " + sqle.toString());
                if (log.isDebugEnabled())
                    log.debug("All db connections are in use. Please try again later! " + sqle.toString());
                throw se;
            }
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.NoApplicableCode,
                                 "PGConnection.getConnection()",
                                 "Could not get connection from connection pool. Please make sure that your database server is running and configured properly. "
                                         + sqle.toString());
            if (log.isDebugEnabled())
                log.debug("Could not get connection from connection pool. Please make sure that your database server is running and configured properly. "
                        + sqle.toString());

            throw se;
        }

        return conn;
    }
}