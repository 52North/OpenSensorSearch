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
package org.n52.sir;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

import org.n52.oss.sir.SirConstants;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.ds.IDAOFactory;
import org.n52.sir.licenses.License;
import org.n52.sir.licenses.Licenses;
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
 * @author Jan Schulte, Daniel Nüst
 * 
 */
@Singleton
public class SirConfigurator {

    private static final String ACCEPTED_SERVICE_VERSIONS = "oss.sir.acceptedVersions";

    private static final String CAPABILITIESSKELETON_FILENAME = "oss.sir.capabilities.skeleton";

    private static final String GMLDATEFORMAT = "oss.sir.gml.dateformat";

    private static SirConfigurator instance = null;

    protected static Logger log = LoggerFactory.getLogger(SirConfigurator.class);

    private static final String SERVICEVERSION = "oss.sir.version";

    private static final String VALIDATE_XML_REQUESTS = "oss.sir.requests.validate";

    private static final String VALIDATE_XML_RESPONSES = "oss.sir.responses.validate";

    private static final String VERSION_SPLIT_CHARACTER = ",";

    private static final String SCRIPTS_PATH = "oss.scripts.storagePath";

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

    private CapabilitiesDocument capabilitiesSkeleton;

    private IDAOFactory factory;

    private String gmlDateFormat;

    private Properties props;

    private String serviceVersion;

    private String ScriptsPath;

    public LinkedHashMap<String, License> getLicenses() {
        return this.licenses;
    }

    public void setLicenses(LinkedHashMap<String, License> licenses) {
        this.licenses = licenses;
    }

    private LinkedHashMap<String, License> licenses;

    private String updateSequence;

    private boolean validateRequests;

    private boolean validateResponses;

    @Inject
    public SirConfigurator(IDAOFactory daoFactory, @Named("sir_properties")
    Properties props) throws OwsExceptionReport {
        this.factory = daoFactory;
        this.props = props;

        initialize();

        log.info("NEW {}", this);
    }

    public String[] getAcceptedServiceVersions() {
        return this.acceptedVersions;
    }

    public CapabilitiesDocument getCapabilitiesSkeleton() {
        return this.capabilitiesSkeleton;
    }

    @Deprecated
    public IDAOFactory getFactory() {
        return this.factory;
    }

    public String getGmlDateFormat() {
        return this.gmlDateFormat;
    }

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

        if (sv.equals(SirConstants.SERVICE_VERSION_0_3_2))
            return VersionAttribute.Version.X_0_3_2;

        throw new RuntimeException("Not a supported version!");
    }

    public String getUpdateSequence() {
        return this.updateSequence;
    }

    private void initialize() throws OwsExceptionReport {
        log.info(" * Initializing SirConfigurator ... ");

        this.serviceVersion = this.props.getProperty(SERVICEVERSION);
        this.gmlDateFormat = this.props.getProperty(GMLDATEFORMAT);

        this.acceptedVersions = this.props.getProperty(ACCEPTED_SERVICE_VERSIONS).split(VERSION_SPLIT_CHARACTER);
        this.validateRequests = Boolean.parseBoolean(this.props.getProperty(VALIDATE_XML_REQUESTS));
        this.validateResponses = Boolean.parseBoolean(this.props.getProperty(VALIDATE_XML_RESPONSES));

        this.ScriptsPath = this.props.getProperty(SCRIPTS_PATH);
        this.licenses = this.initializeLicenses();

        newUpdateSequence();
        loadCapabilitiesSkeleton(this.props);

        log.info(" ***** Initialized SirConfigurator successfully! ***** ");
    }

    private LinkedHashMap<String, License> initializeLicenses() {
        try (InputStream licensesStream = SirConfigurator.class.getResourceAsStream("/prop/licenses.json");) {

            LinkedHashMap<String, License> licensesMap = new LinkedHashMap<>();
            Gson gson = new Gson();
            Licenses list = gson.fromJson(new InputStreamReader(licensesStream), Licenses.class);
            List<License> licenses1 = list.licenses;

            for (int i = 0; i < licenses1.size(); i++) {
                License l = licenses1.get(i);
                licensesMap.put(l.code, l);
            }
            log.info("The list of licenses initialized successfully! {}", list);
            return licensesMap;
        }
        catch (IOException e) {
            log.error("Cannot load licesnes", e);
            return null;
        }
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

    public String newUpdateSequence() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(this.gmlDateFormat);
        this.updateSequence = dateFormat.format(new Date());
        return this.updateSequence;
    }

}