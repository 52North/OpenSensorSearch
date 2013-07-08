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

public interface SirConfig {

    public static final String LISTENERS = "LISTENERS";

    public static final String SERVICEURL = "SERVICEURL";

    public static final String CONFIG_FILE_LIST_SEPARATOR = ",";

    public abstract String[] getAcceptedServiceVersions();

    public abstract CapabilitiesDocument getCapabilitiesSkeleton();

    /**
     * Creates an CatalogFactory (an instance of the class provided in the sir.config file) for the service
     * located at the given URL.
     * 
     * @param url
     * @return
     * @throws OwsExceptionReport
     */
    public abstract ICatalogFactory getCatalogFactory(URL url) throws OwsExceptionReport;

    /**
     * @return the status handler for external access (not from within this SIR instance)
     */
    public abstract ICatalogStatusHandler getCatalogStatusHandler();

    public abstract String getCharacterEncoding();

    public abstract ExecutorService getExecutor();

    public abstract IDAOFactory getFactory();

    public abstract URL getFullServicePath();

    public abstract String getGmlDateFormat();

    public abstract IHttpGetRequestDecoder getHttpGetDecoder();

    public abstract IHttpPostRequestDecoder getHttpPostDecoder();

    public abstract IJobSchedulerFactory getJobSchedulerFactory();

    public abstract String getNamespacePrefix();

    public abstract String getNamespaceUri();

    public abstract String getOpenSearchPath();

    public abstract String getProfile4Discovery();

    public abstract String getProfile4DiscoveryDownloadPath();

    public abstract String getSchemaDownloadLink();

    public abstract String getSchemaUrl();

    /**
     * @return the serviceUrl of the sir.config in the form "host:port/path"
     */
    public abstract URL getServiceUrl();

    public abstract String getServiceVersion();

    /**
     * does the translation from String representation of version number (to be optained by
     * getServiceVersion()) to enum of schema.
     * 
     * @return
     */
    public abstract org.x52North.sir.x032.VersionAttribute.Version.Enum getServiceVersionEnum();

    public abstract String getSvrlSchema();

    public abstract String getTestRequestPath();

    public abstract ITransformerFactory getTransformerFactory();

    public abstract String getUpdateSequence();

    public abstract IValidatorFactory getValidatorFactory();

    public abstract boolean isExtendedDebugToConsole();

    public abstract boolean isValidateRequests();

    public abstract boolean isValidateResponses();

    public abstract void newUpdateSequence();

    public abstract Properties getDaoProps();

    /**
     * @return a list of relative file paths pointing at all files needed for catalog preparation
     */
    public abstract String[] getClassificationInitFileNames();

    /**
     * @return a file to initialize the slots in a catalog instance
     */
    public abstract String getCatalogSlotInitFile();

    /**
     * @return a list of identifiers whichs catalogs are not checked, i.e. they are already instantiated and
     *         prepared for sensor descriptions coming from OSS
     */
    public abstract String[] getCatalogsUnchecked();

}