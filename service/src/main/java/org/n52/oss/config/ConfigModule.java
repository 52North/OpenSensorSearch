/**
 * Copyright 2013 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.oss.config;

import java.util.Properties;

import org.n52.oss.common.AbstractConfigModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.name.Names;

public class ConfigModule extends AbstractConfigModule {

    private static Logger log = LoggerFactory.getLogger(ConfigModule.class);

    private static final String HOME_CONFIG_FILE = "org.n52.oss.service.properties";

    @Override
    protected void configure() {
        // app properties
        try {
            Properties props = loadProperties("/app.properties");

            // update properties from home folder file
            props = updateFromUserHome(props, HOME_CONFIG_FILE);

            Names.bindProperties(binder(), props);
        }
        catch (Exception e) {
            log.error("Could not load properties file.", e);
        }

        // constants
        bind(ApplicationConstants.class).to(PropertyApplicationConstants.class);

        log.debug("Configured {}", this);
    }

}
