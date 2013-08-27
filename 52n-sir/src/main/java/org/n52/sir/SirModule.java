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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class SirModule extends AbstractModule {

    private static Logger log = LoggerFactory.getLogger(SirModule.class);

    @Override
    protected void configure() {
        try {
            Properties sirProps = loadProperties("/prop/sir.properties");
            Names.bindProperties(binder(), sirProps);

            Properties dbProps = loadProperties("/prop/db.properties");
            Names.bindProperties(binder(), dbProps);

            log.debug("Loaded and bound properties: \n\t    {}\n\t    {}", sirProps, dbProps);
        }
        catch (IOException e) {
            log.error("Could not load properties.", e);
        }
    }

    private static Properties loadProperties(String path) throws IOException {
        log.trace("Loading properties for {}", path);

        Properties properties = new Properties();
        try (InputStream stream = SirModule.class.getResourceAsStream(path);) {
            properties.load(stream);
        }

        log.trace("Loaded properties: {}", properties);
        return properties;
    }
}
