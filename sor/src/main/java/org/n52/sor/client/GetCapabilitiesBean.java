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

package org.n52.sor.client;

import net.opengis.ows.x11.AcceptVersionsType;
import net.opengis.ows.x11.SectionsType;

import org.n52.sor.response.SorGetCapabilitiesResponse.Section;
import org.n52.sor.util.XmlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sor.GetCapabilitiesDocument;
import org.x52North.sor.GetCapabilitiesDocument.GetCapabilities;

/**
 * @author Jan Schulte
 * 
 */
public class GetCapabilitiesBean extends AbstractBean {

    private static Logger log = LoggerFactory.getLogger(GetCapabilitiesBean.class);

    private String service;

    private String updateSequence = "";

    private String acceptVersions = "";

    private boolean serviceIdentification;

    private boolean serviceProvider;

    private boolean operationsMetadata;

    private boolean contents;

    private boolean all;

    /**
     * 
     */
    public GetCapabilitiesBean() {
        //
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
            acceptVersion.addVersion(this.acceptVersions);
        }
        // Sections
        if (this.all || this.contents || this.operationsMetadata || this.serviceIdentification || this.serviceProvider) {
            SectionsType sections = request.addNewSections();
            // service Identification
            if (this.serviceIdentification) {
                sections.addSection(Section.SERVICEIDENTIFICATION.name());
            }
            // service provider
            if (this.serviceProvider) {
                sections.addSection(Section.SERVICEPROVIDER.name());
            }
            // operations Metadata
            if (this.operationsMetadata) {
                sections.addSection(Section.OPERATIONSMETADATA.name());
            }
            // Contents
            if (this.contents) {
                sections.addSection(Section.CONTENTS.name());
            }
            // all
            if (this.all) {
                sections.addSection(Section.ALL.name());
            }
        }

        if ( !requestDoc.validate()) {
            log.warn("Request is NOT valid, service may return error!\n"
                    + XmlTools.validateAndIterateErrors(requestDoc));
        }

        this.requestString = requestDoc.xmlText(XmlTools.DEFAULT_OPTIONS);
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