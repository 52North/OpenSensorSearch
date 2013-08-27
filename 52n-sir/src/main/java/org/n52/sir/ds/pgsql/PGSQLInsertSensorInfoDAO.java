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

import org.n52.sir.datastructure.SirPhenomenon;
import org.n52.sir.datastructure.SirSensor;
import org.n52.sir.datastructure.SirSensorIDInSir;
import org.n52.sir.datastructure.SirSensorIdentification;
import org.n52.sir.datastructure.SirServiceReference;
import org.n52.sir.ds.IInsertSensorInfoDAO;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schulte
 * 
 */
public class PGSQLInsertSensorInfoDAO implements IInsertSensorInfoDAO {

    /**
     * the logger, used to log exceptions and additionally information
     */
    private static Logger log = LoggerFactory.getLogger(PGSQLInsertSensorInfoDAO.class);

    /**
     * Connection pool for creating connections to the DB
     */
    private PGConnectionPool cpool;

    public PGSQLInsertSensorInfoDAO(PGConnectionPool cpool) {
        this.cpool = cpool;
    }

    @Override
    public String addNewReference(SirSensorIdentification sensIdent, SirServiceReference servDesc) throws OwsExceptionReport {
        String insert;
        Connection con = null;
        Statement stmt = null;

        try {
            con = this.cpool.getConnection();
            stmt = con.createStatement();

            String sensorIdInSir = getSensorIdInSir(sensIdent);

            // build add service query
            String addService = addServiceString(servDesc);
            if (log.isDebugEnabled())
                log.debug(">>>Database Query: " + addService);
            stmt.execute(addService);

            // build add reference query
            String addReference = addReferenceString(sensorIdInSir, servDesc);
            if (log.isDebugEnabled())
                log.debug(">>>Database Query: " + addReference);
            ResultSet rs = stmt.executeQuery(addReference);
            if (rs.next()) {
                insert = rs.getString(PGDAOConstants.sensorIdSir);
            }
            else {
                insert = null;
            }

        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport();
            log.error("Error while adding a service reference: " + sqle.getMessage());
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 "InsertSensorInfoDAO",
                                 "Error while adding a service reference: " + sqle.getMessage());
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

        return insert;
    }

    private String addReferenceString(String sensorIdInSir, SirServiceReference servDesc) {
        StringBuilder query = new StringBuilder();

        query.append("INSERT INTO ");
        query.append(PGDAOConstants.sensorService);
        query.append(" (");
        query.append(PGDAOConstants.serviceIdOfSensServ);
        query.append(",");
        query.append(PGDAOConstants.sensorIdSirSensServ);
        query.append(",");
        query.append(PGDAOConstants.serviceSpecId);
        query.append(") SELECT (SELECT ");
        query.append(PGDAOConstants.serviceId);
        query.append(" FROM ");
        query.append(PGDAOConstants.service);
        query.append(" WHERE (");
        query.append(PGDAOConstants.serviceUrl);
        query.append("='");
        query.append(servDesc.getService().getUrl());
        query.append("' AND ");
        query.append(PGDAOConstants.serviceType);
        query.append("='");
        query.append(servDesc.getService().getType());
        query.append("')), '");
        query.append(sensorIdInSir);
        query.append("','");
        query.append(servDesc.getServiceSpecificSensorId());
        query.append("' WHERE NOT EXISTS (SELECT ");
        query.append(PGDAOConstants.serviceIdOfSensServ);
        query.append(",");
        query.append(PGDAOConstants.sensorIdSirSensServ);
        query.append(",");
        query.append(PGDAOConstants.serviceSpecId);
        query.append(" FROM ");
        query.append(PGDAOConstants.sensorService);
        query.append(" WHERE ");
        query.append(PGDAOConstants.serviceIdOfSensServ);
        query.append(" = (SELECT ");
        query.append(PGDAOConstants.serviceId);
        query.append(" FROM ");
        query.append(PGDAOConstants.service);
        query.append(" WHERE (");
        query.append(PGDAOConstants.serviceUrl);
        query.append("='");
        query.append(servDesc.getService().getUrl());
        query.append("' AND ");
        query.append(PGDAOConstants.serviceType);
        query.append("='");
        query.append(servDesc.getService().getType());
        query.append("')) AND ");
        query.append(PGDAOConstants.sensorIdSirSensServ);
        query.append(" = '");
        query.append(sensorIdInSir);
        query.append("' AND ");
        query.append(PGDAOConstants.serviceSpecId);
        query.append(" = '");
        query.append(servDesc.getServiceSpecificSensorId());
        query.append("') RETURNING ");
        query.append(PGDAOConstants.sensorIdSir);
        query.append(";");

        return query.toString();
    }

