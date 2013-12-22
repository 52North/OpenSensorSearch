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
package org.n52.sor;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Properties;

import org.n52.sor.OwsExceptionReport.ExceptionCode;
import org.n52.sor.reasoner.IReasonerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This singleton class handles service wide properties.
 * 
 * @author Jan Schulte
 * 
 */
public class PropertiesManager {

    private static Logger log = LoggerFactory.getLogger(PropertiesManager.class);

    /**
     * Path of the phenomenon xml list
     */
    private String phenomenonXML;

    private String capabilitieSkeleton;

    private String serviceURL;

    private String[] keywords;

    private String[] applicationDomains;

    private String[] ontologyRepositoryURLs;

    private String serviceVersion;

    private String updateSequence;

    private String gmlDateFormat;

    /**
     * property names
     */
    private static final String SERVICE = "SERVICE";

    private static final String PHENOMENON_XML = "PHENOMENONXML";

    private static final String CAPABILITIES_SKELETON = "CAPABILITIESSKELETON";

    private static final String SERVICE_URL = "SERVICEURL";

    private static final String KEYWORDS = "KEYWORDS";

    private static final String SERVICE_VERSION = "SERVICEVERSION";

    private static final String APPLICATION_DOMAINS = "APPLICATIONDOMAINS";

    private static final String ONTOLOGY_REPOSITORY_URLS = "ONTOLOGYREPOSITORYURLS";

    private static final String ONTOLOGY_FILE = "ONTOLOGYFILE";

    private static final String UPDATE_SEQUENCE = "UPDATESEQUENCE";

    private static final String GML_DATE_FORMAT = "GMLDATEFORMAT";

    private static final String SERVICE_ENDPOINT_POST = "SERVICE_ENDPOINT_POST";

    private static final String SERVICE_ENDPOINT_GET = "SERVICE_ENDPOINT_GET";

    private static final String PHENOMENONXML_BACKUPS = "PHENOMENONXML_BACKUPS";

    private static final String TESTREQUESTS = "TESTREQUESTS";

    private static final String DEFAULT_MAX_NUMBER_RESULTS = "DEFAULT_MAX_NUMBER_RESULTS";

    private static final String DECODER_ENCODING = "DECODER_ENCODING";

    private static final String RESPONSE_CONTENT_TYPE_XML = "RESPONSE_CONTENT_TYPE_XML";

    private static final String RESPONSE_CONTENT_TYPE_HTML = "RESPONSE_CONTENT_TYPE_HTML";

    private static final String REASONERFACTORY = "REASONERFACTORY";

    private String basepath;

    private String ontologyFile;

    /**
     * the url to which HTML POST requests need to be send to
     */
    private String serviceEndpointPost;

    /**
     * 
     */
    private int phenomenonXmlBackupCount;

    /**
     * including the URL
     */
    private String testRequestPath;

    /**
     * including the URL
     */
    private String serviceEndpointGet;

    /**
     * 
     */
    private int defaultMaximumNumberOfResults;

    /**
     * 
     */
    private String urlDecoderEncoding;

    private String responseContentTypeXml;

    private String service;

    private String responseContentTypeHtml;

    private IReasonerFactory reasonerFactory;

    /**
     * PropertiesManager instance to be a singleton
     */
    private static PropertiesManager instance;

    /**
     * This methode provides the only instance of PropertiesManager
     * 
     * @return The instance of the PropertiesManager
     */
    public static PropertiesManager getInstance() {
        if (instance == null) {
            log.error("PropertiesManager is not instantiated!");
            return null;
        }
        return instance;
    }

    /**
     * This methode provides the only instance of PropertiesManager
     * 
     * @param configStream
     *        The servletcontext stream to get the path for the phenomenonXML file of the web.xml
     * @param basepath
     * @return The instance of the PropertiesManager
     */
    public static PropertiesManager getInstance(InputStream configStream, String basepath) {
        if (instance == null) {
            instance = new PropertiesManager(configStream, basepath);
        }
        return instance;
    }

    /**
     * Constructor to create an instance of the PropertiesManager
     * 
     * @param configStream
     *        The servletcontext stream to get the path for the phenomenonXML file of the web.xml
     * @param basepath
     */
    public PropertiesManager(InputStream configStream, String basepath) {
        this.basepath = basepath;

        Properties props = new Properties();
        // load properties
        try {
            props.load(configStream);
        }
        catch (IOException e) {
            log.error("Load properties failed");
        }

        this.service = props.getProperty(SERVICE);
        this.phenomenonXML = this.basepath + props.getProperty(PHENOMENON_XML);
        this.capabilitieSkeleton = this.basepath + props.getProperty(CAPABILITIES_SKELETON);
        this.testRequestPath = this.basepath + props.getProperty(TESTREQUESTS);
        this.serviceURL = props.getProperty(SERVICE_URL);
        this.serviceVersion = props.getProperty(SERVICE_VERSION);
        this.keywords = props.getProperty(KEYWORDS).split(";");
        this.applicationDomains = props.getProperty(APPLICATION_DOMAINS).split(";");
        this.ontologyRepositoryURLs = props.getProperty(ONTOLOGY_REPOSITORY_URLS).split(";");
        this.ontologyFile = props.getProperty(ONTOLOGY_FILE);
        this.updateSequence = props.getProperty(UPDATE_SEQUENCE);
        this.gmlDateFormat = props.getProperty(GML_DATE_FORMAT);
        this.serviceEndpointPost = props.getProperty(SERVICE_URL) + props.getProperty(SERVICE_ENDPOINT_POST);
        this.serviceEndpointGet = props.getProperty(SERVICE_URL) + props.getProperty(SERVICE_ENDPOINT_GET);
        this.phenomenonXmlBackupCount = Integer.parseInt(props.getProperty(PHENOMENONXML_BACKUPS));
        this.defaultMaximumNumberOfResults = Integer.parseInt(props.getProperty(DEFAULT_MAX_NUMBER_RESULTS));
        this.urlDecoderEncoding = props.getProperty(DECODER_ENCODING);
        this.responseContentTypeXml = props.getProperty(RESPONSE_CONTENT_TYPE_XML);
        this.responseContentTypeHtml = props.getProperty(RESPONSE_CONTENT_TYPE_HTML);

        try {
            initializeReasonerFactory(props);
        }
        catch (OwsExceptionReport e) {
            log.error("Could not initialize PropertiesManager!", e);
        }
    }

