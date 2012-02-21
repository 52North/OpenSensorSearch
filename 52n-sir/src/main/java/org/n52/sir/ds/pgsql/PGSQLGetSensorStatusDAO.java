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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;

import org.n52.sir.datastructure.SirBoundingBox;
import org.n52.sir.datastructure.SirPropertyFilter;
import org.n52.sir.datastructure.SirSearchCriteria;
import org.n52.sir.datastructure.SirSearchCriteria_Phenomenon;
import org.n52.sir.datastructure.SirSensorIDInSir;
import org.n52.sir.datastructure.SirService;
import org.n52.sir.datastructure.SirServiceReference;
import org.n52.sir.datastructure.SirStatus;
import org.n52.sir.datastructure.SirStatusDescription;
import org.n52.sir.ds.IGetSensorStatusDAO;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.ows.OwsExceptionReport.ExceptionCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schulte
 * 
 */
public class PGSQLGetSensorStatusDAO implements IGetSensorStatusDAO {

    /**
     * The logger, used to log exceptions and additionally information
     */
    private static Logger log = LoggerFactory.getLogger(PGSQLGetSensorStatusDAO.class);

    /**
     * Connection pool for creating connections to the DB
     */
    private PGConnectionPool cpool;

    public PGSQLGetSensorStatusDAO(PGConnectionPool cpool) {
        this.cpool = cpool;
    }