    private String addServiceString(SirServiceReference servDesc) {
        StringBuilder query = new StringBuilder();

        query.append("INSERT INTO ");
        query.append(PGDAOConstants.service);
        query.append(" (");
        query.append(PGDAOConstants.serviceUrl);
        query.append(",");
        query.append(PGDAOConstants.serviceType);
        query.append(") SELECT '");
        query.append(servDesc.getService().getUrl());
        query.append("','");
        query.append(servDesc.getService().getType());
        query.append("' WHERE NOT EXISTS (SELECT ");
        query.append(PGDAOConstants.serviceUrl);
        query.append(",");
        query.append(PGDAOConstants.serviceType);
        query.append(" FROM ");
        query.append(PGDAOConstants.service);
        query.append(" WHERE (");
        query.append(PGDAOConstants.serviceUrl);
        query.append("='");
        query.append(servDesc.getService().getUrl());
        query.append("' AND ");
        query.append(PGDAOConstants.serviceType);
        query.append(" = '");
        query.append(servDesc.getService().getType());
        query.append("')); ");

        return query.toString();
    }

    @Override
    public String deleteReference(SirSensorIdentification sensIdent, SirServiceReference servDesc) throws OwsExceptionReport {
        String delete;
        Connection con = null;
        Statement stmt = null;

        try {
            con = this.cpool.getConnection();
            stmt = con.createStatement();

            String sensorIdInSir = getSensorIdInSir(sensIdent);
            // build remove reference query
            String removeReference = removeReferenceString(sensorIdInSir, servDesc);
            if (log.isDebugEnabled())
                log.debug(">>>Database Query: " + removeReference);
            ResultSet rs = stmt.executeQuery(removeReference);
            if (rs.next()) {
                delete = rs.getString(PGDAOConstants.sensorIdSir);
            }
            else {
                delete = null;
            }

        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport();
            log.error("Error while removing a service reference: " + sqle.getMessage());
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 "InsertSensorInfoDAO",
                                 "Error while removing a service reference: " + sqle.getMessage());
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
        return delete;
    }

