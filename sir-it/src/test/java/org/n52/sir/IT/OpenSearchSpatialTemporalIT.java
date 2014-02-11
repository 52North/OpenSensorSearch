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

package org.n52.sir.IT;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import net.opengis.sensorML.x101.KeywordsDocument.Keywords.KeywordList;
import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.oss.sir.Client;
import org.n52.oss.sir.api.SirSensor;
import org.n52.oss.sir.api.TimePeriod;
import org.n52.oss.sir.api.TimePeriod.IndeterminateTime;
import org.n52.oss.util.GuiceUtil;
import org.n52.sir.data.JSONSensor;
import org.n52.sir.data.JSONSensorsCollection;
import org.n52.sir.json.SearchResult;
import org.n52.sir.json.SearchResultElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sir.x032.InsertSensorInfoRequestDocument;
import org.x52North.sir.x032.InsertSensorInfoResponseDocument;

import com.google.gson.Gson;

/**
 * @author Yakoub
 */
public class OpenSearchSpatialTemporalIT {
    private static Logger log = LoggerFactory.getLogger(OpenSearchSpatialTemporalIT.class);

    private static String lon_lat_radius_query = "http://localhost:8080/OpenSensorSearch/search?q=pre&";

    public static final double R = 6372.8; // In kilometers

    public static final double TEST_LAT = 1.5;

    public static final double TEST_LNG = 3.49;

    public static final double TEST_RADIUS = 2;

    private static String temporal_search_query = "http://localhost:8080/OpenSensorSearch/search?q=pre&";

    public static final long TEST_START_DATE = 1379443673354l;

    public static final long TEST_END_DATE = 1379443673355l;

    public static Client client;

    @BeforeClass
    public void setUpClient() {
        client = GuiceUtil.configureSirClient();
    }

    @Before
    public void parseJsonSensorsAndInsert() throws IOException,

    XmlException {

        File sensor_file = new File(ClassLoader.getSystemResource("data/randomSensors.json").getFile());
        File sensor_temp = new File(ClassLoader.getSystemResource("AirBase-test.xml").getFile());
        SensorMLDocument DOC = SensorMLDocument.Factory.parse(sensor_temp);
        Gson gson = new Gson();
        StringBuilder builder = new StringBuilder();
        String s;
        try (BufferedReader reader = new BufferedReader( (new FileReader(sensor_file)));) {
            while ( (s = reader.readLine()) != null)
                builder.append(s);
            JSONSensorsCollection collection = gson.fromJson(builder.toString(), JSONSensorsCollection.class);
            Iterator<JSONSensor> sensors = collection.sensors.iterator();
            while (sensors.hasNext()) {
                SirSensor sensor = new SirSensor();
                JSONSensor jsensor = sensors.next();
                sensor.setKeywords(jsensor.keywords);
                TimePeriod period = new TimePeriod();
                DateTime begin = DateTime.parse(jsensor.beginPosition);
                period.setStartTime(new IndeterminateTime(begin.toDate()));
                DateTime end = DateTime.parse(jsensor.endPosition);
                period.setEndTime(new IndeterminateTime(end.toDate()));
                sensor.setTimePeriod(period);
                sensor.setIdentificationsList(jsensor.Identifiers);
                sensor.setLatitude(jsensor.lat);
                sensor.setLongitude(jsensor.lng);
                KeywordList klist = KeywordList.Factory.newInstance();
                klist.setKeywordArray(jsensor.keywords.toArray(new String[] {}));
                DOC.getSensorML().getMemberArray(0).getProcess().getKeywordsArray(0).setKeywordList(klist);
                InsertSensorInfoRequestDocument req = InsertSensorInfoRequestDocument.Factory.newInstance();
                req.addNewInsertSensorInfoRequest().addNewInfoToBeInserted().setSensorDescription(DOC.getSensorML().getMemberArray(0).getProcess());
                XmlObject res = client.xSendPostRequest(req);
                InsertSensorInfoResponseDocument resp = InsertSensorInfoResponseDocument.Factory.parse(res.getDomNode());
            }
        }
    }

