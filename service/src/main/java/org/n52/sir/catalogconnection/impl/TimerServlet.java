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

package org.n52.sir.catalogconnection.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.GenericServlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.n52.sir.catalog.ICatalogFactory;
import org.n52.sir.catalog.ICatalogStatusHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * 
 * This class can be used to execute {@link TimerTask} instances. It runs as a servlet and can be accessed by
 * other servlets for task scheduling and cancelling. The actual service method for GET and POST requests are
 * not implemented. It also provides methods to access the appropriate instances of
 * {@link ICatalogStatusHandler} and {@link ICatalogFactory} for tasks that run within this servlet.
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
@Deprecated
public class TimerServlet extends GenericServlet {

    /**
     * Inner class to handle storage and cancelling of tasks at runtime.
     */
    private static class TaskElement {
        protected Date date;
        protected long delay;
        protected String id;
        protected long period;
        protected TimerTask task;

        protected TaskElement(String identifier, TimerTask task, long delay, long period) {
            this.id = identifier;
            this.task = task;
            this.delay = delay;
            this.period = period;
            this.date = new Date(0l);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("TaskElement [");
            sb.append(this.task);
            sb.append(", delay=");
            sb.append(this.delay);
            sb.append(", period=");
            sb.append(this.period);
            sb.append(", date=");
            sb.append(this.date);
            sb.append("]");
            return sb.toString();
        }
    }

    private static final String IS_DAEMON_INIT_PARAM_NAME = "isDaemon";

    private static Logger log = LoggerFactory.getLogger(TimerServlet.class);

    /**
     * The identifier that can be used to access the instance of this servlet an run-time.
     */
    public static final String NAME_IN_CONTEXT = "TimerServlet";

    private static final long serialVersionUID = 4704774153636727580L;

    /**
     * Inner {@link Timer} that might run as a daemon according to the init parameter
     * {@link TimerServlet#IS_DAEMON_INIT_PARAM_NAME} in the servlet defintion (web.xml).
     */
    private static Timer timer;

    private ICatalogStatusHandler catalogStatusHandler;

    private ArrayList<TaskElement> tasks = new ArrayList<>();

    @Inject
    public TimerServlet() {
        log.info("NEW {}", this);
    }

    public void cancel(String identifier) {
        for (TaskElement te : this.tasks) {
            if (te.id.equals(identifier)) {
                te.task.cancel();
                log.info("CANCELED " + te);
            }

        }
    }

    @Override
    public void destroy() {
        log.info("called destroy()...");
        super.destroy();
        timer.cancel();
        timer = null;

        log.info("destroyed.");
    }

    /**
     * 
     * Returns an instance of {@link ICatalogStatusHandler} that can be used to update the status description
     * of repeated catalog connections.
     * 
     * @return the catalogStatusHandler
     */
    public ICatalogStatusHandler getCatalogStatusHandler() {
        if (this.catalogStatusHandler == null) {
            ServletContext context = getServletContext();
            this.catalogStatusHandler = (ICatalogStatusHandler) context.getAttribute(ICatalogStatusHandler.NAME_IN_CONTEXT);
            if (this.catalogStatusHandler == null) {
                log.warn("Could not get catalog status handler from servlet context!");
            }
            else
                log.debug("Got catalog status handler from servlet context: {}", this.catalogStatusHandler);
        }
        return this.catalogStatusHandler;
    }

    @Override
    public void init() throws ServletException {
        super.init();
        log.info(" * Initializing Timer ... ");

        // create inner Timer
        timer = new Timer(getServletName(), Boolean.parseBoolean(getInitParameter(IS_DAEMON_INIT_PARAM_NAME)));

        log.info(" ***** Timer initiated successfully! ***** ");
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) {
        throw new UnsupportedOperationException("Not supperted by TimerServlet!");
    }

    /**
     * 
     * " Finally, fixed-rate execution is appropriate for scheduling multiple repeating timer tasks that must
     * remain synchronized with respect to one another." See
     * {@link Timer#scheduleAtFixedRate(TimerTask, long, long)} for details.
     * 
     * @param task
     * @param delay
     * @param period
     */
    public void submit(String identifier, TimerTask task, long delay, long period) {
        timer.scheduleAtFixedRate(task, delay, period);
        if (log.isDebugEnabled()) {
            log.debug("Submitted: " + task + " with period = " + period + ", delay = " + delay);
        }
        // element is scheduled with repetition period, save for later cancelling!
        this.tasks.add(new TaskElement(identifier, task, delay, period));
    }

    public void submit(TimerTask task, Date date) {
        timer.schedule(task, date);
        if (log.isDebugEnabled()) {
            log.debug("Submitted: " + task + " to run at " + date);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TimerServlet [timer =");
        sb.append(timer.toString());
        sb.append(" -- ");
        sb.append("tasks: ");
        for (TaskElement te : this.tasks) {
            sb.append(te);
            sb.append(", ");
        }
        sb.delete(sb.length() - 1, sb.length() - 1);
        sb.append("]");
        return sb.toString();
    }
}
