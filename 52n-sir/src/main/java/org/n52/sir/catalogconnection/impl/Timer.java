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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TimerTask;

import org.apache.xmlbeans.XmlException;
import org.n52.sir.catalog.ICatalog;
import org.n52.sir.catalog.ICatalogConnection;
import org.n52.sir.catalog.ICatalogFactory;
import org.n52.sir.catalog.ICatalogStatusHandler;
import org.n52.sir.catalog.csw.CswFactory;
import org.n52.sir.ows.OwsExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

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
@Singleton
public class Timer {

    /**
     * Inner class to handle storage and cancelling of tasks at runtime.
     */
    private static class TaskElement {
        protected Date date;
        protected long delay;
        protected String id;
        protected long period;
        protected TimerTask task;

        /**
         * 
         * @param identifier
         * @param task
         * @param delay
         * @param period
         */
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

    private static final String CLASSIFICATION_INIT_FILENAMES = "oss.catalogconnection.csw-ebrim.classificationInitFilenames";

    private static final String CONFIG_FILE_LIST_SEPARATOR = ",";

    private static final String DO_NOT_CHECK_CATALOGS = "oss.catalogconnection.doNotCheckCatalogs";

    private static Logger log = LoggerFactory.getLogger(Timer.class);

    private static final String SLOT_INIT_FILENAME = "oss.catalogconnection.csw-ebrim.slotInitFilename";

    private Map<URI, ICatalog> catalogCache = new HashMap<>();

    private String[] catalogInitClassificationFiles;

    private String catalogSlotInitFile;

    private ICatalogStatusHandler catalogStatusHandler;

    private Properties props;

    /**
     * list from config file, NOT changed during runtime
     */
    private static ArrayList<URL> staticDoNotCheckCatalogsList = new ArrayList<>();

    private ArrayList<TaskElement> tasks = new ArrayList<>();

    @Inject
    public Timer(ICatalogStatusHandler handler, @Named(CLASSIFICATION_INIT_FILENAMES)
    String classificationFilenames, @Named(SLOT_INIT_FILENAME)
    String slotInitFile, @Named(DO_NOT_CHECK_CATALOGS)
    String doNotCheckCatalogs) {
        this.catalogStatusHandler = handler;

        // TODO create inner quartz timer
        // timer = new Timer(getServletName(),
        // Boolean.parseBoolean(getInitParameter(IS_DAEMON_INIT_PARAM_NAME)));

        // add classification init files
        String[] splitted = classificationFilenames.split(CONFIG_FILE_LIST_SEPARATOR);
        this.catalogInitClassificationFiles = new String[splitted.length];
        for (int i = 0; i < splitted.length; i++) {
            this.catalogInitClassificationFiles[i] = splitted[i].trim();
        }

        this.catalogSlotInitFile = slotInitFile;

        // check if given url does not need to be checked
        splitted = doNotCheckCatalogs.split(CONFIG_FILE_LIST_SEPARATOR);
        for (String s : splitted) {
            if ( !s.isEmpty()) {
                try {
                    staticDoNotCheckCatalogsList.add(new URL(s.trim()));
                }
                catch (MalformedURLException e) {
                    log.warn("Could not parse catalog url from 'do not check' list, was '{}'. Catalog will be checked during runtime!",
                             s.trim());
                }
            }
        }

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
    protected void finalize() throws Throwable {
        log.info("got called finalize()...");
    }

    public ICatalog getCatalog(ICatalogConnection conn) throws OwsExceptionReport {
        try {
            if ( !this.catalogCache.containsKey(conn.getCatalogURL().toURI())) {
                ICatalogFactory catFact = getCatalogFactory(conn.getCatalogURL());
                ICatalog catalog = catFact.getCatalog();
                this.catalogCache.put(conn.getCatalogURL().toURI(), catalog);
            }

            return this.catalogCache.get(conn.getCatalogURL().toURI());
        }
        catch (URISyntaxException e) {
            log.error("URI", e);
        }

        return null;
    }

    /**
     * 
     * @param catalogUrl
     * @return A new instance of the appropriate catalog factory for the given URL.
     */
    private ICatalogFactory getCatalogFactory(URL catalogUrl) throws OwsExceptionReport {
        try {
            ICatalogFactory newFactory = new CswFactory(catalogUrl,
                                                        this.catalogInitClassificationFiles,
                                                        this.catalogSlotInitFile,
                                                        staticDoNotCheckCatalogsList.contains(catalogUrl));
            return newFactory;
        }
        catch (XmlException | IOException e) {
            log.error("Could not create CswFactory.", e);
            throw new OwsExceptionReport("Error parsing document(s) to initialize a catalog factory.", e);
        }
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
        // timer.scheduleAtFixedRate(task, delay, period);
        log.debug("Submitted: {} under id {}, with period {} and delay {}", task, period, delay, identifier);
        // element is scheduled with repetition period, so save for later cancelling!
        this.tasks.add(new TaskElement(identifier, task, delay, period));
    }

    /**
     * 
     * @param task
     * @param date
     */
    public void submit(TimerTask task, Date date) {
        // timer.schedule(task, date);
        log.debug("Submitted: {} to run at {}", task, date);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Timer [");
        if (this.catalogCache != null) {
            builder.append("catalogCache=");
            builder.append(this.catalogCache);
            builder.append(", ");
        }
        if (this.catalogInitClassificationFiles != null) {
            builder.append("catalogInitClassificationFiles=");
            builder.append(Arrays.toString(this.catalogInitClassificationFiles));
            builder.append(", ");
        }
        if (this.catalogSlotInitFile != null) {
            builder.append("catalogSlotInitFile=");
            builder.append(this.catalogSlotInitFile);
            builder.append(", ");
        }
        if (this.catalogStatusHandler != null) {
            builder.append("catalogStatusHandler=");
            builder.append(this.catalogStatusHandler);
            builder.append(", ");
        }
        if (this.props != null) {
            builder.append("props=");
            builder.append(this.props);
            builder.append(", ");
        }
        if (this.tasks != null) {
            builder.append("tasks=");
            builder.append(this.tasks);
        }
        builder.append("]");
        return builder.toString();
    }

}