    private String bySearchCriteria(SirSearchCriteria searchCriteria) {
        StringBuffer query = new StringBuffer();
        
        query.append("SELECT ");
        query.append(PGDAOConstants.status);
        query.append(".");
        query.append(PGDAOConstants.sensorIdSirOfStatus);
        query.append(", ");
        query.append(PGDAOConstants.status);
        query.append(".");
        query.append(PGDAOConstants.propertyName);
        query.append(", ");
        query.append(PGDAOConstants.status);
        query.append(".");
        query.append(PGDAOConstants.propertyValue);
        query.append(", ");
        query.append(PGDAOConstants.status);
        query.append(".");
        query.append(PGDAOConstants.uom);
        query.append(", ");
        query.append(PGDAOConstants.status);
        query.append(".");
        query.append(PGDAOConstants.time);
        query.append(" FROM ");
        query.append(PGDAOConstants.sensorService);
        query.append(", ");
        query.append(PGDAOConstants.service);
        query.append(", ");
        query.append(PGDAOConstants.sensor);
        query.append(", ");
        query.append(PGDAOConstants.phenomenon);
        query.append(", ");
        query.append(PGDAOConstants.sensorPhen);
        query.append(", ");
        query.append(PGDAOConstants.status);
        query.append(" WHERE ((");

        // extract url and type of service criteria
        ArrayList<String> urls = new ArrayList<String>();
        ArrayList<String> types = new ArrayList<String>();
        for (SirService service : searchCriteria.getServiceCriteria()) {
            if (service.getUrl() != null) {
                urls.add(service.getUrl());
            }
            if (service.getType() != null) {
                types.add(service.getType());
            }
        }

        // service criteria url
        if (urls.size() > 0) {
            Iterator<String> iterator = urls.iterator();
            query.append("(");
            query.append(PGDAOConstants.service);
            query.append(".");
            query.append(PGDAOConstants.serviceUrl);
            query.append(" = '");
            query.append(iterator.next());
            query.append("')");
            while (iterator.hasNext()) {
                query.append(" OR (");
                query.append(PGDAOConstants.service);
                query.append(".");
                query.append(PGDAOConstants.serviceUrl);
                query.append(" = '");
                query.append(iterator.next());
                query.append("')");
            }
        }
        else {
            query.append("(true)");
        }
        query.append(") AND (");

        // service criteria type
        if (types.size() > 0) {
            Iterator<String> iterator = types.iterator();
            query.append("(");
            query.append(PGDAOConstants.service);
            query.append(".");
            query.append(PGDAOConstants.serviceType);
            query.append(" = '");
            query.append(iterator.next());
            query.append("')");
            while (iterator.hasNext()) {
                query.append(" OR (");
                query.append(PGDAOConstants.service);
                query.append(".");
                query.append(PGDAOConstants.serviceType);
                query.append(" = '");
                query.append(iterator.next());
                query.append("')");
            }
        }
        else {
            query.append("(true)");
        }
        query.append(") AND (");

        // search text
        if (searchCriteria.getSearchText().size() > 0) {
            Iterator<String> iterator = searchCriteria.getSearchText().iterator();
            query.append("('");
            query.append(iterator.next());
            query.append("' = ANY (");
            query.append(PGDAOConstants.sensor);
            query.append(".");
            query.append(PGDAOConstants.sensorText);
            query.append("))");
            while (iterator.hasNext()) {
                query.append(" OR ('");
                query.append(iterator.next());
                query.append("' = ANY (");
                query.append(PGDAOConstants.sensor);
                query.append(".");
                query.append(PGDAOConstants.sensorText);
                query.append("))");
            }
        }
        else {
            query.append("(true)");
        }
        query.append(") AND (");

        // phenomenon
        if (searchCriteria.getPhenomena().size() > 0) {
            Iterator<SirSearchCriteria_Phenomenon> iterator = searchCriteria.getPhenomena().iterator();
            query.append("(");
            query.append(PGDAOConstants.phenomenon);
            query.append(".");
            query.append(PGDAOConstants.phenomenonUrn);
            query.append(" = '");
            query.append(iterator.next().getPhenomenonName());
            query.append("')");
            while (iterator.hasNext()) {
                query.append(" OR (");
                query.append(PGDAOConstants.phenomenon);
                query.append(".");
                query.append(PGDAOConstants.phenomenonUrn);
                query.append(" = '");
                query.append(iterator.next().getPhenomenonName());
                query.append("')");
            }
        }
        else {
            query.append("(true)");
        }
        query.append(") AND (");

        // uom
        if (searchCriteria.getUom().size() > 0) {
            Iterator<String> iterator = searchCriteria.getUom().iterator();
            query.append("(");
            query.append(PGDAOConstants.phenomenon);
            query.append(".");
            query.append(PGDAOConstants.uom);
            query.append(" = '");
            query.append(iterator.next());
            query.append("')");
            while (iterator.hasNext()) {
                query.append(" OR (");
                query.append(PGDAOConstants.phenomenon);
                query.append(".");
                query.append(PGDAOConstants.uom);
                query.append(" = '");
                query.append(iterator.next());
                query.append("')");
            }
        }
        else {
            query.append("(true)");
        }
        query.append(") AND (");

        // bounding box
        if (searchCriteria.getBoundingBox() != null) {
            SirBoundingBox bbox = searchCriteria.getBoundingBox();
            query.append("(CONTAINS (GeometryFromText('POLYGON((");
            query.append(bbox.getWest());
            query.append(" ");
            query.append(bbox.getNorth());
            query.append(",");
            query.append(bbox.getEast());
            query.append(" ");
            query.append(bbox.getNorth());
            query.append(",");
            query.append(bbox.getEast());
            query.append(" ");
            query.append(bbox.getSouth());
            query.append(",");
            query.append(bbox.getWest());
            query.append(" ");
            query.append(bbox.getSouth());
            query.append(",");
            query.append(bbox.getWest());
            query.append(" ");
            query.append(bbox.getNorth());
            query.append("))',-1),");
            query.append(PGDAOConstants.sensor);
            query.append(".");
            query.append(PGDAOConstants.bBox);
            query.append("))");
        }
        else {
            query.append("(true)");
        }
        query.append(") AND (");

        // timeperiod
        if (searchCriteria.getStart() != null && searchCriteria.getEnd() != null) {
            query.append("(");
            query.append(PGDAOConstants.sensor);
            query.append(".");
            query.append(PGDAOConstants.sensorTimeStart);
            query.append(" BETWEEN '");
            query.append(searchCriteria.getStart());
            query.append("' AND '");
            query.append(searchCriteria.getEnd());
            query.append("') OR (");
            query.append(PGDAOConstants.sensor);
            query.append(".");
            query.append(PGDAOConstants.sensorTimeEnd);
            query.append(" BETWEEN '");
            query.append(searchCriteria.getStart());
            query.append("' AND '");
            query.append(searchCriteria.getEnd());
            query.append("')");
        }
        else {
            query.append("(true)");
        }

        query.append(") AND (");
        query.append(PGDAOConstants.sensor);
        query.append(".");
        query.append(PGDAOConstants.sensorIdSir);
        query.append(" = ");
        query.append(PGDAOConstants.sensorService);
        query.append(".");
        query.append(PGDAOConstants.sensorIdSirSensServ);
        query.append(") AND (");
        query.append(PGDAOConstants.sensorService);
        query.append(".");
        query.append(PGDAOConstants.serviceIdOfSensServ);
        query.append(" = ");
        query.append(PGDAOConstants.service);
        query.append(".");
        query.append(PGDAOConstants.serviceId);
        query.append(") AND (");
        query.append(PGDAOConstants.sensorPhen);
        query.append(".");
        query.append(PGDAOConstants.sensorIdSirOfSensPhen);
        query.append(" = ");
        query.append(PGDAOConstants.sensor);
        query.append(".");
        query.append(PGDAOConstants.sensorIdSir);
        query.append(") AND (");
        query.append(PGDAOConstants.phenomenon);
        query.append(".");
        query.append(PGDAOConstants.phenomenonId);
        query.append(" = ");
        query.append(PGDAOConstants.sensorPhen);
        query.append(".");
        query.append(PGDAOConstants.phenomeonIdOfSensPhen);
        query.append(")");
        
        return query.toString();
    }

