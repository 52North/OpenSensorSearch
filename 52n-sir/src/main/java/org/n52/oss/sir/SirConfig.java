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

package org.n52.oss.sir;

import java.net.URL;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import org.n52.sir.SirConfigurator;
import org.n52.sir.catalog.ICatalogFactory;
import org.n52.sir.catalog.ICatalogStatusHandler;
import org.n52.sir.decode.IHttpGetRequestDecoder;
import org.n52.sir.decode.IHttpPostRequestDecoder;
import org.n52.sir.ds.IDAOFactory;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.util.jobs.IJobSchedulerFactory;
import org.n52.sir.xml.ITransformerFactory;
import org.n52.sir.xml.IValidatorFactory;
import org.x52North.sir.x032.CapabilitiesDocument;

import com.google.inject.ImplementedBy;

@ImplementedBy(SirConfigurator.class)
public interface SirConfig {

    public static final String LISTENERS = "LISTENERS";
    
    public static final String SERVICEURL = "SERVICEURL";

    /**
     * @return the acceptedVersions
     */
    public abstract String[] getAcceptedServiceVersions();

    /**
     * @return the capabilitiesSkeleton
     */
    public abstract CapabilitiesDocument getCapabilitiesSkeleton();

    /**
     * 
     * Creates an CatalogFactory (an instance of the class provided in the sir.config file) for the service
     * located at the given URL.
     * 
     * @param url
     * @return
     * @throws OwsExceptionReport
     */
    public abstract ICatalogFactory getCatalogFactory(URL url) throws OwsExceptionReport;

    /**
     * 
     * @return the status handler for external access (not from within this SIR instance)
     */
    public abstract ICatalogStatusHandler getCatalogStatusHandler();

    /**
     * @return the characterEncoding
     */
    public abstract String getCharacterEncoding();

    /**
     * 
     * @return
     */
    public abstract ExecutorService getExecutor();

    /**
     * @return the DaoFactory
     */
    public abstract IDAOFactory getFactory();

    /**
     * @return the homepage
     */
    public abstract URL getFullServicePath();

    /**
     * @return the gmlDateFormat
     */
    public abstract String getGmlDateFormat();

    /**
     * @return the httpGetDecoder
     */
    public abstract IHttpGetRequestDecoder getHttpGetDecoder();

    /**
     * @return the httpPostDecoder
     */
    public abstract IHttpPostRequestDecoder getHttpPostDecoder();

    /**
     * @return the jobSchedulerFactory
     */
    public abstract IJobSchedulerFactory getJobSchedulerFactory();

    /**
     * @return the namespacePrefix
     */
    public abstract String getNamespacePrefix();

    /**
     * @return the namespaceUri
     */
    public abstract String getNamespaceUri();

    /**
     * @return the openSearchUrl
     */
    public abstract String getOpenSearchPath();

    /**
     * @return the profile4Discovery
     */
    public abstract String getProfile4Discovery();

    /**
     * 
     * @return
     */
    public abstract String getProfile4DiscoveryDownloadPath();

    /**
     * 
     * @return
     */
    public abstract String getSchemaDownloadLink();

    /**
     * @return the schemaUrl
     */
    public abstract String getSchemaUrl();

    /**
     * @return the serviceUrl of the sir.config in the form "host:port/path"
     */
    public abstract URL getServiceUrl();

    /**
     * @return the serviceVersion
     */
    public abstract String getServiceVersion();

    /**
     * does the translation from String representation of version number (to be optained by
     * getServiceVersion()) to enum of schema.
     * 
     * @return
     */
    public abstract org.x52North.sir.x032.VersionAttribute.Version.Enum getServiceVersionEnum();

    /**
     * @return the svrlSchema
     */
    public abstract String getSvrlSchema();

    /**
     * @return the testRequestPath
     */
    public abstract String getTestRequestPath();

    /**
     * @return the transformerFactory
     */
    public abstract ITransformerFactory getTransformerFactory();

    /**
     * @return the updateSequence
     */
    public abstract String getUpdateSequence();

    /**
     * @return the validatorFactory
     */
    public abstract IValidatorFactory getValidatorFactory();

    /**
     * @return the extendedDebugToConsole
     */
    public abstract boolean isExtendedDebugToConsole();

    /**
     * @return the validateRequests
     */
    public abstract boolean isValidateRequests();

    /**
     * @return the validateResponses
     */
    public abstract boolean isValidateResponses();

    public abstract void newUpdateSequence();

    Properties getDaoProps();

}