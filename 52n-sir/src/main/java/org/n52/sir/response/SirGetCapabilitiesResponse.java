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
        OperationsMetadata opMeData = SirConfigurator.getInstance().getCapabilitiesSkeleton().getCapabilities().getOperationsMetadata();
        // Operations
        for (Operation operation : opMeData.getOperationArray()) {
            for (DCP dcp : operation.getDCPArray()) {
                HTTP http = dcp.getHTTP();
                for (RequestMethodType get : http.getGetArray()) {
                    get.setHref(SirConfigurator.getInstance().getServiceUrl().toString());
                }
                for (RequestMethodType post : http.getPostArray()) {
                    post.setHref(SirConfigurator.getInstance().getServiceUrl().toString());
                }
            }
            // parameter acceptVersion in operation GetCapabilities
            for (DomainType parameter : operation.getParameterArray()) {
                if (parameter.getName().equals(ACCEPT_VERSIONS_PARAMETER_NAME)) {
                    AllowedValues newAllowedValues = AllowedValues.Factory.newInstance();
                    for (String s : SirConfigurator.getInstance().getAcceptedServiceVersions()) {
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
                value.setStringValue(SirConfigurator.getInstance().getServiceVersion());
                parameter.setAllowedValues(newAllowedValues);
            }
        }
        return opMeData;
    }

    private ServiceIdentification createServiceIdentification() {
        ServiceIdentification servIdent = SirConfigurator.getInstance().getCapabilitiesSkeleton().getCapabilities().getServiceIdentification();
        servIdent.getServiceType().setCodeSpace(SirConfigurator.getInstance().getServiceUrl().toString());
        servIdent.setServiceTypeVersionArray(new String[] {SirConfigurator.getInstance().getServiceVersion()});
        return servIdent;
    }

    private ServiceProvider createServiceProvider() {
        ServiceProvider servProv = SirConfigurator.getInstance().getCapabilitiesSkeleton().getCapabilities().getServiceProvider();
        servProv.getProviderSite().setHref(SirConfigurator.getInstance().getServiceUrl().toString());
        return servProv;
    }

    @Override
    public CapabilitiesDocument createXml() {
        CapabilitiesDocument capDoc = CapabilitiesDocument.Factory.newInstance();

        Capabilities caps = capDoc.addNewCapabilities();

        // set version
        caps.setVersion(SirConfigurator.getInstance().getServiceVersion());

        // set updateSequence
        caps.setUpdateSequence(SirConfigurator.getInstance().getUpdateSequence());

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

        if (SirConfigurator.getInstance().isValidateResponses()) {
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
        }
        builder.append("]");
        return builder.toString();
    }

}