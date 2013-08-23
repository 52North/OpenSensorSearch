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
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * Singleton class reads the config file and builds the RequestOperator and DAO
 * 
 * @author Jan Schulte
 * 
 */
@Singleton
public class SirConfigurator {

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
    private static final String CLASSIFICATION_INIT_FILENAMES = "CLASSIFICATION_INIT_FILENAMES";

    /**
     * propertyname of CONFIG_DIRECTORY property
     */
    private static final String CONFIG_DIRECTORY = "CONFIG_DIRECTORY";

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
     * propertyname of listeners
     */
    private static final String LISTENERS = "LISTENERS";

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
     * propertyname of service url
     */
    private static final String SERVICEURL = "oss.sir.serviceurl";

    /**
     * propertyname of the SIR service version
     */
    private static final String SERVICEVERSION = "SERVICEVERSION";

    /**
     * propertyname of SLOT_INIT_FILENAME property
     */
    private static final String SLOT_INIT_FILENAME = "SLOT_INIT_FILENAME";

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

    /**
     * propertyname of XSLT_DIR property
     */
    private static final String XSTL_DIRECTORY = "XSTL_DIRECTORY";

    private static final String SCRIPTS_PATH = "SCRIPTS_PATH";

    /**
     * @deprecated use injection instead
     * @return Returns the instance of the SirConfigurator. Null will be returned if the parameterized
     *         getInstance method was not invoked before. Usuallex this will be done in the SIR
     */
    @Deprecated
    public static SirConfigurator getInstance() {
        return instance;
    }

    private String[] acceptedVersions;

    /**
     * base path for configuration file
     */
    private String basepath;

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
     * 
     */
    private String configDirectory;

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

    /**
     * Service version of the SIR
     */
    private String serviceVersion;

    private String svrlSchema;

    private String testRequestPath;

    /**
     * servlet for scheduling tasks
     */
    private TimerServlet timerServlet;

    /**
     * Implementation of the ITransformerFactory, used to access transformers for XML documents
     */
    private ITransformerFactory transformerFactory;

    private String ScriptsPath;

    /**
     * update sequence
     */
    private String updateSequence;

    private boolean validateRequests;

    private boolean validateResponses;

    private IValidatorFactory validatorFactory;

    private boolean startCatalogConnectionsOnStartup = false;

    /**
     * public constructor for transition to dependency injected properties.
     * 
     * TODO Daniel: remove this after new configuration mechanism is in place.
     * 
     * @throws UnavailableException
     * @throws OwsExceptionReport
     * @throws IOException
     */
    @Inject
    public SirConfigurator(@Named("context.basepath")
    String basepath) throws UnavailableException, OwsExceptionReport, IOException {
        try (InputStream dbStream = SirConfigurator.class.getResourceAsStream("/prop/db.properties");
                InputStream configStream = SirConfigurator.class.getResourceAsStream("/prop/sir.properties");) {

            if (instance == null) {
                instance = new SirConfigurator(configStream, dbStream, basepath, null);
                instance.initialize();
            }
            else
                log.error("SHOULD BE SINGLETON");
        }
        catch (Exception e) {
            log.error("could not init SirConfigurator with properties files.", e);
        }

        log.info("NEW {}", this);
    }

    /**
     * private constructor due to the singleton pattern.
     * 
     * @param configStream
     *        Inputstream of the configfile
     * @param dbConfigStream
     *        Inputstream of the db configfile
     * @param basepath
     *        base path for configuration files
     * @param xsltDir
     * 
     */
    private SirConfigurator(InputStream configStream,
                            InputStream dbConfigStream,
                            String basepath,
                            TimerServlet timerServlet) throws UnavailableException {
        try {
            this.basepath = basepath;
            this.timerServlet = timerServlet;

            // creating common SIR properties object from inputstream
            this.props = loadProperties(configStream);
            this.daoProps = loadProperties(dbConfigStream);

            log.info(" ***** Config Files loaded successfully! ***** ");
        }
        catch (IOException ioe) {
            log.error("Error while loading config file.", ioe);
            throw new UnavailableException(ioe.getMessage());
        }
    }

