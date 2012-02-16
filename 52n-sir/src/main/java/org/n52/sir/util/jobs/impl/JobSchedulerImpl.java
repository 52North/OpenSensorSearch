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

import java.util.Date;
import java.util.TimerTask;

import org.n52.sir.catalog.ICatalog;
import org.n52.sir.catalog.ICatalogConnection;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.util.jobs.IJobScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Class encapsulates a {@link TimerServlet} where tasks are forwared to.
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class JobSchedulerImpl implements IJobScheduler {

    private static Logger log = LoggerFactory.getLogger(JobSchedulerImpl.class);

    private static final long DEFAULT_DELAY_MILLISECS = 100;

    private static final int SECONDS_TO_MILLISECONDS_FACTOR = 1000;

    private TimerServlet timerServlet;

    /**
     * 
     * @param timer
     */
    protected JobSchedulerImpl(TimerServlet timer) {
        this.timerServlet = timer;
        log.info("NEW " + this);
    }

    @Override
    public void submit(ICatalogConnection conn) throws OwsExceptionReport {
        submit(conn, DEFAULT_DELAY_MILLISECS);
    }

    /**
     * 
     * @param conn
     * @param delay
     * @throws OwsExceptionReport
     */
    private void submit(ICatalogConnection conn, long delay) throws OwsExceptionReport {
        ICatalog catalog = this.timerServlet.getCatalog(conn);

        if (conn.getConnectionID() == ICatalogConnection.UNSAVED_CONNECTION_ID
                && conn.getPushIntervalSeconds() == ICatalogConnection.NO_PUSH_INTERVAL) {
            // push only once
            submitOnce(new PushCatalogTask(conn.getConnectionID(), this.timerServlet.getCatalogStatusHandler(), catalog),
                       delay);
        }
        else {
            // schedule periodic push
            submitRepeating(conn.getConnectionID(),
                            new PushCatalogTask(conn.getConnectionID(),
                                                this.timerServlet.getCatalogStatusHandler(),
                                                catalog),
                            delay,
                            conn.getPushIntervalSeconds() * SECONDS_TO_MILLISECONDS_FACTOR);
        }
    }

    @Override
    public void cancel(String identifier) {
        if (log.isDebugEnabled()) {
            log.debug("Cancelling Task: " + identifier + ".");
        }
        this.timerServlet.cancel(identifier);
    }

    private void submitOnce(TimerTask task, long delay) {
        if (log.isDebugEnabled()) {
            log.debug("Scheduling Task: " + task + " for execution: now.");
        }
        Date runAt = new Date();
        runAt.setTime(runAt.getTime() + delay);
        this.timerServlet.submit(task, runAt);
    }

    private void submitRepeating(String identifier, TimerTask task, long delay, long period) {
        if (log.isDebugEnabled()) {
            log.debug("Scheduling Task: " + task + " for execution now and with period of " + period
                    + "ms after a delay of " + delay + "ms.");
        }
        this.timerServlet.submit(identifier, task, delay, period);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("JobSchedulerImpl [default delay (msecs) (ALWAYS applied!)=");
        sb.append(DEFAULT_DELAY_MILLISECS);
        sb.append(", internal task handler: ");
        sb.append(this.timerServlet);
        sb.append("]");
        return sb.toString();
    }
}