    /**
     * 
     * @return
     */
    public String[] getApplicationDomains() {
        return this.applicationDomains;
    }

    /**
     * @return the capabilitieSkeleton
     */
    public String getCapabilitieSkeleton() {
        return this.capabilitieSkeleton;
    }

    /**
     * 
     * @return
     */
    public String getClientRequestContentType() {
        return getResponseContentTypeXml();
    }

    /**
     * 
     * Use the same encoding for the client requests that is used for decoding them.
     * 
     * @return
     */
    public String getClientRequestEncoding() {
        return getUrlDecoderEncoding();
    }

    /**
     * @return the defaultMaximumNumberOfResults
     */
    public int getDefaultMaximumNumberOfResults() {
        return this.defaultMaximumNumberOfResults;
    }

    /**
     * 
     * @return
     */
    public String getGmlDateFormat() {
        return this.gmlDateFormat;
    }

    /**
     * 
     * @return
     */
    public String[] getKeywords() {
        return this.keywords;
    }

    /**
     * 
     * @return
     */
    public String getOntologyFile() {
        return this.ontologyFile;
    }

    /**
     * 
     * @return
     */
    public String[] getOntologyRepositoryURLs() {
        return this.ontologyRepositoryURLs;
    }

    /**
     * @return the phenomenonXmlBackupCount
     */
    public int getPhenomenonXmlBackupCount() {
        return this.phenomenonXmlBackupCount;
    }

    /**
     * @return The path to the phenonmenonXML file
     */
    public String getPhenomenonXMLPath() {
        return this.phenomenonXML;
    }

    /**
     * @return the reasonerFactory
     */
    public IReasonerFactory getReasonerFactory() {
        return this.reasonerFactory;
    }

    /**
     * @return the responseContentTypeHtml
     */
    public String getResponseContentTypeHtml() {
        return this.responseContentTypeHtml;
    }

    /**
     * @return the responseContentType
     */
    public String getResponseContentTypeXml() {
        return this.responseContentTypeXml;
    }

    /**
     * @return the service
     */
    public String getService() {
        return this.service;
    }

    /**
     * @return the serviceEndpointGet
     */
    public String getServiceEndpointGet() {
        return this.serviceEndpointGet;
    }

    /**
     * @return the serviceEndpointPost
     */
    public String getServiceEndpointPost() {
        return this.serviceEndpointPost;
    }

    /**
     * @return the serviceURL
     */
    public String getServiceURL() {
        return this.serviceURL;
    }

    /**
     * @return the serviceVersion
     */
    public String getServiceVersion() {
        return this.serviceVersion;
    }

    /**
     * @return the testRequestPath
     */
    public String getTestRequestPath() {
        return this.testRequestPath;
    }

    /**
     * 
     * @return
     */
    public String getUpdateSequence() {
        return this.updateSequence;
    }

    /**
     * @return the urlDecoderEncoding
     */
    public String getUrlDecoderEncoding() {
        return this.urlDecoderEncoding;
    }

    @SuppressWarnings("unchecked")
    private void initializeReasonerFactory(Properties p) throws OwsExceptionReport {
        String className = p.getProperty(REASONERFACTORY);
        try {

            if (className == null) {
                log.error("No getRequestDecoder Implementation is set in the config file!");
                OwsExceptionReport se = new OwsExceptionReport(OwsExceptionReport.ExceptionCode.NoApplicableCode,
                                                               "PropertiesManager.initializeReasonerFactory()",
                                                               "No REASONERFACTORY implementation is set in the config file!");
                throw se;
            }
            // get Class of the httpGetRequestDecoderClass Implementation
            Class<IReasonerFactory> reasonerFact = (Class<IReasonerFactory>) Class.forName(className);

            // get Constructor of this class with matching parameter types
            Constructor<IReasonerFactory> constructor = reasonerFact.getConstructor();

            this.reasonerFactory = constructor.newInstance();

            log.info(" ***** " + className + " loaded successfully! ***** ");
        }
        catch (Exception e) {
            log.error("The instatiation of an IReasonerFactor failed: ", e);
            throw new OwsExceptionReport(ExceptionCode.NoApplicableCode,
                                         "PropertiesManager.initializeReasonerFactory()",
                                         "Could not instantiate IReasonerFactory!\n" + e.getMessage());
        }
    }

}