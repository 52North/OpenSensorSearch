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

package org.n52.oss.stats;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.oss.api.ApiPaths;
import org.n52.oss.util.Util;
import org.skyscreamer.jsonassert.JSONAssert;

public class StatisticsIT {

    private static Long sensors = Long.valueOf(42l);
    private static Long phenomena = Long.valueOf(1l);
    private static Long services = Long.valueOf(17l);

    private static String sensorResponse = "{ \"sensors\": " + sensors.toString() + " }";
    private static String phenomenaResponse = "{ \"phenomena\": " + phenomena.toString() + "}";
    private static String servicesResponse = "{ \"services\": " + services.toString() + "}";

    private static String path = ApiPaths.STATISTICS_PATH; // "/api/v1/statistics";

    private static String endpoint;

    @BeforeClass
    public static void prepare() {
        endpoint = "http://localhost:8080/OpenSensorSearch" + path;

        // TODO load test dataset
    }

    @Test
    public void sensorCount() throws JSONException, UnsupportedEncodingException, IOException {
        HttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(new HttpGet(StatisticsIT.endpoint + "/sensors"));
        String actual = Util.getResponsePayload(response);

        JSONAssert.assertEquals(sensorResponse, actual, false);
    }

    @Test
    public void phenomenonCount() throws JSONException, UnsupportedEncodingException, IOException {
        HttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(new HttpGet(StatisticsIT.endpoint + "/phenomena"));
        String actual = Util.getResponsePayload(response);

        JSONAssert.assertEquals(phenomenaResponse, actual, false);
    }

    @Test
    public void servicesCount() throws JSONException, UnsupportedEncodingException, IOException {
        HttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(new HttpGet(StatisticsIT.endpoint + "/services"));
        String actual = Util.getResponsePayload(response);

        JSONAssert.assertEquals(servicesResponse, actual, false);
    }
}
