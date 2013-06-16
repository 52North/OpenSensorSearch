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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Nüst
 * 
 */
public abstract class SirTestCase extends TestCase {

    public static final String REQUEST_EXAMPLES_GET_FILE = "RequestExamples_GET.txt";

    private static final String WEB_CONTENT_FOLDER = "/home/daniel/workspace/SIR/WebContent";

    private static final String CONFIG_FILE_IN_WEB_CONTENT = "/WEB-INF/conf/sir.config";

    private static final String DB_CONFIG_FILE_IN_WEB_CONTENT = "/WEB-INF/conf/dbsir.config";

    private static Logger log = LoggerFactory.getLogger(SirTestCase.class);

    /**
     * 
     */
    private static Properties examples;

    public static String insertedSensorId;

    /**
     * 
     * @throws IOException
     */
    @Override
    protected void setUp() throws Exception {
        if (SirConfigurator.getInstance() == null) {
            String basepath = WEB_CONTENT_FOLDER;
            InputStream configStream = new FileInputStream(basepath + CONFIG_FILE_IN_WEB_CONTENT);
            InputStream dbConfigStream = new FileInputStream(basepath + DB_CONFIG_FILE_IN_WEB_CONTENT);
            SirConfigurator.getInstance(configStream, dbConfigStream, basepath, null);

            InputStream exampleStream = new FileInputStream(SirConfigurator.getInstance().getTestRequestPath()
                    + REQUEST_EXAMPLES_GET_FILE);
            examples = new Properties();
            // load properties
            try {
                examples.load(exampleStream);
            }
            catch (IOException e) {
                log.error("Loading examples file failed for: " + SirConfigurator.getInstance().getTestRequestPath()
                        + REQUEST_EXAMPLES_GET_FILE);
            }

            configStream.close();
            exampleStream.close();

            log.info("Instatiated SirConfigurator for Testing!");
        }
    }

    /**
     * 
     * @param name
     * @return
     */
    protected static String loadGetRequestExample(String name) {
        return examples.getProperty(name);
    }

    /**
     * 
     * @param fileName
     * @return
     */
    protected File getPostExampleFile(String fileName) {
        String path = SirConfigurator.getInstance().getTestRequestPath() + fileName;
        return new File(path);
    }
}