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
package org.n52.sir.ds;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.n52.sir.ows.OwsExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Jan Schulte
 * 
 */
public abstract class AbstractConnectionPool {

    /**
     * the logger, used to log exceptions and additionaly information
     */
    protected static Logger log = LoggerFactory.getLogger(AbstractConnectionPool.class);

    /**
     * data source
     */
    protected BasicDataSource dataSource = new BasicDataSource();

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
     *        maximal number of connections in the pool
     */
    public AbstractConnectionPool(String connection,
                                  String user,
                                  String password,
                                  String driverName,
                                  int initConnections,
                                  int maxConnections) {
        this.dataSource.setDriverClassName(driverName);
        this.dataSource.setUsername(user);
        this.dataSource.setPassword(password);
        this.dataSource.setUrl(connection);

        // max connections active
        this.dataSource.setMaxActive(maxConnections);
        this.dataSource.setMaxIdle(maxConnections);

        // initial size of connection pool
        this.dataSource.setInitialSize(initConnections);

        this.dataSource.setMaxWait(5000);

        // important! allow access to underlying connection
        this.dataSource.setAccessToUnderlyingConnectionAllowed(true);

    }

    /**
     * Abstract method returns an available connection from the pool. After the query operation, you have to
     * "give back" the connection to the pool with the returnConnection method!
     * 
     * @return DB Connection to execute the query
     * @throws OwsExceptionReport
     *         If all connections are in use and no further connection could be established
     */
    public abstract Connection getConnection() throws OwsExceptionReport;

    /**
     * Invoke this method after executing the query with this connection, so that the connection is already
     * available in the pool
     * 
     * @param conn
     *        the connection which was used and now is available again
     * @throws OwsExceptionReport
     *         If closing the connection failed
     */
    public void returnConnection(Connection conn) throws OwsExceptionReport {
        try {
            conn.close();
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(sqle);
            log.error("Error closing active connection: " + sqle.toString());
            throw se;
        }
    }

}