    private void checkFile(String path) {
        File f = new File(path);
        if ( !f.exists())
            log.error("Cannot find file " + path);

        f = null;
    }

    public String[] getAcceptedServiceVersions() {
        return this.acceptedVersions;
    }

    public CapabilitiesDocument getCapabilitiesSkeleton() {
        return this.capabilitiesSkeleton;
    }

    /**
     * 
     * Creates an CatalogFactory (an instance of the class provided in the sir.config file) for the service
     * located at the given URL.
     * 
     * @param url
     * @return
     * @throws OwsExceptionReport
     */
    public ICatalogFactory getCatalogFactory(URL url) throws OwsExceptionReport {
        try {
            return this.catalogFactoryConstructor.newInstance(url,
                                                              this.catalogInitClassificationFiles,
                                                              this.catalogInitSlotFile,
                                                              Boolean.valueOf(this.doNotCheckCatalogsList.contains(url)));
        }
        catch (Exception e) {
            log.error("The instatiation of a catalog factory failed.", e);
            throw new OwsExceptionReport("The instatiation of a catalog factory failed: " + e.getMessage(),
                                         e.getCause());
        }
    }

    /**
     * 
     * @return the status handler for external access (not from within this SIR instance)
     */
    public ICatalogStatusHandler getCatalogStatusHandler() {
        return this.catalogStatusHandler;
    }

    /**
     * @return the characterEncoding
     */
    public String getCharacterEncoding() {
        return this.characterEncoding;
    }

    /**
     * 
     * @return
     */
    public ExecutorService getExecutor() {
        return this.exec;
    }

    /**
     * @return the DaoFactory
     */
    public IDAOFactory getFactory() {
        return this.factory;
    }

    /**
     * @return the homepage
     */
    public URL getFullServicePath() {
        return this.fullServicePath;
    }

    /**
     * @return the gmlDateFormat
     */
    public String getGmlDateFormat() {
        return this.gmlDateFormat;
    }

    /**
     * @return the httpGetDecoder
     */
    public IHttpGetRequestDecoder getHttpGetDecoder() {
        return this.httpGetDecoder;
    }

    /**
     * @return the httpPostDecoder
     */
    public IHttpPostRequestDecoder getHttpPostDecoder() {
        return this.httpPostDecoder;
    }

    /**
     * @return the jobSchedulerFactory
     */
    public IJobSchedulerFactory getJobSchedulerFactory() {
        return this.jobSchedulerFactory;
    }

    /**
     * @return the namespacePrefix
     */
    public String getNamespacePrefix() {
        return this.namespacePrefix;
    }

    /**
     * @return the namespaceUri
     */
    public String getNamespaceUri() {
        return this.namespaceUri;
    }

    /**
     * @return the openSearchUrl
     */
    public String getOpenSearchPath() {
        return this.openSearchPath;
    }

    /**
     * @return the profile4Discovery
     */
    public String getProfile4Discovery() {
        return this.profile4Discovery;
    }

    /**
     * 
     * @return
     */
    public String getProfile4DiscoveryDownloadPath() {
        return this.props.getProperty(PROFILE4DISCOVERY);
    }

    /**
     * 
     * @return
     */
    public String getSchemaDownloadLink() {
        return this.props.getProperty(SCHEMATRON_DOWNLOAD);
    }

    /**
     * @return the schemaUrl
     */
    public String getSchemaUrl() {
        return this.schemaUrl;
    }

    /**
     * @return the serviceUrl of the sir.config in the form "host:port/path"
     */
    public URL getServiceUrl() {
        return this.serviceUrl;
    }

    /**
     * @return the serviceVersion
     */
    public String getServiceVersion() {
        return this.serviceVersion;
    }