    private String bySensorID(SirSensorIDInSir sensorId) {
        StringBuilder query = new StringBuilder();

        query.append("SELECT ");
        query.append(PGDAOConstants.status);
        query.append(".");
        query.append(PGDAOConstants.sensorIdSirOfStatus);
        query.append(", ");
        query.append(PGDAOConstants.status);
        query.append(".");
        query.append(PGDAOConstants.propertyName);
        query.append(", ");
        query.append(PGDAOConstants.status);
        query.append(".");
        query.append(PGDAOConstants.propertyValue);
        query.append(", ");
        query.append(PGDAOConstants.status);
        query.append(".");
        query.append(PGDAOConstants.uom);
        query.append(", ");
        query.append(PGDAOConstants.status);
        query.append(".");
        query.append(PGDAOConstants.time);
        query.append(" FROM ");
        query.append(PGDAOConstants.sensor);
        query.append(", status WHERE (");
        query.append(PGDAOConstants.status);
        query.append(".");
        query.append(PGDAOConstants.sensorIdSirOfStatus);
        query.append(" = '");
        query.append(sensorId.getSensorIdInSir());
        query.append("'");

        return query.toString();
    }

    private String byServiceDescription(SirServiceReference servDesc) {
        StringBuilder query = new StringBuilder();
        
        query.append("SELECT ");
        query.append(PGDAOConstants.status);
        query.append(".");
        query.append(PGDAOConstants.sensorIdSirOfStatus);
        query.append(", ");
        query.append(PGDAOConstants.status);
        query.append(".");
        query.append(PGDAOConstants.propertyName);
        query.append(", ");
        query.append(PGDAOConstants.status);
        query.append(".");
        query.append(PGDAOConstants.propertyValue);
        query.append(", ");
        query.append(PGDAOConstants.status);
        query.append(".");
        query.append(PGDAOConstants.uom);
        query.append(", ");
        query.append(PGDAOConstants.status);
        query.append(".");
        query.append(PGDAOConstants.time);
        query.append(" FROM ");
        query.append(PGDAOConstants.sensorService);
        query.append(", ");
        query.append(PGDAOConstants.service);
        query.append(", ");
        query.append(PGDAOConstants.status);
        query.append(" WHERE ((");
        query.append(PGDAOConstants.service);
        query.append(".");
        query.append(PGDAOConstants.serviceUrl);
        query.append(" = '");
        query.append(servDesc.getService().getUrl());
        query.append("') AND (");
        query.append(PGDAOConstants.service);
        query.append(".");
        query.append(PGDAOConstants.serviceType);
        query.append(" = '");
        query.append(servDesc.getService().getType());
        query.append("') AND (");
        query.append(PGDAOConstants.sensorService);
        query.append(".");
        query.append(PGDAOConstants.serviceSpecId);
        query.append(" = '");
        query.append(servDesc.getServiceSpecificSensorId());
        query.append("') AND (");
        query.append(PGDAOConstants.status);
        query.append(".");
        query.append(PGDAOConstants.sensorIdSirOfStatus);
        query.append(" = ");
        query.append(PGDAOConstants.sensorService);
        query.append(".");
        query.append(PGDAOConstants.sensorIdSir);
        query.append(") AND (");
        query.append(PGDAOConstants.sensorService);
        query.append(".");
        query.append(PGDAOConstants.serviceId);
        query.append(" = ");
        query.append(PGDAOConstants.service);
        query.append(".");
        query.append(PGDAOConstants.serviceId);
        query.append(")");
        
        return query.toString();
    }

