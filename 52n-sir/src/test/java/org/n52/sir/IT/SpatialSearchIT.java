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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author Yakoub
 */
public class SpatialSearchIT {
    private static Logger log = LoggerFactory.getLogger(SpatialSearchIT.class);
    private static String lon_lat_radius_query = "http://localhost:8080/OpenSensorSearch/search?q=pre&";
    public static final double R = 6372.8; // In kilometers
    public static final double TEST_LAT = 1.5;
    public static final double TEST_LNG = 3.49;
    public static final double TEST_RADIUS = 2;
  
    @Test
    public void testDistanceLessThanSearchRadius() throws ClientProtocolException, IOException {
        lon_lat_radius_query += "lat="+TEST_LAT+"&lon="+TEST_LNG+"&radius="+TEST_RADIUS+"&httpAccept=application%2Fjson";
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
           double [] latLng =  element.getSensorDescription().getBoundingBox().getCenter();
           assertThat(haversine(latLng[0], latLng[1], TEST_LAT, TEST_LNG),lessThan(TEST_RADIUS));
        }

    }
    //implement the haversine formula for distances

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

}