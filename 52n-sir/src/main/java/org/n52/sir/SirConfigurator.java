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

package org.n52.sir;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.UnavailableException;

import org.n52.oss.sir.SirConfig;
import org.n52.sir.catalog.ICatalogConnection;
import org.n52.sir.catalog.ICatalogFactory;
import org.n52.sir.catalog.ICatalogStatusHandler;
import org.n52.sir.decode.IHttpGetRequestDecoder;
import org.n52.sir.decode.IHttpPostRequestDecoder;
import org.n52.sir.ds.IConnectToCatalogDAO;
import org.n52.sir.ds.IDAOFactory;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.ows.OwsExceptionReport.ExceptionLevel;
import org.n52.sir.util.jobs.IJobScheduler;
import org.n52.sir.util.jobs.IJobSchedulerFactory;
import org.n52.sir.util.jobs.impl.TimerServlet;
import org.n52.sir.xml.ITransformerFactory;
import org.n52.sir.xml.IValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sir.x032.CapabilitiesDocument;
import org.x52North.sir.x032.VersionAttribute;

import com.google.inject.Inject;

/**
 * Singleton class reads the config file and builds the RequestOperator and DAO
 * 
 * @author Jan Schulte
 * 
 */
// @Singleton
public class SirConfigurator implements SirConfig {

    /** Sections for the Capabilities */
    public enum Section {
        All, Contents, OperationsMetadata, ServiceIdentification, ServiceProvider
    }

    /**
     * 
     */
    private static final String ACCEPTED_SERVICE_VERSIONS = "ACCEPTED_SERVICE_VERSIONS";

    /**
     * propertyname of capabilities skeleton
     */
    private static final String CAPABILITIESSKELETON_FILENAME = "CAPABILITIESSKELETON_FILENAME";

    /**
     * propertyname of CATALOGFACTORY property
     */
    private static final String CATALOGFACTORY = "CATALOGFACTORY";

    /**
     * propertyname of character encoding
     */
    private static final String CHARACTER_ENCODING = "CHARACTERENCODING";

    /**
     * propertyname of CLASSIFICATION_INIT_FILENAME property
     */
    private static final String RIM_CLASSIFICATION_INIT_FILENAMES = "RIM_CLASSIFICATION_INIT_FILENAMES";

    /**
     * the separator for config properties with more than one value
     */
    private static final String CONFIG_FILE_LIST_SEPARATOR = ",";

    /**
     * propertyname of DAOFACTORY property
     */
    private static final String DAOFACTORY = "DAOFACTORY";

    /**
     * propertyname of UNCHECKED_CATALOGS property
     */
    private static final String DO_NOT_CHECK_CATALOGS = "DO_NOT_CHECK_CATALOGS";

    /**
     * 
     */
    private static final String EXTENDED_DEBUG_TO_CONSOLE = "EXTENDED_DEBUG_TO_CONSOLE";

    /*
     * 
     */
    private static final String FULL_SERVICE_PATH = "FULL_SERVICE_PATH";

    /**
     * propertyname of get request decoder
     */
    private static final String GETREQUESTDECODER = "GETREQUESTDECODER";

    /**
     * propertyname of GML date format
     */
    private static final String GMLDATEFORMAT = "GMLDATEFORMAT";

    /**
     * instance attribute, due to the singleton pattern
     */
    private static SirConfigurator instance = null;

    /**
     * propertyname of JOBSCHEDULERFACTORY property
     */
    private static final String JOBSCHEDULERFACTORY = "JOBSCHEDULERFACTORY";

    /**
     * logger
     */
    protected static Logger log = LoggerFactory.getLogger(SirConfigurator.class);

    /**
     * 
     */
    private static final String NAMESPACE_PREFIX = "NAMESPACE_PREFIX";

    /**
     * 
     */
    private static final String NAMESPACE_URI = "NAMESPACE_URI";

    /**
     * 
     */
    private static final String OPENSEARCH_ENDPOINT = "OPENSEARCH_ENDPOINT";

    /**
     * propertyname of post request decoder
     */
    private static final String POSTREQUESTDECODER = "POSTREQUESTDECODER";

    /**
     * propertyname of discovery profile file
     */
    private static final String PROFILE4DISCOVERY = "PROFILE4DISCOVERY";

    /**
     * propertyname of SCHEDULE_JOBS_ON_STARTUP property
     */
    private static final String SCHEDULE_JOBS_ON_STARTUP = "SCHEDULE_JOBS_ON_STARTUP";

