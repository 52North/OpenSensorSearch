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
import static org.hamcrest.Matchers.lessThanOrEqualTo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.n52.sir.json.SearchResult;
import org.n52.sir.json.SearchResultElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yakoub
 */
public class TemporalSearchIT {
    private static Logger log = LoggerFactory.getLogger(SpatialSearchIT.class);
    private static String temporal_search_query = "http://localhost:8080/OpenSensorSearch/search?q=pre&";
    public static final double R = 6372.8; // In kilometers
    public static final long TEST_START_DATE = 1379443673354l;
    public static final long TEST_END_DATE = 1379443673355l;
  
    @Test
    public void testDistanceLessThanSearchRadius() throws ClientProtocolException, IOException {
        temporal_search_query += "dtstart="+TEST_START_DATE+"&dtend="+TEST_END_DATE+"&httpAccept=application%2Fjson";
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
}