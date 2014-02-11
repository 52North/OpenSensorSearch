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

package org.n52.sor;

import java.io.IOException;
import java.util.Properties;

import org.n52.oss.common.AbstractConfigModule;
import org.n52.sor.rest.Frontend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Daniel Nüst
 * 
 */
public class SorModule extends AbstractConfigModule {

    private static Logger log = LoggerFactory.getLogger(SorModule.class);

    private static final String HOME_CONFIG_FILE = "org.n52.oss.service.sor.properties";

    @Override
    protected void configure() {
        boolean startSor = true;

        Properties properties = null;
        try {
            properties = loadProperties("/conf/sor.properties");
            properties = updateFromUserHome(properties, HOME_CONFIG_FILE);
            startSor = Boolean.parseBoolean(properties.getProperty("oss.sor.startSor"));
        }
        catch (IOException e) {
            log.error("Could not load properties for SOR.", e);
        }

        if (startSor) {
            try {
                PropertiesManager.getInstance(properties);
                PhenomenonManager.getInstance();
            }
            catch (Exception e) {
                log.error("Could not init SOR.", e);
            }

            bind(SOR.class);
            bind(Frontend.class);

            log.info("Configured {}", this);
        }
        else
            log.warn("SOR startup disabled");
    }
}
