/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */
package org.n52.sir.catalog;

import java.util.ArrayList;
import java.util.Collection;

import org.n52.sir.SirConfigurator;
import org.n52.sir.ds.ICatalogStatusHandlerDAO;
import org.n52.sir.ds.IDAOFactory;
import org.n52.sir.ows.OwsExceptionReport;
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
