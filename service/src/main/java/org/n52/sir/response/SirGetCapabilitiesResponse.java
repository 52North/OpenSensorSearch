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

package org.n52.sir.response;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import net.opengis.ows.x11.AllowedValuesDocument.AllowedValues;
import net.opengis.ows.x11.DCPDocument.DCP;
import net.opengis.ows.x11.DomainType;
import net.opengis.ows.x11.HTTPDocument.HTTP;
import net.opengis.ows.x11.OperationDocument.Operation;
import net.opengis.ows.x11.OperationsMetadataDocument.OperationsMetadata;
import net.opengis.ows.x11.RequestMethodType;
import net.opengis.ows.x11.ServiceIdentificationDocument.ServiceIdentification;
import net.opengis.ows.x11.ServiceProviderDocument.ServiceProvider;
import net.opengis.ows.x11.ValueType;

import org.n52.oss.sir.SirConstants.CapabilitiesSection;
import org.n52.oss.sir.api.SirService;
import org.n52.oss.util.XmlTools;
import org.n52.sir.SirConfigurator;
import org.n52.sir.catalog.ICatalogConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sir.x032.CapabilitiesDocument;
import org.x52North.sir.x032.CapabilitiesDocument.Capabilities;
import org.x52North.sir.x032.CapabilitiesDocument.Capabilities.Contents;
import org.x52North.sir.x032.CapabilitiesDocument.Capabilities.Contents.HarvestedService;
import org.x52North.sir.x032.CapabilitiesDocument.Capabilities.Contents.LinkedCatalog;

/**
 * @author Jan Schulte
 * 
 */
public class SirGetCapabilitiesResponse extends AbstractXmlResponse {

    private static final Object ACCEPT_VERSIONS_PARAMETER_NAME = "AcceptVersions";

    private static Logger log = LoggerFactory.getLogger(SirGetCapabilitiesResponse.class);

    private static final Object VERSION_PARAMETER_NAME = "version";

    private Collection<ICatalogConnection> catalogConnections;

    private ArrayList<CapabilitiesSection> sections;

    private Collection<SirService> harvestedServices;

    private SirConfigurator config;

    private URI serviceUri;

    public SirGetCapabilitiesResponse(SirConfigurator config, URI uri) {
        this.config = config;
        this.serviceUri = uri;

        log.info("NEW {}", this);
    }

    private Contents createContents() {
        Contents contents = Contents.Factory.newInstance();

        // set list of services
        for (SirService service : this.harvestedServices) {
            HarvestedService harvestedService = contents.addNewHarvestedService();
            harvestedService.setServiceType(service.getType());
            harvestedService.setServiceURL(service.getUrl());
        }

        // set catalog connections
        for (ICatalogConnection catConn : this.catalogConnections) {
            LinkedCatalog linkedCatalog = contents.addNewLinkedCatalog();
            linkedCatalog.setCatalogURL(catConn.getCatalogURL().toString());
            linkedCatalog.setStatus(catConn.getStatus());
            linkedCatalog.setPushIntervalSeconds(catConn.getPushIntervalSeconds());
        }

        return contents;
    }

    private OperationsMetadata createOperationsMetadata() {
        CapabilitiesDocument skeleton = this.config.getCapabilitiesSkeleton();
        OperationsMetadata opMeData = skeleton.getCapabilities().getOperationsMetadata();
        // Operations
        for (Operation operation : opMeData.getOperationArray()) {
            for (DCP dcp : operation.getDCPArray()) {
                HTTP http = dcp.getHTTP();
                for (RequestMethodType get : http.getGetArray()) {
                    get.setHref(this.serviceUri.toString());
                }
                for (RequestMethodType post : http.getPostArray()) {
                    post.setHref(this.serviceUri.toString());
                }
            }
            // parameter acceptVersion in operation GetCapabilities
            for (DomainType parameter : operation.getParameterArray()) {
                if (parameter.getName().equals(ACCEPT_VERSIONS_PARAMETER_NAME)) {
                    AllowedValues newAllowedValues = AllowedValues.Factory.newInstance();
                    for (String s : this.config.getAcceptedServiceVersions()) {
                        newAllowedValues.addNewValue().setStringValue(s);
                    }
                    // replace the old values
                    parameter.setAllowedValues(newAllowedValues);

                    break;
                }
            }
        }
        // parameter version in other operations
        for (DomainType parameter : opMeData.getParameterArray()) {
            if (parameter.getName().equals(VERSION_PARAMETER_NAME)) {
                AllowedValues newAllowedValues = AllowedValues.Factory.newInstance();
                ValueType value = newAllowedValues.addNewValue();
                value.setStringValue(this.config.getServiceVersion());
                parameter.setAllowedValues(newAllowedValues);
            }
        }
        return opMeData;
    }