    @Override
    public String deleteSensor(SirSensorIdentification sensIdent) throws OwsExceptionReport {
        String sensorIdInSir;
        Connection con = null;
        Statement stmt = null;

        try {
            con = this.cpool.getConnection();
            stmt = con.createStatement();

            sensorIdInSir = getSensorIdInSir(sensIdent);
            // build delete sensor query
            String removeSensor = removeSensorString(sensorIdInSir);
            if (log.isDebugEnabled())
                log.debug(">>>Database Query: " + removeSensor);
            ResultSet rs = stmt.executeQuery(removeSensor);
            if (rs.next()) {
                sensorIdInSir = rs.getString(PGDAOConstants.sensorIdSir);
            }
            else {
                sensorIdInSir = null;
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport();
            log.error("Error while removing a sensor: " + sqle.getMessage());
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 "InsertSensorInfoDAO",
                                 "Error while removing a sensor: " + sqle.getMessage());
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
        return sensorIdInSir;
    }

    private String getSensorIdInSir(SirSensorIdentification sensIdent) throws OwsExceptionReport {
        String sensorID = null;
        Connection con = null;
        Statement stmt = null;

        if (sensIdent instanceof SirServiceReference) {
            try {
                con = this.cpool.getConnection();
                stmt = con.createStatement();
                String getSensorID = getSensorIdInSirString((SirServiceReference) sensIdent);
                if (log.isDebugEnabled())
                    log.debug(">>> Database Query: " + getSensorID);
                ResultSet rs = stmt.executeQuery(getSensorID);
                if (rs == null) {
                    return sensorID;
                }
                while (rs.next()) {
                    sensorID = rs.getString(PGDAOConstants.sensorIdSirSensServ);
                }
            }
            catch (SQLException sqle) {
                OwsExceptionReport se = new OwsExceptionReport();
                log.error("Error while requesting a sensor ID: " + sqle.getMessage());
                se.addCodedException(ExceptionCode.NoApplicableCode,
                                     "InsertSensorInfoDAO",
                                     "Error while requesting a sensor ID: " + sqle.getMessage());
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
        }
        else {
            SirSensorIDInSir temp = (SirSensorIDInSir) sensIdent;
            return temp.getSensorIdInSir();
        }
        return sensorID;
    }

    private String getSensorIdInSirString(SirServiceReference servDesc) {
        StringBuilder query = new StringBuilder();

        query.append("SELECT ");
        query.append(PGDAOConstants.sensorIdSir);
        query.append(" FROM ");
        query.append(PGDAOConstants.sensorService);
        query.append(" WHERE (");
        query.append(PGDAOConstants.serviceSpecId);
        query.append(" = '");
        query.append(servDesc.getServiceSpecificSensorId());
        query.append("') AND (");
        query.append(PGDAOConstants.serviceIdOfSensServ);
        query.append(" = (SELECT ");
        query.append(PGDAOConstants.serviceId);
        query.append(" FROM ");
        query.append(PGDAOConstants.service);
        query.append(" WHERE (");
        query.append(PGDAOConstants.serviceUrl);
        query.append(" = '");
        query.append(servDesc.getService().getUrl());
        query.append("') AND (");
        query.append(PGDAOConstants.serviceType);
        query.append(" = '");
        query.append(servDesc.getService().getType());
        query.append("')));");

        return query.toString();
    }

    private String insertPhenomenonCommand(SirPhenomenon phenom) {
        StringBuilder cmd = new StringBuilder();

        cmd.append("INSERT INTO ");
        cmd.append(PGDAOConstants.phenomenon);
        cmd.append(" (");
        cmd.append(PGDAOConstants.phenomenonUrn);
        cmd.append(", ");
        cmd.append(PGDAOConstants.phenomenonUom);
        cmd.append(") SELECT '");
        cmd.append(phenom.getUrn());
        cmd.append("', '");
        cmd.append(phenom.getUom());
        cmd.append("' WHERE NOT EXISTS (SELECT ");
        cmd.append(PGDAOConstants.phenomenonUrn);
        cmd.append(", ");
        cmd.append(PGDAOConstants.phenomenonUom);
        cmd.append(" FROM ");
        cmd.append(PGDAOConstants.phenomenon);
        cmd.append(" WHERE (");
        cmd.append(PGDAOConstants.phenomenonUrn);
        cmd.append(" = '");
        cmd.append(phenom.getUrn());
        cmd.append("' AND ");
        cmd.append(PGDAOConstants.phenomenonUom);
        cmd.append(" = '");
        cmd.append(phenom.getUom());
        cmd.append("')) RETURNING ");
        cmd.append(PGDAOConstants.phenomenonId);

        return cmd.toString();
    }

    @Override
    public String insertSensor(SirSensor sensor) throws OwsExceptionReport {

        Connection con = null;
        Statement stmt = null;

        try {
            con = this.cpool.getConnection();
            stmt = con.createStatement();

            // insert in sensor table
            String insertSensor = insertSensorCommand(sensor);
            if (log.isDebugEnabled())
                log.debug(">>>Database Query: " + insertSensor.toString());
            ResultSet rs = stmt.executeQuery(insertSensor);
            if (rs.next()) {
                sensor.setSensorIDInSIR(rs.getString(PGDAOConstants.sensorIdSir));
            }

            if (sensor.getSensorIDInSIR() != null) {
                for (SirPhenomenon phenom : sensor.getPhenomenon()) {
                    // insert in phenomenon table
                    String phenomenonID = "";
                    String insertPhenomenon = insertPhenomenonCommand(phenom);
                    if (log.isDebugEnabled())
                        log.debug(">>>Database Query: " + insertPhenomenon);
                    rs = stmt.executeQuery(insertPhenomenon);
                    while (rs.next()) {
                        phenomenonID = rs.getString(PGDAOConstants.phenomenonId);
                    }
                    if (phenomenonID.isEmpty()) {
                        // phenomenon ID query
                        String phenomenonIDQuery = phenomenonIDQuery(phenom);
                        if (log.isDebugEnabled())
                            log.debug(">>>Database Query: " + phenomenonIDQuery);
                        rs = stmt.executeQuery(phenomenonIDQuery);
                        while (rs.next()) {
                            phenomenonID = rs.getString(PGDAOConstants.phenomenonId);
                        }
                    }
                    // insert in sensor/phenomenon table
                    String insertSensorPhenomenon = insertSensorPhenomenonCommand(sensor, phenomenonID);
                    if (log.isDebugEnabled())
                        log.debug(">>>Database Query: " + insertSensorPhenomenon);
                    stmt.execute(insertSensorPhenomenon);
                }
            }

        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport();
            log.error("Error while adding sensor to database: " + sqle.getMessage());
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 null,
                                 "Error while adding sensor to database: " + sqle.getMessage());
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
        return sensor.getSensorIDInSIR();
    }

    private String insertSensorCommand(SirSensor sensor) {
        StringBuffer cmd = new StringBuffer();
        cmd.append("INSERT INTO ");
        cmd.append(PGDAOConstants.sensor);
        cmd.append(" ( ");
        cmd.append(PGDAOConstants.bBox);
        cmd.append(", ");
        cmd.append(PGDAOConstants.sensorTimeStart);
        cmd.append(", ");
        cmd.append(PGDAOConstants.sensorTimeEnd);
        cmd.append(", ");
        cmd.append(PGDAOConstants.sensorml);
        cmd.append(", ");
        cmd.append(PGDAOConstants.sensorText);
        cmd.append(", ");
        cmd.append(PGDAOConstants.lastUpdate);
        cmd.append(") SELECT ST_GeomFromText('POLYGON((");
        cmd.append(sensor.getbBox().getWest());
        cmd.append(" ");
        cmd.append(sensor.getbBox().getNorth());
        cmd.append(",");
        cmd.append(sensor.getbBox().getEast());
        cmd.append(" ");
        cmd.append(sensor.getbBox().getNorth());
        cmd.append(",");
        cmd.append(sensor.getbBox().getEast());
        cmd.append(" ");
        cmd.append(sensor.getbBox().getSouth());
        cmd.append(",");
        cmd.append(sensor.getbBox().getWest());
        cmd.append(" ");
        cmd.append(sensor.getbBox().getSouth());
        cmd.append(",");
        cmd.append(sensor.getbBox().getWest());
        cmd.append(" ");
        cmd.append(sensor.getbBox().getNorth());
        cmd.append("))',-1), '");
        cmd.append(sensor.getTimePeriod().getStartTime());
        cmd.append("', '");
        cmd.append(sensor.getTimePeriod().getEndTime());
        cmd.append("', '");
        cmd.append(Tools.escapeSQLString(sensor.getSensorMLDocument().xmlText()));
        cmd.append("', '{");
        if ( !sensor.getText().isEmpty()) {
            for (String text : sensor.getText()) {
                cmd.append(text);
                cmd.append(",");
            }
            cmd.deleteCharAt(cmd.length() - 1);
        }
        cmd.append("}', '");
        cmd.append(sensor.getLastUpdate());
        cmd.append("' WHERE NOT EXISTS (SELECT ");
        cmd.append(PGDAOConstants.bBox);
        cmd.append(", ");
        cmd.append(PGDAOConstants.sensorTimeStart);
        cmd.append(", ");
        cmd.append(PGDAOConstants.sensorTimeEnd);
        cmd.append(", ");
        cmd.append(PGDAOConstants.sensorml);
        cmd.append(", ");
        cmd.append(PGDAOConstants.sensorText);
        cmd.append(" FROM ");
        cmd.append(PGDAOConstants.sensor);
        cmd.append(" WHERE (");
        cmd.append(PGDAOConstants.bBox);
        cmd.append(" = ST_GeomFromText('POLYGON((");
        cmd.append(sensor.getbBox().getWest());
        cmd.append(" ");
        cmd.append(sensor.getbBox().getNorth());
        cmd.append(",");
        cmd.append(sensor.getbBox().getEast());
        cmd.append(" ");
        cmd.append(sensor.getbBox().getNorth());
        cmd.append(",");
        cmd.append(sensor.getbBox().getEast());
        cmd.append(" ");
        cmd.append(sensor.getbBox().getSouth());
        cmd.append(",");
        cmd.append(sensor.getbBox().getWest());
        cmd.append(" ");
        cmd.append(sensor.getbBox().getSouth());
        cmd.append(",");
        cmd.append(sensor.getbBox().getWest());
        cmd.append(" ");
        cmd.append(sensor.getbBox().getNorth());
        cmd.append("))',-1)) AND (");
        cmd.append(PGDAOConstants.sensorTimeStart);
        cmd.append(" = '");
        cmd.append(sensor.getTimePeriod().getStartTime());
        cmd.append("') AND (");
        cmd.append(PGDAOConstants.sensorTimeEnd);
        cmd.append(" = '");
        cmd.append(sensor.getTimePeriod().getEndTime());
        cmd.append("') AND (");
        cmd.append(PGDAOConstants.sensorml);
        cmd.append(" = '");
        cmd.append(Tools.escapeSQLString(sensor.getSensorMLDocument().xmlText()));
        cmd.append("') AND (");
        cmd.append(PGDAOConstants.sensorText);
        cmd.append(" = '{");
        if ( !sensor.getText().isEmpty()) {
            for (String text : sensor.getText()) {
                cmd.append(text);
                cmd.append(",");
            }
            cmd.deleteCharAt(cmd.length() - 1);
        }
        cmd.append("}')) RETURNING ");
        cmd.append(PGDAOConstants.sensorIdSir);

        return cmd.toString();
    }

    private String insertSensorPhenomenonCommand(SirSensor sensor, String phenomenonID) {
        StringBuilder cmd = new StringBuilder();

        cmd.append("INSERT INTO ");
        cmd.append(PGDAOConstants.sensorPhen);
        cmd.append(" (");
        cmd.append(PGDAOConstants.sensorIdSirOfSensPhen);
        cmd.append(", ");
        cmd.append(PGDAOConstants.phenomeonIdOfSensPhen);
        cmd.append(") SELECT '");
        cmd.append(sensor.getSensorIDInSIR());
        cmd.append("', '");
        cmd.append(phenomenonID);
        cmd.append("' WHERE NOT EXISTS (SELECT ");
        cmd.append(PGDAOConstants.sensorIdSirOfSensPhen);
        cmd.append(", ");
        cmd.append(PGDAOConstants.phenomeonIdOfSensPhen);
        cmd.append(" FROM ");
        cmd.append(PGDAOConstants.sensorPhen);
        cmd.append(" WHERE (");
        cmd.append(PGDAOConstants.sensorIdSirOfSensPhen);
        cmd.append("='");
        cmd.append(sensor.getSensorIDInSIR());
        cmd.append("' AND ");
        cmd.append(PGDAOConstants.phenomeonIdOfSensPhen);
        cmd.append("='");
        cmd.append(phenomenonID);
        cmd.append("'));");

        return cmd.toString();
    }

    private String phenomenonIDQuery(SirPhenomenon phenom) {
        StringBuilder query = new StringBuilder();

        query.append("SELECT ");
        query.append(PGDAOConstants.phenomenonId);
        query.append(" FROM ");
        query.append(PGDAOConstants.phenomenon);
        query.append(" WHERE (");
        query.append(PGDAOConstants.phenomenonUrn);
        query.append("='");
        query.append(phenom.getUrn());
        query.append("' AND ");
        query.append(PGDAOConstants.phenomenonUom);
        query.append("='");
        query.append(phenom.getUom());
        query.append("');");

        return query.toString();
    }

    private String removeReferenceString(String sensorIdInSir, SirServiceReference servDesc) {
        StringBuilder query = new StringBuilder();

        query.append("DELETE FROM ");
        query.append(PGDAOConstants.sensorService);
        query.append(" WHERE (");
        query.append(PGDAOConstants.sensorIdSirSensServ);
        query.append(" = '");
        query.append(sensorIdInSir);
        query.append("') AND (");
        query.append(PGDAOConstants.serviceSpecId);
        query.append(" = '");
        query.append(servDesc.getServiceSpecificSensorId());
        query.append("') AND (");
        query.append(PGDAOConstants.serviceIdOfSensServ);
        query.append(" = (SELECT ");
        query.append(PGDAOConstants.serviceId);
        query.append(" FROM ");
        query.append(PGDAOConstants.service);
        query.append(" WHERE (");
        query.append(PGDAOConstants.serviceUrl);
        query.append(" = '");
        query.append(servDesc.getService().getUrl());
        query.append("') AND (");
        query.append(PGDAOConstants.serviceType);
        query.append(" = '");
        query.append(servDesc.getService().getType());
        query.append("'))) RETURNING ");
        query.append(PGDAOConstants.sensorIdSir);
        query.append(";");

        return query.toString();
    }

    private String removeSensorString(String sensorIdInSir) {
        StringBuilder query = new StringBuilder();

        query.append("DELETE FROM ");
        query.append(PGDAOConstants.sensor);
        query.append(" WHERE (");
        query.append(PGDAOConstants.sensorIdSir);
        query.append(" = '");
        query.append(sensorIdInSir);
        query.append("') RETURNING ");
        query.append(PGDAOConstants.sensorIdSir);
        query.append(";");

        return query.toString();
    }

    private String updatePhenomenonCommand(SirPhenomenon phenom) {
        StringBuilder cmd = new StringBuilder();

        cmd.append("UPDATE ");
        cmd.append(PGDAOConstants.phenomenon);
        cmd.append(" SET ");
        cmd.append(PGDAOConstants.phenomenonUrn);
        cmd.append(" = '");
        cmd.append(phenom.getUrn());
        cmd.append("', ");
        cmd.append(PGDAOConstants.phenomenonUom);
        cmd.append(" = '");
        cmd.append(phenom.getUom());
        cmd.append("' WHERE ");
        cmd.append(PGDAOConstants.phenomenonId);
        cmd.append(" = ");
        cmd.append(phenom.getPhenomenonIdInSIR());
        cmd.append(";");

        return cmd.toString();
    }

    @Override
    public String updateSensor(SirSensorIdentification sensIdent, SirSensor sensor) throws OwsExceptionReport {
        Connection con = null;
        Statement stmt = null;

        try {
            con = this.cpool.getConnection();
            stmt = con.createStatement();
            String updateSensor = updateSensorCommand(sensor);

            if (log.isDebugEnabled()) {
                String debugString = updateSensor.toString();
                if (debugString.length() > 500) {
                    debugString = debugString.substring(0, 400) + " [...] "
                            + debugString.substring(debugString.length() - 100, debugString.length());
                }
                log.debug(">>>Database Query: " + debugString);
            }

            boolean sensorUpdate = stmt.execute(updateSensor);

            if (sensorUpdate) {
                log.warn("Wanted to do an update, but got a result set as response.");
            }
            else {
                if (log.isDebugEnabled())
                    log.debug("Updated sensor! " + sensor);

                if (sensor.getSensorIDInSIR() != null) {
                    for (SirPhenomenon phenom : sensor.getPhenomenon()) {
                        // phenomenon ID query
                        String phenomenonIDQuery = phenomenonIDQuery(phenom);
                        log.debug(">>>Database Query: " + phenomenonIDQuery);
                        ResultSet rs = stmt.executeQuery(phenomenonIDQuery);
                        while (rs.next()) {
                            String phenomenonID = rs.getString(PGDAOConstants.phenomenonId);
                            phenom.setPhenomenonIdInSIR(phenomenonID);
                        }

                        String updatePhenomenon = updatePhenomenonCommand(phenom);
                        log.debug(">>>Database Query: " + updatePhenomenon);
                        boolean phenUpdate = stmt.execute(updatePhenomenon);

                        if (phenUpdate)
                            log.warn("Wanted to only update phenomenon, but got a result set.");
                        else if (log.isDebugEnabled())
                            log.debug("Updated phenomenon: " + phenom);

                    }
                }
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport();
            log.error("Error while updating sensor in database: " + sqle.getMessage());
            se.addCodedException(ExceptionCode.NoApplicableCode, null, "Error while updating sensor in database: "
                    + sqle.getMessage());
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

        return sensor.getSensorIDInSIR();
    }

    private String updateSensorCommand(SirSensor sensor) {
        StringBuffer cmd = new StringBuffer();

        cmd.append("UPDATE ");
        cmd.append(PGDAOConstants.sensor);
        cmd.append(" SET ");
        cmd.append(PGDAOConstants.bBox);
        cmd.append(" = ");
        cmd.append("ST_GeomFromText('POLYGON((");
        cmd.append(sensor.getbBox().getWest());
        cmd.append(" ");
        cmd.append(sensor.getbBox().getNorth());
        cmd.append(",");
        cmd.append(sensor.getbBox().getEast());
        cmd.append(" ");
        cmd.append(sensor.getbBox().getNorth());
        cmd.append(",");
        cmd.append(sensor.getbBox().getEast());
        cmd.append(" ");
        cmd.append(sensor.getbBox().getSouth());
        cmd.append(",");
        cmd.append(sensor.getbBox().getWest());
        cmd.append(" ");
        cmd.append(sensor.getbBox().getSouth());
        cmd.append(",");
        cmd.append(sensor.getbBox().getWest());
        cmd.append(" ");
        cmd.append(sensor.getbBox().getNorth());
        cmd.append("))',-1)");
        cmd.append(", ");
        cmd.append(PGDAOConstants.sensorTimeStart);
        cmd.append(" = '");
        cmd.append(sensor.getTimePeriod().getStartTime());
        cmd.append("'");
        cmd.append(", ");
        cmd.append(PGDAOConstants.sensorTimeEnd);
        cmd.append(" = '");
        cmd.append(sensor.getTimePeriod().getEndTime());
        cmd.append("'");
        cmd.append(", ");
        cmd.append(PGDAOConstants.sensorml);
        cmd.append(" = '");
        cmd.append(Tools.escapeSQLString(sensor.getSensorMLDocument().xmlText()));
        cmd.append("'");
        cmd.append(", ");
        cmd.append(PGDAOConstants.sensorText);
        cmd.append(" = '{");
        if ( !sensor.getText().isEmpty()) {
            for (String text : sensor.getText()) {
                cmd.append(text);
                cmd.append(",");
            }
            cmd.deleteCharAt(cmd.length() - 1);
        }
        cmd.append("}' ");
        cmd.append(", ");
        cmd.append(PGDAOConstants.lastUpdate);
        cmd.append(" = '");
        cmd.append(sensor.getLastUpdate());
        cmd.append("' WHERE ");
        cmd.append(PGDAOConstants.sensorIdSir);
        cmd.append(" = ");
        cmd.append(sensor.getSensorIDInSIR());
        cmd.append(";");

        return cmd.toString();
    }

}
