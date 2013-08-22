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

package org.n52.sir.IT.solr;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.util.Iterator;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.xmlbeans.XmlException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.n52.sir.ds.solr.SOLRInsertSensorInfoDAO;
import org.n52.sir.ds.solr.SolrConnection;
import org.n52.sir.json.SearchResult;
import org.n52.sir.json.SearchResultElement;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.sml.SensorMLDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Yakoub
 */
public class OpenSearchTemporalExtensionIT {
    private static Logger log = LoggerFactory.getLogger(OpenSearchTemporalExtensionIT.class);
    
    // TODO get the base URL using dependency injectin
    private static String query = "http://localhost:8080/OpenSensorSearch/search?q=test&dtstart=2009-12-31T22:00:00Z&dtend=2011-12-30T22:00:00Z&httpAccept=application%2Fjson";
    private Date start = new Date(1262296800000l);
    private Date end = new Date(1325282400000l);

    @Before
    public void insertSensor() throws OwsExceptionReport, XmlException, IOException {
        File sensor_status = new File(ClassLoader.getSystemResource("Requests/testsensor.xml").getFile());
        SensorMLDocument doc = SensorMLDocument.Factory.parse(sensor_status);

        SOLRInsertSensorInfoDAO dao = new SOLRInsertSensorInfoDAO();
        dao.insertSensor(SensorMLDecoder.decode(doc));
    }

    @Test
    public void testTemporal() throws ClientProtocolException, IOException {
        org.apache.http.client.HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(query);

        HttpResponse response = client.execute(get);
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String s;
        while ( (s = reader.readLine()) != null)
            builder.append(s);

        log.debug(builder.toString());
        System.out.println(builder.toString());
        ObjectMapper mapper = new ObjectMapper();
        SearchResult result = mapper.readValue(builder.toString(), SearchResult.class);

        Iterator<SearchResultElement> iter = result.getResults().iterator();
        while (iter.hasNext()) {
            SearchResultElement element = iter.next();
            if (element.getBeginDate() != null) {
                assertTrue(element.getBeginDate().getTime() >= this.start.getTime());
                assertTrue(element.getBeginDate().getTime() <= this.end.getTime());
            }
            if (element.getEndDate() != null) {
                assertTrue(element.getEndDate().getTime() >= this.start.getTime());
                assertTrue(element.getEndDate().getTime() <= this.end.getTime());
            }

        }

    }

    @After
    public void deleteTestSensor() throws SolrServerException, IOException {
        new SolrConnection().deleteByQuery("");
    }
}
