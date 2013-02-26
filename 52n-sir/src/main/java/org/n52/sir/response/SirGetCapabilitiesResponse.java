/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
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

import org.n52.sir.SirConfigurator;
import org.n52.sir.SirConfigurator.Section;
import org.n52.sir.catalog.ICatalogConnection;
import org.n52.sir.datastructure.SirService;
import org.n52.sir.util.XmlTools;
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

    /**
     * Collection of catalog connections
     */
    private Collection<ICatalogConnection> catalogConnection;

    private ArrayList<Section> sections;

    /**
     * Collection of all services harvested by the SIR
     */
    private Collection<SirService> services;

    private Contents createContents() {
        Contents contents = Contents.Factory.newInstance();

        // set list of services
        for (SirService service : this.services) {
            HarvestedService harvestedService = contents.addNewHarvestedService();
            harvestedService.setServiceType(service.getType());
            harvestedService.setServiceURL(service.getUrl());
        }

        // set catalog connections
        for (ICatalogConnection catConn : this.catalogConnection) {
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
                    // remove existing placeholder
                    for (int i = 0; i < parameter.getAllowedValues().getValueArray().length; i++) {
                        parameter.getAllowedValues().removeValue(i);
                    }
                    // set value
                    AllowedValues values = parameter.getAllowedValues();
                    for (String s : SirConfigurator.getInstance().getAcceptedServiceVersions()) {
                        values.addNewValue().setStringValue(s);
                    }
                }
            }
        }
        // parameter version
        for (DomainType parameter : opMeData.getParameterArray()) {
            if (parameter.getName().equals(VERSION_PARAMETER_NAME)) {
                // remove existing
                for (int i = 0; i < parameter.getAllowedValues().getValueArray().length; i++) {
                    parameter.getAllowedValues().removeValue(i);
                }
                // set value
                ValueType value = parameter.getAllowedValues().addNewValue();
                value.setStringValue(SirConfigurator.getInstance().getServiceVersion());
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
            if (this.sections.contains(Section.All)) {
                caps.setServiceIdentification(createServiceIdentification());
                caps.setServiceProvider(createServiceProvider());
                caps.setOperationsMetadata(createOperationsMetadata());
                caps.setContents(createContents());
            }
            else {
                // set ServiceIdentification
                if (this.sections.contains(Section.ServiceIdentification)) {
                    caps.setServiceIdentification(createServiceIdentification());
                }
                // set ServiceProvider
                if (this.sections.contains(Section.ServiceProvider)) {
                    caps.setServiceProvider(createServiceProvider());
                }
                // set OperationsMetadata
                if (this.sections.contains(Section.OperationsMetadata)) {
                    caps.setOperationsMetadata(createOperationsMetadata());
                }
                // set Contents
                if (this.sections.contains(Section.Contents)
                        && (this.services.size() != 0 || this.catalogConnection.size() != 0)) {
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

    /**
     * @return the catalog collections
     */
    public Collection<ICatalogConnection> getCatalogConnection() {
        return this.catalogConnection;
    }

    /**
     * @return the sections
     */
    public ArrayList<Section> getSections() {
        return this.sections;
    }

    /**
     * @return the services
     */
    public Collection<SirService> getServices() {
        return this.services;
    }

    /**
     * @param collection
     *        the catalog connections to set
     */
    public void setCatalogConnection(Collection<ICatalogConnection> collection) {
        this.catalogConnection = collection;
    }

    /**
     * @param sections
     *        the sections to set
     */
    public void setSections(ArrayList<Section> sections) {
        this.sections = sections;
    }

    /**
     * @param services
     *        the services to set
     */
    public void setServices(Collection<SirService> services) {
        this.services = services;
    }

}