    /**
     * does the translation from String representation of version number (to be optained by
     * getServiceVersion()) to enum of schema.
     * 
     * @return
     */
    public org.x52North.sir.x032.VersionAttribute.Version.Enum getServiceVersionEnum() {
        String sv = getServiceVersion();

        if (sv.equals(SirConstants.SERVICE_VERSION_0_3_0))
            return VersionAttribute.Version.X_0_3_0;

        if (sv.equals(SirConstants.SERVICE_VERSION_0_3_1))
            return VersionAttribute.Version.X_0_3_1;

        throw new RuntimeException("Not a supported version!");
    }

    /**
     * @return the svrlSchema
     */
    public String getSvrlSchema() {
        return this.svrlSchema;
    }

    /**
     * @return the testRequestPath
     */
    public String getTestRequestPath() {
        return this.testRequestPath;
    }

    /**
     * @return the transformerFactory
     */
    public ITransformerFactory getTransformerFactory() {
        return this.transformerFactory;
    }

    /**
     * @return the updateSequence
     */
    public String getUpdateSequence() {
        return this.updateSequence;
    }

    /**
     * @return the validatorFactory
     */
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
        this.configDirectory = this.props.getProperty(CONFIG_DIRECTORY);
        this.schemaUrl = this.props.getProperty(SCHEMA_URL);
        this.namespaceUri = this.props.getProperty(NAMESPACE_URI);
        this.namespacePrefix = this.props.getProperty(NAMESPACE_PREFIX);
        this.testRequestPath = this.basepath + this.props.getProperty(TESTREQUESTS);

        this.extendedDebugToConsole = Boolean.parseBoolean(this.props.getProperty(EXTENDED_DEBUG_TO_CONSOLE));
        this.acceptedVersions = this.props.getProperty(ACCEPTED_SERVICE_VERSIONS).split(VERSION_SPLIT_CHARACTER);
        this.validateRequests = Boolean.parseBoolean(this.props.getProperty(VALIDATE_XML_REQUESTS));
        this.validateResponses = Boolean.parseBoolean(this.props.getProperty(VALIDATE_XML_RESPONSES));

