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
package org.n52.sir;

import java.util.Properties;

import org.n52.oss.common.AbstractConfigModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.name.Names;

public class ConfigModule extends AbstractConfigModule {

    private static Logger log = LoggerFactory.getLogger(ConfigModule.class);

    private static final String HOME_CONFIG_FILE = "org.n52.oss.service.sir.properties";

    @Override
    protected void configure() {
        try {
            Properties sirProps = loadProperties("/prop/sir.properties");

            // update properties from home folder file
            sirProps = updateFromUserHome(sirProps, HOME_CONFIG_FILE);

            // bind properties class
            bind(Properties.class).annotatedWith(Names.named("sir_properties")).toInstance(sirProps);

            // bind alle properties as named properties
            Names.bindProperties(binder(), sirProps);

            log.debug("Loaded and bound properties:\n\t{}", sirProps);
        }
        catch (Exception e) {
            log.error("Could not load properties file.", e);
        }

        bind(SirConfigurator.class);

        // overwrite with properties from home folder

        // these don't work yet - use workaround with org.w3c.dom.Document
        // bind(OwsExMessageBodyWriter.class);
        // bind(EbRimMessageBodyWriter.class);

        log.info("Configured {}", this);
    }

}
