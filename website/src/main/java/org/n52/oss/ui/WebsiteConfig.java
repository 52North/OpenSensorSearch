/**
 * ﻿Copyright (C) 2013 52°North Initiative for Geospatial Open Source Software GmbH
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

package org.n52.oss.ui;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thanks to Matthes, based on
 * https://github.com/enviroCar/enviroCar-server/blob/master/event/src/main/java/org
 * /envirocar/server/event/SendVerificationCodeViaMailListener.java
 * 
 * @author Daniel
 * 
 */
public class WebsiteConfig {

    protected static Logger log = LoggerFactory.getLogger(WebsiteConfig.class);

    private static final String CONFIG_FILE = "org.n52.oss.ui.properties";

    private static final String SIR_ENDPOINT = "oss.ui.sir.endpoint";

    private static final String API_ENDPOINT = "oss.ui.api.endpoint";

    private String sirEndpoint;

    private String apiEndpoint;

    public WebsiteConfig() {
        Properties props = new Properties();

        try {
            // load default values
            InputStream stream = openStreamForResource("/" + CONFIG_FILE);
            try (Reader r = new InputStreamReader(stream);) {
                props.load(r);
                log.debug("Loaded properties from /{}", CONFIG_FILE);
            }
        }
        catch (IOException e) {
            log.error("Could not load properties.", e);
        }

        String home = System.getProperty("user.home");
        if (home != null) {
            File homeDirectory = new File(home);

            try {
                if (homeDirectory != null && homeDirectory.isDirectory()) {
                    File configFile = new File(homeDirectory, CONFIG_FILE);
                    if (configFile != null && configFile.exists())
                        try (Reader r = openFileWithFallback(configFile, CONFIG_FILE);) {
                            props.load(r);
                            log.debug("Loaded properties (overwriting defaults) from {}", configFile);
                        }
                    else
                        log.info("No config file in user home ({}), let's see if the defaults work...", homeDirectory);
                }
            }
            catch (IOException e) {
                log.error("Could not load properties.", e);
            }
        }
        else
            log.warn("user.home is not specified. Will try to use fallback resources.");

        load(props);

        log.info("NEW {}", this);
    }

    private void load(Properties props) {
        this.sirEndpoint = props.getProperty(SIR_ENDPOINT);
        this.apiEndpoint = props.getProperty(API_ENDPOINT);
    }

    private Reader openFileWithFallback(File f, String fallbackResource) throws IOException {

        log.info("File {} is not available. Try to use fallback at {}", (f != null ? f : "null"), fallbackResource);

        return new InputStreamReader(openStreamForResource(fallbackResource));
    }

    private InputStream openStreamForResource(String string) throws IOException {
        URL resource = getClass().getResource(string);
        URLConnection conn = resource.openConnection();
        conn.setUseCaches(false);
        return conn.getInputStream();
    }

    public String getSirEndpoint() {
        return this.sirEndpoint;
    }

    public void setSirEndpoint(String sirEndpoint) {
        this.sirEndpoint = sirEndpoint;
    }

    public String getApiEndpoint() {
        return this.apiEndpoint;
    }

    public void setApiEndpoint(String apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Config [");
        if (this.sirEndpoint != null) {
            builder.append("sirEndpoint=");
            builder.append(this.sirEndpoint);
            builder.append(", ");
        }
        if (this.apiEndpoint != null) {
            builder.append("apiEndpoint=");
            builder.append(this.apiEndpoint);
        }
        builder.append("]");
        return builder.toString();
    }
}
