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

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.n52.sir.catalog.ICatalogConnection;
import org.n52.sir.catalog.csw.CatalogConnectionImpl;
import org.n52.sir.ds.IConnectToCatalogDAO;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.ows.OwsExceptionReport.ExceptionCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schulte
 * 
 */
public class PGSQLConnectToCatalogDAO implements IConnectToCatalogDAO {

    private static Logger log = LoggerFactory.getLogger(PGSQLConnectToCatalogDAO.class);

    private PGConnectionPool cpool;

    public PGSQLConnectToCatalogDAO(PGConnectionPool cpool) {
        this.cpool = cpool;
    }

    private String catalogConnectionsListQuery() {
        StringBuilder query = new StringBuilder();

        query.append("SELECT * FROM ");
        query.append(PGDAOConstants.catalog);
        query.append(";");

        return query.toString();
    }

    @Override
    public List<ICatalogConnection> getCatalogConnectionList() throws OwsExceptionReport {
        ArrayList<ICatalogConnection> connections = new ArrayList<ICatalogConnection>();
        Connection con = null;
        Statement stmt = null;

        String catalogConnectionList = catalogConnectionsListQuery();

        try {
            con = this.cpool.getConnection();
            stmt = con.createStatement();
            if (log.isDebugEnabled())
                PGSQLConnectToCatalogDAO.log.debug(">>>Database Query: " + catalogConnectionList);
            ResultSet rs = stmt.executeQuery(catalogConnectionList);

            if (rs == null) {
                return connections;
            }

            while (rs.next()) {
                String connectionID = rs.getString(PGDAOConstants.catalogIdSir);
                URL url = new URL(rs.getString(PGDAOConstants.catalogUrl));
                int pushInterval = rs.getInt(PGDAOConstants.pushInterval);
                String status = rs.getString(PGDAOConstants.catalogStatus);
                connections.add(new CatalogConnectionImpl(connectionID, url, pushInterval, status));
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 null,
                                 "Error while requesting a connection to catalog from database: " + sqle.getMessage());
            PGSQLConnectToCatalogDAO.log.error("Error while requesting a connection to catalog from database: "
                    + sqle.getMessage());
        }
        catch (MalformedURLException e) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 null,
                                 "Error while requesting a connection to catalog from database: " + e.getMessage());
            PGSQLConnectToCatalogDAO.log.error("Error while requesting a connection to catalog from database: "
                    + e.getMessage());
        }
        finally {
            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (SQLException e) {
                    log.error("SQL Error.", e);
                }
            }

            if (con != null) {
                this.cpool.returnConnection(con);
            }
        }
        return connections;
    }

    @Override
    public String getConnectionID(URL cswUrl, int pushInterval) throws OwsExceptionReport {
        String connectionID = null;
        Connection con = null;
        Statement stmt = null;

        String getConnectionQuery = getConnectionQuery(cswUrl);
        try {
            con = this.cpool.getConnection();
            stmt = con.createStatement();
            if (log.isDebugEnabled())
                PGSQLConnectToCatalogDAO.log.debug(">>>Database Query: " + getConnectionQuery);
            ResultSet rs = stmt.executeQuery(getConnectionQuery);
            while (rs.next()) {
                connectionID = rs.getString(PGDAOConstants.catalogIdSir);
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 null,
                                 "Error while requesting a connection to catalog from database: " + sqle.getMessage());
            PGSQLConnectToCatalogDAO.log.error("Error while requesting a connection to catalog from database: "
                    + sqle.getMessage());
        }
        finally {
            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (SQLException e) {
                    log.error("SQL Error.", e);
                }
            }

            if (con != null) {
                this.cpool.returnConnection(con);
            }
        }
        return connectionID;
    }

    private String getConnectionQuery(URL cswUrl) {
        StringBuilder query = new StringBuilder();

        query.append("SELECT ");
        query.append(PGDAOConstants.catalogIdSir);
        query.append(" FROM ");
        query.append(PGDAOConstants.catalog);
        query.append(" WHERE (");
        query.append(PGDAOConstants.catalogUrl);
        query.append(" = '");
        query.append(cswUrl.toString());
        query.append("');");

        return query.toString();
    }

    private String insertCatalogQuery(URL cswUrl, int pushInterval) {
        StringBuilder query = new StringBuilder();

        query.append("INSERT INTO ");
        query.append(PGDAOConstants.catalog);
        query.append(" (");
        query.append(PGDAOConstants.catalogUrl);
        query.append(", ");
        query.append(PGDAOConstants.pushInterval);
        query.append(", ");
        query.append(PGDAOConstants.catalogStatus);
        query.append(") SELECT '");
        query.append(cswUrl.toString());
        query.append("', '");
        query.append(pushInterval);
        query.append("', '");
        query.append(ICatalogConnection.NEW_CONNECTION_STATUS);
        query.append("'  RETURNING ");
        query.append(PGDAOConstants.catalogIdSir);
        query.append(";");

        return query.toString();
    }

    @Override
    public String insertConnection(URL cswUrl, int pushInterval) throws OwsExceptionReport {
        String connectionID = null;
        Connection con = null;
        Statement stmt = null;

        String insertCatalogQuery = insertCatalogQuery(cswUrl, pushInterval);

        try {
            con = this.cpool.getConnection();
            stmt = con.createStatement();
            if (log.isDebugEnabled())
                PGSQLConnectToCatalogDAO.log.debug(">>>Database Query: " + insertCatalogQuery);
            ResultSet rs = stmt.executeQuery(insertCatalogQuery);
            while (rs.next()) {
                connectionID = rs.getString(PGDAOConstants.catalogIdSir);
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 null,
                                 "Error while adding a connection to catalog to database: " + sqle.getMessage());
            PGSQLConnectToCatalogDAO.log.error("Error while adding a connection to catalog to database: "
                    + sqle.getMessage());
        }
        finally {
            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (SQLException e) {
                    log.error("SQL Error.", e);
                }
            }

            if (con != null) {
                this.cpool.returnConnection(con);
            }
        }
        return connectionID;
    }

    @Override
    public void updateConnection(URL cswUrl, int pushInterval) throws OwsExceptionReport {
        Connection con = null;
        Statement stmt = null;

        String updateConnectionQuery = updateConnectionQuery(cswUrl, pushInterval);
        try {
            con = this.cpool.getConnection();
            stmt = con.createStatement();
            if (log.isDebugEnabled())
                PGSQLConnectToCatalogDAO.log.debug(">>>Database Query: " + updateConnectionQuery);
            stmt.execute(updateConnectionQuery);
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 null,
                                 "Error while requesting a connection to catalog from database: " + sqle.getMessage());
            PGSQLConnectToCatalogDAO.log.error("Error while requesting a connection to catalog from database: "
                    + sqle.getMessage());
        }
        finally {
            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (SQLException e) {
                    log.error("SQL Error.", e);
                }
            }

            if (con != null) {
                this.cpool.returnConnection(con);
            }
        }
    }

    private String updateConnectionQuery(URL cswUrl, int pushInterval) {
        StringBuilder query = new StringBuilder();

        query.append("UPDATE ");
        query.append(PGDAOConstants.catalog);
        query.append(" SET ");
        query.append(PGDAOConstants.pushInterval);
        query.append(" = '");
        query.append(pushInterval);
        query.append("' WHERE ");
        query.append(PGDAOConstants.catalogUrl);
        query.append(" = '");
        query.append(cswUrl);
        query.append("';");

        return query.toString();
    }
}
