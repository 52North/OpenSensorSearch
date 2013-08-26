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

import java.net.URL;
import java.util.Properties;

import org.n52.sir.SirConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class ConfigModule extends AbstractModule {

    private static Logger log = LoggerFactory.getLogger(ConfigModule.class);

    @Override
    protected void configure() {
        try {
            Properties props = loadProperties("app.properties");
            Names.bindProperties(binder(), props);
        }
        catch (Exception e) {
            log.error("Could not load properties file.", e);
        }
        
        bind(ApplicationConstants.class).to(PropertyApplicationConstants.class);
        bind(SirConfigurator.class);
        
        log.info("Configured {}", this);
    }

    private static Properties loadProperties(String name) throws Exception {
        log.trace("Loading properties for {}", name);
        
        Properties properties = new Properties();
        ClassLoader loader = ConfigModule.class.getClassLoader();
        URL url = loader.getResource(name);
        properties.load(url.openStream());
        
        log.trace("Loaded properties: {}", properties);
        return properties;
    }
}
