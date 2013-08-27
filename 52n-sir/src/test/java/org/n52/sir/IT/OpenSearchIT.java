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

package org.n52.sir.IT;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Iterator;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.sir.client.Client;
import org.n52.sir.json.MapperFactory;
import org.n52.sir.json.SearchResult;
import org.n52.sir.json.SearchResultElement;
import org.n52.sir.ows.OwsExceptionReport;
import org.x52North.sir.x032.InsertSensorInfoRequestDocument;
import org.x52North.sir.x032.InsertSensorInfoResponseDocument;
import org.xml.sax.SAXException;

import uk.co.datumedge.hamcrest.json.SameJSONAs;

import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * TODO Add all OpenSourceOutputFormats IT to a single OpenSourceIT file
 */

public class OpenSearchIT {

    private static Client client;

    @BeforeClass
    public static void setUp() {
        client = GuiceUtil.configureSirClient();
    }

    private void insertSensor(String path) throws XmlException, IOException, OwsExceptionReport, HttpException {
        File sensor = new File(ClassLoader.getSystemResource(path).getFile());
        SensorMLDocument DOC = SensorMLDocument.Factory.parse(sensor);

        InsertSensorInfoRequestDocument req = InsertSensorInfoRequestDocument.Factory.newInstance();
        req.addNewInsertSensorInfoRequest().addNewInfoToBeInserted().setSensorDescription(DOC.getSensorML().getMemberArray(0).getProcess());
        XmlObject res = client.xSendPostRequest(req);

        InsertSensorInfoResponseDocument resp = InsertSensorInfoResponseDocument.Factory.parse(res.getDomNode());

        assertThat("Failed to insert sensor",
                   resp.getInsertSensorInfoResponse().getNumberOfInsertedSensors(),
                   is(not(0)));
    }

    @BeforeClass
    public void insertSensors() throws XmlException, IOException, OwsExceptionReport, HttpException {

        insertSensor("Requests/testsensor.xml");
        // insertSensor("Requests/Sensors/testSensor02.xml");
    }

    public String buildQuery(String q, String format) {
        /*
         * I'm sure that the server will be localhost port : 8080 If it's not installed on this the mvn verify
         * will through a BindingException : Address already in use.
         */
        StringBuilder query = new StringBuilder();
        query.append("http://localhost:8080/SIR");
        // FIXME Daniel: get to run integration tests
        query.append("/search?q=");
        query.append(q);
        query.append("&httpAccept=");
        query.append(format);
        return query.toString();
    }

    private String sendRequest(String query) throws ClientProtocolException, IOException {
        HttpClient c = new DefaultHttpClient();
        /*
         * I test using the unique ID of testSenosr01
         */
        HttpGet get = new HttpGet(query);

        HttpResponse response = c.execute(get);
        StringBuilder builder = new StringBuilder();

        String responseString = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));) {
            String s = "";
            while ( (s = reader.readLine()) != null)
                builder.append(s);

            responseString = builder.toString();
            reader.close();
        }

        return responseString;
    }

    private String readResource(String name) throws IOException {
        File results = new File(ClassLoader.getSystemResource(name).getFile());
        StringBuilder builder = new StringBuilder();

        String realResults = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(results));) {
            String string = "";
            while ( (string = reader.readLine()) != null)
                builder.append(string);
            realResults = builder.toString();
            reader.close();
        }

        return realResults;
    }

    @Test
    public void testRSSResponseFromOpenSearch() throws IOException, SAXException {
        String realResult = readResource("Requests/Sensors/testSensor01Result.rss");
        String responseResult = sendRequest(buildQuery("urn:ogc:object:feature:Sensor:EEA:airbase:4.0:DEBB059",
                                                       "application/rss"));

        assertXMLEqual(realResult, responseResult);
    }

    @Test
    public void testXMLResponseFromOpenSearch() throws IOException, SAXException {
        String realResult = readResource("Requests/Sensors/testSensor01Result.XML");
        String responseResult = sendRequest(buildQuery("urn:ogc:object:feature:Sensor:EEA:airbase:4.0:DEBB059",
                                                       "application/xml"));

        assertXMLEqual(realResult, responseResult);
    }

    @Test
    public void testJSONResponseFromOpenSearch() throws IOException {
        String realResult = readResource("Requests/Sensors/jsonSensor.json");

        ObjectMapper mapper = MapperFactory.getMapper();

        SearchResult realResultObj = mapper.readValue(realResult, SearchResult.class);

        // Get the sensor
        Collection<SearchResultElement> sensors = realResultObj.getResults();

        SearchResultElement sensorJson = sensors.iterator().next();

        String sensorJsonStr = mapper.writeValueAsString(sensorJson);

        String responseResult = sendRequest(buildQuery("test", "application/json"));

        SearchResult result = mapper.readValue(responseResult, SearchResult.class);
        Collection<SearchResultElement> results = result.getResults();
        assertTrue(results.size() > 0);

        Iterator<SearchResultElement> it = results.iterator();

        while (it.hasNext()) {
            SearchResultElement elem = it.next();
            String resultsensor = mapper.writeValueAsString(elem);
            if (elem.getSensorIdInSir() == sensorJson.getSensorIdInSir())
                assertThat(resultsensor,
                           SameJSONAs.sameJSONAs(sensorJsonStr).allowingExtraUnexpectedFields().allowingAnyArrayOrdering());

        }

    }

    @Test
    public void testKMLResponseFromOpenSearch() throws IOException, SAXException {
        String realResult = readResource("Requests/Sensors/testSensor01Result.kml");
        String responseResult = sendRequest(buildQuery("urn:ogc:object:feature:Sensor:EEA:airbase:4.0:DEBB059",
                                                       "application/kml"));
        assertXMLEqual(realResult, responseResult);
    }

}