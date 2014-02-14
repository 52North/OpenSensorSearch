/**
 * Copyright 2013 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
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
import java.sql.Timestamp;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.ds.IDescribeSensorDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class PGSQLDescribeSensorDAO implements IDescribeSensorDAO {

    private static Logger log = LoggerFactory.getLogger(PGSQLDescribeSensorDAO.class);

    private PGConnectionPool cpool;

    @Inject
    public PGSQLDescribeSensorDAO(PGConnectionPool cpool) {
        this.cpool = cpool;
    }

    @Override
    public XmlObject getSensorDescription(String sensorId) throws OwsExceptionReport {
        XmlObject sensorML = null;

        try (Connection con = this.cpool.getConnection(); Statement stmt = con.createStatement();) {
            // sensorML query by sensorId
            String sensorMlQuery = sensorMlQuery(sensorId);
            log.debug(">>>Database Query: {}", sensorMlQuery);
            try (ResultSet rs = stmt.executeQuery(sensorMlQuery);) {
                Timestamp timestamp = null;

                while (rs.next()) {
                    sensorML = XmlObject.Factory.parse(rs.getString(PGDAOConstants.sensorml));
                    timestamp = rs.getTimestamp(PGDAOConstants.lastUpdate);
                }

                log.debug("Got SensorML from datbase, last update: {}", timestamp);
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

        return sensorML;
    }

    private String sensorMlQuery(String sensorId) {
        StringBuilder query = new StringBuilder();

        query.append("SELECT ");
        query.append(PGDAOConstants.sensorml);
        query.append(", ");
        query.append(PGDAOConstants.lastUpdate);
        query.append(" FROM ");
        query.append(PGDAOConstants.sensor);
        query.append(" WHERE ");
        query.append(PGDAOConstants.sensorId);
        query.append(" = '");
        query.append(sensorId);
        query.append("'");

        return query.toString();
    }
}