    /**
     * 
     */
    private static final String SCHEMA_URL = "SCHEMA_URL";

    /**
     * 
     */
    private static final String SCHEMATRON_DOWNLOAD = "SCHEMATRON_DOWNLOAD";

    /**
     * propertyname of the SIR service version
     */
    private static final String SERVICEVERSION = "SERVICEVERSION";

    /**
     * propertyname of SLOT_INIT_FILENAME property
     */
    private static final String RIM_SLOT_INIT_FILENAME = "RIM_SLOT_INIT_FILENAME";

    /**
     * Delay (in seconds) for scheduling the first run of repeated catalog connections on startup.
     */
    private static final int STARTUP_CATALOG_CONNECTION_DELAY_SECS = 10;

    /**
     * propertyname of STATUS_HANDLER property
     */
    private static final String STATUS_HANDLER = "STATUS_HANDLER";

    /**
     * propertyname of discovery profile file
     */
    private static final String SVRL_SCHEMA = "SVRL_SCHEMA";

    /**
     * propertyname of test requests
     */
    private static final String TESTREQUESTS = "TESTREQUESTS";

    private static final int THREAD_POOL_SIZE = 10;

    /**
     * propertyname of TRANSFORMERFACTORY property
     */
    private static final String TRANSFORMERFACTORY = "TRANSFORMERFACTORY";

    /**
     * 
     */
    private static final String VALIDATE_XML_REQUESTS = "VALIDATE_XML_REQUESTS";

    /**
     * 
     */
    private static final String VALIDATE_XML_RESPONSES = "VALIDATE_XML_RESPONSES";

    /**
     * 
     */
    private static final String VALIDATORFACTORY = "VALIDATORFACTORY";

    /**
     * 
     */
    private static final String VERSION_SPLIT_CHARACTER = ",";

    private static final String XSLT_DIRECTORY = "XSLT_DIRECTORY";

    /**
     * @return Returns the instance of the SirConfigurator. Null will be returned if the parameterized
     *         getInstance method was not invoked before. Usuallex this will be done in the SIR
     */
    public static SirConfig getInstance() {
        return instance;
    }

    /**
     * @param configStream
     * @param dbConfigStream
     * @param basepath
     * @return Returns an instance of the SirConfigurator. This method is used to implement the singleton
     *         pattern
     * @throws UnavailableException
     *         if the configFile could not be loaded
     * @throws OwsExceptionReport
     */
    public synchronized static SirConfig getInstance(Properties props) throws UnavailableException, OwsExceptionReport {
        if (instance == null) {
            instance = new SirConfigurator(props);
            instance.initialize();
        }
        return instance;
    }

    private String[] acceptedVersions;

    /**
     * base path for configuration file
     */
    // private String basepath;

    /**
     * The skeleton of a standart capabilities response document
     */
    private CapabilitiesDocument capabilitiesSkeleton;

    /**
     * The constructor for the catalog factory to create catalogs for give URLs on demand
     */
    private Constructor<ICatalogFactory> catalogFactoryConstructor;

    /**
     * file names of catalog initialization files, i.e. files that need to be loaded into the catalog prior to
     * usage.
     */
    private String[] catalogInitClassificationFiles;

    /**
     * file name of a catalog initialization file
     */
    private String catalogInitSlotFile;

    /**
     * Implementation of ICatalogStatusHandler to allow other servlets to change stati of catalogs in the
     * database
     */
    private ICatalogStatusHandler catalogStatusHandler;

    /**
     * character encoding for responses
     */
    private String characterEncoding;

    /**
     * properties for DAO implementation
     */
    private Properties daoProps;

    /**
     * a list of catalogue-URLs that are not checked when data is pushed into them
     */
    private ArrayList<URL> doNotCheckCatalogsList;

    private ExecutorService exec;

    private boolean extendedDebugToConsole;

    /**
     * Implementation of the DAOFactory, used to build the DAOs for the request listeners
     */
    private IDAOFactory factory;

    private URL fullServicePath;

    /**
     * GML date format
     */
    private String gmlDateFormat;

    /**
     * decoder for decoding httpGet request
     */
    private IHttpGetRequestDecoder httpGetDecoder;

    /**
     * decoder for decoding httpPost request
     */
    private IHttpPostRequestDecoder httpPostDecoder;

