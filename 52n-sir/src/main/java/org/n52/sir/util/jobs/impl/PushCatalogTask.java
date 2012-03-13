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
package org.n52.sir.util.jobs.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.n52.sir.catalog.ICatalog;
import org.n52.sir.catalog.ICatalogStatusHandler;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.util.XmlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Class for executing the method {@link ICatalog#pushAllDataToCatalog()}. Error handling, logging and status
 * updating are included.
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class PushCatalogTask extends TimerTask {

    private static final String CAPABILITIES_NOT_ENSURED = "Catalog does not have sufficient capabilities and extensions could not be inserted!";

    private static final String LAST_COMPLETE_PUSH = "Last push finished succesfully with [insert, update, delete]: ";

    private static final String LAST_ERROR_OWS = "Error on last push: ";

    private static final String LAST_ERROR_PUSH = "Last push with errors (see the log for details).";

    private static final String LAST_PUSH_WITH_REPORTS = "Last push finished with errors, see the log for details. Error count: ";

    private static Logger log = LoggerFactory.getLogger(PushCatalogTask.class);

    private ICatalog catalog;

    private ICatalogStatusHandler catalogStatusHandler;

    private String connectionID;

    private boolean repeated;

    /**
     * 
     * @param connectionID
     * @param catalogStatusHandler
     * @param catalogP
     */
    public PushCatalogTask(String connectionID, ICatalogStatusHandler catalogStatusHandler, ICatalog catalogP) {
        this.connectionID = connectionID;
        this.catalogStatusHandler = catalogStatusHandler;
        this.catalog = catalogP;
    }

    /*
     * (non-Javadoc)
     * @see java.util.TimerTask#cancel()
     */
    @Override
    public boolean cancel() {
        log.info("Cancelling " + this);
        return super.cancel();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() throws Throwable {
        if (log.isDebugEnabled())
            log.debug("Finalizing " + this);
        super.finalize();
    }

    /**
     * 
     * @param reports
     * @return
     */
    private String getReportString(List<OwsExceptionReport> reports) {
        StringBuilder sb = new StringBuilder();
        for (OwsExceptionReport owsExceptionReport : reports) {
            sb.append("\n");
            sb.append(owsExceptionReport.getDocument().xmlText(XmlTools.PRETTY_PRINT_OPTIONS));
        }
        return sb.toString();
    }

    /**
     * 
     * @return
     */
    private String getStatusSignature() {
        return " AT " + new Date(System.currentTimeMillis()).toString() + " WITH CATALOG " + this.catalog;
    }

    /**
     * 
     * @param reports
     */
    private void logReports(List<OwsExceptionReport> reports) {
        int i = 0;
        for (OwsExceptionReport owsExceptionReport : reports) {
            i++;
            log.error("Error " + i + " of " + reports.size() + " when pushing data to catalog (" + this.connectionID
                    + "): " + owsExceptionReport);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.TimerTask#run()
     */
    @Override
    public void run() {
        log.info("*** Run PushCatalogTask to " + this.catalog);

        try {
            // check (probably once more, as the catalog was probably checked during first insertion - BUT
            // check here will be saved during runtime!)
            boolean caps = this.catalog.ensureSufficientCapabilities();
            if ( !caps) {
                log.error(CAPABILITIES_NOT_ENSURED);
                this.catalogStatusHandler.setStatus(this.connectionID, CAPABILITIES_NOT_ENSURED + getStatusSignature());
                return;
            }

            List<OwsExceptionReport> reports = this.catalog.pushAllDataToCatalog();
            if (reports.size() > 0) {
                logReports(reports);
                this.catalogStatusHandler.setStatus(this.connectionID, LAST_PUSH_WITH_REPORTS + reports.size() + "."
                        + getStatusSignature() + ":" + getReportString(reports));
            }
            else {
                int[] summary = this.catalog.getSummaryOfLastPush();
                this.catalogStatusHandler.setStatus(this.connectionID, LAST_COMPLETE_PUSH + Arrays.toString(summary)
                        + getStatusSignature());
            }

        }
        catch (OwsExceptionReport e) {
            log.error("*** Could not complete push catalog task!", e);
            this.catalogStatusHandler.setStatus(this.connectionID, LAST_ERROR_OWS + e.getMessage()
                    + getStatusSignature());
        }
        catch (Exception e) {
            log.error("*** Could not complete push catalog task!", e);
            this.catalogStatusHandler.setStatus(this.connectionID, LAST_ERROR_PUSH + getStatusSignature());
        }
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PushCatalogTask [connectionID=");
        sb.append(this.connectionID);
        sb.append(", catalog=");
        sb.append(this.catalog);
        sb.append(", repeated=");
        sb.append(this.repeated);
        sb.append("]");
        return sb.toString();
    }

}
