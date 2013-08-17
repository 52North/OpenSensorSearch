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

package org.n52.sir.IT.solr;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.xmlbeans.XmlException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.n52.sir.datastructure.SirSearchCriteria;
import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.datastructure.SirSensor;
import org.n52.sir.datastructure.detailed.SirDetailedSensorDescription;
import org.n52.sir.ds.solr.SOLRInsertSensorInfoDAO;
import org.n52.sir.ds.solr.SOLRSearchSensorDAO;
import org.n52.sir.ds.solr.SolrConnection;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.sml.SensorMLDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchByKeywordTest {

    private static Logger log = LoggerFactory.getLogger(SearchByKeywordTest.class);

    public String id;

    private SirSensor sensor;

    @Before
    public void insertTestSensor() throws OwsExceptionReport, XmlException, IOException {
        // Inserts the sensor
        String basePath = (this.getClass().getResource("/Requests").getFile());
        File sensor_file = new File(basePath + "/testSensor.xml");
        SensorMLDocument doc = SensorMLDocument.Factory.parse(sensor_file);
        this.sensor = SensorMLDecoder.decode(doc);
        log.trace(this.sensor.getText().toArray()[0].toString());

        // FIXME Moh-Yakoub: probably this will take some configuration - haven't decided yet.
        SOLRInsertSensorInfoDAO dao = new SOLRInsertSensorInfoDAO();
        this.id = dao.insertSensor(this.sensor);
    }

    @Test
    public void searchKeywords() throws OwsExceptionReport {
        SOLRSearchSensorDAO searchDAO = new SOLRSearchSensorDAO();
        SirSearchCriteria criteria = new SirSearchCriteria();

        ArrayList<String> searchkeywords = new ArrayList<>();
        for (String keyword : this.sensor.getKeywords()) {
            searchkeywords.add(keyword);
        }
        criteria.setSearchText(searchkeywords);

        Collection<SirSearchResultElement> results = searchDAO.searchSensor(criteria, true);

        assertNotNull(results);
        assertEquals(results.size(), 1);

        Iterator<SirSearchResultElement> iter = results.iterator();
        SirSearchResultElement result = iter.next();

        // SensorML is stored in the sensor description value
        SirDetailedSensorDescription description = (SirDetailedSensorDescription) result.getSensorDescription();
        assertNotNull(description);

        Collection<String> actual = description.getKeywords();
        Collection<String> expected = this.sensor.getKeywords();

        assertEquals(actual.size(), expected.size());

        assertThat("keywords used for search are given in the returned sensor",
                   actual,
                   containsInAnyOrder(expected.toArray()));
    }

    @After
    public void deleteSensor() throws SolrServerException, IOException {
        new SolrConnection().deleteByQuery(this.id);
    }
}