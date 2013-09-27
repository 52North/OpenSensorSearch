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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.n52.sir.ds.IDisconnectFromCatalogDAO;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.ows.OwsExceptionReport.ExceptionCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schulte
 * 
 */
public class PGSQLDisconnetFromCatalogDAO implements IDisconnectFromCatalogDAO {

    private static Logger log = LoggerFactory.getLogger(PGSQLDisconnetFromCatalogDAO.class);

    private PGConnectionPool cpool;

    public PGSQLDisconnetFromCatalogDAO(PGConnectionPool cpool) {
        this.cpool = cpool;
    }

    @Override
    public String deleteConnection(String cswURL) throws OwsExceptionReport {
        String connectionID = null;
        String deleteConnectionQuery = deleteConnectionQuery(cswURL);

        try (Connection con = this.cpool.getConnection(); Statement stmt = con.createStatement();) {
            log.debug(">>>Database Query: {}", deleteConnectionQuery);
            ResultSet rs = stmt.executeQuery(deleteConnectionQuery);
            while (rs.next()) {
                connectionID = rs.getString(PGDAOConstants.catalogIdSir);
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 null,
                                 "Error while deleting a connection from catalog from database: " + sqle.getMessage());
            log.error("Error while deleting a connection from catalog from database: " + sqle.getMessage());
        }

        return connectionID;
    }

    private String deleteConnectionQuery(String cswURL) {
        StringBuilder query = new StringBuilder();

        query.append("DELETE FROM ");
        query.append(PGDAOConstants.catalog);
        query.append(" WHERE ");
        query.append(PGDAOConstants.catalogUrl);
        query.append(" = '");
        query.append(cswURL);
        query.append("' RETURNING ");
        query.append(PGDAOConstants.catalogIdSir);
        query.append(";");

        return query.toString();
    }

}
