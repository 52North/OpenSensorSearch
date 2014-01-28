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
package org.n52.sir.response;

import java.net.URL;

import org.n52.oss.util.XmlTools;
import org.n52.sir.SirConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sir.x032.ConnectToCatalogResponseDocument;
import org.x52North.sir.x032.ConnectToCatalogResponseDocument.ConnectToCatalogResponse;

/**
 * @author Jan Schulte
 * 
 */
public class SirConnectToCatalogResponse extends AbstractXmlResponse {

    private static Logger log = LoggerFactory.getLogger(SirConnectToCatalogResponse.class);

    /**
     * the url to the catalog service
     */
    private URL catalogUrl;

    @Override
    public ConnectToCatalogResponseDocument createXml() {
        ConnectToCatalogResponseDocument document = ConnectToCatalogResponseDocument.Factory.newInstance();
        ConnectToCatalogResponse conCatResp = document.addNewConnectToCatalogResponse();

        // set csw URL
        conCatResp.setCatalogURL(this.catalogUrl.toString());

        // add schema location for validation
        XmlTools.addSirAndSensorMLSchemaLocation(conCatResp);

        if (SirConfigurator.getInstance().isValidateResponses()) {
            if ( !document.validate())
                log.warn("Service created invalid document!\n" + XmlTools.validateAndIterateErrors(document));
        }

        return document;
    }

    /**
     * @return the catalogUrl
     */
    public URL getCatalogUrl() {
        return this.catalogUrl;
    }

    /**
     * @param cswUrl
     *        the cswUrl to set
     */
    public void setCatalogUrl(URL catalogUrl) {
        this.catalogUrl = catalogUrl;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SirConnectToCatalogResponse: ");
        sb.append("CatalogUrl: " + this.catalogUrl);
        return sb.toString();
    }

}
