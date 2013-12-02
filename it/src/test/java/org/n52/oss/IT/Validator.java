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

package org.n52.oss.IT;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class Validator {

    private static String smlCheckEndpoint = "http://localhost:8080/oss-service/api/v1/check/sml";

    @BeforeClass
    public static void prepare() {
        //
    }

    @Test
    public void testValid_json() throws JSONException, ClientProtocolException, IOException {
        String responseString = executeCheckRequest("/AirBase-test.xml");
        JSONAssert.assertEquals("{ \"status\": \"valid\" }", responseString, false);
    }

    @Test
    public void testInvalid_json() throws ClientProtocolException, IOException {
        String responseString = executeCheckRequest("/AirBase-test-invalid.xml");

        assertThat("is invalid", responseString, containsString("invalid"));
        assertThat("has message0", responseString, containsString("message0"));
        assertThat("has message1", responseString, containsString("message1"));
        assertThat("found missing validTime", responseString, containsString("sml:validTime"));
        assertThat("found missing description", responseString, containsString("gml:description"));
    }

    private String executeCheckRequest(String testFile) throws IOException, ClientProtocolException {
        String responseString = null;
        File f = new File(getClass().getResource(testFile).getFile());
    
        HttpPost post = new HttpPost(smlCheckEndpoint);
        HttpEntity entity = new FileEntity(f);
        post.setEntity(entity);
        post.setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
    
        try (CloseableHttpClient client = HttpClientBuilder.create().build();
                CloseableHttpResponse response = client.execute(post);) {
    
            HttpEntity responseEntity = response.getEntity();
            responseString = EntityUtils.toString(responseEntity);
        }
    
        return responseString;
    }
}
