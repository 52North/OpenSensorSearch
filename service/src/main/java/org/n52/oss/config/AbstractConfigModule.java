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

package org.n52.oss.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;

public abstract class AbstractConfigModule extends AbstractModule {

    private static Logger log = LoggerFactory.getLogger(AbstractConfigModule.class);

    public static final String HOME_CONFIG_FILE = "org.n52.oss.service.properties";

    protected Properties updateFromUserHome(Properties props, String homeConfigFile) {
        String home = System.getProperty("user.home");
        log.debug("Used home directory: {}", home);

        if (home != null) {
            File homeDirectory = new File(home);

            try {
                if (homeDirectory != null && homeDirectory.isDirectory()) {
                    File configFile = new File(homeDirectory, homeConfigFile);
                    if (configFile != null && configFile.exists())
                        try (Reader r = new FileReader(configFile);) {
                            props.load(r);
                            log.info("Loaded properties (overwriting defaults) from {}", configFile);
                        }
                    else
                        log.debug("No config file in user home ({}), let's see if the defaults work...", homeDirectory);
                }
            }
            catch (IOException e) {
                log.error("Could not load properties.", e);
            }
        }
        else
            log.warn("user.home is not specified. Will try to use fallback resources.");

        return props;
    }

    private InputStream openStreamForResource(String string) throws IOException {
        URL resource = getClass().getResource(string);
        URLConnection conn = resource.openConnection();
        conn.setUseCaches(false);
        return conn.getInputStream();
    }

    protected Properties loadProperties(String name) throws Exception {
        log.trace("Loading properties for {}", name);

        Properties properties = new Properties();
        ClassLoader loader = getClass().getClassLoader();
        URL url = loader.getResource(name);
        properties.load(url.openStream());

        log.trace("Loaded properties: {}", properties);
        return properties;
    }
}
