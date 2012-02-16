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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.GenericServlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.UnavailableException;

import org.apache.xmlbeans.XmlException;
import org.n52.sir.catalog.ICatalog;
import org.n52.sir.catalog.ICatalogConnection;
import org.n52.sir.catalog.ICatalogFactory;
import org.n52.sir.catalog.ICatalogStatusHandler;
import org.n52.sir.catalog.csw.CswFactory;
import org.n52.sir.ows.OwsExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class TimerServlet extends GenericServlet {

    /**
     * propertyname of CONFIG_DIRECTORY property
     */
    private final String CONFIG_DIRECTORY = "CONFIG_DIRECTORY";

    /**
     * The init parameter of the configFile
     */
    private static final String INIT_PARAM_CONFIG_FILE = "configFile";

    /**
     * propertyname of CLASSIFICATION_INIT_FILENAME property
     */
    private static final String CLASSIFICATION_INIT_FILENAMES = "CLASSIFICATION_INIT_FILENAMES";

    /**
     * propertyname of SLOT_INIT_FILENAME property
     */
    private static final String SLOT_INIT_FILENAME = "SLOT_INIT_FILENAME";

    /**
     * 
     */
    private static final long serialVersionUID = 4704774153636727580L;

    /**
     * The identifier that can be used to access the instance of this servlet an run-time.
     */
    public static final String NAME_IN_CONTEXT = "TimerServlet";

    /**
     * 
     */
    private static final String IS_DAEMON_INIT_PARAM_NAME = "isDaemon";

    /**
     * 
     */
    private static final String DO_NOT_CHECK_CATALOGS = "DO_NOT_CHECK_CATALOGS";

    private static final String CONFIG_FILE_LIST_SEPARATOR = ",";

    private static Logger log = LoggerFactory.getLogger(TimerServlet.class);

    /**
     * Inner {@link Timer} that might run as a daemon according to the init parameter
     * {@link TimerServlet#IS_DAEMON_INIT_PARAM_NAME} in the servlet defintion (web.xml).
     */
    private static Timer timer;

    /**
     * List that holds all repeated task during run-time.
     */
    private ArrayList<TaskElement> tasks;

    private ICatalogStatusHandler catalogStatusHandler;

    private String catalogSlotInitFile;

    private Properties props;

    /**
     * list from config file, NOT changed during runtime
     */
    private ArrayList<URL> staticDoNotCheckCatalogsList;

    private String[] catalogInitClassificationFiles;

    private Map<URI, ICatalog> catalogCache = new HashMap<URI, ICatalog>();

    /**
     * Default constructor.
     */
    public TimerServlet() {
        super();
    }

    @Override
    public void init() throws ServletException {
        super.init();
        log.info(" * Initializing Timer ... ");

        ServletContext context = getServletContext();
        context.setAttribute(NAME_IN_CONTEXT, this);

        this.tasks = new ArrayList<TaskElement>();

        // create inner Timer
        timer = new Timer(getServletName(), Boolean.parseBoolean(getInitParameter(IS_DAEMON_INIT_PARAM_NAME)));

        // get configFile as Inputstream
        InputStream configStream = context.getResourceAsStream(getInitParameter(INIT_PARAM_CONFIG_FILE));
        if (configStream == null) {
            log.error("Could not opoen the config file!");
            throw new UnavailableException("Could not open the config file.");
        }

        // load properties file
        try {
            this.props = loadProperties(configStream);
        }
        catch (IOException e) {
            log.error("Could not load properties file!");
            throw new UnavailableException("Could not load properties file!");
        }

        String basepath = context.getRealPath("/");
        String configDirectory = this.props.getProperty(this.CONFIG_DIRECTORY);

        // add classification init files
        String[] splitted = this.props.getProperty(CLASSIFICATION_INIT_FILENAMES).split(CONFIG_FILE_LIST_SEPARATOR);
        this.catalogInitClassificationFiles = new String[splitted.length];
        for (int i = 0; i < splitted.length; i++) {
            this.catalogInitClassificationFiles[i] = basepath + configDirectory + splitted[i].trim();
        }

        this.catalogSlotInitFile = basepath + configDirectory + this.props.getProperty(SLOT_INIT_FILENAME);

        // check if given url does not need to be checked
        this.staticDoNotCheckCatalogsList = new ArrayList<URL>();
        splitted = this.props.getProperty(DO_NOT_CHECK_CATALOGS).split(",");
        for (String s : splitted) {
            if ( !s.isEmpty()) {
                try {
                    this.staticDoNotCheckCatalogsList.add(new URL(s.trim()));
                }
                catch (MalformedURLException e) {
                    log.warn("Could not parse catalog url from 'do not check' list, was '" + s
                            + "'. Catalog will be checked during runtime!");
                }
            }
        }

        log.info(" ***** Timer initiated successfully! ***** ");
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
            else {
                if (log.isDebugEnabled())
                    log.debug("Got catalog status handler from servlet context: " + this.catalogStatusHandler);
            }

        }
        return this.catalogStatusHandler;
    }

    /**
     * 
     * returns new catalog factory
     * 
     * @param catalogUrl
     * @return A new instance of the appropriate catalog factory for the given URL.
     * @throws OwsExceptionReport
     *         If there is a problem reading or parsing the init files for the catalog factory.
     */
    private ICatalogFactory getCatalogFactory(URL catalogUrl) throws OwsExceptionReport {
        try {
            ICatalogFactory newFactory = new CswFactory(catalogUrl,
                                                        this.catalogInitClassificationFiles,
                                                        this.catalogSlotInitFile,
                                                        Boolean.valueOf(this.staticDoNotCheckCatalogsList.contains(catalogUrl)));
            return newFactory;
        }
        catch (XmlException xe) {
            throw new OwsExceptionReport("Error parsing document(s) to initialize a catalog factory.", xe);
        }
        catch (IOException ioe) {
            throw new OwsExceptionReport("Error reading document(s) to initialize a catalog factory.", ioe);
        }
    }

    /**
     * 
     * @param conn
     * @return
     * @throws OwsExceptionReport
     */
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
     * @param task
     * @param date
     */
    public void submit(TimerTask task, Date date) {
        timer.schedule(task, date);
        if (log.isDebugEnabled()) {
            log.debug("Submitted: " + task + " to run at " + date);
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
        timer.scheduleAtFixedRate(task, delay, period);
        if (log.isDebugEnabled()) {
            log.debug("Submitted: " + task + " with period = " + period + ", delay = " + delay);
        }
        // element is scheduled with repetition period, save for later cancelling!
        this.tasks.add(new TaskElement(identifier, task, delay, period));
    }

    /**
     * 
     * @param identifier
     */
    public void cancel(String identifier) {
        for (TaskElement te : this.tasks) {
            if (te.id.equals(identifier)) {
                te.task.cancel();
                log.info("CANCELED " + te);
            }

        }
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.GenericServlet#service(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
     */
    @Override
    public void service(ServletRequest req, ServletResponse res) {
        throw new UnsupportedOperationException("Not supperted by TimerServlet!");
    }
    
    /*
     * (non-Javadoc)
     * @see javax.servlet.GenericServlet#destroy()
     */
    @Override
    public void destroy() {
        log.info("called destroy()...");
        super.destroy();
        timer.cancel();
        timer = null;
        
        log.info("destroyed.");
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
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

    /**
     * method loads the config file
     * 
     * @param is
     *        InputStream containing the config file
     * @return Returns properties of the given config file
     * @throws IOException
     */
    private Properties loadProperties(InputStream is) throws IOException {
        Properties properties = new Properties();
        properties.load(is);

        return properties;
    }

    /**
     * Inner class to handle storage and cancelling of tasks at runtime.
     */
    private static class TaskElement {
        protected TimerTask task;
        protected long delay;
        protected long period;
        protected Date date;
        protected String id;

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
}