    /**
     * Implementation of IJobSchedulerFactory to schedule (repeated) tasks
     */
    private IJobSchedulerFactory jobSchedulerFactory;

    private String namespacePrefix;

    private String namespaceUri;

    private String openSearchPath;

    private String profile4Discovery;

    /**
     * common SIR properties
     */
    private Properties props;

    private String schemaUrl;

    /**
     * the url of the service, e.g. needed by test client
     */
    private URL serviceUrl;

    private String serviceVersion;

    private String svrlSchema;

    private String testRequestPath;

    private TimerServlet timerServlet;

    private ITransformerFactory transformerFactory;

    private String updateSequence;

    private boolean validateRequests;

    private boolean validateResponses;

    private IValidatorFactory validatorFactory;

    @Inject
    private SirConfigurator(Properties props) {
        // this.basepath = basepath;
        // this.timerServlet = timerServlet;

        this.props = props;
        this.daoProps = props;

        log.info(" ***** Config Files loaded successfully! ***** ");
    }

    /**
     * 
     * @return
     * @throws OwsExceptionReport
     */
    // public RequestOperator buildRequestOperator() throws OwsExceptionReport {
    // // initialize RequestOperator
    // // RequestOperator ro = new RequestOperator();
    //
    // // loading names of listeners
    // ArrayList<String> listeners = loadListeners();
    //
    // Iterator<String> iter = listeners.iterator();
    //
    // // initialize listeners and add them to the RequestOperator
    // while (iter.hasNext()) {
    //
    // // classname of the listener
    // String classname = iter.next();
    //
    // try {
    // // get Class of the Listener
    // @SuppressWarnings("unchecked")
    // Class<ISirRequestListener> listenerClass = (Class<ISirRequestListener>) Class.forName(classname);
    //
    // Class< ? >[] constrArgs = {};
    //
    // Object[] args = {};
    //
    // // get Constructor of this class with matching parameter types,
    // // throws a NoSuchMethodException
    // Constructor<ISirRequestListener> constructor = listenerClass.getConstructor(constrArgs);
    //
    // // add new requestListener to RequestOperator throws a
    // // Instantiation, IllegalAccess and InvocationTargetException
    // ro.addRequestListener(constructor.newInstance(args));
    //
    // }
    // catch (ClassNotFoundException cnfe) {
    // log.error("Error while loading RequestListeners, required class could not be loaded: "
    // + cnfe.toString());
    // throw new OwsExceptionReport(cnfe.getMessage(), cnfe.getCause());
    // }
    // catch (NoSuchMethodException nsme) {
    // log.error("Error while loading RequestListeners," + " no required constructor available: "
    // + nsme.toString());
    // throw new OwsExceptionReport(nsme.getMessage(), nsme.getCause());
    // }
    // catch (InvocationTargetException ite) {
    // log.error("The instatiation of a RequestListener failed: " + ite.toString());
    // throw new OwsExceptionReport(ite.getMessage(), ite.getCause());
    // }
    // catch (InstantiationException ie) {
    // log.error("The instatiation of a RequestListener failed: " + ie.toString());
    // throw new OwsExceptionReport(ie.getMessage(), ie.getCause());
    // }
    // catch (IllegalAccessException iace) {
    // log.error("The instatiation of a RequestListener failed: " + iace.toString());
    // throw new OwsExceptionReport(iace.getMessage(), iace.getCause());
    // }
    // }
    //
    // return ro;
    // }

    private void checkFile(String path) {
        File f = new File(path);
        if ( !f.exists())
            log.error("Cannot find file " + path);

        f = null;
    }

    private void checkResource(String resourcePath) {
        try (InputStream resource = SirConfigurator.class.getResourceAsStream(resourcePath)) {

            if (resource != null) {
                try {
                    int i = resource.read();
                    if (i == -1)
                        log.error("Resource is empty.");
                }
                catch (IOException e) {
                    log.error("Cannot read resource " + resourcePath);
                }
            }
            else
                log.error("Cannot find resource " + resourcePath);
        }
        catch (Exception e) {
            log.error("Error checking resource", e);
        }

    }

    @Override
    public String[] getAcceptedServiceVersions() {
        return this.acceptedVersions;
    }

    @Override
    public CapabilitiesDocument getCapabilitiesSkeleton() {
        return this.capabilitiesSkeleton;
    }

