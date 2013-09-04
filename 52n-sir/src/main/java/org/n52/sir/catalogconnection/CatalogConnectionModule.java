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

package org.n52.sir.catalogconnection;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.n52.sir.catalogconnection.impl.CatalogConnectionSchedulerProvider;
import org.n52.sir.catalogconnection.impl.StartupThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;

public class CatalogConnectionModule extends AbstractModule {

    private static Logger log = LoggerFactory.getLogger(CatalogConnectionModule.class);

    private ExecutorService exec = Executors.newSingleThreadExecutor();

    @Override
    protected void configure() {
        try {
            // TODO move catalog connection properties to own file in own module
            Properties properties = new Properties();
            properties.load(CatalogConnectionModule.class.getResourceAsStream("/prop/sir.properties"));
            Names.bindProperties(binder(), properties);

            log.debug("Loaded and bound properties:\n\t{}", properties);
        }
        catch (IOException e) {
            log.error("Could not load properties.", e);
        }

        bind(CatalogConnectionScheduler.class).toProvider(CatalogConnectionSchedulerProvider.class);

        // having the exec here is not really nice... and does not work...
        // Provider<StartupThread> provider = getProvider(StartupThread.class);
        // this.exec.submit(provider.get());
        // FIXME

        log.debug("Configured {}", this);
    }
    
    @Provides
    public StartupThread provideStartupThread() {
        StartupThread st = new StartupThread();
        log.debug("Created new thread: {}", st);
        return st;
    }

}
