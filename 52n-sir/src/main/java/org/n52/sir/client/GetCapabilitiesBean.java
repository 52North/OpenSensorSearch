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
package org.n52.sir.client;

import net.opengis.ows.x11.AcceptVersionsType;
import net.opengis.ows.x11.SectionsType;

import org.n52.sir.SirConfigurator;
import org.n52.sir.SirConfigurator.Section;
import org.n52.sir.util.XmlTools;
import org.x52North.sir.x032.GetCapabilitiesDocument;
import org.x52North.sir.x032.GetCapabilitiesDocument.GetCapabilities;

/**
 * @author Jan Schulte
 * 
 */
public class GetCapabilitiesBean extends TestClientBean {

    private String acceptVersions;

    private boolean all;

    private boolean contents;

    private boolean operationsMetadata;

    private String service;

    private boolean serviceIdentification;

    private boolean serviceProvider;

    private String updateSequence = "";

    /**
     * 
     */
    public GetCapabilitiesBean() {
        super();

        StringBuilder sb = new StringBuilder();
        String[] acceptedServiceVersions = SirConfigurator.getInstance().getAcceptedServiceVersions();
        for (String s : acceptedServiceVersions) {
            sb.append(s);
            sb.append(",");
        }
        sb.replace(sb.length() - 1, sb.length(), "");
        this.acceptVersions = sb.toString();
    }

    /**
     * @param service
     * @param updateSequence
     * @param acceptVersions
     * @param serviceIdentification
     * @param serviceProvider
     * @param operationsMetadata
     * @param contents
     * @param all
     */
    public GetCapabilitiesBean(String service,
                               String updateSequence,
                               String acceptVersions,
                               boolean serviceIdentification,
                               boolean serviceProvider,
                               boolean operationsMetadata,
                               boolean contents,
                               boolean all) {
        super();

        this.service = service;
        this.updateSequence = updateSequence;
        this.acceptVersions = acceptVersions;
        this.serviceIdentification = serviceIdentification;
        this.serviceProvider = serviceProvider;
        this.operationsMetadata = operationsMetadata;
        this.contents = contents;
        this.all = all;
    }

    @Override
    public void buildRequest() {
        this.responseString = "";

        GetCapabilitiesDocument requestDoc = GetCapabilitiesDocument.Factory.newInstance();
        GetCapabilities request = requestDoc.addNewGetCapabilities();

        // service
        request.setService(this.service);

        // update sequence
        if ( !this.updateSequence.isEmpty()) {
            request.setUpdateSequence(this.updateSequence);
        }

        // accept versions
        if ( !this.acceptVersions.isEmpty()) {
            AcceptVersionsType acceptVersion = request.addNewAcceptVersions();
            String[] versions = this.acceptVersions.split(",");
            for (String s : versions) {
                acceptVersion.addVersion(s);
            }
        }

        // Sections
        if (this.all || this.contents || this.operationsMetadata || this.serviceIdentification || this.serviceProvider) {
            SectionsType sections = request.addNewSections();

            // all
            if (this.all) {
                sections.addSection(Section.ServiceIdentification.name());
                sections.addSection(Section.ServiceProvider.name());
                sections.addSection(Section.OperationsMetadata.name());
                sections.addSection(Section.Contents.name());
            }

            else {
                // service Identification
                if (this.serviceIdentification) {
                    sections.addSection(Section.ServiceIdentification.name());
                }
                // service provider
                if (this.serviceProvider) {
                    sections.addSection(Section.ServiceProvider.name());
                }
                // operations Metadata
                if (this.operationsMetadata) {
                    sections.addSection(Section.OperationsMetadata.name());
                }
                // Contents
                if (this.contents) {
                    sections.addSection(Section.Contents.name());
                }
            }
        }

        // TODO implement handling of acceptFormats

        XmlTools.addSirAndSensorMLSchemaLocation(request);

        if (requestDoc.validate())
            this.requestString = requestDoc.xmlText(XmlTools.xmlOptionsForNamespaces());
        else
            this.requestString = XmlTools.validateAndIterateErrors(requestDoc);
    }

    /**
     * @return the acceptVersions
     */
    public String getAcceptVersions() {
        return this.acceptVersions;
    }

    /**
     * @return the service
     */
    public String getService() {
        return this.service;
    }

    /**
     * @return the updateSequence
     */
    public String getUpdateSequence() {
        return this.updateSequence;
    }

    /**
     * @return the all
     */
    public boolean isAll() {
        return this.all;
    }

    /**
     * @return the contents
     */
    public boolean isContents() {
        return this.contents;
    }

    /**
     * @return the operationsMetadata
     */
    public boolean isOperationsMetadata() {
        return this.operationsMetadata;
    }

    /**
     * @return the serviceIdentification
     */
    public boolean isServiceIdentification() {
        return this.serviceIdentification;
    }

    /**
     * @return the serviceProvider
     */
    public boolean isServiceProvider() {
        return this.serviceProvider;
    }

    /**
     * @param acceptVersions
     *        the acceptVersions to set
     */
    public void setAcceptVersions(String acceptVersions) {
        this.acceptVersions = acceptVersions;
    }

    /**
     * @param all
     *        the all to set
     */
    public void setAll(boolean all) {
        this.all = all;
    }

    /**
     * @param contents
     *        the contents to set
     */
    public void setContents(boolean contents) {
        this.contents = contents;
    }

    /**
     * @param operationsMetadata
     *        the operationsMetadata to set
     */
    public void setOperationsMetadata(boolean operationsMetadata) {
        this.operationsMetadata = operationsMetadata;
    }

    /**
     * @param service
     *        the service to set
     */
    public void setService(String service) {
        this.service = service;
    }

    /**
     * @param serviceIdentification
     *        the serviceIdentification to set
     */
    public void setServiceIdentification(boolean serviceIdentification) {
        this.serviceIdentification = serviceIdentification;
    }

    /**
     * @param serviceProvider
     *        the serviceProvider to set
     */
    public void setServiceProvider(boolean serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    /**
     * @param updateSequence
     *        the updateSequence to set
     */
    public void setUpdateSequence(String updateSequence) {
        this.updateSequence = updateSequence;
    }

}
