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

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.n52.sir.SirConfigurator;
import org.n52.sir.catalog.ICatalogConnection;
import org.n52.sir.catalog.ICatalogStatusHandler;
import org.n52.sir.catalogconnection.impl.CatalogConnectionSchedulerFactoryImpl;
import org.n52.sir.ds.IConnectToCatalogDAO;
import org.n52.sir.ds.IDAOFactory;
import org.n52.sir.ows.OwsExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class CatalogConnectionModule extends AbstractModule {

    private static Logger log = LoggerFactory.getLogger(CatalogConnectionModule.class);

    protected static final int STARTUP_DELAY_SECS = 10;

    private boolean scheduleJobsOnStartup;

    private ExecutorService exec;

    private SirConfigurator config;
    
    @Inject
    public CatalogConnectionModule(@Named("oss.catalogconnection.scheduleJobsOnStartup")
    boolean startup,SirConfigurator config) {
        super();
        
        this.scheduleJobsOnStartup = startup;
        this.exec = Executors.newSingleThreadExecutor();
        this.config = config;
    }

    @Override
    protected void configure() {
        bind(CatalogConnectionSchedulerFactory.class).to(CatalogConnectionSchedulerFactoryImpl.class);

        // TODO start the catalog connections from database
        if (this.scheduleJobsOnStartup)
            startCatalogConnections();

    }

    /**
     * Uses a thread for a delayed execution. This is necessary if both the catalog and the SIR run in the
     * same container. The update can be blocked if the {@link ICatalogStatusHandler} is not available in the
     * context.
     */
    private void startCatalogConnections() {
        if ( !this.scheduleJobsOnStartup) {
            log.warn("Catalog connections are disabled on startup.");
            return;
        }

        if (log.isDebugEnabled())
            log.debug(" ***** Starting Thread for catalog connections with a delay of " + STARTUP_DELAY_SECS
                    + " seconds ***** ");

        this.exec.submit(new Thread("CatalogConnector") {

            @Override
            public void run() {
                // wait with the catalog connection, because if the catalog runs
                // in the same tomcat, problems
                // might occur during startup phase

                try {
                    sleep(STARTUP_DELAY_SECS * 1000);
                }
                catch (InterruptedException e1) {
                    log.error("Error sleeping before catalog connections.", e1);
                }

                log.info(" ***** Starting catalog connections ***** ");

                // run tasks for existing catalogs
                int i = 0;
                try {
                    IDAOFactory f = CatalogConnectionModule.this.config.getInstance().getFactory();
                    if (f == null) {
                        log.error("Factory is null");
                        throw new RuntimeException("Could not get factory");
                    }

                    IConnectToCatalogDAO catalogDao = f.connectToCatalogDAO();
                    List<ICatalogConnection> savedConnections = catalogDao.getCatalogConnectionList();

                    CatalogConnectionScheduler scheduler = CatalogConnectionModule.this.config.getInstance().getJobSchedulerFactory().getScheduler();
                    for (ICatalogConnection iCatalogConnection : savedConnections) {
                        if (iCatalogConnection.getPushIntervalSeconds() != ICatalogConnection.NO_PUSH_INTERVAL) {
                            scheduler.submit(iCatalogConnection);
                            i++;
                        }
                        else {
                            if (log.isDebugEnabled())
                                log.debug("ICatalogConnection without push interval is ignored: "
                                        + iCatalogConnection.getConnectionID());
                        }
                    }
                }
                catch (OwsExceptionReport e) {
                    log.error("Could not run tasks for saved catalog connections: {}", e.getMessage());
                }

                log.info(" ***** Scheduled " + i + " task(s) from the database. ***** ");
            }

        });
    }

}