    @Test
    public void testDistanceLessThanSearchRadius() throws ClientProtocolException, IOException {
        lon_lat_radius_query += "lat=" + TEST_LAT + "&lon=" + TEST_LNG + "&radius=" + TEST_RADIUS
                + "&httpAccept=application%2Fjson";
        org.apache.http.client.HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(lon_lat_radius_query);

        HttpResponse response = client.execute(get);
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String s;
        while ( (s = reader.readLine()) != null)
            builder.append(s);

        log.debug(builder.toString());
        ObjectMapper mapper = new ObjectMapper();
        SearchResult result = mapper.readValue(builder.toString(), SearchResult.class);

        Iterator<SearchResultElement> iter = result.getResults().iterator();
        while (iter.hasNext()) {
            SearchResultElement element = iter.next();
            double[] latLng = element.getSensorDescription().getBoundingBox().getCenter();
            assertThat(haversine(latLng[0], latLng[1], TEST_LAT, TEST_LNG), lessThan(TEST_RADIUS));
        }

    }

    // implement the haversine formula for distances

    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1)
                * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }

    @Test
    public void testTimeInValidRange() throws ClientProtocolException, IOException {
        temporal_search_query += "dtstart=" + TEST_START_DATE + "&dtend=" + TEST_END_DATE
                + "&httpAccept=application%2Fjson";
        org.apache.http.client.HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(temporal_search_query);

        HttpResponse response = client.execute(get);
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String s;
        while ( (s = reader.readLine()) != null)
            builder.append(s);

        log.debug(builder.toString());
        ObjectMapper mapper = new ObjectMapper();
        SearchResult result = mapper.readValue(builder.toString(), SearchResult.class);

        Iterator<SearchResultElement> iter = result.getResults().iterator();
        while (iter.hasNext()) {
            SearchResultElement element = iter.next();
            long start = element.getBeginDate().getTime();
            long end = element.getEndDate().getTime();
            assertThat(start, is(both(lessThanOrEqualTo(TEST_END_DATE)).and(greaterThanOrEqualTo(TEST_START_DATE))));
            assertThat(end, is(both(lessThanOrEqualTo(TEST_END_DATE)).and(greaterThanOrEqualTo(TEST_START_DATE))));
        }

    }

    public void testTimeInValidRangeAndLocationInValidRange() throws ClientProtocolException, IOException {
        String query = temporal_search_query + "dtstart=" + TEST_START_DATE + "&dtend=" + TEST_END_DATE;
        query += "&lat=" + TEST_LAT + "&lon=" + TEST_LNG + "&radius=" + TEST_RADIUS + "&httpAccept=application%2Fjson";
        org.apache.http.client.HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(temporal_search_query);

        HttpResponse response = client.execute(get);
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String s;
        while ( (s = reader.readLine()) != null)
            builder.append(s);

        log.debug(builder.toString());
        ObjectMapper mapper = new ObjectMapper();
        SearchResult result = mapper.readValue(builder.toString(), SearchResult.class);

        Iterator<SearchResultElement> iter = result.getResults().iterator();
        while (iter.hasNext()) {
            SearchResultElement element = iter.next();
            double[] latLng = element.getSensorDescription().getBoundingBox().getCenter();
            long start = element.getBeginDate().getTime();
            long end = element.getEndDate().getTime();
            assertThat(haversine(latLng[0], latLng[1], TEST_LAT, TEST_LNG), lessThan(TEST_RADIUS));
            assertThat(start, is(both(lessThanOrEqualTo(TEST_END_DATE)).and(greaterThanOrEqualTo(TEST_START_DATE))));
            assertThat(end, is(both(lessThanOrEqualTo(TEST_END_DATE)).and(greaterThanOrEqualTo(TEST_START_DATE))));
        }

    }
}