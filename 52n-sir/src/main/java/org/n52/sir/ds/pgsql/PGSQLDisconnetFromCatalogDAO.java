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
        Connection con = null;
        Statement stmt = null;

        String deleteConnectionQuery = deleteConnectionQuery(cswURL);
        try {
            con = this.cpool.getConnection();
            stmt = con.createStatement();
            if (log.isDebugEnabled())
                log.debug(">>>Database Query: " + deleteConnectionQuery);
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
