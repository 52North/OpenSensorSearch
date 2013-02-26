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

import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sir.datastructure.SirBoundingBox;
import org.n52.sir.datastructure.SirSearchCriteria;
import org.n52.sir.datastructure.SirSearchCriteria_Phenomenon;
import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.datastructure.SirService;
import org.n52.sir.datastructure.SirServiceReference;
import org.n52.sir.datastructure.SirSimpleSensorDescription;
import org.n52.sir.datastructure.SirXmlSensorDescription;
import org.n52.sir.ds.ISearchSensorDAO;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.util.GMLDateParser;
import org.postgis.Geometry;
import org.postgis.PGgeometry;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schulte
 * 
 */
public class PGSQLSearchSensorDAO implements ISearchSensorDAO {

    /**
     * the logger, used to log exceptions and additionally information
     */
    private static Logger log = LoggerFactory.getLogger(PGSQLSearchSensorDAO.class);

    /**
     * Connection pool for creating connections to the DBs
     */
    private PGConnectionPool cpool;

    /**
     * 
     * @param cpool
     */
    public PGSQLSearchSensorDAO(PGConnectionPool cpool) {
        this.cpool = cpool;
    }

    /**
     * 
     * @return
     */
    private String allSensors() {
        StringBuilder query = new StringBuilder();

        query.append("SELECT ");
        query.append(PGDAOConstants.sensor);
        query.append(".");
        query.append(PGDAOConstants.sensorIdSir);
        query.append(", ");
        query.append(PGDAOConstants.sensor);
        query.append(".");
        query.append(PGDAOConstants.sensorml);
        query.append(", ");
        query.append(PGDAOConstants.sensor);
        query.append(".");
        query.append(PGDAOConstants.sensorText);
        query.append(", ");
        query.append(PGDAOConstants.sensor);
        query.append(".");
        query.append(PGDAOConstants.bBox);
        query.append(", ");
        query.append(PGDAOConstants.sensor);
        query.append(".");
        query.append(PGDAOConstants.lastUpdate);
        query.append(" FROM ");
        query.append(PGDAOConstants.sensor);
        query.append(" GROUP BY ");
        query.append(PGDAOConstants.sensor);
        query.append(".");
        query.append(PGDAOConstants.sensorIdSir);
        query.append(", ");
        query.append(PGDAOConstants.sensor);
        query.append(".");
        query.append(PGDAOConstants.sensorml);
        query.append(", ");
        query.append(PGDAOConstants.sensor);
        query.append(".");
        query.append(PGDAOConstants.sensorText);
        query.append(", ");
        query.append(PGDAOConstants.sensor);
        query.append(".");
        query.append(PGDAOConstants.lastUpdate);
        query.append(";");

        return query.toString();
    }

