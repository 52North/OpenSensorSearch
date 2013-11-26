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

package org.n52.oss.IT.solr;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.xmlbeans.XmlException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.oss.sir.api.InternalSensorID;
import org.n52.oss.sir.api.SirDetailedSensorDescription;
import org.n52.oss.sir.api.SirSearchResultElement;
import org.n52.oss.sir.api.SirSensor;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.ds.solr.SOLRInsertSensorInfoDAO;
import org.n52.sir.ds.solr.SOLRSearchSensorDAO;
import org.n52.sir.ds.solr.SolrConnection;
import org.n52.sir.sml.SensorMLDecoder;

public class SearchByIdentifierTest {

    private String id = UUID.randomUUID().toString();
    private SOLRInsertSensorInfoDAO dao;

    private static SolrConnection connection;

    @BeforeClass
    public static void prepare() {
        connection = new SolrConnection("http://localhost:8983/solr", 2000);
    }

    @Before
    public void insertSensor() throws XmlException, IOException, OwsExceptionReport {
        String basePath = (this.getClass().getResource("/Requests").getFile());
        File sensor_file = new File(basePath + "/testSensor.xml");
        SensorMLDocument doc = SensorMLDocument.Factory.parse(sensor_file);
        SirSensor sensor = SensorMLDecoder.decode(doc);
        sensor.setInternalSensorId(this.id);
        this.dao = new SOLRInsertSensorInfoDAO(connection);
        this.dao.insertSensor(sensor);
    }

    @Test
    public void searchByIdentification() {
        SOLRSearchSensorDAO searchDAO = new SOLRSearchSensorDAO(connection);
        Collection<SirSearchResultElement> results = searchDAO.searchByIdentification(this.id);

        assertNotNull(results);

        Iterator<SirSearchResultElement> iter = results.iterator();
        ArrayList<Object> resultsIdentifiers = new ArrayList<>();
        while (iter.hasNext()) {
            SirSearchResultElement element = iter.next();
            resultsIdentifiers.addAll( ((SirDetailedSensorDescription) element.getSensorDescription()).getIdentifiers());
        }
        if (resultsIdentifiers.size() > 0)
            assertFalse(resultsIdentifiers.indexOf(this.id) == -1);
    }

    @After
    public void deleteSensor() throws OwsExceptionReport {
        String deleted = this.dao.deleteSensor(new InternalSensorID(this.id));
        assertThat(deleted, is(equalTo(this.id)));
    }
}
