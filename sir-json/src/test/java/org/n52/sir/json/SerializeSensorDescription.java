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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SerializeSensorDescription {

    private static File f;
    private ObjectMapper mapper;
    private ByteArrayOutputStream baos;
    private SimpleSensorDescription expected;
    
    @BeforeClass
    public static void prepare() {
        f = new File(SerializeSensorDescription.class.getResource("/sensordescription.json").getFile());
    }

    @Before
    public void setUp() throws Exception {
        this.mapper = MapperFactory.getMapper();
        this.baos = new ByteArrayOutputStream();
        this.expected = this.mapper.readValue(f, SimpleSensorDescription.class);
    }

    @Test
    public void mappingMatchesTestFile() throws Exception {
        SimpleSensorDescription sd = TestObjectGenerator.getSensorDescription();
        this.mapper.writeValue(this.baos, sd);

        try (BufferedReader br = new BufferedReader(new FileReader(f));) {
            StringBuilder sb = new StringBuilder();
            String line = "";
            while ( (line = br.readLine()) != null) {
                sb.append(line);
            }

            JSONAssert.assertEquals(sb.toString(), new String(this.baos.toByteArray()), false);
        }

    }
}
