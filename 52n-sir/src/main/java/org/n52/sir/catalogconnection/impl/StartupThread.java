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
 * 
 */
package org.n52.sir.catalogconnection.impl;

import java.util.List;

import org.n52.sir.catalog.ICatalogConnection;
import org.n52.sir.catalog.ICatalogStatusHandler;
import org.n52.sir.catalogconnection.CatalogConnectionScheduler;
import org.n52.sir.ds.IConnectToCatalogDAO;
import org.n52.sir.ds.pgsql.DAOFactory;
import org.n52.sir.ows.OwsExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Uses a thread for a delayed execution. This is necessary if both the catalog and the SIR run in the same
 * container. The update can be blocked if the {@link ICatalogStatusHandler} is not available in the context.
 * 
 * @author Daniel
 * 
 */
public class StartupThread extends Thread {

    protected static final int STARTUP_DELAY_SECS = 10;

    private static Logger log = LoggerFactory.getLogger(StartupThread.class);

    @Inject
    private DAOFactory daoFactory;

    @Inject
    private CatalogConnectionScheduler scheduler;

    @Inject
    @Named("oss.catalogconnection.scheduleJobsOnStartup")
    private boolean scheduleOnStartup = false;

    private IConnectToCatalogDAO catalogDao;

    // @Inject
    // public StartupThread(IConnectToCatalogDAO catalogDao,
    // CatalogConnectionScheduler scheduler,
    // @Named("oss.catalogconnection.scheduleJobsOnStartup")
    // boolean startup) {
    // this.catalogDao = catalogDao;
    // this.scheduler = scheduler;
    // this.scheduleOnStartup = startup;
    // }

    public StartupThread() {
        try {
            this.catalogDao = this.daoFactory.connectToCatalogDAO();
        }
        catch (OwsExceptionReport e) {
            log.error("Could not get dao from factory.", e);
        }
        log.debug("NEW {}", this);
    }

    @Override
    public void run() {
        if ( !this.scheduleOnStartup) {
            log.info("Startup scheduling not activated");
            return;
        }
        log.debug("* Starting Thread for catalog connections with a delay of {} seconds *", STARTUP_DELAY_SECS);

        try {
            // delay startup to minize issues if the catalog runs in the same container
            sleep(STARTUP_DELAY_SECS * 1000);
        }
        catch (InterruptedException e1) {
            log.error("Error waiting before start of catalog connections.", e1);
        }

        log.info("* Starting catalog connections *");

        int i = 0, j = 0, k = 0;
        try {
            List<ICatalogConnection> savedConnections = this.catalogDao.getCatalogConnectionList();

            for (ICatalogConnection iCatalogConnection : savedConnections) {
                if (iCatalogConnection.getPushIntervalSeconds() != ICatalogConnection.NO_PUSH_INTERVAL) {
                    boolean submit = this.scheduler.submit(iCatalogConnection);
                    if (submit)
                        i++;
                    else
                        j++;
                }
                else
                    log.debug("ICatalogConnection without push interval is ignored: {}",
                              iCatalogConnection.getConnectionID());
                k++;
            }
        }
        catch (OwsExceptionReport e) {
            log.error("Could not run tasks for saved catalog connections.", e.getMessage());
        }

        log.info("* Scheduled {} of {} task(s) from the database, could not schedule {}, and {} did not have a schedule *",
                 i,
                 i + j + k,
                 j,
                 k);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("StartupThread [");
        if (this.catalogDao != null) {
            builder.append("catalogDao=");
            builder.append(this.catalogDao);
            builder.append(", ");
        }
        if (this.scheduler != null) {
            builder.append("scheduler=");
            builder.append(this.scheduler);
            builder.append(", ");
        }
        builder.append("scheduleOnStartup=");
        builder.append(this.scheduleOnStartup);
        builder.append("]");
        return builder.toString();
    }

}
