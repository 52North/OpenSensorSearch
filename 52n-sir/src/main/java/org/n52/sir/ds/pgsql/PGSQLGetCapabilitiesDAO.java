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

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import org.n52.sir.catalog.ICatalogConnection;
import org.n52.sir.catalog.csw.CatalogConnectionImpl;
import org.n52.sir.datastructure.SirService;
import org.n52.sir.ds.IGetCapabilitiesDAO;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.ows.OwsExceptionReport.ExceptionLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PGSQLGetCapabilitiesDAO implements IGetCapabilitiesDAO {

    /**
     * the logger, used to log exceptions and additionally information
     */
    private static Logger log = LoggerFactory.getLogger(PGSQLGetCapabilitiesDAO.class);

    /**
     * Connection pool for creating connections to the DB
     */
    private PGConnectionPool cpool;

    /**
     * constructor
     * 
     * @param cpool
     *        the connection pool containing the connections to the DB
     */
    public PGSQLGetCapabilitiesDAO(PGConnectionPool cpool) {
        this.cpool = cpool;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.ds.IGetCapabilitiesDAO#getCatalogConnections()
     */
    @Override
    public Collection<ICatalogConnection> getCatalogConnections() throws OwsExceptionReport {
        ArrayList<ICatalogConnection> result = new ArrayList<ICatalogConnection>();

        Connection con = null;
        Statement stmt = null;

        StringBuffer query = new StringBuffer();

        // build query
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

        // execute query
        try {
            con = this.cpool.getConnection();
            stmt = con.createStatement();
            if (log.isDebugEnabled())
                log.debug(">>>Database Query: " + query.toString());
            ResultSet rs = stmt.executeQuery(query.toString());

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

        }
        catch (OwsExceptionReport se) {
            log.error("Error while query catalog connections for the getCapabilities from database!", se);
            throw se;
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

        finally {
            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (SQLException e) {
                    log.error("SQL Error.", e);
                }
            }

            // return connection
            if (con != null)
                this.cpool.returnConnection(con);
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.ds.IGetCapabilitiesDAO#getPhenomenaCount()
     */
    @Override
    public long getPhenomenonCount() throws OwsExceptionReport {
        String tableName = PGDAOConstants.phenomenon;
        return getTableSize(tableName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.ds.IGetCapabilitiesDAO#getPhenomenonURNs()
     */
    @Override
    public Collection<String> getPhenomenonURNs() throws OwsExceptionReport {
        ArrayList<String> result = new ArrayList<String>();

        Connection con = null;
        Statement stmt = null;

        StringBuffer query = new StringBuffer();

        // build query
        query.append("SELECT " + PGDAOConstants.phenomenonUrn + " FROM " + PGDAOConstants.phenomenon + ";");

        // execute query
        try {
            con = this.cpool.getConnection();
            stmt = con.createStatement();
            if (log.isDebugEnabled())
                log.debug(">>>Database Query: " + query.toString());
            ResultSet rs = stmt.executeQuery(query.toString());

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
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            log.error("Error while query phenomenon for the getCapabilities from database!", sqle);
            se.addCodedException(ExceptionCode.NoApplicableCode, null, sqle);
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

            // return connection
            if (con != null)
                this.cpool.returnConnection(con);
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.ds.IGetCapabilitiesDAO#getSensorsCount()
     */
    @Override
    public long getSensorCount() throws OwsExceptionReport {
        String tableName = PGDAOConstants.sensor;
        return getTableSize(tableName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.ds.IGetCapabilitiesDAO#getServicesCount()
     */
    @Override
    public long getServiceCount() throws OwsExceptionReport {
        String tableName = PGDAOConstants.service;
        return getTableSize(tableName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.ds.IGetCapabilitiesDAO#getServices()
     */
    @Override
    public Collection<SirService> getServices() throws OwsExceptionReport {
        ArrayList<SirService> result = new ArrayList<SirService>();

        Connection con = null;
        Statement stmt = null;

        StringBuffer query = new StringBuffer();

        // build query
        query.append("SELECT ");
        query.append(PGDAOConstants.serviceUrl);
        query.append(", ");
        query.append(PGDAOConstants.serviceType);
        query.append(" FROM ");
        query.append(PGDAOConstants.service);
        query.append(";");

        // execute query
        try {
            con = this.cpool.getConnection();
            stmt = con.createStatement();
            if (log.isDebugEnabled())
                log.debug(">>>Database Query: " + query.toString());
            ResultSet rs = stmt.executeQuery(query.toString());

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
        catch (OwsExceptionReport se) {
            log.error("Error while query services for the getCapabilities from database!", se);
            throw se;
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            log.error("Error while query services for the getCapabilities from database!", sqle);
            se.addCodedException(ExceptionCode.NoApplicableCode, null, sqle);
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

            // return connection
            if (con != null)
                this.cpool.returnConnection(con);
        }

        return result;
    }

    /**
     * @param tableName
     * @return
     * @throws OwsExceptionReport
     */
    private long getTableSize(String tableName) throws OwsExceptionReport {
        long result = Long.MIN_VALUE;

        Connection con = null;
        Statement stmt = null;

        StringBuffer query = new StringBuffer();

        // build query
        query.append("SELECT COUNT(*) FROM ");
        query.append(tableName);
        query.append(";");

        // execute query
        try {
            con = this.cpool.getConnection();
            stmt = con.createStatement();
            if (log.isDebugEnabled())
                log.debug(">>>Database Query: " + query.toString());
            ResultSet rs = stmt.executeQuery(query.toString());

            // if no phenomenon available give back empty list
            if (rs == null) {
                return result;
            }

            // get result as long
            while (rs.next()) {
                result = rs.getLong(1);
            }

        }
        catch (OwsExceptionReport se) {
            log.error("Error while query services for " + tableName + " from database!", se);
            throw se;
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            log.error("Error while query services for " + tableName + " from database!", sqle);
            se.addCodedException(ExceptionCode.NoApplicableCode, null, sqle);
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

            // return connection
            if (con != null)
                this.cpool.returnConnection(con);
        }

        return result;
    }

}