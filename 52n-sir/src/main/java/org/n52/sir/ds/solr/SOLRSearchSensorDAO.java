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
/**
 * @author Yakoub
 */

package org.n52.sir.ds.solr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.n52.sir.datastructure.SirBoundingBox;
import org.n52.sir.datastructure.SirSearchCriteria;
import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.datastructure.SirServiceReference;
import org.n52.sir.datastructure.detailed.SirDetailedSensorDescription;
import org.n52.sir.ds.ISearchSensorDAO;
import org.n52.sir.ows.OwsExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SOLRSearchSensorDAO implements ISearchSensorDAO {

    private static Logger log = LoggerFactory.getLogger(SOLRSearchSensorDAO.class);

    @Override
    public Collection<SirSearchResultElement> getAllSensors(boolean simpleReponse) throws OwsExceptionReport {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SirSearchResultElement getSensorBySensorID(String sensorIdInSir, boolean simpleReponse) throws OwsExceptionReport {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SirSearchResultElement getSensorByServiceDescription(SirServiceReference servDesc, boolean simpleReponse) throws OwsExceptionReport {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<SirSearchResultElement> searchSensor(SirSearchCriteria searchCriteria, boolean simpleReponse) throws OwsExceptionReport {
        Collection<String> q = searchCriteria.getSearchText();
        StringBuilder wordslist = new StringBuilder();
        Iterator<String> iter = q.iterator();
        wordslist.append(iter.next());
        while (iter.hasNext()) {
            wordslist.append("+");
            wordslist.append(iter.next());
        }
        
        return searchByAll(wordslist.toString(),
                           searchCriteria.getDtstart(),
                           searchCriteria.getDtend(),
                           searchCriteria.getLng(),
                           searchCriteria.getLat(),
                           searchCriteria.getRadius(),
                           searchCriteria.getBoundingBox());
    }

    /**
     * @param lat
     *        : Latitude of the desired point
     * @param lng
     *        : longitude of the desired point
     * @param kms
     *        : The distance of kms to match against
     * 
     */
    private Collection<SirSearchResultElement> spatialSearch(String lat, String lng, double kms, String column) {
        return spatialSearchWithQuery("*:*", lat, lng, kms, column);
    }

    private Collection<SirSearchResultElement> spatialSearchWithAllQuery(String query,
                                                                         String lat,
                                                                         String lng,
                                                                         double kms,
                                                                         String column) {
        SolrConnection connection = new SolrConnection();
        ModifiableSolrParams params = new ModifiableSolrParams();
        params.set("q", query);
        params.set("fq", "{!geofilt sfield=" + column + "}");
        params.set("pt", lat + "," + lng);
        params.set("d", kms + "");
        params.set("defType", "dismax");
        params.set("qf", SolrConstants.EDISMAX);

        try {
            QueryResponse response = connection.SolrQuery(params);
            SolrDocumentList list = response.getResults();
            return encodeResult(list);
        }
        catch (Exception e) {
            log.error(e.getLocalizedMessage());
            return null;
        }
    }

    private Collection<SirSearchResultElement> spatialSearchWithQuery(String query,
                                                                      String lat,
                                                                      String lng,
                                                                      double kms,
                                                                      String column) {
        SolrConnection connection = new SolrConnection();
        ModifiableSolrParams params = new ModifiableSolrParams();
        params.set("q", query);
        params.set("fq", "{!geofilt sfield=" + column + "}");
        params.set("pt", lat + "," + lng);
        params.set("d", kms + "");
        try {
            QueryResponse response = connection.SolrQuery(params);
            SolrDocumentList list = response.getResults();
            return encodeResult(list);
        }
        catch (Exception e) {
            log.error(e.getLocalizedMessage());
            return null;
        }

    }

    public Collection<SirSearchResultElement> searchSensorByBoundingBox(SirBoundingBox bbox) {
        double[] center = bbox.getCenter();
        return spatialSearch(center[0] + "", center[1] + "", 10, SolrConstants.BBOX_CENTER);
    }

    public Collection<SirSearchResultElement> searchSensorByLocation(String lat, String lng, double radius) {
        return spatialSearch(lat, lng, radius, SolrConstants.LOCATION);
    }

    public Collection<SirSearchResultElement> searchSensorByGeoBox(float west, float south, float east, float north) {
        // params : minX,minY,maxX,maxY
        return searchSensorByGeoBoxWithQuery("*:*", west, south, east, north);
    }

    public Collection<SirSearchResultElement> searchSensorByGeoBoxWithQuery(String query,
                                                                            float west,
                                                                            float south,
                                                                            float east,
                                                                            float north) {
        SirBoundingBox geoBox = new SirBoundingBox(east, south, west, north);
        double[] center = geoBox.getCenter();
        // getting radius
        double centerX = center[0];
        double centerY = center[1];
        double dist = Math.sqrt(Math.pow(east - centerX, 2) + Math.pow(north - centerY, 2));
        Collection<SirSearchResultElement> results = new ArrayList<>();
        Collection<SirSearchResultElement> allResults = spatialSearchWithQuery(query,
                                                                               centerX + "",
                                                                               centerY + "",
                                                                               dist,
                                                                               SolrConstants.BBOX_CENTER);
        if (allResults.size() == 0)
            return allResults;
        Iterator<SirSearchResultElement> iterator = allResults.iterator();
        while (iterator.hasNext()) {
            SirSearchResultElement result = iterator.next();
            SirDetailedSensorDescription desc = (SirDetailedSensorDescription) result.getSensorDescription();
            double x = desc.getbbox_x();
            double y = desc.getbbox_y();
            if (x >= west && x <= east && y >= south && y <= north)
                results.add(result);
        }
        return results;

    }

    private String temporalQuery(Date start, Date end) {
        String startDate = SolrUtils.getISO8601UTCString(start);
        String endDate = SolrUtils.getISO8601UTCString(end);
        StringBuilder query = new StringBuilder();
        query.append(SolrConstants.START_DATE);
        query.append(":[");
        query.append(startDate);
        query.append(" TO *]");
        query.append(" AND ");
        query.append(SolrConstants.END_DATE);
        query.append(":[* TO ");
        query.append(endDate);
        query.append("]");
        return query.toString();
    }

    public Collection<SirSearchResultElement> searchByValidTimeRange(Date start, Date end) {
        SolrConnection connection = new SolrConnection();
        ModifiableSolrParams params = new ModifiableSolrParams();
        params.set("q", temporalQuery(start, end));
        QueryResponse response;
        try {
            response = connection.SolrQuery(params);
            SolrDocumentList list = response.getResults();
            return encodeResult(list);
        }
        catch (SolrServerException e) {
            log.error("Cannot search by query", e);
            return null;
        }

    }

    private List<SirSearchResultElement> encodeResult(SolrDocumentList doc) {
        List<SirSearchResultElement> results = new ArrayList<>();
        for (int i = 0; i < doc.size(); i++) {
            SirSearchResultElement element = new SirSearchResultElement();
            SirDetailedSensorDescription solrDescription = new SirDetailedSensorDescription();
            SolrDocument solrresult = doc.get(i);

            if (solrresult.getFieldValue(SolrConstants.ID) != null)
                solrDescription.setId(solrresult.get(SolrConstants.ID).toString());

            Collection<Object> keywords = solrresult.getFieldValues(SolrConstants.KEYWORD);
            solrDescription.setKeywords(getCollectionStrings(keywords));

            if (solrresult.get(SolrConstants.START_DATE) != null)
                solrDescription.setBegineDate((Date) (solrresult.get(SolrConstants.START_DATE)));
            if (solrresult.get(SolrConstants.END_DATE) != null)
                solrDescription.setEndDate((Date) (solrresult.get(SolrConstants.END_DATE)));
            if (solrresult.getFieldValue(SolrConstants.DESCRIPTION) != null)
                solrDescription.setDescription(solrresult.get(SolrConstants.DESCRIPTION).toString());
            if (solrresult.getFieldValues(SolrConstants.CLASSIFIER) != null)
                solrDescription.setClassifiers(getCollectionStrings(solrresult.getFieldValues(SolrConstants.CLASSIFIER)));
            if (solrresult.getFieldValues(SolrConstants.IDENTIFICATION) != null)
                solrDescription.setIdentifiers(getCollectionStrings(solrresult.getFieldValues(SolrConstants.IDENTIFICATION)));
            if (solrresult.getFieldValues(SolrConstants.CONTACTS) != null) {
                Iterator<Object> it = solrresult.getFieldValues(SolrConstants.CONTACTS).iterator();
                Collection<String> results_contacts = new ArrayList<>();
                while (it.hasNext())
                    results_contacts.add(it.next().toString());
                solrDescription.setContacts(results_contacts);
            }
            if (solrresult.getFieldValues(SolrConstants.INPUT) != null) {
                Iterator<Object> it = solrresult.getFieldValues(SolrConstants.INPUT).iterator();
                Collection<String> inputs = new ArrayList<>();
                while (it.hasNext())
                    inputs.add(it.next().toString());
                solrDescription.setInputs(inputs);

            }
            if (solrresult.getFieldValues(SolrConstants.OUTPUT) != null) {
                Iterator<Object> it = solrresult.getFieldValues(SolrConstants.OUTPUT).iterator();
                Collection<String> outputs = new ArrayList<>();
                while (it.hasNext())
                    outputs.add(it.next().toString());
                solrDescription.setOutputs(outputs);

            }
            if (solrresult.getFieldValue(SolrConstants.BBOX_CENTER) != null) {
                String s = solrresult.getFieldValue(SolrConstants.BBOX_CENTER).toString();
                String d[] = s.split(",");
                solrDescription.setbbox_x(Double.parseDouble(d[0]));
                solrDescription.setbbox_y(Double.parseDouble(d[1]));
            }
            if (solrresult.getFieldValue(SolrConstants.LOCATION) != null) {
                solrDescription.setLocation(solrresult.getFieldValue(SolrConstants.LOCATION).toString());
            }

            element.setSensorDescription(solrDescription);
            results.add(element);
        }
        return results;
    }

    private Collection<String> getCollectionStrings(Collection<Object> objects) {
        Collection<String> strings = new ArrayList<>();
        for (Object o : objects) {
            strings.add(o.toString());
        }
        return strings;
    }

    public Collection<SirSearchResultElement> searchByDescription(String description) {
        SolrConnection connection = new SolrConnection();
        ModifiableSolrParams params = new ModifiableSolrParams();
        StringBuilder builder = new StringBuilder();
        builder.append(SolrConstants.DESCRIPTION);
        builder.append(":");
        builder.append('"');
        builder.append(description);
        builder.append('"');
        params.set("q", builder.toString());
        try {
            QueryResponse response = connection.SolrQuery(params);
            SolrDocumentList list = response.getResults();
            return encodeResult(list);
        }
        catch (Exception e) {
            log.error("Solr exception", e);
            return null;
        }

    }

    public Collection<SirSearchResultElement> searchByClassifer(String classifer) {
        SolrConnection connection = new SolrConnection();
        ModifiableSolrParams params = new ModifiableSolrParams();
        StringBuilder builder = new StringBuilder();
        builder.append(SolrConstants.CLASSIFIER);
        builder.append(":");
        builder.append('"');
        builder.append(classifer);
        builder.append('"');
        params.set("q", builder.toString());
        try {
            QueryResponse response = connection.SolrQuery(params);
            SolrDocumentList list = response.getResults();
            return encodeResult(list);
        }
        catch (Exception e) {
            log.error("Solr exception", e);
            return null;
        }

    }

    public Collection<SirSearchResultElement> searchByContact(String contact) {
        SolrConnection connection = new SolrConnection();
        ModifiableSolrParams params = new ModifiableSolrParams();
        StringBuilder builder = new StringBuilder();
        builder.append(SolrConstants.CONTACTS);
        builder.append(":");
        builder.append('"');
        builder.append(contact);
        builder.append('"');
        params.set("q", builder.toString());
        try {
            QueryResponse response = connection.SolrQuery(params);
            SolrDocumentList list = response.getResults();
            return encodeResult(list);
        }
        catch (Exception e) {
            log.error("Solr exception", e);
            return null;
        }

    }

    public Collection<SirSearchResultElement> searchByIdentification(String identifier) {
        SolrConnection connection = new SolrConnection();
        ModifiableSolrParams params = new ModifiableSolrParams();
        StringBuilder builder = new StringBuilder();
        builder.append(SolrConstants.IDENTIFICATION);
        builder.append(":");
        builder.append('"');
        builder.append(identifier);
        builder.append('"');
        params.set("q", builder.toString());
        try {
            QueryResponse response = connection.SolrQuery(params);
            SolrDocumentList list = response.getResults();
            return encodeResult(list);
        }
        catch (Exception e) {
            log.error("Solr exception", e);
            return null;
        }
    }

    public Collection<SirSearchResultElement> searchByInput(String input) {
        SolrConnection connection = new SolrConnection();
        ModifiableSolrParams params = new ModifiableSolrParams();
        StringBuilder builder = new StringBuilder();
        builder.append(SolrConstants.INPUT);
        builder.append(":");
        builder.append('"');
        builder.append(input);
        builder.append('"');
        params.set("q", builder.toString());
        try {
            QueryResponse response = connection.SolrQuery(params);
            SolrDocumentList list = response.getResults();
            return encodeResult(list);
        }
        catch (Exception e) {
            log.error("Solr exception", e);
            return null;
        }
    }

    public Collection<SirSearchResultElement> searchByOutput(String output) {
        SolrConnection connection = new SolrConnection();
        ModifiableSolrParams params = new ModifiableSolrParams();
        StringBuilder builder = new StringBuilder();
        builder.append(SolrConstants.OUTPUT);
        builder.append(":");
        builder.append('"');
        builder.append(output);
        builder.append('"');
        params.set("q", builder.toString());
        try {
            QueryResponse response = connection.SolrQuery(params);
            SolrDocumentList list = response.getResults();
            return encodeResult(list);
        }
        catch (Exception e) {
            log.error("Solr exception", e);
            return null;
        }
    }

    public Collection<SirSearchResultElement> searchByID(String ID) {
        SolrConnection connection = new SolrConnection();
        ModifiableSolrParams params = new ModifiableSolrParams();
        StringBuilder builder = new StringBuilder();
        builder.append(SolrConstants.ID);
        builder.append(":");
        builder.append('"');
        builder.append(ID);
        builder.append('"');
        params.set("q", builder.toString());
        try {
            QueryResponse response = connection.SolrQuery(params);
            SolrDocumentList list = response.getResults();
            return encodeResult(list);
        }
        catch (Exception e) {
            log.error("Solr exception", e);
            return null;
        }
    }

    public Collection<SirSearchResultElement> searchByAll(String query,
                                                          String dstart,
                                                          String dtend,
                                                          String lng,
                                                          String lat,
                                                          String radius,
                                                          SirBoundingBox bbox) {
        SolrConnection connection = new SolrConnection();
        ModifiableSolrParams params = new ModifiableSolrParams();
        
        StringBuilder builder = new StringBuilder();
        String[] qs = query.split("[+]");
        StringBuilder qualified = new StringBuilder();
        qualified.append('"');
        qualified.append(qs[0]);
        qualified.append('"');

        for (int i = 1; i < qs.length; i++) {
            qualified.append("+");
            qualified.append('"');
            qualified.append(qs[i]);
            qualified.append('"');

        }
        builder.append(qualified.toString());
        params.set("q", builder.toString());
        params.set("defType", "dismax");
        params.set("qf", SolrConstants.EDISMAX);
        StringBuilder temporalFilter = new StringBuilder();
        StringBuilder locationFilter = new StringBuilder();
        if (dstart != null && dtend != null) {
            temporalFilter.append(SolrConstants.START_DATE);
            temporalFilter.append(":[");
            temporalFilter.append(dstart);
            temporalFilter.append(" TO * ] AND ");
            temporalFilter.append(SolrConstants.END_DATE);
            temporalFilter.append(":[ * TO ");
            temporalFilter.append(dtend);
            temporalFilter.append("]");
        }
        if (lat != null && lng != null && radius != null) {
            locationFilter.append("{!geofilt sfield=");
            locationFilter.append(SolrConstants.LOCATION);
            locationFilter.append("}");
            // set spatialParams
            params.set("pt", lat + "," + lng);
            params.set("d", radius + "");
        }

        if (locationFilter.toString().length() != 0) {
            if (temporalFilter.toString().length() != 0)
                params.set("fq", locationFilter.toString() + " AND " + temporalFilter.toString());
            else
                params.set("fq", locationFilter.toString());
        }
        else {
            if (temporalFilter.toString().length() != 0)
                params.set("fq", temporalFilter.toString());
        }
        Collection<SirSearchResultElement> result = new ArrayList<>();
        
        try {
            QueryResponse response = connection.SolrQuery(params);
            SolrDocumentList list = response.getResults();
            result.addAll(encodeResult(list));
            
            if (bbox == null)
                return result;
            double[] center = bbox.getCenter();
            double centerX = center[0];
            double centerY = center[1];
            double east = bbox.getEast();
            double west = bbox.getWest();
            double south = bbox.getSouth();
            double north = bbox.getNorth();
            double dist = Math.sqrt(Math.pow(east - centerX, 2) + Math.pow(north - centerY, 2));

            Collection<SirSearchResultElement> bbox_result = spatialSearchWithAllQuery(query, centerX + "", centerY
                    + "", dist, SolrConstants.BBOX_CENTER);
            result.addAll(bbox_result);
            Collection<SirSearchResultElement> filtered_Result = new ArrayList<>();
            Iterator<SirSearchResultElement> iterator = bbox_result.iterator();
            log.info(bbox_result + " :results found");
            if (bbox_result.size() == 0)
                return bbox_result;
            while (iterator.hasNext()) {
                SirSearchResultElement resultelement = iterator.next();
                SirDetailedSensorDescription desc = (SirDetailedSensorDescription) resultelement.getSensorDescription();
                double x = desc.getbbox_x();
                double y = desc.getbbox_y();
                if (x >= west && x <= east && y >= south && y <= north)
                    filtered_Result.add(resultelement);
            }
            return filtered_Result;

        }
        catch (Exception e) {
            log.error("Solr exception", e);
            return result;
        }
    }

    private void appendParameter(StringBuilder builder, String k, Map<String, String> queryMap) {
        if (k.equals("lng") || k.equals("lat") || k.equals("radius"))
            return;
        builder.append(k);
        builder.append(':');
        if (k.equals(SolrConstants.START_DATE)) {
            builder.append('[');
            builder.append(queryMap.get(k));
            builder.append(" TO * ]");
        }
        else if (k.equals(SolrConstants.END_DATE)) {
            builder.append("[ * TO ");
            builder.append(queryMap.get(k));
            builder.append(" ]");
        }
        else {
            builder.append('"');
            builder.append(queryMap.get(k));
            builder.append('"');
        }

    }

    public Collection<SirSearchResultElement> searchByQuery(Map<String, String> queryMap, String delimiter) {
        Collection<String> keys = queryMap.keySet();
        if (queryMap.size() == 0)
            return null;
        SolrConnection connection = new SolrConnection();
        ModifiableSolrParams params = new ModifiableSolrParams();
        StringBuilder builder = new StringBuilder();
        Iterator<String> iterator = keys.iterator();
        String k = iterator.next();
        appendParameter(builder, k, queryMap);
        while (iterator.hasNext()) {
            k = iterator.next();
            if (k.equals("lng") || k.equals("lat") || k.equals("radius"))
                continue;
            builder.append(' ');
            builder.append(delimiter);
            builder.append(' ');
            appendParameter(builder, k, queryMap);
        }
        if (keys.contains("radius")) {
            // Search by radius
            String lat = (queryMap.get("lat"));
            String lng = (queryMap.get("lng"));
            double radius = Double.parseDouble(queryMap.get("radius"));
            return spatialSearchWithQuery(builder.toString(), lat, lng, radius, SolrConstants.LOCATION);
        }
        else if (keys.contains("b")) {
            // search by bounding box
            /**
             * TODO Implement GeoBox searchByQuery
             * 
             */
        }
        else {
            params.set("q", builder.toString());
            log.info(params.toString());
            try {
                QueryResponse response = connection.SolrQuery(params);
                SolrDocumentList list = response.getResults();
                return encodeResult(list);
            }
            catch (Exception e) {
                log.error("Solr exception", e);
                return null;
            }
        }
        return null;
    }

}
