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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.UnavailableException;

import org.n52.sir.catalog.ICatalogFactory;
import org.n52.sir.catalog.ICatalogStatusHandler;
import org.n52.sir.decode.IHttpGetRequestDecoder;
import org.n52.sir.decode.IHttpPostRequestDecoder;
import org.n52.sir.ds.IDAOFactory;
import org.n52.sir.licenses.License;
import org.n52.sir.licenses.Licenses;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.ows.OwsExceptionReport.ExceptionLevel;
import org.n52.sir.xml.ITransformerFactory;
import org.n52.sir.xml.IValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sir.x032.CapabilitiesDocument;
import org.x52North.sir.x032.VersionAttribute;

import com.google.gson.Gson;
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

    private static final String ACCEPTED_SERVICE_VERSIONS = "oss.sir.acceptedVersions";

    private static final String CAPABILITIESSKELETON_FILENAME = "CAPABILITIESSKELETON_FILENAME";

    private static final String CATALOGFACTORY = "oss.sir.catalog.csw.factoryImpl";

    private static final String CHARACTER_ENCODING = "oss.characterencoding";

    private static final String CLASSIFICATION_INIT_FILENAMES = "oss.catalogconnection.csw-ebrim.classificationInitFilenanes";

    private static final String CONFIG_FILE_LIST_SEPARATOR = ",";

    private static final String DO_NOT_CHECK_CATALOGS = "oss.catalogconnection.doNotCheckCatalogs";

    private static final String EXTENDED_DEBUG_TO_CONSOLE = "EXTENDED_DEBUG_TO_CONSOLE";

    private static final String FULL_SERVICE_PATH = "oss.serviceurl";

    private static final String GETREQUESTDECODER = "oss.sir.requestdecoder.get";

    private static final String GMLDATEFORMAT = "GMLDATEFORMAT";

    @Deprecated
    private static SirConfigurator instance = null;

    private static final String LISTENERS = "LISTENERS";

    protected static Logger log = LoggerFactory.getLogger(SirConfigurator.class);

    private static final String NAMESPACE_PREFIX = "NAMESPACE_PREFIX";

    private static final String NAMESPACE_URI = "NAMESPACE_URI";

    private static final String OPENSEARCH_ENDPOINT = "OPENSEARCH_ENDPOINT";

    private static final String POSTREQUESTDECODER = "oss.sir.requestdecoder.post";

    private static final String PROFILE4DISCOVERY = "PROFILE4DISCOVERY";

    private static final String SCHEMA_URL = "SCHEMA_URL";

    private static final String SCHEMATRON_DOWNLOAD = "SCHEMATRON_DOWNLOAD";

    private static final String SERVICEURL = "oss.sir.serviceurl";

    private static final String SERVICEVERSION = "oss.sir.version";

    private static final String SLOT_INIT_FILENAME = "oss.catalogconnection.csw-ebrim.slotInitFilename";

    private static final String STATUS_HANDLER = "STATUS_HANDLER";

    private static final String SVRL_SCHEMA = "SVRL_SCHEMA";

    private static final String TESTREQUESTS = "TESTREQUESTS";

    private static final int THREAD_POOL_SIZE = 10;

    private static final String TRANSFORMERFACTORY = "TRANSFORMERFACTORY";

    private static final String VALIDATE_XML_REQUESTS = "VALIDATE_XML_REQUESTS";

    private static final String VALIDATE_XML_RESPONSES = "VALIDATE_XML_RESPONSES";

    private static final String VALIDATORFACTORY = "VALIDATORFACTORY";

    private static final String VERSION_SPLIT_CHARACTER = ",";

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

    private String basepath;

    private CapabilitiesDocument capabilitiesSkeleton;

    private Constructor<ICatalogFactory> catalogFactoryConstructor;

    private String[] catalogInitClassificationFiles;

    private String catalogInitSlotFile;

    /**
     * Implementation of ICatalogStatusHandler to allow other servlets to change stati of catalogs in the
     * database
     */
    private ICatalogStatusHandler catalogStatusHandler;

    private String characterEncoding;

    /**
     * a list of catalogue-URLs that are not checked when data is pushed into them
     */
    private ArrayList<URL> doNotCheckCatalogsList;

    private ExecutorService exec;

    private boolean extendedDebugToConsole;

    private IDAOFactory factory;

    private URL fullServicePath;

    private String gmlDateFormat;

    private IHttpGetRequestDecoder httpGetDecoder;

    private IHttpPostRequestDecoder httpPostDecoder;

    private String namespacePrefix;

    private String namespaceUri;

    private String openSearchPath;

    private String profile4Discovery;

    private Properties props;

    private String schemaUrl;

    /**
     * the url of the service, e.g. needed by test client
     */
    private URL serviceUrl;

    private String serviceVersion;

    private String svrlSchema;

    private String testRequestPath;

    private ITransformerFactory transformerFactory;

    private String ScriptsPath;
    public LinkedHashMap<String,License> getLicenses() {
		return licenses;
	}

	public void setLicenses(LinkedHashMap<String,License> licenses) {
		this.licenses = licenses;
	}

	private LinkedHashMap<String,License> licenses;

    private String updateSequence;

    private boolean validateRequests;

    private boolean validateResponses;

    private IValidatorFactory validatorFactory;

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
    String basepath, IDAOFactory daoFactory) throws UnavailableException, OwsExceptionReport, IOException {
        try (InputStream dbStream = SirConfigurator.class.getResourceAsStream("/prop/db.properties");
                InputStream configStream = SirConfigurator.class.getResourceAsStream("/prop/sir.properties");) {

            if (instance == null) {
                instance = new SirConfigurator(configStream, dbStream, basepath, daoFactory);
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

    @Deprecated
    private SirConfigurator(InputStream configStream,
                            InputStream dbConfigStream,
                            String basepath,
                            IDAOFactory daoFactory) throws UnavailableException {
        this.factory = daoFactory;

        try {
            this.basepath = basepath;

            // creating common SIR properties object from inputstream
            this.props = loadProperties(configStream);
            // this.daoProps = loadProperties(dbConfigStream);

            log.info(" ***** Config Files loaded successfully! ***** ");
        }
        catch (IOException ioe) {
            log.error("Error while loading config file.", ioe);
            throw new UnavailableException(ioe.getMessage());
        }

        log.debug("DEPRECATED CONSTRUCTION of {}", this);
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
     * @deprecated the character encoding should be only UTF-8, which is Jersey's default and must not be set
     * @return the characterEncoding
     */
    @Deprecated
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
    @Deprecated
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
        this.schemaUrl = this.props.getProperty(SCHEMA_URL);
        this.namespaceUri = this.props.getProperty(NAMESPACE_URI);
        this.namespacePrefix = this.props.getProperty(NAMESPACE_PREFIX);
        this.testRequestPath = this.basepath + this.props.getProperty(TESTREQUESTS);

        this.extendedDebugToConsole = Boolean.parseBoolean(this.props.getProperty(EXTENDED_DEBUG_TO_CONSOLE));
        this.acceptedVersions = this.props.getProperty(ACCEPTED_SERVICE_VERSIONS).split(VERSION_SPLIT_CHARACTER);
        this.validateRequests = Boolean.parseBoolean(this.props.getProperty(VALIDATE_XML_REQUESTS));
        this.validateResponses = Boolean.parseBoolean(this.props.getProperty(VALIDATE_XML_RESPONSES));

        this.ScriptsPath = this.props.getProperty(SCRIPTS_PATH);
        this.licenses = this.initializeLicenses();
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
        // initializeDAOFactory(this.daoProps);

        // initialize CatalogFactory
        initializeCatalogFactory(this.props);

        // initialize decoders
        initializeHttpGetRequestDecoder(this.props);
        initializeHttpPostRequestDecoder(this.props);

        loadCapabilitiesSkeleton(this.props);

        // initialize status handler
        initializeStatusHandler(this.props);

        // initialize transformer
        initializeTransformerFactory(this.props);

        // initialize validator
        initializeValidatorFactory(this.props);

        log.info(" ***** Initialized SirConfigurator successfully! ***** ");
    }

    private LinkedHashMap<String,License> initializeLicenses() {
        try (InputStream licensesStream = SirConfigurator.class.getResourceAsStream("/prop/licenses.json");) {

    	LinkedHashMap<String, License> licensesMap = new LinkedHashMap<>();
    	Gson gson = new Gson();
		Licenses list = gson.fromJson(new InputStreamReader(licensesStream),Licenses.class);
		List<License> licenses = list.licenses;
		
		for(int i=0;i<licenses.size();i++){
			License l = licenses.get(i);
			licensesMap.put(l.code,l);
		}
		log.info("The list of licenses initialized successfully! {}",list);
		return licensesMap; 
        } catch (IOException e) {
        	log.error("Cannot load licesnes",e);
        	return null;
		}
    }

    private void initializeCatalogFactory(Properties sirProps) throws OwsExceptionReport {
        String className = sirProps.getProperty(CATALOGFACTORY);

        String slotInitFile = sirProps.getProperty(SLOT_INIT_FILENAME);
        this.catalogInitSlotFile = getAbsolutePath(slotInitFile);

        // add classification init files
        String[] splitted = sirProps.getProperty(CLASSIFICATION_INIT_FILENAMES).split(CONFIG_FILE_LIST_SEPARATOR);
        this.catalogInitClassificationFiles = new String[splitted.length];
        for (int i = 0; i < splitted.length; i++) {
            this.catalogInitClassificationFiles[i] = getAbsolutePath(splitted[i].trim());
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
                                                                                boolean.class);

            log.info(" ***** " + className + " loaded successfully! ***** ");
        }
        catch (ClassNotFoundException | NoSuchMethodException e) {
            log.error("Error while loading catalog factory, required class could not be loaded.", e);
            throw new OwsExceptionReport(e.getMessage(), e.getCause());
        }
        }

    private String getAbsolutePath(String file) {
        try {
            URL r = SirConfigurator.class.getResource(file);
            Path p = Paths.get(r.toURI());
            String s = p.toAbsolutePath().toString();
            return s;
            }
        catch (URISyntaxException e) {
            log.error("Could not load resource " + file, e);
        }

        return file;
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
        catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | InvocationTargetException e) {
            log.error("Error while loading GET RequestDecoder", e);
            throw new OwsExceptionReport(e.getMessage(), e.getCause());
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
        catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | InvocationTargetException e) {
            log.error("Error while loading POST RequestDecoder", e);
            throw new OwsExceptionReport(e.getMessage(), e.getCause());
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
        catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException
                | ClassNotFoundException e) {
            log.error("Error while loading catalogStatusHandler.", e);
            throw new OwsExceptionReport(e.getMessage(), e.getCause());
        }
        }

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
        catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException
                | ClassNotFoundException e) {
            throw new OwsExceptionReport(e.getMessage(), e.getCause());
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
        catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException
                | ClassNotFoundException e) {
            log.error("Error while loading validator factory.", e);
            throw new OwsExceptionReport(e.getMessage(), e.getCause());
        }
        }

    public boolean isExtendedDebugToConsole() {
        return this.extendedDebugToConsole;
    }

    public String getScriptsPath() {
        return this.ScriptsPath;
    }

    public boolean isValidateRequests() {
        return this.validateRequests;
    }

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

    @Deprecated
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

    private Properties loadProperties(InputStream is) throws IOException {
        Properties properties = new Properties();
        properties.load(is);

        return properties;
    }

    public void newUpdateSequence() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(this.gmlDateFormat);
        this.updateSequence = dateFormat.format(new Date());
    }

}