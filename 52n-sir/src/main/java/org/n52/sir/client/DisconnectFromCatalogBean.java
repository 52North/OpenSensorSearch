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

import org.n52.sir.SirConfigurator;
import org.n52.sir.SirConstants;
import org.n52.sir.util.XmlTools;
import org.x52North.sir.x032.DisconnectFromCatalogRequestDocument;
import org.x52North.sir.x032.DisconnectFromCatalogRequestDocument.DisconnectFromCatalogRequest;

/**
 * @author Jan Schulte
 * 
 */
public class DisconnectFromCatalogBean extends TestClientBean {

    private String catalogUrl = "";

    /**
     * 
     */
    public DisconnectFromCatalogBean() {
        // empty constructor required for JSPs
    }

    /**
     * @param catalogUrl
     */
    public DisconnectFromCatalogBean(String catalogUrl) {
        this.catalogUrl = catalogUrl;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.client.beans.IBean#buildRequest()
     */
    @Override
    public void buildRequest() {
        this.responseString = "";

        DisconnectFromCatalogRequestDocument requestDoc = DisconnectFromCatalogRequestDocument.Factory.newInstance();
        DisconnectFromCatalogRequest request = requestDoc.addNewDisconnectFromCatalogRequest();
        request.setService(SirConstants.SERVICE_NAME);
        request.setVersion(SirConfigurator.getInstance().getServiceVersionEnum());

        // cswURL
        if (this.catalogUrl != null && !this.catalogUrl.equals("")) {
            request.setCatalogURL(this.catalogUrl);
        }

        XmlTools.addSirAndSensorMLSchemaLocation(request);

        if (requestDoc.validate())
            this.requestString = requestDoc.xmlText(XmlTools.xmlOptionsForNamespaces());
        else
            this.requestString = XmlTools.validateAndIterateErrors(requestDoc);
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
    public void setCatalogUrl(String cswUrl) {
        this.catalogUrl = cswUrl;
    }

}
