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
import java.util.Collection;

import org.n52.sir.datastructure.InternalSensorID;
import org.n52.sir.datastructure.SirSensorIdentification;
import org.n52.sir.datastructure.SirServiceReference;
import org.n52.sir.datastructure.SirStatus;
import org.n52.sir.ds.IInsertSensorStatusDAO;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.util.GMLDateParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schulte
 * 
 */
public class PGSQLInsertSensorStatusDAO implements IInsertSensorStatusDAO {

    /**
     * the logger, used to log exceptions and additionally information
     */
    private static Logger log = LoggerFactory.getLogger(PGSQLInsertSensorStatusDAO.class);

    /**
     * Connection pool for creating connections to the database
     */
    private PGConnectionPool cpool;

    public PGSQLInsertSensorStatusDAO(PGConnectionPool cpool) {
        this.cpool = cpool;
    }

    private String getSensorIdByInternalID(InternalSensorID ident) {
        StringBuilder query = new StringBuilder();

        query.append("SELECT ");
        query.append(PGDAOConstants.internalSensorId);
        query.append(" FROM ");
        query.append(PGDAOConstants.sensor);
        query.append(" WHERE (");
        query.append(PGDAOConstants.internalSensorId);
        query.append(" = '");
        query.append(ident.getId());
        query.append("');");

        return query.toString();
    }

    private String getSensorIdByServiceDescription(SirServiceReference ident) {
        StringBuilder query = new StringBuilder();

        query.append("SELECT ");
        query.append(PGDAOConstants.sensorIdSirSensServ);
        query.append(" FROM ");
        query.append(PGDAOConstants.sensorService);
        query.append(" WHERE (");
        query.append(PGDAOConstants.serviceIdOfSensServ);
        query.append(" = (SELECT ");
        query.append(PGDAOConstants.serviceId);
        query.append(" FROM ");
        query.append(PGDAOConstants.service);
        query.append(" WHERE (");
        query.append(PGDAOConstants.serviceUrl);
        query.append(" = '");
        query.append(ident.getService().getUrl());
        query.append("' AND ");
        query.append(PGDAOConstants.serviceType);
        query.append(" = '");
        query.append(ident.getService().getType());
        query.append("')) AND ");
        query.append(PGDAOConstants.sensorIdSirSensServ);
        query.append(" = (SELECT ");
        query.append(PGDAOConstants.internalSensorId);
        query.append(" FROM ");
        query.append(PGDAOConstants.sensorService);
        query.append(" WHERE (");
        query.append(PGDAOConstants.serviceSpecId);
        query.append(" = '");
        query.append(ident.getServiceSpecificSensorId());
        query.append("')));");

        return query.toString();
    }

