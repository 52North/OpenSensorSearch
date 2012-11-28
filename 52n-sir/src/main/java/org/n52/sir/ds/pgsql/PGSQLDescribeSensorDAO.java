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
import java.sql.Timestamp;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sir.ds.IDescribeSensorDAO;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.ows.OwsExceptionReport.ExceptionCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schulte
 * 
 */
public class PGSQLDescribeSensorDAO implements IDescribeSensorDAO {

    /**
     * the logger, used to log exceptions and additionally information
     */
    private static Logger log = LoggerFactory.getLogger(PGSQLDescribeSensorDAO.class);

    /**
     * Connection pool for creating connections to the DB
     */
    private PGConnectionPool cpool;

    public PGSQLDescribeSensorDAO(PGConnectionPool cpool) {
        this.cpool = cpool;
    }

    @Override
    public XmlObject getSensorDescription(String sensorIdInSir) throws OwsExceptionReport {
        XmlObject sensorML = null;
        Statement stmt = null;

        Connection con = null;
        try {
            con = this.cpool.getConnection();
            stmt = con.createStatement();

            // sensorML query by sensorID in SIR
            String sensorMlQuery = sensorMlQuery(sensorIdInSir);
            if (log.isDebugEnabled())
                log.debug(">>>Database Query: " + sensorMlQuery);
            ResultSet rs = stmt.executeQuery(sensorMlQuery);
            Timestamp timestamp = null;

            while (rs.next()) {
                sensorML = XmlObject.Factory.parse(rs.getString(PGDAOConstants.sensorml));
                timestamp = rs.getTimestamp(PGDAOConstants.lastUpdate);
            }

            if (log.isDebugEnabled()) {
                log.debug("Got SensorML from datbase, last update: " + timestamp);
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport();
            log.error("Error while quering sensorMLDocument: " + sqle.getMessage());
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 "DescribeSensorDAO",
                                 "Error while quering sensorMLDocument: " + sqle.getMessage());
            throw se;
        }
        catch (XmlException xmle) {
            OwsExceptionReport se = new OwsExceptionReport();
            log.error("Error while parsing sensorMLDocument: " + xmle.getMessage());
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 "DescribeSensorDAO",
                                 "Error while parsing sensorMLDocument: " + xmle.getMessage());
            throw se;
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

            if (con != null)
                this.cpool.returnConnection(con);
        }

        return sensorML;
    }

    private String sensorMlQuery(String sensorIdInSir) {
        StringBuilder query = new StringBuilder();

        query.append("SELECT ");
        query.append(PGDAOConstants.sensorml);
        query.append(", ");
        query.append(PGDAOConstants.lastUpdate);
        query.append(" FROM ");
        query.append(PGDAOConstants.sensor);
        query.append(" WHERE ");
        query.append(PGDAOConstants.sensorIdSir);
        query.append(" = '");
        query.append(sensorIdInSir);
        query.append("'");

        return query.toString();
    }
}
