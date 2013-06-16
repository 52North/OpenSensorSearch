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
import org.n52.sir.ds.IHarvestServiceDAO;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class PGSQLHarvestServiceDAO implements IHarvestServiceDAO {

    /**
     * the logger, used to log exceptions and additionally information
     */
    private static Logger log = LoggerFactory.getLogger(PGSQLHarvestServiceDAO.class);

    /**
     * Connection pool for creating connections to the DB
     */
    private PGConnectionPool cpool;

    /**
     * 
     * @param cpool
     */
    public PGSQLHarvestServiceDAO(PGConnectionPool cpool) {
        this.cpool = cpool;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.ds.IHarvestServiceDAO#addService(java.lang.String, java.lang.String)
     */
    @Override
    public String addService(String serviceUrl, String serviceType) throws OwsExceptionReport {
        String result = "";

        Connection con = null;
        Statement stmt = null;

        try {
            con = this.cpool.getConnection();
            stmt = con.createStatement();

            // insert service
            String insertService = insertServiceCommand(serviceUrl, serviceType);
            if (log.isDebugEnabled())
                log.debug(">>>Database Query: " + insertService.toString());
            stmt.execute(insertService.toString());

            // service ID query
            String serviceIDQuery = serviceIDQuery(serviceUrl, serviceType);
            if (log.isDebugEnabled())
                log.debug(">>>Database Query: " + serviceIDQuery.toString());
            ResultSet rs = stmt.executeQuery(serviceIDQuery.toString());
            if (rs == null) {
                return result;
            }

            while (rs.next()) {
                result = rs.getString(PGDAOConstants.serviceId);
            }

        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport();
            log.error("Error while adding service to database: " + sqle.getMessage());
            se.addCodedException(ExceptionCode.NoApplicableCode, null, "Error while adding service to database: "
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
        return result;
    }

    /**
     * 
     * @param phenom
     * @return
     */
    private String insertPhenomenonCommand(SirPhenomenon phenom) {
        StringBuffer cmd = new StringBuffer();

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

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.ds.IHarvestServiceDAO#insertSensor(org.n52.sir.datastructure.SirSensor)
     */
    @Override
    public SirSensor insertSensor(SirSensor sensor) throws OwsExceptionReport {

        Connection con = null;
        Statement stmt = null;

        try {
            con = this.cpool.getConnection();
            stmt = con.createStatement();

            // insert in sensor table
            String insertSensor = insertSensorCommand(sensor);

            if (log.isDebugEnabled()) {
                String debugString = insertSensor.toString();
                if (debugString.length() > 500) {
                    debugString = debugString.substring(0, 500) + " [...]";
                }
                if (log.isDebugEnabled())
                    log.debug(">>>Database Query (partial): " + debugString);
            }

            ResultSet rs = stmt.executeQuery(insertSensor);
            if (rs.next()) {
                sensor.setSensorIDInSIR(rs.getString(PGDAOConstants.sensorIdSir));
            }
            else {
                log.warn("Did not receive result set when inserting sensor!");
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

                // insert in sensor/service table
                String insertSensorService = insertSensorServiceCommand(sensor);
                if (log.isDebugEnabled())
                    log.debug(">>>Database Query: " + insertSensorService);
                stmt.execute(insertSensorService);

                // end transaction to insert service and sensors
                if (log.isDebugEnabled())
                    log.debug(">>>Database Query: COMMIT;");
                stmt.execute("COMMIT;");
            }
            else {
                log.error("Error while adding sensor to database! Possible it is already there...");
                // OwsExceptionReport se = new OwsExceptionReport();
                // se.addCodedException(ExceptionCode.NoApplicableCode, null,
                // "Error while adding sensor to database!");
                // throw se;
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

        return sensor;
    }

    /**
     * 
     * @param sensor
     * @return
     */
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
        cmd.append(") SELECT GeometryFromText('POLYGON((");
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

        // check if the document contains characters, namely ' which could break the insert command
        String sensorMLDocument = Tools.escapeSQLString(sensor.getSensorMLDocument().xmlText());
        cmd.append(sensorMLDocument);

        cmd.append("', '{");
        if ( !sensor.getText().isEmpty()) {
            for (String text : sensor.getText()) {
                if ( !text.isEmpty()) {
                    cmd.append(text);
                    cmd.append(",");
                }
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
        cmd.append(" = GeometryFromText('POLYGON((");
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
        cmd.append(sensorMLDocument);
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

    /**
     * 
     * @param sensor
     * @param phenomenonID
     * @return
     */
    private String insertSensorPhenomenonCommand(SirSensor sensor, String phenomenonID) {
        StringBuffer cmd = new StringBuffer();

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

    /**
     * 
     * @param sensor
     * @return
     */
    private String insertSensorServiceCommand(SirSensor sensor) {
        StringBuffer cmd = new StringBuffer();

        cmd.append("INSERT INTO ");
        cmd.append(PGDAOConstants.sensorService);
        cmd.append(" (");
        cmd.append(PGDAOConstants.serviceId);
        cmd.append(", ");
        cmd.append(PGDAOConstants.sensorIdSir);
        cmd.append(", ");
        cmd.append(PGDAOConstants.serviceSpecId);
        cmd.append(") SELECT (SELECT ");
        cmd.append(PGDAOConstants.serviceId);
        cmd.append(" FROM ");
        cmd.append(PGDAOConstants.service);
        cmd.append(" WHERE (");
        cmd.append(PGDAOConstants.serviceUrl);
        cmd.append(" = '");
        cmd.append(sensor.getServDescs().iterator().next().getService().getUrl());
        cmd.append("' AND ");
        cmd.append(PGDAOConstants.serviceType);
        cmd.append(" = '");
        cmd.append(sensor.getServDescs().iterator().next().getService().getType());
        cmd.append("')), '");
        cmd.append(sensor.getSensorIDInSIR());
        cmd.append("', '");
        cmd.append(sensor.getServDescs().iterator().next().getServiceSpecificSensorId());
        cmd.append("' WHERE NOT EXISTS (SELECT ");
        cmd.append(PGDAOConstants.serviceId);
        cmd.append(", ");
        cmd.append(PGDAOConstants.sensorIdSir);
        cmd.append(", ");
        cmd.append(PGDAOConstants.serviceSpecId);
        cmd.append(" FROM ");
        cmd.append(PGDAOConstants.sensorService);
        cmd.append(" WHERE (");
        cmd.append(PGDAOConstants.serviceId);
        cmd.append("=(SELECT ");
        cmd.append(PGDAOConstants.serviceId);
        cmd.append(" FROM ");
        cmd.append(PGDAOConstants.service);
        cmd.append(" WHERE (");
        cmd.append(PGDAOConstants.serviceUrl);
        cmd.append(" = '");
        cmd.append(sensor.getServDescs().iterator().next().getService().getUrl());
        cmd.append("' AND ");
        cmd.append(PGDAOConstants.serviceType);
        cmd.append(" = '");
        cmd.append(sensor.getServDescs().iterator().next().getService().getType());
        cmd.append("')) AND ");
        cmd.append(PGDAOConstants.sensorIdSir);
        cmd.append(" = '");
        cmd.append(sensor.getSensorIDInSIR());
        cmd.append("' AND ");
        cmd.append(PGDAOConstants.serviceSpecId);
        cmd.append(" = '");
        cmd.append(sensor.getServDescs().iterator().next().getServiceSpecificSensorId());
        cmd.append("'));");

        return cmd.toString();
    }

    /**
     * Builds a command, which insert a service
     * 
     * @param serviceUrl
     *        The service url
     * @param serviceType
     *        The service Type
     * @return Returns an insert service command
     */
    private String insertServiceCommand(String serviceUrl, String serviceType) {
        StringBuilder cmd = new StringBuilder();

        cmd.append("INSERT INTO ");
        cmd.append(PGDAOConstants.service);
        cmd.append(" (");
        cmd.append(PGDAOConstants.serviceUrl);
        cmd.append(",");
        cmd.append(PGDAOConstants.serviceType);
        cmd.append(") SELECT '");
        cmd.append(serviceUrl);
        cmd.append("','");
        cmd.append(serviceType);
        cmd.append("' WHERE NOT EXISTS (SELECT ");
        cmd.append(PGDAOConstants.serviceUrl);
        cmd.append(",");
        cmd.append(PGDAOConstants.serviceType);
        cmd.append(" FROM ");
        cmd.append(PGDAOConstants.service);
        cmd.append(" WHERE (");
        cmd.append(PGDAOConstants.serviceUrl);
        cmd.append("='");
        cmd.append(serviceUrl);
        cmd.append("' AND ");
        cmd.append(PGDAOConstants.serviceType);
        cmd.append("='");
        cmd.append(serviceType);
        cmd.append("'));");

        return cmd.toString();
    }

    /**
     * 
     * @param phenom
     * @return
     */
    private String phenomenonIDQuery(SirPhenomenon phenom) {
        StringBuffer query = new StringBuffer();

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

    /**
     * Builds a query, which request the ID of a service by given service URL and service Type
     * 
     * @param serviceUrl
     *        The service URL
     * @param serviceType
     *        The service type
     * @return Returns an service query
     */
    private String serviceIDQuery(String serviceUrl, String serviceType) {
        StringBuilder query = new StringBuilder();

        query.append("SELECT ");
        query.append(PGDAOConstants.serviceId);
        query.append(" FROM ");
        query.append(PGDAOConstants.service);
        query.append(" WHERE (");
        query.append(PGDAOConstants.serviceUrl);
        query.append("='");
        query.append(serviceUrl);
        query.append("' AND ");
        query.append(PGDAOConstants.serviceType);
        query.append("='");
        query.append(serviceType);
        query.append("');");

        return query.toString();
    }
}
