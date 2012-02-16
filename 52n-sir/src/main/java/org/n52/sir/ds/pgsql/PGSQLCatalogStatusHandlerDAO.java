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
import java.sql.Statement;

import org.n52.sir.ds.ICatalogStatusHandlerDAO;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.ows.OwsExceptionReport.ExceptionCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schulte
 * 
 */
public class PGSQLCatalogStatusHandlerDAO implements ICatalogStatusHandlerDAO {

    private static Logger log = LoggerFactory.getLogger(PGSQLCatalogStatusHandlerDAO.class);

    private PGConnectionPool cpool;

    public PGSQLCatalogStatusHandlerDAO(PGConnectionPool cpool) {
        this.cpool = cpool;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.ds.ICatalogStatusHandlerDAO#setNewStatus(java.lang.String, java.lang.String)
     */
    @Override
    public void setNewStatus(String connectionID, String status) throws OwsExceptionReport {
        Connection con = null;
        Statement stmt = null;

        String setNewStatusQuery = setNewStatusQuery(connectionID, status);

        try {
            con = this.cpool.getConnection();
            stmt = con.createStatement();
            if (log.isDebugEnabled())
                PGSQLCatalogStatusHandlerDAO.log.debug(">>>Database Query: " + setNewStatusQuery);
            stmt.execute(setNewStatusQuery);
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.NoApplicableCode, null, "Error while set a new Status for ID "
                    + connectionID + " in database: " + sqle.getMessage());
            PGSQLCatalogStatusHandlerDAO.log.error("Error while set a new Status for ID " + connectionID
                    + " in database: " + sqle.getMessage());
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

    private String setNewStatusQuery(String connectionID, String status) {
        StringBuilder query = new StringBuilder();

        query.append("UPDATE ");
        query.append(PGDAOConstants.catalog);
        query.append(" SET ");
        query.append(PGDAOConstants.catalogStatus);
        query.append(" = '");
        query.append(status);
        query.append("' WHERE ");
        query.append(PGDAOConstants.catalogIdSir);
        query.append(" = ");
        query.append(connectionID);
        query.append("");

        return query.toString();
    }

}