    @Override
    public ICatalogFactory getCatalogFactory(URL url) throws OwsExceptionReport {
        try {
            Object onNotCheckList = Boolean.valueOf(this.doNotCheckCatalogsList.contains(url));
            ICatalogFactory cf = this.catalogFactoryConstructor.newInstance(url,
                                                                            this.catalogInitClassificationFiles,
                                                                            this.catalogInitSlotFile,
                                                                            onNotCheckList);
            return cf;
        }
        catch (Exception e) {
            log.error("The instatiation of a catalog factory failed." + e.toString());
            throw new OwsExceptionReport("The instatiation of a catalog factory failed: " + e.getMessage(),
                                         e.getCause());
        }
    }

    @Override
    public ICatalogStatusHandler getCatalogStatusHandler() {
        return this.catalogStatusHandler;
    }

    @Override
    public String getCharacterEncoding() {
        return this.characterEncoding;
    }

    @Override
    public ExecutorService getExecutor() {
        return this.exec;
    }

    @Override
    public IDAOFactory getFactory() {
        return this.factory;
    }

    @Override
    public URL getFullServicePath() {
        return this.fullServicePath;
    }

    @Override
    public String getGmlDateFormat() {
        return this.gmlDateFormat;
    }

    @Override
    public IHttpGetRequestDecoder getHttpGetDecoder() {
        return this.httpGetDecoder;
    }

    @Override
    public IHttpPostRequestDecoder getHttpPostDecoder() {
        return this.httpPostDecoder;
    }

    @Override
    public IJobSchedulerFactory getJobSchedulerFactory() {
        return this.jobSchedulerFactory;
    }

    @Override
    public String getNamespacePrefix() {
        return this.namespacePrefix;
    }

    @Override
    public String getNamespaceUri() {
        return this.namespaceUri;
    }

    @Override
    public String getOpenSearchPath() {
        return this.openSearchPath;
    }

    @Override
    public String getProfile4Discovery() {
        return this.profile4Discovery;
    }

    @Override
    public String getProfile4DiscoveryDownloadPath() {
        return this.props.getProperty(PROFILE4DISCOVERY);
    }

    @Override
    public String getSchemaDownloadLink() {
        return this.props.getProperty(SCHEMATRON_DOWNLOAD);
    }

    @Override
    public String getSchemaUrl() {
        return this.schemaUrl;
    }