    private ServiceIdentification createServiceIdentification() {
        ServiceIdentification servIdent = this.config.getCapabilitiesSkeleton().getCapabilities().getServiceIdentification();
        servIdent.getServiceType().setCodeSpace(this.serviceUri.toString());
        servIdent.setServiceTypeVersionArray(new String[] {this.config.getServiceVersion()});
        return servIdent;
    }

    private ServiceProvider createServiceProvider() {
        ServiceProvider servProv = this.config.getCapabilitiesSkeleton().getCapabilities().getServiceProvider();
        servProv.getProviderSite().setHref(this.serviceUri.toString());
        return servProv;
    }

    @Override
    public CapabilitiesDocument createXml() {
        CapabilitiesDocument capDoc = CapabilitiesDocument.Factory.newInstance();

        Capabilities caps = capDoc.addNewCapabilities();

        // set version
        caps.setVersion(this.config.getServiceVersion());

        // set updateSequence
        caps.setUpdateSequence(this.config.getUpdateSequence());

        if (this.sections != null) {
            // set all
            if (this.sections.contains(CapabilitiesSection.All)) {
                caps.setServiceIdentification(createServiceIdentification());
                caps.setServiceProvider(createServiceProvider());
                caps.setOperationsMetadata(createOperationsMetadata());
                caps.setContents(createContents());
            }
            else {
                // set ServiceIdentification
                if (this.sections.contains(CapabilitiesSection.ServiceIdentification)) {
                    caps.setServiceIdentification(createServiceIdentification());
                }
                // set ServiceProvider
                if (this.sections.contains(CapabilitiesSection.ServiceProvider)) {
                    caps.setServiceProvider(createServiceProvider());
                }
                // set OperationsMetadata
                if (this.sections.contains(CapabilitiesSection.OperationsMetadata)) {
                    caps.setOperationsMetadata(createOperationsMetadata());
                }
                // set Contents
                if (this.sections.contains(CapabilitiesSection.Contents)
                        && (this.harvestedServices.size() != 0 || this.catalogConnections.size() != 0)) {
                    caps.setContents(createContents());
                }
            }
        }

        XmlTools.addSirAndSensorMLSchemaLocation(caps);

        if (this.config.isValidateResponses()) {
            if ( !capDoc.validate())
                log.warn("Service created invalid document!\n" + XmlTools.validateAndIterateErrors(capDoc));
        }

        return capDoc;
    }

    public Collection<ICatalogConnection> getCatalogConnection() {
        return this.catalogConnections;
    }

    public ArrayList<CapabilitiesSection> getSections() {
        return this.sections;
    }

    public Collection<SirService> getServices() {
        return this.harvestedServices;
    }

    public void setCatalogConnection(Collection<ICatalogConnection> collection) {
        this.catalogConnections = collection;
    }

    public void setSections(ArrayList<CapabilitiesSection> sections) {
        this.sections = sections;
    }

    public void setServices(Collection<SirService> services) {
        this.harvestedServices = services;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SirGetCapabilitiesResponse [");
        if (this.catalogConnections != null) {
            builder.append("catalogConnections=");
            builder.append(this.catalogConnections);
            builder.append(", ");
        }
        if (this.sections != null) {
            builder.append("sections=");
            builder.append(this.sections);
            builder.append(", ");
        }
        if (this.harvestedServices != null) {
            builder.append("harvestedServices=");
            builder.append(this.harvestedServices);
            builder.append(", ");
        }
        if (this.serviceUri != null) {
            builder.append("serviceUri=");
            builder.append(this.serviceUri);
        }
        builder.append("]");
        return builder.toString();
    }

}