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
import java.util.Collection;

import org.n52.oss.sir.api.SirService;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.oss.sir.ows.OwsExceptionReport.ExceptionLevel;
import org.n52.sir.catalog.ICatalogConnection;
import org.n52.sir.catalog.csw.CatalogConnectionImpl;
import org.n52.sir.ds.IGetCapabilitiesDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class PGSQLGetCapabilitiesDAO implements IGetCapabilitiesDAO {

    private static Logger log = LoggerFactory.getLogger(PGSQLGetCapabilitiesDAO.class);

    private PGConnectionPool cpool;

    @Inject
    public PGSQLGetCapabilitiesDAO(PGConnectionPool cpool) {
        this.cpool = cpool;

        log.debug("NEW {}", this);
    }

    @Override
    public Collection<ICatalogConnection> getCatalogConnections() throws OwsExceptionReport {
        ArrayList<ICatalogConnection> result = new ArrayList<>();

        StringBuffer query = new StringBuffer();
        query.append("SELECT ");
        query.append(PGDAOConstants.catalogIdSir);
        query.append(", ");
        query.append(PGDAOConstants.catalogUrl);
        query.append(", ");
        query.append(PGDAOConstants.pushInterval);
        query.append(", ");
        query.append(PGDAOConstants.catalogStatus);
        query.append(" FROM ");
        query.append(PGDAOConstants.catalog);
        query.append(";");

        try (Connection con = this.cpool.getConnection();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query.toString());) {
            log.debug(">>>Database Query: {}", query.toString());

            // if no phenomenon available give back empty list
            if (rs == null) {
                return result;
            }

            // get result as string
            while (rs.next()) {
                result.add(new CatalogConnectionImpl(rs.getString(PGDAOConstants.catalogIdSir),
                                                     new URL(rs.getString(PGDAOConstants.catalogUrl)),
                                                     rs.getInt(PGDAOConstants.pushInterval),
                                                     rs.getString(PGDAOConstants.catalogStatus)));
            }

            con.close();
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            log.error("Error while query catalog connections for the getCapabilities from database!", sqle);
            se.addCodedException(ExceptionCode.NoApplicableCode, null, sqle);
            throw se;
        }
        catch (MalformedURLException e) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            log.error("Error while query catalog connections for the getCapabilities from database!", e);
            se.addCodedException(ExceptionCode.NoApplicableCode, null, e);
            throw se;
        }

        return result;
    }

    @Override
    public long getPhenomenonCount() throws OwsExceptionReport {
        String tableName = PGDAOConstants.phenomenon;
        return getTableSize(tableName);
    }

    @Override
    public Collection<String> getPhenomenonURNs() throws OwsExceptionReport {
        ArrayList<String> result = new ArrayList<>();

        StringBuffer query = new StringBuffer();

        // build query
        query.append("SELECT " + PGDAOConstants.phenomenonUrn + " FROM " + PGDAOConstants.phenomenon + ";");

        // execute query
        try (Connection con = this.cpool.getConnection(); Statement stmt = con.createStatement();) {
            log.debug(">>>Database Query: {}", query.toString());

            try (ResultSet rs = stmt.executeQuery(query.toString());) {
                // if no phenomenon available give back empty list
                if (rs == null) {
                    return result;
                }

                // get result as string
                while (rs.next()) {
                    String phenom = rs.getString(PGDAOConstants.phenomenonUrn);
                    result.add(phenom);
                }
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            log.error("Error while query phenomenon for the getCapabilities from database!", sqle);
            se.addCodedException(ExceptionCode.NoApplicableCode, null, sqle);
            throw se;
        }

        return result;
    }

    @Override
    public long getSensorCount() throws OwsExceptionReport {
        String tableName = PGDAOConstants.sensor;
        return getTableSize(tableName);
    }

    @Override
    public long getServiceCount() throws OwsExceptionReport {
        String tableName = PGDAOConstants.service;
        return getTableSize(tableName);
    }

    @Override
    public Collection<SirService> getServices() throws OwsExceptionReport {
        ArrayList<SirService> result = new ArrayList<>();

        StringBuffer query = new StringBuffer();
        query.append("SELECT ");
        query.append(PGDAOConstants.serviceUrl);
        query.append(", ");
        query.append(PGDAOConstants.serviceType);
        query.append(" FROM ");
        query.append(PGDAOConstants.service);
        query.append(";");

        try (Connection con = this.cpool.getConnection(); Statement stmt = con.createStatement();) {
            log.debug(">>>Database Query: {}", query.toString());

            try (ResultSet rs = stmt.executeQuery(query.toString());) {
                // if no phenomenon available give back empty list
                if (rs == null) {
                    return result;
                }

                // get result as string
                while (rs.next()) {
                    SirService serv = new SirService(rs.getString(PGDAOConstants.serviceUrl),
                                                     rs.getString(PGDAOConstants.serviceType));
                    result.add(serv);
                }
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            log.error("Error while query services for the getCapabilities from database!", sqle);
            se.addCodedException(ExceptionCode.NoApplicableCode, null, sqle);
            throw se;
        }

        return result;
    }

    private long getTableSize(String tableName) throws OwsExceptionReport {
        long result = Long.MIN_VALUE;

        StringBuffer query = new StringBuffer();
        query.append("SELECT COUNT(*) FROM ");
        query.append(tableName);
        query.append(";");

        try (Connection con = this.cpool.getConnection(); Statement stmt = con.createStatement();) {
            log.debug(">>>Database Query: {}", query.toString());

            try (ResultSet rs = stmt.executeQuery(query.toString());) {
                // if no phenomenon available give back empty list
                if (rs == null) {
                    return result;
                }

                // get result as long
                while (rs.next()) {
                    result = rs.getLong(1);
                }
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            log.error("Error while query services for " + tableName + " from database!", sqle);
            se.addCodedException(ExceptionCode.NoApplicableCode, null, sqle);
            throw se;
        }

        return result;
    }

}