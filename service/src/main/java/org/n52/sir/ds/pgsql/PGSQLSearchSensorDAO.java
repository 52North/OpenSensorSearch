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
import org.n52.oss.sir.api.SirBoundingBox;
import org.n52.oss.sir.api.SirSearchCriteria;
import org.n52.oss.sir.api.SirSearchCriteria_Phenomenon;
import org.n52.oss.sir.api.SirSearchResultElement;
import org.n52.oss.sir.api.SirService;
import org.n52.oss.sir.api.SirServiceReference;
import org.n52.oss.sir.api.SirSimpleSensorDescription;
import org.n52.oss.sir.api.SirXmlSensorDescription;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.ds.ISearchSensorDAO;
import org.n52.sir.util.GMLDateParser;
import org.postgis.Geometry;
import org.postgis.PGgeometry;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author Jan Schulte
 * 
 */
public class PGSQLSearchSensorDAO implements ISearchSensorDAO {

    private static Logger log = LoggerFactory.getLogger(PGSQLSearchSensorDAO.class);

    private PGConnectionPool cpool;

    private boolean crazyDebug = false;

    @Inject
    public PGSQLSearchSensorDAO(PGConnectionPool cpool) {
        this.cpool = cpool;

        log.debug("NEW {}", this);
    }

    private String allSensorPublicIds() {
        StringBuilder query = new StringBuilder();

        query.append("SELECT ");
        query.append(PGDAOConstants.sensor);
        query.append(".");
        query.append(PGDAOConstants.sensorId);
        query.append(", ");
        query.append(PGDAOConstants.sensor);
        query.append(".");
        query.append(PGDAOConstants.lastUpdate);
        query.append(" FROM ");
        query.append(PGDAOConstants.sensor);
        query.append(" GROUP BY ");
        query.append(PGDAOConstants.sensor);
        query.append(".");
        query.append(PGDAOConstants.sensorId);
        query.append(", ");
        query.append(PGDAOConstants.sensor);
        query.append(".");
        query.append(PGDAOConstants.lastUpdate);
        query.append(";");

        return query.toString();
    }

