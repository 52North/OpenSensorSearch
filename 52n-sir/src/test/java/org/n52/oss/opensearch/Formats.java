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

package org.n52.oss.opensearch;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.json.JSONException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.n52.oss.config.ApplicationConstants;
import org.n52.oss.opensearch.listeners.JsonListener;
import org.n52.oss.opensearch.listeners.OpenSearchListener;
import org.n52.oss.util.GuiceUtil;
import org.n52.oss.util.IgnoreDateValueComparator;
import org.n52.oss.util.Util;
import org.n52.sir.datastructure.SirSearchCriteria;
import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.datastructure.SirSimpleSensorDescription;
import org.n52.sir.ds.ISearchSensorDAO;
import org.n52.sir.json.SearchResult;
import org.n52.sir.listener.SearchSensorListener;
import org.n52.sir.ows.OwsExceptionReport;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.inject.Inject;
import com.google.inject.Injector;

//@RunWith(MockitoJUnitRunner.class)
public class Formats {

    @Inject
    static ApplicationConstants appConstants;

    @Inject
    static OpenSearchConfigurator osConfig;

    private static SearchSensorListener ssl;

    private static Set<OpenSearchListener> listeners;

    @Mock
    private static ISearchSensorDAO mockupDao;

    private static String query = "myRandom test query";

    private static ArrayList<SirSearchResultElement> resultElements;

    private static SirSearchCriteria searchCriteria;

    private OpenSearch opensearch;

    @Mock
    private static UriInfo queryUriInfo;

    @Mock
    private static MultivaluedMap<String, String> queryMap;

    private static String sensorIdentifier = "my:mockup:id:42";

    @BeforeClass
    public static void setUp() throws OwsExceptionReport {
        osConfig = new OpenSearchConfigurator();

        mockupDao = mock(ISearchSensorDAO.class);
        searchCriteria = new SirSearchCriteria();
        searchCriteria.setSearchText(Arrays.asList(new String[] {query}));

        resultElements = new ArrayList<>();
        SirSearchResultElement elem = new SirSearchResultElement();
        elem.setLastUpdate(new Date(0l));
        elem.setSensorId(sensorIdentifier);
        elem.setSensorDescription(new SirSimpleSensorDescription(null, "description", "url"));
        resultElements.add(elem);

        when(mockupDao.searchSensor(any(SirSearchCriteria.class), eq(true))).thenReturn(resultElements);

        queryUriInfo = mock(UriInfo.class);
        queryMap = mock(MultivaluedMap.class);
        when(queryMap.getFirst("q")).thenReturn(query);
        when(queryUriInfo.getQueryParameters()).thenReturn(queryMap);

        ssl = new SearchSensorListener(mockupDao, mockupDao);
        Injector i = GuiceUtil.configurePropertiesFiles();
        i.injectMembers(ssl);

        listeners = new HashSet<>();
        listeners.add(new JsonListener(osConfig));
    }

    @Before
    public void createListener() {
        this.opensearch = new OpenSearch(appConstants, ssl, osConfig, listeners);
    }

    @Test
    public void jsonResponseIsCorrect() throws JsonGenerationException,
            JsonMappingException,
            IOException,
            JSONException {
        Response json = this.opensearch.json(MediaType.APPLICATION_JSON, queryUriInfo);

        // compare to file
        String actual = Util.entityToString(json);
        String expected = Util.readResourceFile("/responses/json/searchResult.json");
        JSONAssert.assertEquals(expected, actual, new IgnoreDateValueComparator(JSONCompareMode.LENIENT));

        // assert some more
        SearchResult actualResult = (SearchResult) json.getEntity();
        assertThat("query is correct", actualResult.getQuery(), is(equalTo(query)));
        // attention: cannot just assert list content here because one is json.SearchResultElement and the
        // other is SirSearchResultElement (an internal class)
        assertThat("result size is correct", actualResult.getResults().size(), is(equalTo(1)));
        assertThat("result item has correct id",
                   actualResult.getResults().iterator().next().getSensorId(),
                   is(equalTo(sensorIdentifier)));
        assertThat("source is full URL path", actualResult.getSource(), is(equalTo(osConfig.getWebsiteHome())));
    }

    // TODO test for KML

    // TODO test for XML

    // TODO test for RSS

    // TODO test for Atom

}