    /**
     * 
     * @param searchString
     * @param simpleResponse
     * @return
     * @throws OwsExceptionReport
     */
    private Collection<SirSearchResultElement> doQuery(String searchString, boolean simpleResponse) throws OwsExceptionReport {
        ArrayList<SirSearchResultElement> results = new ArrayList<SirSearchResultElement>();
        Connection con = null;
        Statement stmt = null;

        try {
            con = this.cpool.getConnection();
            stmt = con.createStatement();

            if (log.isDebugEnabled())
                log.debug(">>>Database Query: " + searchString);
            ResultSet rs = stmt.executeQuery(searchString);

            while (rs.next()) {
                SirSearchResultElement result = new SirSearchResultElement();

                // sensorIDSir
                result.setSensorIdInSir(rs.getString(PGDAOConstants.sensorIdSirSensServ));
                if (log.isDebugEnabled())
                    log.debug("SensorID: " + result.getSensorIdInSir());

                // sensorDescription
                if (simpleResponse) {
                    SirSimpleSensorDescription descr = new SirSimpleSensorDescription();

                    // sensorDescriptionText
                    StringBuilder sb = new StringBuilder();
                    sb.append("![CDATA[\n"); // preserve line breaks in text
                    Array text = rs.getArray(PGDAOConstants.sensorText);
                    String[] texts = (String[]) text.getArray();
                    for (String temp : texts) {
                        sb.append(temp.trim());
                        sb.append("\n\n");
                    }
                    sb.append("]");
                    if (log.isDebugEnabled())
                        log.debug("Description text: " + sb.toString());

                    descr.setDescriptionText(sb.toString());
                    result.setSensorDescription(descr);

                    Object object = rs.getObject(PGDAOConstants.bBox);
                    Geometry boundingGeom = getGeometry(object);
                    SirBoundingBox bbox = new SirBoundingBox(boundingGeom);
                    if (log.isDebugEnabled())
                        log.debug("Description bounding box: " + bbox);

                    descr.setBoundingBox(bbox);
                }
                else {
                    XmlObject sD = XmlObject.Factory.parse(rs.getString(PGDAOConstants.sensorml));
                    result.setSensorDescription(new SirXmlSensorDescription(sD));
                }

                // last update
                result.setLastUpdate(rs.getDate(PGDAOConstants.lastUpdate));

                results.add(result);
            }

            // get corresponding service references
            for (SirSearchResultElement result : results) {
                String requestServicesString = requestServices(result.getSensorIdInSir());
                if (log.isDebugEnabled())
                    log.debug(">>>Database Query: " + requestServicesString);
                rs = stmt.executeQuery(requestServicesString);

                ArrayList<SirServiceReference> servRefs = new ArrayList<SirServiceReference>();
                while (rs.next()) {
                    // serviceDescription

                    SirServiceReference servDesc = new SirServiceReference();
                    servDesc.setServiceSpecificSensorId(rs.getString(PGDAOConstants.serviceSpecId));
                    servDesc.setService(new SirService(rs.getString(PGDAOConstants.serviceUrl),
                                                       rs.getString(PGDAOConstants.serviceType)));
                    servRefs.add(servDesc);
                }
                result.setServiceReferences(servRefs);
                if (log.isDebugEnabled()) {
                    log.debug("ServiceReferences: " + servRefs + ", lastUpdate: " + result.getLastUpdate());
                }
            }

        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport();
            log.error("Error while quering with search criteria: " + sqle.getMessage());
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 "SearchSensorDAO",
                                 "Error while quering with search criteria: " + sqle.getMessage());
            throw se;
        }
        catch (XmlException xmle) {
            OwsExceptionReport se = new OwsExceptionReport();
            log.error("Error while parsing sensorMLDocument: " + xmle.getMessage());
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 "SearchSensorDAO",
                                 "Error while parsing sensorMLDocument: " + xmle.getMessage());
            throw se;
        }
        catch (Exception e) {
            OwsExceptionReport se = new OwsExceptionReport();
            log.error("Error while parsing sensorMLDocument: " + e.getMessage());
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 "SearchSensorDAO",
                                 "Error while quering with search criteria: " + e.getMessage());
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

        return results;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.ds.ISearchSensorDAO#getAllSensors(boolean)
     */
    @Override
    public Collection<SirSearchResultElement> getAllSensors(boolean simpleReponse) throws OwsExceptionReport {
        // all sensors
        String allSensorsQuery = allSensors();
        return doQuery(allSensorsQuery, simpleReponse);
    }

    /**
     * 
     * @param obj
     * @return
     */
    private Geometry getGeometry(Object obj) {
        if (obj instanceof PGobject) {
            PGobject pgobj = (PGobject) obj;

            if (log.isDebugEnabled())
                log.debug("Trying to get geometry from " + pgobj);

            if (pgobj instanceof PGgeometry) {
                PGgeometry pggeo = (PGgeometry) pgobj;

                if (log.isDebugEnabled())
                    log.debug("Found PGGeometry: " + pggeo);
                // System.out.println(geom.getGeometry().getClass().getCanonicalName());
                // System.out.println(geom.getType());
                // System.out.println(geom.getGeoType());

                Geometry g = pggeo.getGeometry();
                return g;
            }
            return null;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.ds.ISearchSensorDAO#getSensorBySensorID(java.lang.String, boolean)
     */
    @Override
    public SirSearchResultElement getSensorBySensorID(String sensorIdInSir, boolean simpleReponse) throws OwsExceptionReport {
        // Sensor by sensorID query
        String sensorIdQuery = sensorBySensorIdQuery(sensorIdInSir);

        ArrayList<SirSearchResultElement> results = new ArrayList<SirSearchResultElement>();
        results = (ArrayList<SirSearchResultElement>) doQuery(sensorIdQuery, simpleReponse);

        Iterator<SirSearchResultElement> iter = results.iterator();
        if (iter.hasNext()) {
            return results.iterator().next();
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.n52.sir.ds.ISearchSensorDAO#getSensorByServiceDescription(org.n52.sir.datastructure.SirServiceReference
     * , boolean)
     */
    @Override
    public SirSearchResultElement getSensorByServiceDescription(SirServiceReference servDesc, boolean simpleReponse) throws OwsExceptionReport {
        // Sensor by serviceDescription query
        String servDescQuery = sensorByServDescQuery(servDesc);

        ArrayList<SirSearchResultElement> results = new ArrayList<SirSearchResultElement>();
        results = (ArrayList<SirSearchResultElement>) doQuery(servDescQuery, simpleReponse);

        Iterator<SirSearchResultElement> iter = results.iterator();
        if (iter.hasNext()) {
            return results.iterator().next();
        }

        return null;
    }

    /**
     * 
     * @param sensorIdInSir
     * @return
     */
    private String requestServices(String sensorIdInSir) {
        StringBuilder query = new StringBuilder();

        query.append("SELECT ");
        query.append(PGDAOConstants.service);
        query.append(".");
        query.append(PGDAOConstants.serviceUrl);
        query.append(", ");
        query.append(PGDAOConstants.service);
        query.append(".");
        query.append(PGDAOConstants.serviceType);
        query.append(", ");
        query.append(PGDAOConstants.sensorService);
        query.append(".");
        query.append(PGDAOConstants.serviceSpecId);
        query.append(" FROM ");
        query.append(PGDAOConstants.sensorService);
        query.append(", ");
        query.append(PGDAOConstants.service);
        query.append(" WHERE (");
        query.append(PGDAOConstants.sensorService);
        query.append(".");
        query.append(PGDAOConstants.sensorIdSirSensServ);
        query.append(" = '");
        query.append(sensorIdInSir);
        query.append("' AND ");
        query.append(PGDAOConstants.sensorService);
        query.append(".");
        query.append(PGDAOConstants.serviceIdOfSensServ);
        query.append(" = ");
        query.append(PGDAOConstants.service);
        query.append(".");
        query.append(PGDAOConstants.serviceId);
        query.append("); ");

        return query.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.ds.ISearchSensorDAO#searchSensor(org.n52.sir.datastructur .SirSearchCriteria)
     */
    @Override
    public Collection<SirSearchResultElement> searchSensor(SirSearchCriteria searchCriteria, boolean simpleReponse) throws OwsExceptionReport {
        // Sensors by search criteria
        String sensorsBySearchCriteria = sensorsBySearchCriteria(searchCriteria);
        return doQuery(sensorsBySearchCriteria, simpleReponse);
    }

    /**
     * 
     * @param sensorIdInSir
     * @return
     */
    private String sensorBySensorIdQuery(String sensorIdInSir) {
        StringBuilder query = new StringBuilder();

        query.append("SELECT ");
        query.append(PGDAOConstants.sensor);
        query.append(".");
        query.append(PGDAOConstants.sensorIdSir);
        query.append(", ");
        query.append(PGDAOConstants.sensor);
        query.append(".");
        query.append(PGDAOConstants.sensorml);
        query.append(", ");
        query.append(PGDAOConstants.sensor);
        query.append(".");
        query.append(PGDAOConstants.sensorText);
        query.append(", ");
        query.append(PGDAOConstants.sensor);
        query.append(".");
        query.append(PGDAOConstants.lastUpdate);
        query.append(", ");
        query.append(PGDAOConstants.sensor);
        query.append(".");
        query.append(PGDAOConstants.bBox);
        query.append(" FROM ");
        query.append(PGDAOConstants.sensor);
        query.append(" WHERE (");
        query.append(PGDAOConstants.sensor);
        query.append(".");
        query.append(PGDAOConstants.sensorIdSir);
        query.append(" = '");
        query.append(sensorIdInSir);
        query.append("');");

        return query.toString();
    }

    /**
     * 
     * @param servDesc
     * @return
     */
    private String sensorByServDescQuery(SirServiceReference servDesc) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
        query.append(PGDAOConstants.sensor);
        query.append(".");
        query.append(PGDAOConstants.sensorIdSir);
        query.append(", ");
        query.append(PGDAOConstants.sensor);
        query.append(".");
        query.append(PGDAOConstants.sensorml);
        query.append(", ");
        query.append(PGDAOConstants.sensor);
        query.append(".");
        query.append(PGDAOConstants.sensorText);
        query.append(", ");
        query.append(PGDAOConstants.sensor);
        query.append(".");
        query.append(PGDAOConstants.lastUpdate);
        query.append(" FROM ");
        query.append(PGDAOConstants.sensorService);
        query.append(", ");
        query.append(PGDAOConstants.service);
        query.append(", ");
        query.append(PGDAOConstants.sensor);
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
        query.append(PGDAOConstants.sensor);
        query.append(".");
        query.append(PGDAOConstants.sensorIdSir);
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
        query.append("));");

        return query.toString();
    }

    /**
     * 
     * @param searchCriteria
     * @return
     */
    private String sensorsBySearchCriteria(SirSearchCriteria searchCriteria) {
        StringBuffer query = new StringBuffer();

        // extract url and type of service criteria
        ArrayList<String> urls = new ArrayList<String>();
        ArrayList<String> types = new ArrayList<String>();

        if (searchCriteria.getServiceCriteria() != null) {
            for (SirService service : searchCriteria.getServiceCriteria()) {
                if (service.getUrl() != null) {
                    urls.add(service.getUrl());
                }
                if (service.getType() != null) {
                    types.add(service.getType());
                }
            }
        }

        query.append("SELECT ");
        query.append(PGDAOConstants.sensor);
        query.append(".");
        query.append(PGDAOConstants.sensorIdSir);
        query.append(", ");
        query.append(PGDAOConstants.sensor);
        query.append(".");
        query.append(PGDAOConstants.sensorml);
        query.append(", ");
        query.append(PGDAOConstants.sensor);
        query.append(".");
        query.append(PGDAOConstants.sensorText);
        query.append(", ");
        query.append(PGDAOConstants.sensor);
        query.append(".");
        query.append(PGDAOConstants.lastUpdate);
        query.append(", ");
        query.append(PGDAOConstants.sensor);
        query.append(".");
        query.append(PGDAOConstants.bBox);

        // split this up here so that only the tables that are actually used for search are queried
        query.append(" FROM ");
        query.append(PGDAOConstants.sensor);
        query.append(", ");

        boolean serviceUsed = urls.size() > 0 || types.size() > 0;
        if (serviceUsed) {
            query.append(PGDAOConstants.sensorService);
            query.append(", ");
            query.append(PGDAOConstants.service);
            query.append(", ");
        }

        boolean phenomenonUsed = searchCriteria.getPhenomena() != null && searchCriteria.getPhenomena().size() > 0;
        if (phenomenonUsed) {
            query.append(PGDAOConstants.phenomenon);
            query.append(", ");
            query.append(PGDAOConstants.sensorPhen);
            query.append(", ");
        }

        // remove possible trailing comma
        if (query.lastIndexOf(", ") == (query.length() - ", ".length())) {
            query.replace(query.lastIndexOf(", "), query.length(), "");
        }

        query.append(" WHERE ((");

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
        if (searchCriteria.getSearchText() != null && searchCriteria.getSearchText().size() > 0) {
            Iterator<String> iterator = searchCriteria.getSearchText().iterator();
            // query.append("('" + iterator.next() + "' = ANY (" + PGDAOConstants.sensor + "." +
            // PGDAOConstants.sensorText
            // + "))");
            // while (iterator.hasNext()) {
            // query.append(" OR ('" + iterator.next() + "' = ANY (" + PGDAOConstants.sensor + "."
            // + PGDAOConstants.sensorText + "))");
            // }
            query.append("( to_tsvector(array_to_string(");
            query.append(PGDAOConstants.sensorText);
            query.append(", ' -- ')) @@ to_tsquery('");
            while (iterator.hasNext()) {
                query.append(" ''");
                query.append(iterator.next());
                if (iterator.hasNext())
                    query.append(" '' | ");
                else {
                    query.append("'' ");
                    break;
                }
            }
            query.append(" '))");
        }
        else {
            query.append("(true)");
        }
        query.append(") AND (");

        // phenomenon
        if (phenomenonUsed) {
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
        if (searchCriteria.getUom() != null && searchCriteria.getUom().size() > 0) {
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

        if (searchCriteria.getStart() != null && searchCriteria.getEnd() != null) {
            // time period
            query.append("('");
            query.append(GMLDateParser.getInstance().parseDate(searchCriteria.getStart()));
            query.append("' <= ");
            query.append(PGDAOConstants.sensor);
            query.append(".");
            query.append(PGDAOConstants.sensorTimeEnd);
            query.append(" AND '");
            query.append(GMLDateParser.getInstance().parseDate(searchCriteria.getEnd()));
            query.append("' >= ");
            query.append(PGDAOConstants.sensor);
            query.append(".");
            query.append(PGDAOConstants.sensorTimeStart);
            query.append(")");
        }
        else if (searchCriteria.getStart() != null && searchCriteria.getEnd() == null) {
            // time instant
            query.append("('");
            query.append(GMLDateParser.getInstance().parseDate(searchCriteria.getStart()));
            query.append("' <= ");
            query.append(PGDAOConstants.sensor);
            query.append(".");
            query.append(PGDAOConstants.sensorTimeEnd);
            query.append(" AND '");
            query.append(GMLDateParser.getInstance().parseDate(searchCriteria.getStart()));
            query.append("' >= ");
            query.append(PGDAOConstants.sensor);
            query.append(".");
            query.append(PGDAOConstants.sensorTimeStart);
            query.append(")");
        }
        else {
            query.append("(true)");
        }

        query.append(")");

        if (serviceUsed) {
            query.append(" AND (");
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
            query.append(")");
        }
        if (phenomenonUsed) {
            query.append(" AND (");
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
        }
        query.append(") ");

        query.append(" GROUP BY ");
        query.append(PGDAOConstants.sensor);
        query.append(".");
        query.append(PGDAOConstants.sensorIdSir);
        query.append(", ");
        query.append(PGDAOConstants.sensor);
        query.append(".");
        query.append(PGDAOConstants.sensorml);
        query.append(", ");
        query.append(PGDAOConstants.sensor);
        query.append(".");
        query.append(PGDAOConstants.sensorText);
        query.append(", ");
        query.append(PGDAOConstants.sensor);
        query.append(".");
        query.append(PGDAOConstants.lastUpdate);
        query.append(", ");
        query.append(PGDAOConstants.sensor);
        query.append(".");
        query.append(PGDAOConstants.bBox);
        query.append(";");

        return query.toString();
    }

}