    private Collection<SirStatusDescription> doQuery(String query) throws OwsExceptionReport {
        ArrayList<SirStatusDescription> result = new ArrayList<SirStatusDescription>();
        Connection con = null;
        Statement stmt = null;
        
        try {
            con = this.cpool.getConnection();
            stmt = con.createStatement();

            if (log.isDebugEnabled())
                log.debug(">>>Database Query: " + query);
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                SirStatusDescription statusDesc = new SirStatusDescription();
                SirStatus status = new SirStatus();

                // sensorIDInSIR
                statusDesc.setSensorIdInSir(rs.getString(PGDAOConstants.sensorIdSirOfStatus));
                // propertyName
                status.setPropertyName(rs.getString(PGDAOConstants.propertyName));
                // propertyValue
                status.setPropertyValue(rs.getString(PGDAOConstants.propertyValue));
                // uom
                status.setUom(rs.getString(PGDAOConstants.uom));
                // timestamp
                Calendar cal = Calendar.getInstance();
                Timestamp t = rs.getTimestamp(PGDAOConstants.time);
                cal.setTime(t);
                status.setTimestamp(cal);

                statusDesc.setStatus(status);
                result.add(statusDesc);
            }

        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport();
            log.error("Error while quering for sensor status with search criteria: " + sqle.getMessage());
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 "PGSQLGetSensorStatusDAO.getSensorStatusbySearchCriteria()",
                                 "Error while quering for sensor status with search criteria: " + sqle.getMessage());
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
        
