/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
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