        this.ScriptsPath = this.props.getProperty(SCRIPTS_PATH);

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
        }
        catch (MalformedURLException e) {
            log.error("No valid service url is defined in the config file: " + url);
        }

        try {
            url = this.props.getProperty(FULL_SERVICE_PATH);
            this.fullServicePath = new URL(url);
        }
        catch (MalformedURLException e) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 null,
                                 "No valid service url is defined in the config file: " + url);
            log.error("No valid service url is defined in the config file: " + url);
            throw se;
        }

        this.openSearchPath = this.props.getProperty(OPENSEARCH_ENDPOINT);

        // set updateSequence
        newUpdateSequence();

        // initialize DAO Factory
        initializeDAOFactory(this.daoProps);

        // initialize CatalogFactory
        initializeCatalogFactory(this.props);

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

    @SuppressWarnings("unchecked")
    private void initializeCatalogFactory(Properties sirProps) throws OwsExceptionReport {
        String className = sirProps.getProperty(CATALOGFACTORY);

        this.catalogInitSlotFile = this.basepath + this.configDirectory + sirProps.getProperty(SLOT_INIT_FILENAME);

        // add classification init files
        String[] splitted = sirProps.getProperty(CLASSIFICATION_INIT_FILENAMES).split(CONFIG_FILE_LIST_SEPARATOR);
        this.catalogInitClassificationFiles = new String[splitted.length];
        for (int i = 0; i < splitted.length; i++) {
            this.catalogInitClassificationFiles[i] = this.basepath + this.configDirectory + splitted[i].trim();
        }

        // check if given url does not need to be checked
        this.doNotCheckCatalogsList = new ArrayList<>();
        splitted = sirProps.getProperty(DO_NOT_CHECK_CATALOGS).split(CONFIG_FILE_LIST_SEPARATOR);
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

    @SuppressWarnings("unchecked")
    private void initializeDAOFactory(Properties daoPropsP) throws OwsExceptionReport {
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
            Class< ? >[] constrArgs = {Properties.class};

            Object[] args = {daoPropsP};

            // get Constructor of this class with matching parameter types
            Constructor<IDAOFactory> constructor = daoFactoryClass.getConstructor(constrArgs);

            this.factory = constructor.newInstance(args);

            log.info(" ***** " + daoName + " loaded successfully! ***** ");

        }
        catch (ClassNotFoundException cnfe) {
            log.error("Error while loading DaoFactory, required class could not be loaded: " + cnfe.toString());
            throw new OwsExceptionReport(cnfe.getMessage(), cnfe.getCause());
        }
        catch (SecurityException se) {
            log.error("Error while loading DAOFactory: " + se.toString());
            throw new OwsExceptionReport(se.getMessage(), se.getCause());
        }
        catch (NoSuchMethodException nsme) {
            log.error("Error while loading DAOFactory, no required constructor available: ");
            throw new OwsExceptionReport(nsme.getMessage(), nsme.getCause());
        }
        catch (IllegalArgumentException iae) {
            log.error("Error while loading DAOFactory, parameters for the constructor are illegal: " + iae.toString());
            throw new OwsExceptionReport(iae.getMessage(), iae.getCause());
        }
        catch (InstantiationException ie) {
            log.error("The instantiation of a DAOFactory failed: " + ie.toString());
            throw new OwsExceptionReport(ie.getMessage(), ie.getCause());
        }
        catch (IllegalAccessException iace) {
            log.error("The instantiation of a DAOFactory failed: " + iace.toString());
            throw new OwsExceptionReport(iace.getMessage(), iace.getCause());
        }
        catch (InvocationTargetException ite) {
            log.error("The instantiation of a DAOFactory failed: " + ite.toString());
            throw new OwsExceptionReport(ite.getMessage(), ite.getCause());
        }
    }

    @SuppressWarnings("unchecked")
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
        catch (ClassNotFoundException cnfe) {
            log.error("Error while loading getRequestDecoder, required class could not be loaded: " + cnfe.toString());
            throw new OwsExceptionReport(cnfe.getMessage(), cnfe.getCause());
        }
        catch (NoSuchMethodException nsme) {
            log.error("Error while loading getRequestDecoder, no required constructor available: " + nsme.toString());
            throw new OwsExceptionReport(nsme.getMessage(), nsme.getCause());
        }
        catch (InstantiationException ie) {
            log.error("The instatiation of a getRequestDecoder failed: " + ie.toString());
            throw new OwsExceptionReport(ie.getMessage(), ie.getCause());

        }
        catch (IllegalAccessException iace) {
            log.error("The instatiation of an getRequestDecoder failed: " + iace.toString());
            throw new OwsExceptionReport(iace.getMessage(), iace.getCause());
        }
        catch (InvocationTargetException ite) {
            log.error("the instatiation of an getRequestDecoder failed: " + ite.toString() + ite.getLocalizedMessage()
                    + ite.getCause());
            throw new OwsExceptionReport(ite.getMessage(), ite.getCause());
        }
    }

    @SuppressWarnings("unchecked")
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
        catch (ClassNotFoundException cnfe) {
            log.error("Error while loading getRequestDecoder, required class could not be loaded: " + cnfe.toString());
            throw new OwsExceptionReport(cnfe.getMessage(), cnfe.getCause());
        }
        catch (NoSuchMethodException nsme) {
            log.error("Error while loading getRequestDecoder, no required constructor available: " + nsme.toString());
            throw new OwsExceptionReport(nsme.getMessage(), nsme.getCause());
        }
        catch (InstantiationException ie) {
            log.error("The instatiation of a getRequestDecoder failed: " + ie.toString());
            throw new OwsExceptionReport(ie.getMessage(), ie.getCause());
        }
        catch (IllegalAccessException iace) {
            log.error("The instatiation of an getRequestDecoder failed: " + iace.toString());
            throw new OwsExceptionReport(iace.getMessage(), iace.getCause());
        }
        catch (InvocationTargetException ite) {
            log.error("the instatiation of an getRequestDecoder failed: " + ite.toString() + ite.getLocalizedMessage()
                    + ite.getCause());
            throw new OwsExceptionReport(ite.getMessage(), ite.getCause());
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
            Constructor<IJobSchedulerFactory> constructor = jobSchedulerFactoryClass.getConstructor(TimerServlet.class);

            this.jobSchedulerFactory = constructor.newInstance(timer);

            log.info(" ***** " + className + " loaded successfully! ***** ");
        }
        catch (NoSuchMethodException nsme) {
            log.error("Error while loading jobSchedulerFactoryClass, no required constructor available: "
                    + nsme.toString());
            throw new OwsExceptionReport(nsme.getMessage(), nsme.getCause());
        }
        catch (InstantiationException ie) {
            log.error("The instatiation of a jobSchedulerFactoryClass failed: " + ie.toString());
            throw new OwsExceptionReport(ie.getMessage(), ie.getCause());
        }
        catch (IllegalAccessException iae) {
            log.error("The instatiation of an jobSchedulerFactoryClass failed: " + iae.toString());
            throw new OwsExceptionReport(iae.getMessage(), iae.getCause());
        }
        catch (InvocationTargetException ite) {
            log.error("The instatiation of an jobSchedulerFactoryClass failed: " + ite.toString()
                    + ite.getLocalizedMessage() + ite.getCause());
            throw new OwsExceptionReport(ite.getMessage(), ite.getCause());
        }
        catch (ClassNotFoundException cnfe) {
            log.error("Error while loading jobSchedulerFactoryClass, required class could not be loaded: "
                    + cnfe.toString());
            throw new OwsExceptionReport(cnfe.getMessage(), cnfe.getCause());
        }
    }

    @SuppressWarnings("unchecked")
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
        catch (NoSuchMethodException nsme) {
            log.error("Error while loading catalogStatusHandler, no required constructor available: " + nsme.toString());
            throw new OwsExceptionReport(nsme.getMessage(), nsme.getCause());
        }
        catch (InstantiationException ie) {
            log.error("The instatiation of a catalogStatusHandler failed: " + ie.toString());
            throw new OwsExceptionReport(ie.getMessage(), ie.getCause());
        }
        catch (IllegalAccessException iae) {
            log.error("The instatiation of an catalogStatusHandler failed: " + iae.toString());
            throw new OwsExceptionReport(iae.getMessage(), iae.getCause());
        }
        catch (InvocationTargetException ite) {
            log.error("The instatiation of an catalogStatusHandler failed: " + ite.toString()
                    + ite.getLocalizedMessage() + ite.getCause());
            throw new OwsExceptionReport(ite.getMessage(), ite.getCause());
        }
        catch (ClassNotFoundException cnfe) {
            log.error("Error while loading catalogStatusHandler, required class could not be loaded: "
                    + cnfe.toString());
            throw new OwsExceptionReport(cnfe.getMessage(), cnfe.getCause());
        }
    }

    /**
     * 
     * @param p
     * @throws OwsExceptionReport
     */
    @SuppressWarnings("unchecked")
    private void initializeTransformerFactory(Properties p) throws OwsExceptionReport {
        String xsltDir = p.getProperty(XSTL_DIRECTORY);
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

            this.transformerFactory = constructor.newInstance(this.basepath + xsltDir);

            log.info(" ***** " + className + " loaded successfully! Using files from folder " + this.basepath + xsltDir
                    + ". ***** ");
        }
        catch (NoSuchMethodException nsme) {
            log.error("Error while loading transformerFactoryClass, no required constructor available: "
                    + nsme.toString());
            throw new OwsExceptionReport(nsme.getMessage(), nsme.getCause());
        }
        catch (InstantiationException ie) {
            log.error("The instatiation of a transformerFactoryClass failed: " + ie.toString());
            throw new OwsExceptionReport(ie.getMessage(), ie.getCause());
        }
        catch (IllegalAccessException iae) {
            log.error("The instatiation of an transformerFactoryClass failed: " + iae.toString());
            throw new OwsExceptionReport(iae.getMessage(), iae.getCause());
        }
        catch (InvocationTargetException ite) {
            log.error("The instatiation of an transformerFactoryClass failed: " + ite.toString()
                    + ite.getLocalizedMessage() + ite.getCause());
            throw new OwsExceptionReport(ite.getMessage(), ite.getCause());
        }
        catch (ClassNotFoundException cnfe) {
            log.error("Error while loading transformerFactoryClass, required class could not be loaded: "
                    + cnfe.toString());
            throw new OwsExceptionReport(cnfe.getMessage(), cnfe.getCause());
        }
    }

    /**
     * 
     * @param p
     * @throws OwsExceptionReport
     */
    @SuppressWarnings("unchecked")
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
        catch (NoSuchMethodException nsme) {
            log.error("Error while loading validatorFactoryClass, no required constructor available: "
                    + nsme.toString());
            throw new OwsExceptionReport(nsme.getMessage(), nsme.getCause());
        }
        catch (InstantiationException ie) {
            log.error("The instatiation of a validatorFactoryClass failed: " + ie.toString());
            throw new OwsExceptionReport(ie.getMessage(), ie.getCause());
        }
        catch (IllegalAccessException iae) {
            log.error("The instatiation of an validatorFactoryClass failed: " + iae.toString());
            throw new OwsExceptionReport(iae.getMessage(), iae.getCause());
        }
        catch (InvocationTargetException ite) {
            log.error("The instatiation of an validatorFactoryClass failed: " + ite.toString()
                    + ite.getLocalizedMessage() + ite.getCause());
            throw new OwsExceptionReport(ite.getMessage(), ite.getCause());
        }
        catch (ClassNotFoundException cnfe) {
            log.error("Error while loading validatorFactoryClass, required class could not be loaded: "
                    + cnfe.toString());
            throw new OwsExceptionReport(cnfe.getMessage(), cnfe.getCause());
        }
    }

    /**
     * @return the extendedDebugToConsole
     */
    public boolean isExtendedDebugToConsole() {
        return this.extendedDebugToConsole;
    }

    public String getScriptsPath() {
        return this.ScriptsPath;
    }

    /**
     * @return the validateRequests
     */
    public boolean isValidateRequests() {
        return this.validateRequests;
    }

    /**
     * @return the validateResponses
     */
    public boolean isValidateResponses() {
        return this.validateResponses;
    }

    private void loadCapabilitiesSkeleton(Properties sirProps) throws OwsExceptionReport {
        String skeletonPath = sirProps.getProperty(CAPABILITIESSKELETON_FILENAME);

        try (InputStream resource = SirConfigurator.class.getResourceAsStream(skeletonPath);) {

            log.info("Loading capabilities skeleton from " + skeletonPath);

            this.capabilitiesSkeleton = CapabilitiesDocument.Factory.parse(resource);
        }
        catch (Exception e) {
            log.error("Error on loading capabilities skeleton file: " + e.getMessage());
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.NoApplicableCode,
                                 null,
                                 "Error on loading capabilities skeleton file: " + e.getMessage());

            throw se;
        }
    }

    public ArrayList<String> getListenerClassnames() {
        ArrayList<String> listeners = new ArrayList<>();
        String listenersList = this.props.getProperty(LISTENERS);

        if (listenersList == null) {
            log.error("No RequestListeners are defined in the ConfigFile!");
        }

        StringTokenizer tokenizer = new StringTokenizer(listenersList, ",");
        while (tokenizer.hasMoreTokens()) {
            listeners.add(tokenizer.nextToken());
        }
        return listeners;
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
        if ( !this.startCatalogConnectionsOnStartup) {
            log.warn("Catalog connections are disabled on startup.");
            return;
        }

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
                    if (f == null) {
                        log.error("Factory is null");
                        throw new RuntimeException("Could not get factory");
                    }

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
                    log.error("Could not run tasks for saved catalog connections: {}", e.getMessage());
                }

                log.info(" ***** Scheduled " + i + " task(s) from the database. ***** ");
            }

        });
    }

}