    @Override
    public URL getServiceUrl() {
        return this.serviceUrl;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.SirConfig#getServiceVersion()
     */
    @Override
    public String getServiceVersion() {
        return this.serviceVersion;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.SirConfig#getServiceVersionEnum()
     */
    @Override
    public org.x52North.sir.x032.VersionAttribute.Version.Enum getServiceVersionEnum() {
        String sv = getServiceVersion();

        if (sv.equals(SirConstants.SERVICE_VERSION_0_3_0))
            return VersionAttribute.Version.X_0_3_0;

        if (sv.equals(SirConstants.SERVICE_VERSION_0_3_1))
            return VersionAttribute.Version.X_0_3_1;

        throw new RuntimeException("Not a supported version!");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.SirConfig#getSvrlSchema()
     */
    @Override
    public String getSvrlSchema() {
        return this.svrlSchema;
    }

    @Override
    public String getTestRequestPath() {
        return this.testRequestPath;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.SirConfig#getTransformerFactory()
     */
    @Override
    public ITransformerFactory getTransformerFactory() {
        return this.transformerFactory;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.SirConfig#getUpdateSequence()
     */
    @Override
    public String getUpdateSequence() {
        return this.updateSequence;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.SirConfig#getValidatorFactory()
     */
    @Override
    public IValidatorFactory getValidatorFactory() {
        return this.validatorFactory;
    }

    private void initialize() throws OwsExceptionReport {
        log.info(" * Initializing SirConfigurator ... ");

        // to be used by listeners, saved here to allow shutdown.
        this.exec = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        String characterEncodingString = this.props.getProperty(CHARACTER_ENCODING);
        if (characterEncodingString == null) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 null,
                                 "No characterEncoding is defined in the config file!");
            log.error("No characterEncoding is defined in the config file!");
            throw se;
        }

        this.characterEncoding = characterEncodingString;
        this.serviceVersion = this.props.getProperty(SERVICEVERSION);
        this.gmlDateFormat = this.props.getProperty(GMLDATEFORMAT);
        this.schemaUrl = this.props.getProperty(SCHEMA_URL);
        this.namespaceUri = this.props.getProperty(NAMESPACE_URI);
        this.namespacePrefix = this.props.getProperty(NAMESPACE_PREFIX);
        this.testRequestPath = this.props.getProperty(TESTREQUESTS);

        this.extendedDebugToConsole = Boolean.parseBoolean(this.props.getProperty(EXTENDED_DEBUG_TO_CONSOLE));
        this.acceptedVersions = this.props.getProperty(ACCEPTED_SERVICE_VERSIONS).split(VERSION_SPLIT_CHARACTER);
        this.validateRequests = Boolean.parseBoolean(this.props.getProperty(VALIDATE_XML_REQUESTS));
        this.validateResponses = Boolean.parseBoolean(this.props.getProperty(VALIDATE_XML_RESPONSES));

        String resourceName = this.props.getProperty(PROFILE4DISCOVERY);
        URL location = this.getClass().getResource(resourceName);
        if (location == null) {
            log.error("Could not get resource using class loader!");
            throw new OwsExceptionReport(ExceptionCode.NoApplicableCode,
                                         "root",
                                         "Could not get resource using class loader: " + resourceName);
        }

        this.profile4Discovery = location.getPath();
        checkFile(this.profile4Discovery);

        resourceName = this.props.getProperty(SVRL_SCHEMA);
        location = this.getClass().getResource(resourceName);
        if (location == null) {
            log.error("Could not get resource using class loader!");
            throw new OwsExceptionReport(ExceptionCode.NoApplicableCode,
                                         "root",
                                         "Could not get resource using class loader: " + resourceName);
        }

        this.svrlSchema = location.getPath();
        checkFile(this.svrlSchema);

        String url = null;
        try {
            url = this.props.getProperty(SERVICEURL);
            this.serviceUrl = new URL(url);
            log.debug("Using service URL {}", this.serviceUrl);
        }
        catch (MalformedURLException e) {
            log.error("No valid service url is defined in the config file: {}", url);
        }

        url = null;
        try {
            url = this.props.getProperty(FULL_SERVICE_PATH);
            this.fullServicePath = new URL(url);
        }
        catch (MalformedURLException e) {
            log.error("No valid service path is defined in the config file: {}", url);
        }

        this.openSearchPath = this.props.getProperty(OPENSEARCH_ENDPOINT);

        // set updateSequence
        newUpdateSequence();

        // initialize DAO Factory
        initializeDAOFactory();

        // initialize CatalogFactory
        initializeCatalogFactory();

        // initialize decoders
        initializeHttpGetRequestDecoder(this.props);
        initializeHttpPostRequestDecoder(this.props);

        loadCapabilitiesSkeleton(this.props);

        // initialize status handler
        initializeStatusHandler(this.props);

        // initialize job scheduler and start the connections from database
        initializeJobScheduling(this.props, this.timerServlet);
        if (Boolean.parseBoolean(this.props.getProperty(SCHEDULE_JOBS_ON_STARTUP)))
            startCatalogConnections();

        // initialize transformer
        initializeTransformerFactory(this.props);

        // initialize validator
        initializeValidatorFactory(this.props);

        log.info(" ***** Initialized SirConfigurator successfully! ***** ");
    }

    private void initializeCatalogFactory() throws OwsExceptionReport {
        String className = this.props.getProperty(CATALOGFACTORY);

        this.catalogInitSlotFile = this.props.getProperty(RIM_SLOT_INIT_FILENAME);

        // add classification init files
        String[] splitted = this.props.getProperty(RIM_CLASSIFICATION_INIT_FILENAMES).split(SirConfig.CONFIG_FILE_LIST_SEPARATOR);
        this.catalogInitClassificationFiles = new String[splitted.length];
        for (int i = 0; i < splitted.length; i++) {
            this.catalogInitClassificationFiles[i] = splitted[i].trim();
        }

        // check if given url does not need to be checked
        this.doNotCheckCatalogsList = new ArrayList<>();
        splitted = getCatalogsUnchecked();
        if (splitted.length > 0) {
            for (String s : splitted) {
                try {
                    if ( !s.isEmpty())
                        this.doNotCheckCatalogsList.add(new URL(s.trim()));
                }
                catch (MalformedURLException e) {
                    log.error("Could not parse catalog url to 'do not check' list. Catalog will be checked during runtime!");
                }
            }
        }
        else {
            if (log.isDebugEnabled())
                log.debug("Property " + DO_NOT_CHECK_CATALOGS + " returned no string list.");
        }

        try {

            if (className == null) {
                log.error("No catalog factory implementation is set in the config file! Use " + CATALOGFACTORY);
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.NoApplicableCode,
                                     "SirConfigurator.initializeCatalogFactory()",
                                     "No catalog factory Implementation is set in the config file!");
                throw se;
            }
            // get Class of the catalog factory implementation
            Class<ICatalogFactory> catalogFactoryClass = (Class<ICatalogFactory>) Class.forName(className);

            // get constructor of this class with matching parameter types
            this.catalogFactoryConstructor = catalogFactoryClass.getConstructor(URL.class,
                                                                                String[].class,
                                                                                String.class,
                                                                                Boolean.class);

            log.info(" ***** " + className + " loaded successfully! ***** ");
        }
        catch (ClassNotFoundException cnfe) {
            log.error("Error while loading catalog factory, required class could not be loaded: " + cnfe.toString());
            throw new OwsExceptionReport(cnfe.getMessage(), cnfe.getCause());
        }
        catch (NoSuchMethodException nsme) {
            log.error("Error while loading catalog factory, no required constructor available: " + nsme.toString());
            throw new OwsExceptionReport(nsme.getMessage(), nsme.getCause());
        }
    }

    private void initializeDAOFactory() throws OwsExceptionReport {
        try {
            String daoName = this.props.getProperty(DAOFACTORY);

            if (daoName == null) {
                log.error("No DAOFactory Implementation is set in the config file!");
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.NoApplicableCode,
                                     "SosConfigurator.initializeDAOFactory()",
                                     "No DAOFactory Implementation is set in the configFile!");
                throw se;
            }

            // get Class of the DAOFactory Implementation
            Class<IDAOFactory> daoFactoryClass = (Class<IDAOFactory>) Class.forName(daoName);

            // types of the constructor arguments
            // Class< ? >[] constrArgs = {Properties.class};
            // Object[] args = {daoPropsP};

            // get Constructor of this class with matching parameter types
            // Constructor<IDAOFactory> constructor = daoFactoryClass.getConstructor(constrArgs);
            Constructor<IDAOFactory> constructor = daoFactoryClass.getConstructor();

            // this.factory = constructor.newInstance(args);
            this.factory = constructor.newInstance();

            log.info(" ***** " + daoName + " loaded successfully! ***** ");

        }
        catch (ClassNotFoundException | SecurityException | NoSuchMethodException | IllegalArgumentException
                | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            log.error("Error while loading DaoFactory, required class could not be loaded", e);
        }
    }

    private void initializeHttpGetRequestDecoder(Properties sirProps) throws OwsExceptionReport {
        String className = sirProps.getProperty(GETREQUESTDECODER);
        try {

            if (className == null) {
                log.error("No getRequestDecoder Implementation is set in the config file!");
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.NoApplicableCode,
                                     "SirConfigurator.initializeHttpGetRequestDecoder()",
                                     "No getRequestDecoder Implementation is set in the config file!");
                throw se;
            }
            // get Class of the httpGetRequestDecoderClass Implementation
            Class<IHttpGetRequestDecoder> httpGetRequestDecoderClass = (Class<IHttpGetRequestDecoder>) Class.forName(className);

            // get Constructor of this class with matching parameter types
            Constructor<IHttpGetRequestDecoder> constructor = httpGetRequestDecoderClass.getConstructor();

            this.httpGetDecoder = constructor.newInstance();

            log.info(" ***** " + className + " loaded successfully! ***** ");

        }
        catch (ClassNotFoundException | SecurityException | NoSuchMethodException | IllegalArgumentException
                | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            log.error("Instatiation of an getRequestDecoder failed", e);
        }
    }

    private void initializeHttpPostRequestDecoder(Properties sirProps) throws OwsExceptionReport {
        String className = sirProps.getProperty(POSTREQUESTDECODER);
        try {

            if (className == null) {
                log.error("No postRequestDecoder Implementation is set in the config file!");
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.NoApplicableCode,
                                     "SirConfigurator.initializeHttpPostRequestDecoder()",
                                     "No postRequestDecoder Implementation is set in the config file!");
                throw se;
            }
            // get Class of the httpGetRequestDecoderClass Implementation
            Class<IHttpPostRequestDecoder> httpPostRequestDecoderClass = (Class<IHttpPostRequestDecoder>) Class.forName(className);

            // get Constructor of this class with matching parameter types
            Constructor<IHttpPostRequestDecoder> constructor = httpPostRequestDecoderClass.getConstructor();

            this.httpPostDecoder = constructor.newInstance();

            log.info(" ***** " + className + " loaded successfully! ***** ");

        }
        catch (ClassNotFoundException | SecurityException | NoSuchMethodException | IllegalArgumentException
                | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            log.error("Error while loading POST request decoder, required class could not be loaded", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void initializeJobScheduling(Properties p, TimerServlet timer) throws OwsExceptionReport {
        String className = p.getProperty(JOBSCHEDULERFACTORY);
        try {
            if (className == null) {
                log.error("No job scheduler factory implementation is set in the config file!");
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.NoApplicableCode,
                                     "SirConfigurator.initializeJobScheduling()",
                                     "No job scheduling implementation is set in the config file!");
                throw se;
            }
            // get Class of the httpGetRequestDecoderClass Implementation
            Class<IJobSchedulerFactory> jobSchedulerFactoryClass = (Class<IJobSchedulerFactory>) Class.forName(className);

            // get Constructor of this class with matching parameter types
            // Constructor<IJobSchedulerFactory> constructor =
            // jobSchedulerFactoryClass.getConstructor(TimerServlet.class);
            // this.jobSchedulerFactory = constructor.newInstance(timer);
            Constructor<IJobSchedulerFactory> constructor = jobSchedulerFactoryClass.getConstructor();
            this.jobSchedulerFactory = constructor.newInstance();

            log.info(" ***** " + className + " loaded successfully! ***** ");
        }
        catch (ClassNotFoundException | SecurityException | NoSuchMethodException | IllegalArgumentException
                | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            log.error("Error while loading job scheduler", e);
        }
    }

    private void initializeStatusHandler(Properties sirProps) throws OwsExceptionReport {
        String className = sirProps.getProperty(STATUS_HANDLER);
        try {
            if (className == null) {
                log.error("No status handler implementation is set in the config file!");
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.NoApplicableCode,
                                     "SirConfigurator.initializeStatusHandler()",
                                     "No status handler implementation is set in the config file!");
                throw se;
            }
            // get Class of the httpGetRequestDecoderClass Implementation
            Class<ICatalogStatusHandler> CatalogStatusHandlerClass = (Class<ICatalogStatusHandler>) Class.forName(className);

            // get Constructor of this class with matching parameter types
            Constructor<ICatalogStatusHandler> constructor = CatalogStatusHandlerClass.getConstructor();

            this.catalogStatusHandler = constructor.newInstance();

            log.info(" ***** " + className + " loaded successfully! ***** ");
        }
        catch (ClassNotFoundException | SecurityException | NoSuchMethodException | IllegalArgumentException
                | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            log.error("Error while loading catalog status handler, required class could not be loaded", e);
        }
    }

    private void initializeTransformerFactory(Properties p) throws OwsExceptionReport {
        String xsltDir = p.getProperty(XSLT_DIRECTORY);
        String className = p.getProperty(TRANSFORMERFACTORY);
        try {
            if (className == null) {
                log.error("No transformer factory implementation is set in the config file!");
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.NoApplicableCode,
                                     "SirConfigurator.initializeHttpPostRequestDecoder()",
                                     "No postRequestDecoder Implementation is set in the config file!");
                throw se;
            }
            // get Class of the httpGetRequestDecoderClass Implementation
            Class<ITransformerFactory> transformerFactoryClass = (Class<ITransformerFactory>) Class.forName(className);

            // get Constructor of this class with matching parameter types
            Constructor<ITransformerFactory> constructor = transformerFactoryClass.getConstructor(String.class);

            this.transformerFactory = constructor.newInstance(xsltDir);

            log.info(" ***** " + className + " loaded successfully! Using files from folder " + xsltDir + ". ***** ");
        }
        catch (ClassNotFoundException | SecurityException | NoSuchMethodException | IllegalArgumentException
                | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            log.error("Error while loading transformer factory, required class could not be loaded", e);
        }
    }

