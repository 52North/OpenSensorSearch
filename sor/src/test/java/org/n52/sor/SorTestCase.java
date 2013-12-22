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

package org.n52.sor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Nüst
 * 
 */
public abstract class SorTestCase {

    public static final String REQUEST_EXAMPLES_GET_FILE = "requests/RequestExamples_GET.txt";

    private static Logger log = LoggerFactory.getLogger(SorTestCase.class);

    private static Properties examples;

    @Test
    protected static String loadGetRequestExample(String name) {
        return examples.getProperty(name);
    }

    @BeforeClass
    public void setUp() throws Exception {
        if (PropertiesManager.getInstance() == null) {
            try (InputStream configStream = getClass().getResourceAsStream("/conf/sor.properties");) {
                PropertiesManager.getInstance(configStream);

                examples = new Properties();

                try (InputStream exampleStream = getClass().getResourceAsStream(REQUEST_EXAMPLES_GET_FILE);) {
                    examples.load(exampleStream);
                }
            }
            catch (IOException e) {
                log.error("Loading examples properties failed");
            }

            log.info("Instatiated PropertiesManager");
        }
    }
}