        return result;
    }

    private Collection<SirStatusDescription> filter(Collection<SirStatusDescription> unfiltered,
                                                    SirPropertyFilter sirPropertyFilter) throws OwsExceptionReport {
        Collection<SirStatusDescription> filtered = new ArrayList<SirStatusDescription>();

        // do filtering other than text-based equals
        if (sirPropertyFilter.getPropConst() != null) {
            switch (sirPropertyFilter.getPropConst().getConstraint().getConsType()) {
            case isLessThan:
                for (SirStatusDescription sirStatusDescription : unfiltered) {
                    double d = Double.parseDouble((String) sirStatusDescription.getStatus().getPropertyValue());
                    if (d < sirPropertyFilter.getPropConst().getConstraint().getValueDouble()) {
                        filtered.add(sirStatusDescription);
                    }
                }
                break;
            case isLessThanOrEqualTo:
                for (SirStatusDescription sirStatusDescription : unfiltered) {
                    double d = Double.parseDouble((String) sirStatusDescription.getStatus().getPropertyValue());
                    if (d <= sirPropertyFilter.getPropConst().getConstraint().getValueDouble()) {
                        filtered.add(sirStatusDescription);
                    }
                }
                break;
            case isGreaterThan:
                for (SirStatusDescription sirStatusDescription : unfiltered) {
                    double d = Double.parseDouble((String) sirStatusDescription.getStatus().getPropertyValue());
                    if (d > sirPropertyFilter.getPropConst().getConstraint().getValueDouble()) {
                        filtered.add(sirStatusDescription);
                    }
                }
                break;
            case isGreaterThanOrEqualTo:
                for (SirStatusDescription sirStatusDescription : unfiltered) {
                    double d = Double.parseDouble((String) sirStatusDescription.getStatus().getPropertyValue());
                    if (d >= sirPropertyFilter.getPropConst().getConstraint().getValueDouble()) {
                        filtered.add(sirStatusDescription);
                    }
                }
                break;
            case isBetween:
                for (SirStatusDescription sirStatusDescription : unfiltered) {
                    double d = Double.parseDouble((String) sirStatusDescription.getStatus().getPropertyValue());
                    if (sirPropertyFilter.getPropConst().getConstraint().getLowerBoundary() < d
                            && d < sirPropertyFilter.getPropConst().getConstraint().getUpperBoundary()) {
                        filtered.add(sirStatusDescription);
                    }
                }
                break;
            case isEqualTo:
                for (SirStatusDescription sirStatusDescription : unfiltered) {
                    Object value = sirStatusDescription.getStatus().getPropertyValue();
                    if (sirPropertyFilter.getPropConst().getConstraint().getValueString().equals(value)) {
                        filtered.add(sirStatusDescription);
                    }
                }
                break;
            default:
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(ExceptionCode.OperationNotSupported,
                                     "PGSQLGetSensorStatusDAO",
                                     "Constraint currently not supported: "
                                             + sirPropertyFilter.getPropConst().getConstraint().getConsType().name()
                                             + "!");
                log.error("Constraint currently not supported: "
                        + sirPropertyFilter.getPropConst().getConstraint().getConsType().name() + "!");
                throw se;
            }

            return filtered;
        }

        return unfiltered;
    }

    /**
     * @param propertyFilter
     * @param fullQuery
     * @return
     * @throws OwsExceptionReport
     */
    private Collection<SirStatusDescription> filterQueryResult(Collection<SirPropertyFilter> propertyFilter,
                                                               Collection<SirStatusDescription> fullQuery) throws OwsExceptionReport {
        Collection<SirStatusDescription> filtered = new ArrayList<SirStatusDescription>(fullQuery);

        if (propertyFilter == null) {
            if (log.isDebugEnabled()) {
                log.debug("No property filter given!");
            }
        }
        else {
            for (SirPropertyFilter sirPropertyFilter : propertyFilter) {
                filtered = filter(filtered, sirPropertyFilter);
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("Filtered to " + filtered.size() + " of " + fullQuery.size() + " results.");
        }
        return filtered;
    }

    @Override
    public Collection<SirStatusDescription> getSensorStatusBySearchCriteria(SirSearchCriteria searchCriteria,
                                                                            Collection<SirPropertyFilter> propertyFilter) throws OwsExceptionReport {

        // build query with search criteria
        String searchCriteriaQuery = bySearchCriteria(searchCriteria) + propertyFilterQuery(propertyFilter);
        Collection<SirStatusDescription> fullQuery = doQuery(searchCriteriaQuery);

        Collection<SirStatusDescription> filtered = filterQueryResult(propertyFilter, fullQuery);

        return filtered;
    }

    @Override
    public Collection<SirStatusDescription> getSensorStatusBySensorID(SirSensorIDInSir sensorId,
                                                                      Collection<SirPropertyFilter> propertyFilter) throws OwsExceptionReport {
        // build query with sensorID in SIR
        String sensorIDQuery = bySensorID(sensorId) + propertyFilterQuery(propertyFilter);
        Collection<SirStatusDescription> fullQuery = doQuery(sensorIDQuery);
        Collection<SirStatusDescription> filtered = filterQueryResult(propertyFilter, fullQuery);

        return filtered;
    }

    @Override
    public Collection<SirStatusDescription> getSensorStatusByServiceDescription(SirServiceReference servDesc,
                                                                                Collection<SirPropertyFilter> propertyFilter) throws OwsExceptionReport {

        // build query with service description
        String serviceDescQuery = byServiceDescription(servDesc) + propertyFilterQuery(propertyFilter);
        Collection<SirStatusDescription> fullQuery = doQuery(serviceDescQuery);
        Collection<SirStatusDescription> filtered = filterQueryResult(propertyFilter, fullQuery);

        return filtered;
    }

    private String propertyFilterQuery(Collection<SirPropertyFilter> propertyFilter) throws OwsExceptionReport {
        StringBuffer query = new StringBuffer();

        if (propertyFilter != null && !propertyFilter.isEmpty()) {
            query.append(" AND (");
            Iterator<SirPropertyFilter> iterator = propertyFilter.iterator();
            query.append(propFilt(iterator.next()));
            while (iterator.hasNext()) {
                query.append(" OR ");
                query.append(propFilt(iterator.next()));
            }
            query.append(")");
        }
        query.append(") GROUP BY ");
        query.append(PGDAOConstants.status);
        query.append(".");
        query.append(PGDAOConstants.sensorIdSirOfStatus);
        query.append(", ");
        query.append(PGDAOConstants.status);
        query.append(".");
        query.append(PGDAOConstants.propertyName);
        query.append(", ");
        query.append(PGDAOConstants.status);
        query.append(".");
        query.append(PGDAOConstants.propertyValue);
        query.append(", ");
        query.append(PGDAOConstants.status);
        query.append(".");
        query.append(PGDAOConstants.uom);
        query.append(", ");
        query.append(PGDAOConstants.status);
        query.append(".");
        query.append(PGDAOConstants.time);
        query.append(";");
        
        return query.toString();
    }

    private String propFilt(SirPropertyFilter propFilter) throws OwsExceptionReport {
        StringBuffer filter = new StringBuffer();
        
        filter.append("(");
        filter.append("(");
        filter.append(PGDAOConstants.status);
        filter.append(".");
        filter.append(PGDAOConstants.propertyName);
        filter.append(" = '");
        filter.append(propFilter.getPropertyName());
        filter.append("') AND ");
        if (propFilter.getPropConst() != null) {
            switch (propFilter.getPropConst().getConstraint().getConsType()) {
            case isEqualTo:
                filter.append("(");
                filter.append(PGDAOConstants.status);
                filter.append(".");
                filter.append(PGDAOConstants.propertyValue);
                filter.append(" = '");
                filter.append(propFilter.getPropConst().getConstraint().getValueString());
                filter.append("')");
                break;
            case isNotEqualTo:
                filter.append("(");
                filter.append(PGDAOConstants.status);
                filter.append(".");
                filter.append(PGDAOConstants.propertyValue);
                filter.append(" <> '");
                filter.append(propFilter.getPropConst().getConstraint().getValueString());
                filter.append("')");
                break;
            // cases are filtered later on, just use the same code as in isNotEqualTo
            case isLessThan:
                filter.append("(");
                filter.append(PGDAOConstants.status);
                filter.append(".");
                filter.append(PGDAOConstants.propertyValue);
                filter.append(" <> '");
                filter.append(propFilter.getPropConst().getConstraint().getValueString());
                filter.append("')");
                break;
            case isLessThanOrEqualTo:
                filter.append("(");
                filter.append(PGDAOConstants.status);
                filter.append(".");
                filter.append(PGDAOConstants.propertyValue);
                filter.append(" <> '");
                filter.append(propFilter.getPropConst().getConstraint().getValueString());
                filter.append("')");
                break;
            case isGreaterThan:
                filter.append("(");
                filter.append(PGDAOConstants.status);
                filter.append(".");
                filter.append(PGDAOConstants.propertyValue);
                filter.append(" <> '");
                filter.append(propFilter.getPropConst().getConstraint().getValueString());
                filter.append("')");
                break;
            case isGreaterThanOrEqualTo:
                filter.append("(");
                filter.append(PGDAOConstants.status);
                filter.append(".");
                filter.append(PGDAOConstants.propertyValue);
                filter.append(" <> '");
                filter.append(propFilter.getPropConst().getConstraint().getValueString());
                filter.append("')");
                break;
            case isBetween:
                filter.append("(");
                filter.append(PGDAOConstants.status);
                filter.append(".");
                filter.append(PGDAOConstants.propertyValue);
                filter.append(" <> '");
                filter.append(propFilter.getPropConst().getConstraint().getLowerBoundary());
                filter.append("')");
                break;
            default:
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(ExceptionCode.OperationNotSupported,
                                     "PGSQLGetSensorStatusDAO",
                                     "Constraint currently not supported: "
                                             + propFilter.getPropConst().getConstraint().getConsType().name() + "!");
                log.error("Constraint currently not supported: "
                        + propFilter.getPropConst().getConstraint().getConsType().name() + "!");
                throw se;
            }
            if (propFilter.getPropConst().getUom() != null) {
                filter.append(" AND (");
                filter.append(PGDAOConstants.status);
                filter.append(".");
                filter.append(PGDAOConstants.uom);
                filter.append(" = '");
                filter.append(propFilter.getPropConst().getUom());
                filter.append("')");
            }
        }
        else {
            filter.append(" (true) ");
        }
        filter.append(")");
        
        return filter.toString();
    }
}
