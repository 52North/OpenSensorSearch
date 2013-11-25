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
package org.n52.sir.catalog;

import java.util.ArrayList;
import java.util.Collection;

import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.SirConfigurator;
import org.n52.sir.ds.ICatalogStatusHandlerDAO;
import org.n52.sir.ds.IDAOFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Implementation of {@link ICatalogStatusHandler} that utilizes a database (via an instance of
 * {@link ICatalogStatusHandlerDAO}) to save the status description as a simple string.
 * 
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class CatalogStatusHandlerImpl implements ICatalogStatusHandler {

    private static Logger log = LoggerFactory.getLogger(CatalogStatusHandlerImpl.class);

    private static final int MAXIMUM_INFOLIST_SIZE = 100;

    private ICatalogStatusHandlerDAO catStatHandlerDao;

    private ArrayList<String> runtimeInfo;

    public CatalogStatusHandlerImpl() {
        SirConfigurator configurator = SirConfigurator.getInstance();
        IDAOFactory factory = configurator.getFactory();

        this.catStatHandlerDao = factory.catalogStatusHandlerDAO();
        this.runtimeInfo = new ArrayList<String>();
    }

    @Override
    public int getMaximumInfolistSize() {
        return MAXIMUM_INFOLIST_SIZE;
    }

    @Override
    public Collection<String> getRuntimeInfo() {
        ArrayList<String> infolist = new ArrayList<String>(this.runtimeInfo);
        if (infolist.size() == MAXIMUM_INFOLIST_SIZE) {
            infolist.add(0, "(Status information of the last " + MAXIMUM_INFOLIST_SIZE + " events only.)");
            infolist.add(1, " ");
        }
        else if (infolist.isEmpty()) {
            infolist.add("No events logged yet.");
        }
        return infolist;
    }

    private void saveRuntimeInfo(String identifier, String statusMessage) {
        if (this.runtimeInfo.size() == MAXIMUM_INFOLIST_SIZE) {
            this.runtimeInfo.remove(0);
        }

        this.runtimeInfo.add("Connection id: " + identifier + "\t>>>\t" + statusMessage);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.catalog.ICatalogStatusHandler#setStatus(java.lang.String, java.lang.String)
     */
    @Override
    public void setStatus(String identifier, String statusMessage) {
        if (identifier.equals(ICatalogConnection.UNSAVED_CONNECTION_ID)) {
            log.info("* STATUS CHANGE FOR UNSAVED CONNECTION: " + statusMessage + " *");
            saveRuntimeInfo(identifier, statusMessage);
            return;
        }

        try {
            this.catStatHandlerDao.setNewStatus(identifier, statusMessage);
            saveRuntimeInfo(identifier, statusMessage);
        }
        catch (OwsExceptionReport e) {
            log.error("Error setting new status for " + identifier, e);
        }
    }

}
