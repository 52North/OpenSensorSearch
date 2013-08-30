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
package org.n52.sir.json;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Deserialize {

    private ObjectMapper mapper;

    private String searchResult = "{" + "\"source\" : \"http://service.url\"," + "\"query\" : \"temperature\","
            + "\"url\" : \"http://service.url/search?q=temperature\","
            + "\"description\" : \"Search results for the keyword 'temperature' from Open Sensor Search.\","
            + "\"author\" : \"52°North\"," + "\"date\" : 1331022803081," + "\"results\" : [ {"
            + "\"lastUpdate\" : 1331022803082," + "\"sensorDescription\" : {" + "\"boundingBox\" : {"
            + "\"east\" : 1.0," + "\"north\" : 4.0," + "\"south\" : 2.0," + "\"srid\" : 1234," + "\"west\" : 3.0"
            + "}," + "\"text\" : \"This text describes the sensor.\","
            + "\"url\" : \"http://domain.tld:port/path/001\"" + "}," + "\"sensorIdInSir\" : \"001\","
            + "\"serviceReferences\" : [ {" + "\"service\" : {" + "\"type\" : \"SOS\","
            + "\"url\" : \"http://host:port/path\"" + "}," + "\"serviceSpecificSensorId\" : \"urn:sos:001\"" + "} ]"
            + "} ]" + "}";

    @Before
    public void setUp() throws Exception {
        this.mapper = MapperFactory.getMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Test
    public void test() throws Exception {
        SearchResult result = this.mapper.readValue(this.searchResult, SearchResult.class);
        System.out.println(result);
    }

}
