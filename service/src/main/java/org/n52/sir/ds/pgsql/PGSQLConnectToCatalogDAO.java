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

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.catalog.CatalogConnectionImpl;
import org.n52.sir.catalog.ICatalogConnection;
import org.n52.sir.ds.IConnectToCatalogDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author Jan Schulte
 * 
 */
public class PGSQLConnectToCatalogDAO implements IConnectToCatalogDAO {

    private static Logger log = LoggerFactory.getLogger(PGSQLConnectToCatalogDAO.class);

    private PGConnectionPool cpool;

    @Inject
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
        ArrayList<ICatalogConnection> connections = new ArrayList<>();
        String catalogConnectionList = catalogConnectionsListQuery();

        try (Connection con = this.cpool.getConnection(); Statement stmt = con.createStatement();) {
            log.debug(">>>Database Query: {}", catalogConnectionList);
            try (ResultSet rs = stmt.executeQuery(catalogConnectionList);) {
                if (rs == null)
                    return connections;

                while (rs.next()) {
                    String connectionID = rs.getString(PGDAOConstants.catalogIdSir);
                    URL url = new URL(rs.getString(PGDAOConstants.catalogUrl));
                    int pushInterval = rs.getInt(PGDAOConstants.pushInterval);
                    String status = rs.getString(PGDAOConstants.catalogStatus);
                    connections.add(new CatalogConnectionImpl(connectionID, url, pushInterval, status));
                }
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 null,
                                 "Error while requesting a connection to catalog from database: " + sqle.getMessage());
            log.error("Error while requesting a connection to catalog from database: " + sqle.getMessage());
        }
        catch (MalformedURLException e) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 null,
                                 "Error while requesting a connection to catalog from database: " + e.getMessage());
            PGSQLConnectToCatalogDAO.log.error("Error while requesting a connection to catalog from database: "
                    + e.getMessage());
        }

        return connections;
    }

    @Override
    public String getConnectionID(URL cswUrl, int pushInterval) throws OwsExceptionReport {
        String connectionID = null;
        String getConnectionQuery = getConnectionQuery(cswUrl);

        try (Connection con = this.cpool.getConnection(); Statement stmt = con.createStatement();) {
            log.debug(">>>Database Query: {}", getConnectionQuery);
            try (ResultSet rs = stmt.executeQuery(getConnectionQuery);) {
                while (rs.next()) {
                    connectionID = rs.getString(PGDAOConstants.catalogIdSir);
                }
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
        String insertCatalogQuery = insertCatalogQuery(cswUrl, pushInterval);

        try (Connection con = this.cpool.getConnection(); Statement stmt = con.createStatement();) {
            PGSQLConnectToCatalogDAO.log.debug(">>>Database Query: {}", insertCatalogQuery);
            try (ResultSet rs = stmt.executeQuery(insertCatalogQuery);) {
                while (rs.next()) {
                    connectionID = rs.getString(PGDAOConstants.catalogIdSir);
                }
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 null,
                                 "Error while adding a connection to catalog to database: " + sqle.getMessage());
            log.error("Error while adding a connection to catalog to database: " + sqle.getMessage());
        }

        return connectionID;
    }

    @Override
    public void updateConnection(URL cswUrl, int pushInterval) throws OwsExceptionReport {
        String updateConnectionQuery = updateConnectionQuery(cswUrl, pushInterval);

        try (Connection con = this.cpool.getConnection(); Statement stmt = con.createStatement();) {
            log.debug(">>>Database Query: {}", updateConnectionQuery);
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