    private void initializeValidatorFactory(Properties p) throws OwsExceptionReport {
        String className = p.getProperty(VALIDATORFACTORY);
        try {
            if (className == null) {
                log.error("No validator factory implementation is set in the config file!");
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.NoApplicableCode,
                                     "SirConfigurator.initializeHttpPostRequestDecoder()",
                                     "No postRequestDecoder Implementation is set in the config file!");
                throw se;
            }
            // get Class of the httpGetRequestDecoderClass Implementation
            Class<IValidatorFactory> validatorFactoryClass = (Class<IValidatorFactory>) Class.forName(className);

            // get Constructor of this class with matching parameter types
            Constructor<IValidatorFactory> constructor = validatorFactoryClass.getConstructor();

            this.validatorFactory = constructor.newInstance();

            log.info(" ***** " + className + " loaded successfully! ***** ");
        }
        catch (ClassNotFoundException | SecurityException | NoSuchMethodException | IllegalArgumentException
                | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            log.error("Error while loading validator factory", e);
        }
    }

    @Override
    public boolean isExtendedDebugToConsole() {
        return this.extendedDebugToConsole;
    }

    @Override
    public boolean isValidateRequests() {
        return this.validateRequests;
    }

    @Override
    public boolean isValidateResponses() {
        return this.validateResponses;
    }

    private void loadCapabilitiesSkeleton(Properties sirProps) {
        String skeletonPath = sirProps.getProperty(CAPABILITIESSKELETON_FILENAME);
        try (InputStream resource = SirConfigurator.class.getResourceAsStream(skeletonPath)) {
            log.info("Loading capabilities skeleton from " + skeletonPath);
            this.capabilitiesSkeleton = CapabilitiesDocument.Factory.parse(resource);
        }
        catch (Exception e) {
            log.error("Error on loading capabilities skeleton file: " + e.getMessage());
        }
    }

    private ArrayList<String> loadListeners() {
        ArrayList<String> listeners = new ArrayList<>();
        String listenersList = this.props.getProperty(LISTENERS);

        if (listenersList == null) {
            log.error("No RequestListeners are defined in the ConfigFile, property {}", LISTENERS);
        }
        StringTokenizer tokenizer = new StringTokenizer(listenersList, ",");
        while (tokenizer.hasMoreTokens()) {
            listeners.add(tokenizer.nextToken());
        }
        return listeners;
    }

    @Override
    public void newUpdateSequence() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(this.gmlDateFormat);
        this.updateSequence = dateFormat.format(new Date());
    }

    /**
     * Uses a thread for a delayed execution. This is necessary if both the catalog and the SIR run in the
     * same container. The update can be blocked if the {@link ICatalogStatusHandler} is not available in the
     * context.
     */
    private void startCatalogConnections() {
        if (log.isDebugEnabled())
            log.debug(" ***** Starting Thread for catalog connections with a delay of "
                    + STARTUP_CATALOG_CONNECTION_DELAY_SECS + " seconds ***** ");

        this.exec.submit(new Thread("CatalogConnector") {

            @Override
            public void run() {
                // wait with the catalog connection, because if the catalog runs
                // in the same tomcat, problems
                // might occur during startup phase

                try {
                    sleep(STARTUP_CATALOG_CONNECTION_DELAY_SECS * 1000);
                }
                catch (InterruptedException e1) {
                    log.error("Error sleeping before catalog connections.", e1);
                }

                log.info(" ***** Starting catalog connections ***** ");

                // run tasks for existing catalogs
                int i = 0;
                try {
                    IDAOFactory f = getFactory();
                    IConnectToCatalogDAO catalogDao = f.connectToCatalogDAO();
                    List<ICatalogConnection> savedConnections = catalogDao.getCatalogConnectionList();

                    IJobScheduler scheduler = SirConfigurator.getInstance().getJobSchedulerFactory().getJobScheduler();
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
                    log.error("Could not run tasks for saved catalog connections.", e);
                }

                log.info(" ***** Scheduled " + i + " task(s) from the database. ***** ");
            }

        });
    }

    @Override
    public Properties getDaoProps() {
        return this.daoProps;
    }

    @Override
    public String[] getClassificationInitFileNames() {
        String files = this.props.getProperty(RIM_CLASSIFICATION_INIT_FILENAMES);
        return files.split(CONFIG_FILE_LIST_SEPARATOR);
    }

    @Override
    public String getCatalogSlotInitFile() {
        // maybe must be basepath + configDirectory + this.props.getProperty(RIM_SLOT_INIT_FILENAME);
        return this.catalogInitSlotFile;
    }

    @Override
    public String[] getCatalogsUnchecked() {
        String cats = this.props.getProperty(DO_NOT_CHECK_CATALOGS);
        return cats.split(",");
    }

}