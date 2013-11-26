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

package org.n52.oss.rest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Date;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.json.JSONException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.oss.sir.api.SirDetailedSensorDescription;
import org.n52.oss.sir.api.SirSearchResultElement;
import org.n52.oss.sir.api.SirSimpleSensorDescription;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.util.IgnoreDateValueComparator;
import org.n52.oss.util.Util;
import org.n52.sir.ds.ISearchSensorDAO;
import org.n52.sir.json.SearchResultElement;
import org.n52.sir.json.SimpleSensorDescription;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class RestfulInterface {

    private SensorResource resource;
    private static ISearchSensorDAO searchDao;
    private static String testId = "1";
    private static String testDesription = "test description";
    private static String testUrl = "http://a.test.url";
    private static String classifier01 = "myClass01";
    private static String identifier01 = "myId01";
    private static String keyword02 = "testkeyword02";
    private static String keyword01 = "testkeyword01";
    private static String output01 = "myOutput";
    private static String contact01 = "some@contact";
    private static String input02 = "anInput02";
    private static String input01 = "input01";

    @BeforeClass
    public static void createMockups() throws OwsExceptionReport {
        searchDao = mock(ISearchSensorDAO.class);
        SirSearchResultElement elem = new SirSearchResultElement();
        elem.setLastUpdate(new Date(0l));
        elem.setSensorId(testId);
        elem.setSensorDescription(new SirSimpleSensorDescription(null, testDesription, testUrl));
        when(searchDao.getSensorBySensorID(eq(testId), eq(true))).thenReturn(elem);

        SirSearchResultElement elemDetailed = new SirSearchResultElement();
        elemDetailed.setLastUpdate(new Date(0l));
        elemDetailed.setSensorId(testId);
        SirDetailedSensorDescription descr = new SirDetailedSensorDescription();
        descr.setClassifiers(Arrays.asList(new String[] {classifier01}));
        descr.setIdentifiers(Arrays.asList(new String[] {identifier01}));
        descr.setKeywords(Arrays.asList(new String[] {keyword01, keyword02}));
        descr.setId(testId);
        descr.setDescription(testDesription);
        descr.setOutputs(Arrays.asList(new String[] {output01}));
        descr.setContacts(Arrays.asList(new String[] {contact01}));
        descr.setBegineDate(new Date(17));
        descr.setEndDate(new Date(42));
        descr.setInputs(Arrays.asList(new String[] {input01, input02}));
        elemDetailed.setSensorDescription(descr);
        when(searchDao.getSensorBySensorID(eq(testId), eq(false))).thenReturn(elemDetailed);
    }

    @Before
    public void prepare() throws URISyntaxException {
        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getBaseUri()).thenReturn(new URI("TEST_URL"));
        this.resource = new SensorResource(searchDao, uriInfo);
    }

    @Test
    public void sensorResourceIsCorrect() {
        Response response = this.resource.getSensor(testId, false);
        SearchResultElement actual = (SearchResultElement) response.getEntity();

        assertThat("id matches", actual.getSensorId(), is(equalTo(testId)));
        SimpleSensorDescription actualDescription = actual.getSensorDescription();
        assertThat("id matches", actualDescription.getText(), is(equalTo(testDesription)));
        assertThat("id matches", actualDescription.getUrl(), is(equalTo(testUrl)));
    }

    @Test
    public void sensorResourceMatchesTestfile() throws JsonGenerationException,
            JsonMappingException,
            IOException,
            JSONException {
        Response response = this.resource.getSensor(testId, false);
        String actual = Util.entityToString(response);
        String expected = Util.readResourceFile("/responses/json/sensor1.json");

        JSONAssert.assertEquals(expected, actual, new IgnoreDateValueComparator(JSONCompareMode.LENIENT));
    }

    @Test
    public void sensorResourceMatchesDetailedTestfile() throws JsonGenerationException,
            JsonMappingException,
            IOException,
            JSONException {
        Response response = this.resource.getSensor(testId, true);
        String actual = Util.entityToString(response);
        String expected = Util.readResourceFile("/responses/json/sensor1_detailed.json");

        JSONAssert.assertEquals(expected, actual, new IgnoreDateValueComparator(JSONCompareMode.LENIENT));
    }
}