    private String allSensors() {
        StringBuilder query = new StringBuilder();

        query.append("SELECT ");
        query.append(PGDAOConstants.sensor);
        query.append(".");
        query.append(PGDAOConstants.sensorId);
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
        query.append(PGDAOConstants.sensorId);
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

    private Collection<SirSearchResultElement> doQuery(String searchString,
                                                       boolean simpleResponse,
                                                       boolean addServiceReferences) throws OwsExceptionReport {
        ArrayList<SirSearchResultElement> results = new ArrayList<>();

        try (Connection con = this.cpool.getConnection(); Statement stmt = con.createStatement()) {

            log.debug(">>>Database Query: {}", searchString);
            try (ResultSet rs = stmt.executeQuery(searchString);) {

                while (rs.next()) {
                    SirSearchResultElement result = new SirSearchResultElement();

                    // sensorIDSir
                    result.setSensorId(rs.getString(PGDAOConstants.sensorId));

                    if (this.crazyDebug)
                        log.debug("SensorID: {}", result.getSensorId());

                    // sensorDescription
                    if (simpleResponse) {
                        SirSimpleSensorDescription descr = new SirSimpleSensorDescription();

                        // sensorDescriptionText
                        StringBuilder sb = new StringBuilder();
                        sb.append("![CDATA["); // preserve line breaks in text
                        Array text = rs.getArray(PGDAOConstants.sensorText);
                        String[] texts = (String[]) text.getArray();
                        for (String s : texts) {
                            sb.append(s.trim());
                            sb.append("\t");
                        }
                        sb.append("]");

                        if (this.crazyDebug)
                            log.debug("Description text: {}", sb.toString());

                        descr.setDescriptionText(sb.toString());
                        result.setSensorDescription(descr);

                        Object object = rs.getObject(PGDAOConstants.bBox);
                        Geometry boundingGeom = getGeometry(object);
                        SirBoundingBox bbox = new SirBoundingBox(boundingGeom);

                        if (this.crazyDebug)
                            log.debug("Description bounding box: {}", bbox);

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
            } // try-with ResultSet

            // get corresponding service references
            if (addServiceReferences) {
                for (SirSearchResultElement result : results) {
                    String requestServicesString = requestServices(result.getSensorId());
                    log.debug(">>>Database Query: {}", requestServicesString);
                    try (ResultSet rs = stmt.executeQuery(requestServicesString);) {

                        ArrayList<SirServiceReference> servRefs = new ArrayList<>();
                        while (rs.next()) {
                            // serviceDescription

                            SirServiceReference servDesc = new SirServiceReference();
                            servDesc.setServiceSpecificSensorId(rs.getString(PGDAOConstants.serviceSpecId));
                            servDesc.setService(new SirService(rs.getString(PGDAOConstants.serviceUrl),
                                                               rs.getString(PGDAOConstants.serviceType)));
                            servRefs.add(servDesc);
                        }
                        result.setServiceReferences(servRefs);

                        log.debug("ServiceReferences: {}, lastUpdate: {}", servRefs, result.getLastUpdate());
                    }
                }
            }

        }
        catch (SQLException | XmlException e) {
            OwsExceptionReport se = new OwsExceptionReport();
            log.error("Error while quering with search criteria: " + e.getMessage());
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 "SearchSensorDAO",
                                 "Error while quering with search criteria: " + e.getMessage());
            throw se;
        }

        return results;
    }

    @Override
    public Collection<String> getAllSensorIds() throws OwsExceptionReport {
        String query = allSensorPublicIds();
        log.debug(">>>Database Query: {}", query);

        ArrayList<String> results = new ArrayList<>();

        try (Connection con = this.cpool.getConnection(); Statement stmt = con.createStatement()) {
            try (ResultSet rs = stmt.executeQuery(query);) {
                while (rs.next()) {
                    String id = rs.getString(PGDAOConstants.sensorId);
                    if (this.crazyDebug)
                        log.debug("SensorID: {}", id);

                    results.add(id);
                }
            }
        }
        catch (SQLException e) {
            OwsExceptionReport se = new OwsExceptionReport();
            log.error("Error while quering with search criteria: " + e.getMessage());
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 "SearchSensorDAO",
                                 "Error while quering with search criteria: " + e.getMessage());
            throw se;
        }

        return results;
    }

    @Override
    public Collection<SirSearchResultElement> getAllSensors(boolean simpleReponse) throws OwsExceptionReport {
        String allSensorsQuery = allSensors();
        return doQuery(allSensorsQuery, simpleReponse, true);
    }

    private Geometry getGeometry(Object obj) {
        if (obj instanceof PGobject) {
            PGobject pgobj = (PGobject) obj;

            log.debug("Trying to get geometry from {}", pgobj);

            if (pgobj instanceof PGgeometry) {
                PGgeometry pggeo = (PGgeometry) pgobj;

                log.debug("Found PGGeometry: {} of type {} and geotype {}", pggeo, pggeo.getType(), pggeo.getGeoType());

                Geometry g = pggeo.getGeometry();
                return g;
            }
            return null;
        }
        return null;
    }

    @Override
    public SirSearchResultElement getSensorBySensorID(String sensorId, boolean simpleReponse) throws OwsExceptionReport {
        // Sensor by sensorID query
        String sensorIdQuery = sensorBySensorIdQuery(sensorId);

        ArrayList<SirSearchResultElement> results = new ArrayList<>();
        results = (ArrayList<SirSearchResultElement>) doQuery(sensorIdQuery, simpleReponse, true);

        Iterator<SirSearchResultElement> iter = results.iterator();
        if (iter.hasNext()) {
            return results.iterator().next();
        }

        return null;
    }

    @Override
    public SirSearchResultElement getSensorByServiceDescription(SirServiceReference servDesc, boolean simpleReponse) throws OwsExceptionReport {
        // Sensor by serviceDescription query
        String servDescQuery = sensorByServDescQuery(servDesc);

        ArrayList<SirSearchResultElement> results = new ArrayList<>();
        results = (ArrayList<SirSearchResultElement>) doQuery(servDescQuery, simpleReponse, true);

        Iterator<SirSearchResultElement> iter = results.iterator();
        if (iter.hasNext()) {
            return results.iterator().next();
        }

        return null;
    }

    private String requestServices(String sensorId) {
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
        query.append(" = ");

        // id subquery
        query.append("(");
        query.append("SELECT ");
        query.append(PGDAOConstants.sensor);
        query.append(".");
        query.append(PGDAOConstants.sensorIdSirSensServ);
        query.append(" FROM ");
        query.append(PGDAOConstants.sensor);
        query.append(" WHERE ");
        query.append(PGDAOConstants.sensor);
        query.append(".");
        query.append(PGDAOConstants.sensorId);
        query.append(" = '");
        query.append(sensorId);
        query.append("')");

        query.append(" AND ");
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

    @Override
    public Collection<SirSearchResultElement> searchSensor(SirSearchCriteria searchCriteria, boolean simpleReponse) throws OwsExceptionReport {
        // Sensors by search criteria
        String sensorsBySearchCriteria = sensorsBySearchCriteria(searchCriteria);
        return doQuery(sensorsBySearchCriteria, simpleReponse, true);
    }

    private String sensorBySensorIdQuery(String sensorId) {
        StringBuilder query = new StringBuilder();

        query.append("SELECT ");
        query.append(PGDAOConstants.sensor);
        query.append(".");
        query.append(PGDAOConstants.sensorId);
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
        query.append(PGDAOConstants.sensorId);
        query.append(" = '");
        query.append(sensorId);
        query.append("');");

        return query.toString();
    }

    private String sensorByServDescQuery(SirServiceReference servDesc) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
        query.append(PGDAOConstants.sensor);
        query.append(".");
        query.append(PGDAOConstants.sensorId);
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
        query.append(PGDAOConstants.sensorId);
        query.append(" = ");
        query.append(PGDAOConstants.sensorService);
        query.append(".");
        query.append(PGDAOConstants.sensorId);
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

    private String sensorsBySearchCriteria(SirSearchCriteria searchCriteria) {
        StringBuffer query = new StringBuffer();

        // extract url and type of service criteria
        ArrayList<String> urls = new ArrayList<>();
        ArrayList<String> types = new ArrayList<>();

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
        query.append(PGDAOConstants.sensorId);
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
            query.append("(CONTAINS (ST_GeometryFromText('POLYGON((");
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
            query.append(PGDAOConstants.sensorId);
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
            query.append(PGDAOConstants.sensorId);
            query.append(") AND (");
            query.append(PGDAOConstants.phenomenon);
            query.append(".");
            query.append(PGDAOConstants.obsPropId);
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
        query.append(PGDAOConstants.sensorId);
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PGSQLSearchSensorDAO [");
        if (this.cpool != null) {
            builder.append("cpool=");
            builder.append(this.cpool);
        }
        builder.append("]");
        return builder.toString();
    }

}
