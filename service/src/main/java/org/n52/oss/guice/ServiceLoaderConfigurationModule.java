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

package org.n52.oss.guice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

/**
 * http://docs.oracle.com/javase/6/docs/api/java/util/ServiceLoader.html
 *
 * @author Daniel
 *
 */
public class ServiceLoaderConfigurationModule extends AbstractModule {

    private static Logger log = LoggerFactory.getLogger(ServiceLoaderConfigurationModule.class);

    private static Collection<String> loadedModules = new ArrayList<>();
    
    @Override
    protected void configure() {
        for (Module m : ServiceLoader.load(Module.class)) {
            log.trace("Loading module {}...", m);
            install(m);
            loadedModules.add(m.toString());
            log.debug("Installed {}", m);
        }
        
        log.info("Configured {}, modules: {}", this, Arrays.toString(loadedModules.toArray()));
    }
}
