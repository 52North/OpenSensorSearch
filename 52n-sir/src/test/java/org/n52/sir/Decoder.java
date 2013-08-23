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
package org.n52.sir;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.xmlbeans.XmlException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.sir.datastructure.SirSensor;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.sml.SensorMLDecoder;

public class Decoder {
    
    private static SensorMLDocument doc = null;
    
    @BeforeClass
    public static void loadDocuments() throws XmlException, IOException {
        File sensor_file = new File(ClassLoader.getSystemResource("Requests/testsensor.xml").getFile());
        doc = SensorMLDocument.Factory.parse(sensor_file);
    }
    
    @Test
    public void keywordsAreDecoded() throws OwsExceptionReport {
        SirSensor sensor = SensorMLDecoder.decode(doc);
        
        Collection<String> keywords = sensor.getKeywords();
        ArrayList<String> actual = new ArrayList<>();
        actual.addAll(keywords);
        
        assertThat(actual, hasItems("testkeyword", "test", "another keyword"));
    }
    
}
