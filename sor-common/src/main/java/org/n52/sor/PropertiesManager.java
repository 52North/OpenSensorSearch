/**
 * Copyright 2013 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
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

    private static final String PHENOMENONXML_BACKUPS = "PHENOMENONXML_BACKUPS";

    private static final String REST_CACHING_TIME = "REST_CACHING_TIME";

    private static final String DEFAULT_MAX_NUMBER_RESULTS = "DEFAULT_MAX_NUMBER_RESULTS";

    private static final String DECODER_ENCODING = "DECODER_ENCODING";

    private static final String RESPONSE_CONTENT_TYPE_XML = "RESPONSE_CONTENT_TYPE_XML";

    private static final String RESPONSE_CONTENT_TYPE_HTML = "RESPONSE_CONTENT_TYPE_HTML";

    private static final String REASONERFACTORY = "REASONERFACTORY";

    private String ontologyFile;

    private String serviceEndpointPost;

    private int phenomenonXmlBackupCount;

    private String serviceEndpointGet;

    private int defaultMaximumNumberOfResults;

    private String urlDecoderEncoding;

    private String responseContentTypeXml;

    private String service;

    private String responseContentTypeHtml;

    private IReasonerFactory reasonerFactory;

    private String restChachingTime;

    private static PropertiesManager instance;

    public static PropertiesManager getInstance() {
        if (instance == null) {
            log.error("PropertiesManager is not instantiated!");
            return null;
        }
        return instance;
    }

    public static PropertiesManager getInstance(InputStream configStream) {
        if (instance == null) {
            Properties props = new Properties();
            try {
                props.load(configStream);
                getInstance(props);
            }
            catch (IOException e) {
                log.error("Load properties failed");
            }
        }

        return instance;
    }

    public static PropertiesManager getInstance(Properties properties) {
        if (properties == null)
            log.error("Given properties are null, cannot instantiate!");

        if (instance == null)
            instance = new PropertiesManager(properties);

        return instance;
    }

    public PropertiesManager(Properties props) {
        this.service = props.getProperty(SERVICE);
        this.phenomenonXML = props.getProperty(PHENOMENON_XML);
        this.capabilitieSkeleton = props.getProperty(CAPABILITIES_SKELETON);
        this.serviceURL = props.getProperty(SERVICE_URL);
        this.serviceVersion = props.getProperty(SERVICE_VERSION);
        this.keywords = props.getProperty(KEYWORDS).split(";");
        this.applicationDomains = props.getProperty(APPLICATION_DOMAINS).split(";");
        this.ontologyRepositoryURLs = props.getProperty(ONTOLOGY_REPOSITORY_URLS).split(";");
        this.ontologyFile = props.getProperty(ONTOLOGY_FILE);
        this.updateSequence = props.getProperty(UPDATE_SEQUENCE);
        this.gmlDateFormat = props.getProperty(GML_DATE_FORMAT);
        this.restChachingTime = props.getProperty(REST_CACHING_TIME);
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

    public String[] getApplicationDomains() {
        return this.applicationDomains;
    }

    public String getCapabilitieSkeleton() {
        return this.capabilitieSkeleton;
    }

    public String getClientRequestContentType() {
        return getResponseContentTypeXml();
    }

    public String getClientRequestEncoding() {
        return getUrlDecoderEncoding();
    }

    public int getDefaultMaximumNumberOfResults() {
        return this.defaultMaximumNumberOfResults;
    }

    public String getGmlDateFormat() {
        return this.gmlDateFormat;
    }

    public String[] getKeywords() {
        return this.keywords;
    }

    public String getOntologyFile() {
        return this.ontologyFile;
    }

    public String[] getOntologyRepositoryURLs() {
        return this.ontologyRepositoryURLs;
    }

    public int getPhenomenonXmlBackupCount() {
        return this.phenomenonXmlBackupCount;
    }

    public String getPhenomenonXMLPath() {
        return this.phenomenonXML;
    }

    public IReasonerFactory getReasonerFactory() {
        return this.reasonerFactory;
    }

    public String getResponseContentTypeHtml() {
        return this.responseContentTypeHtml;
    }

    public String getResponseContentTypeXml() {
        return this.responseContentTypeXml;
    }

    public String getService() {
        return this.service;
    }

    public String getServiceEndpointGet() {
        return this.serviceEndpointGet;
    }

    public String getServiceEndpointPost() {
        return this.serviceEndpointPost;
    }

    public String getServiceURL() {
        return this.serviceURL;
    }

    public String getServiceVersion() {
        return this.serviceVersion;
    }

    public String getUpdateSequence() {
        return this.updateSequence;
    }

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

    public String getRestChachingTime() {
        return this.restChachingTime;
    }

}