    private String getStatusId(SirStatus status, String sensorId) {
        StringBuilder query = new StringBuilder();

        query.append("SELECT ");
        query.append(PGDAOConstants.statusId);
        query.append(" FROM ");
        query.append(PGDAOConstants.status);
        query.append(" WHERE (");
        query.append(PGDAOConstants.sensorIdSirOfStatus);
        query.append(" = '");
        query.append(sensorId);
        query.append("' AND ");
        query.append(PGDAOConstants.propertyName);
        query.append(" = '");
        query.append(status.getPropertyName());
        query.append("' AND ");
        query.append(PGDAOConstants.uom);
        query.append(" ='");
        query.append(status.getUom());
        query.append("');");

        return query.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.ds.IInsertSensorStatusDAO#insertSensorStatus(org.n52.sir.
     * datastructur.SirSensorIdentification, org.n52.sir.datastructur.SirStatus)
     */
    @Override
    public String insertSensorStatus(SirSensorIdentification ident, Collection<SirStatus> status) throws OwsExceptionReport {
        String sensorId = null;

        Connection con = null;
        Statement stmt = null;

        try {
            con = this.cpool.getConnection();
            stmt = con.createStatement();

            // query identification
            // check sensorID in SIR
            if (ident instanceof InternalSensorID) {
                String sensorIdQuery = getSensorIdByInternalID((InternalSensorID) ident);
                if (log.isDebugEnabled())
                    log.debug(">>>Database Query: " + sensorIdQuery);
                ResultSet rs = stmt.executeQuery(sensorIdQuery);
                while (rs.next()) {
                    sensorId = rs.getString(PGDAOConstants.internalSensorId);
                }
            }
            // check service description
            if (ident instanceof SirServiceReference) {
                String sensorIdQuery = getSensorIdByServiceDescription((SirServiceReference) ident);
                if (log.isDebugEnabled())
                    log.debug(">>>Database Query: " + sensorIdQuery);
                ResultSet rs = stmt.executeQuery(sensorIdQuery);
                while (rs.next()) {
                    sensorId = rs.getString(PGDAOConstants.internalSensorId);
                }
            }
            if (sensorId == null) {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(ExceptionCode.NoApplicableCode, null, "Unknown sensor identification!");
                log.error("Unknown sensor identification!");
            }

            for (SirStatus sirStatus : status) {
                // get status id
                String statusID = null;
                String statusIdQuery = getStatusId(sirStatus, sensorId);
                if (log.isDebugEnabled())
                    log.debug(">>> Database Query: " + statusIdQuery);
                ResultSet rs = stmt.executeQuery(statusIdQuery);
                while (rs.next()) {
                    statusID = rs.getString(PGDAOConstants.statusId);
                }
                if (statusID == null) {
                    // insert status
                    String insertSensorStatus = insertStatus(sirStatus, sensorId);
                    if (log.isDebugEnabled())
                        log.debug(">>> Database Query: " + insertSensorStatus);
                    rs = stmt.executeQuery(insertSensorStatus);
                    while (rs.next()) {
                        sensorId = rs.getString(PGDAOConstants.sensorIdSirOfStatus);
                    }
                }
                else {
                    // update status
                    String updateSensorStatus = updateStatus(sirStatus, statusID);
                    log.debug(">>> Database Query: " + updateSensorStatus);
                    stmt.execute(updateSensorStatus);
                }
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 null,
                                 "Error while adding a sensor status to database: " + sqle.getMessage());
            log.error("Error while adding a sensor status to database: " + sqle.getMessage());
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

            if (con != null) {
                this.cpool.returnConnection(con);
            }
        }
        return sensorId;
    }

    private String insertStatus(SirStatus status, String sensorId) {
        StringBuilder query = new StringBuilder();

        query.append("INSERT INTO ");
        query.append(PGDAOConstants.status);
        query.append(" (");
        query.append(PGDAOConstants.sensorIdSirOfStatus);
        query.append(", ");
        query.append(PGDAOConstants.propertyName);
        query.append(", ");
        query.append(PGDAOConstants.propertyValue);
        query.append(", ");
        query.append(PGDAOConstants.time);
        query.append(", ");
        query.append(PGDAOConstants.uom);
        query.append(") SELECT '");
        query.append(sensorId);
        query.append("', '");
        query.append(status.getPropertyName());
        query.append("', '");
        query.append(status.getPropertyValue());
        query.append("', '");
        query.append(GMLDateParser.getInstance().parseDate(status.getTimestamp()));
        query.append("', '");
        query.append(status.getUom());
        query.append("' WHERE NOT EXISTS (SELECT ");
        query.append(PGDAOConstants.sensorIdSirOfStatus);
        query.append(", ");
        query.append(PGDAOConstants.propertyName);
        query.append(", ");
        query.append(PGDAOConstants.uom);
        query.append(" FROM ");
        query.append(PGDAOConstants.status);
        query.append(" WHERE (");
        query.append(PGDAOConstants.sensorIdSirOfStatus);
        query.append(" = '");
        query.append(sensorId);
        query.append("' AND ");
        query.append(PGDAOConstants.propertyName);
        query.append(" = '");
        query.append(status.getPropertyName());
        query.append("' AND ");
        query.append(PGDAOConstants.uom);
        query.append(" ='");
        query.append(status.getUom());
        query.append("')) RETURNING ");
        query.append(PGDAOConstants.sensorIdSirOfStatus);
        query.append(";");

        return query.toString();
    }

    private String updateStatus(SirStatus status, String statusID) {
        StringBuilder query = new StringBuilder();

        query.append("UPDATE ");
        query.append(PGDAOConstants.status);
        query.append(" SET ");
        query.append(PGDAOConstants.propertyValue);
        query.append(" = '");
        query.append(status.getPropertyValue());
        query.append("', ");
        query.append(PGDAOConstants.time);
        query.append(" = '");
        query.append(GMLDateParser.getInstance().parseDate(status.getTimestamp()));
        query.append("' WHERE ");
        query.append(PGDAOConstants.statusId);
        query.append(" = '");
        query.append(statusID);
        query.append("';");

        return query.toString();
    }
}
