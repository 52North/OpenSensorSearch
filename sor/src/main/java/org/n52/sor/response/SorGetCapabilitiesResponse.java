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
package org.n52.sor.response;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import net.opengis.ows.x11.OperationDocument.Operation;
import net.opengis.ows.x11.OperationsMetadataDocument.OperationsMetadata;
import net.opengis.ows.x11.ServiceIdentificationDocument.ServiceIdentification;
import net.opengis.ows.x11.ServiceProviderDocument.ServiceProvider;

import org.apache.xmlbeans.XmlException;
import org.n52.sor.ISorResponse;
import org.n52.sor.OwsExceptionReport;
import org.n52.sor.PhenomenonManager;
import org.n52.sor.PropertiesManager;
import org.n52.sor.util.XmlTools;
import org.x52North.sor.x031.CapabilitiesDocument;
import org.x52North.sor.x031.CapabilitiesDocument.Capabilities;
import org.x52North.sor.x031.CapabilitiesDocument.Capabilities.Contents;

/**
 * @created 20-Okt-2008 16:44:48
 * @version 1.0
 */
public class SorGetCapabilitiesResponse implements ISorResponse {

    private ArrayList<CapabilitiesSection> sections;

    private CapabilitiesDocument capabilitiesSkeleton;

    public SorGetCapabilitiesResponse() {
        //
    }

    /**
     * @param operation
     */
    private void addStandardServiceEndpointURLs(Operation operation) {
        operation.getDCPArray(0).getHTTP().getGetArray(0).setHref(PropertiesManager.getInstance().getServiceEndpointGet());
        operation.getDCPArray(0).getHTTP().getPostArray(0).setHref(PropertiesManager.getInstance().getServiceEndpointPost());
    }

    private Contents createContents() throws OwsExceptionReport {
        // set contents
        Contents contents = Contents.Factory.newInstance();
        // set number of entries
        contents.setNumberOfEntries(PhenomenonManager.getInstance().getPhenomenaList().size());
        // set keywords
        contents.setKeywordArray(PropertiesManager.getInstance().getKeywords());
        // set application domain
        contents.setApplicationDomainArray(PropertiesManager.getInstance().getApplicationDomains());
        // ontology repository URL
        contents.setOntologyRepositoryURLArray(PropertiesManager.getInstance().getOntologyRepositoryURLs());
        return contents;
    }

    /**
     * 
     * @return
     */
    private OperationsMetadata createOperationsMetadata() {
        OperationsMetadata opMeta = this.capabilitiesSkeleton.getCapabilities().getOperationsMetadata();
        Operation[] operations = opMeta.getOperationArray();
        for (Operation operation : operations) {
            if (operation.getName().equals("GetCapabilities")) {
                addStandardServiceEndpointURLs(operation);
            }
            else if (operation.getName().equals("GetDefinition")) {
                addStandardServiceEndpointURLs(operation);
            }
            else if (operation.getName().equals("GetDefinitionURIs")) {
                addStandardServiceEndpointURLs(operation);
            }
            else if (operation.getName().equals("GetMatchingDefinitions")) {
                addStandardServiceEndpointURLs(operation);
            }
            else if (operation.getName().equals("InsertDefinition")) {
                operation.getDCPArray(0).getHTTP().getPostArray(0).setHref(PropertiesManager.getInstance().getServiceEndpointPost());
            }
            else if (operation.getName().equals("DeleteDefinition")) {
                operation.getDCPArray(0).getHTTP().getPostArray(0).setHref(PropertiesManager.getInstance().getServiceEndpointPost());
            }
        }
        return opMeta;
    }

    private ServiceIdentification createServiceIdentification() {
        ServiceIdentification servIdent = this.capabilitiesSkeleton.getCapabilities().getServiceIdentification();
        // set serviceURL
        servIdent.getServiceType().setCodeSpace(PropertiesManager.getInstance().getServiceURL());
        // set Versions
        servIdent.addNewServiceTypeVersion().setStringValue(PropertiesManager.getInstance().getServiceVersion());
        return servIdent;
    }

    private ServiceProvider createServiceProvider() {
        ServiceProvider servProv = this.capabilitiesSkeleton.getCapabilities().getServiceProvider();
        // set serviceURL
        servProv.getProviderSite().setHref(PropertiesManager.getInstance().getServiceURL());
        return servProv;
    }

    /**
     * @return Returns the response as byte[]
     * @throws IOException
     *         if getting the byte[] failed
     * @throws OwsExceptionReport
     * 
     */
    @Override
    public byte[] getByteArray() throws IOException, OwsExceptionReport {
        // load skeleton
        try {
            this.capabilitiesSkeleton = CapabilitiesDocument.Factory.parse(new File(PropertiesManager.getInstance().getCapabilitieSkeleton()));
        }
        catch (XmlException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CapabilitiesDocument capRespDoc = CapabilitiesDocument.Factory.newInstance();
        Capabilities caps = capRespDoc.addNewCapabilities();
        // set version
        caps.setVersion(PropertiesManager.getInstance().getServiceVersion());
        // set updateSequence
        caps.setUpdateSequence(PropertiesManager.getInstance().getUpdateSequence());
        if (this.sections != null) {
            // set all
            if (this.sections.contains(CapabilitiesSection.ALL)) {
                caps.setServiceIdentification(createServiceIdentification());
                caps.setServiceProvider(createServiceProvider());
                caps.setOperationsMetadata(createOperationsMetadata());
                caps.setContents(createContents());
            }
            // set ServiceIdentification
            if (this.sections.contains(CapabilitiesSection.SERVICEIDENTIFICATION)) {
                caps.setServiceIdentification(createServiceIdentification());
            }
            // set ServiceProcider
            if (this.sections.contains(CapabilitiesSection.SERVICEPROVIDER)) {
                caps.setServiceProvider(createServiceProvider());
            }
            // set OperationsMetadata
            if (this.sections.contains(CapabilitiesSection.OPERATIONSMETADATA)) {
                caps.setOperationsMetadata(createOperationsMetadata());
            }
            // set Contents
            if (this.sections.contains(CapabilitiesSection.CONTENTS)) {
                caps.setContents(createContents());
            }
        }

        XmlTools.insertAttributesForValidationSOR(caps);

        capRespDoc.save(baos, XmlTools.DEFAULT_OPTIONS);
        byte[] bytes = baos.toByteArray();
        return bytes;
    }

    /**
     * @return Returns the length of the content in bytes
     * @throws IOException
     *         if getting the content length failed
     * @throws OwsExceptionReport
     */
    @Override
    public int getContentLength() throws IOException, OwsExceptionReport {
        return getByteArray().length;
    }

    /**
     * @return the sections
     */
    public ArrayList<CapabilitiesSection> getSections() {
        return this.sections;
    }

    /**
     * @param sections
     *        the sections to set
     */
    public void setSections(ArrayList<CapabilitiesSection> sections) {
        this.sections = sections;
    }

    @Override
    public String toString() {
        return "GetCapabilitesResponse";
    }
}