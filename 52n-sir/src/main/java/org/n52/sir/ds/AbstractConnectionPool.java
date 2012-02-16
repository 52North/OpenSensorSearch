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