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

import org.n52.sir.SirConfigurator;
import org.n52.sir.util.XmlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sir.x032.DisconnectFromCatalogResponseDocument;
import org.x52North.sir.x032.DisconnectFromCatalogResponseDocument.DisconnectFromCatalogResponse;

/**
 * @author Jan Schulte
 * 
 */
public class SirDisconnectFromCatalogResponse extends AbstractXmlResponse {

    private static Logger log = LoggerFactory.getLogger(SirDisconnectFromCatalogResponse.class);

    /**
     * the url to the catalog service
     */
    private String catalogUrl;

    @Override
    public DisconnectFromCatalogResponseDocument createXml() {
        DisconnectFromCatalogResponseDocument document = DisconnectFromCatalogResponseDocument.Factory.newInstance();
        DisconnectFromCatalogResponse disconCat = document.addNewDisconnectFromCatalogResponse();

        XmlTools.addSirAndSensorMLSchemaLocation(disconCat);

        // set csw url
        disconCat.setCatalogURL(this.catalogUrl);

        if (SirConfigurator.getInstance().isValidateResponses()) {
            if ( !document.validate())
                log.warn("Service created invalid document!\n" + XmlTools.validateAndIterateErrors(document));
        }

        return document;
    }

    /**
     * @return the cswUrl
     */
    public String getCatalogUrl() {
        return this.catalogUrl;
    }

    /**
     * @param cswUrl
     *        the cswUrl to set
     */
    public void setCatalogUrl(String catalogUrl) {
        this.catalogUrl = catalogUrl;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SirDisconnectFromCatalogResponse: ");
        sb.append("CatalogUrl: " + this.catalogUrl);
        return sb.toString();
    }

}
