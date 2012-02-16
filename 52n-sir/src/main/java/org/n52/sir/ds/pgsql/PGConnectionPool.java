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
        
        log.info(" ***** Config File loaded successfully! ****** ");
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