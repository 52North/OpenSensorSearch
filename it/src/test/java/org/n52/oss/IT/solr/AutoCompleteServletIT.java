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

package org.n52.oss.IT.solr;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.xmlbeans.XmlException;
import org.junit.Test;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.util.Util;
import org.n52.sir.ds.solr.SOLRInsertSensorInfoDAO;
import org.n52.sir.ds.solr.SolrConnection;
import org.n52.sir.sml.SensorMLDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * TODO inserted test sensor is not deleted afterwards.
 * 
 * @author Yakoub
 */
public class AutoCompleteServletIT {
    private static Logger log = LoggerFactory.getLogger(AutoCompleteServletIT.class);
    private String insertedSensorId;
    private String enteredText = "str";

    // @Before
    public void insertSensor() throws OwsExceptionReport, XmlException, IOException {
        File sensor_status = new File(ClassLoader.getSystemResource("Requests/testsensor.xml").getFile());
        SensorMLDocument doc = SensorMLDocument.Factory.parse(sensor_status);

        // FIXME use mocked up up database backend
        SOLRInsertSensorInfoDAO dao = new SOLRInsertSensorInfoDAO(new SolrConnection("http://localhost:8983/solr", 1000));
        this.insertedSensorId = dao.insertSensor(SensorMLDecoder.decode(doc));
        log.debug("inserted test sensor: {}", this.insertedSensorId);
    }

    @Test
    public void testServlet() throws ClientProtocolException, IOException {
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet("http://localhost:8080/OpenSensorSearch/suggest?q=te");

        HttpResponse response = client.execute(get);
        String actual = Util.getResponsePayload(response);

        String expected = "{ \"suggestions\": [\"structual\", \"stringtheory\", \"a really strange keyword to use in a sensor description\"] }";
        assertThat("reponse string is correct", actual, is(equalTo(expected)));
    }

    // @After
    // public void deleteTestSensor() throws SolrServerException, IOException{
    // new SolrConnection().deleteByQuery("");
    // }
}
