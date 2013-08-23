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
import org.x52North.sir.x032.ConnectToCatalogRequestDocument;
import org.x52North.sir.x032.ConnectToCatalogRequestDocument.ConnectToCatalogRequest;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class ConnectToCatalogBean extends TestClientBean {

    private String catalogUrl = "";

    private int pushInterval = 0;

    /**
     * 
     */
    public ConnectToCatalogBean() {
        //
    }

    /**
     * @param catalogUrl
     * @param pushInterval
     */
    public ConnectToCatalogBean(String catalogUrl, int pushInterval) {
        this.catalogUrl = catalogUrl;
        this.pushInterval = pushInterval;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.client.beans.IBean#buildRequest()
     */
    @Override
    public void buildRequest() {
        this.responseString = "";

        ConnectToCatalogRequestDocument requestDoc = ConnectToCatalogRequestDocument.Factory.newInstance();
        ConnectToCatalogRequest request = requestDoc.addNewConnectToCatalogRequest();
        request.setService(SirConstants.SERVICE_NAME);
        request.setVersion(SirConfigurator.getInstance().getServiceVersionEnum());

        // cswURL
        if (this.catalogUrl != null && !this.catalogUrl.equals("")) {
            request.setCatalogURL(this.catalogUrl);
        }
        else {
            this.requestString = "<!-- Catalog URL is mandatory! -->";
            return;
        }

        // pushInterval
        if (this.pushInterval > 0) {
            request.setPushIntervalSeconds(this.pushInterval);
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
     * @return the pushIntervalSeconds
     */
    public int getPushInterval() {
        return this.pushInterval;
    }

    /**
     * @param cswUrl
     *        the cswUrl to set
     */
    public void setCatalogUrl(String cswUrl) {
        this.catalogUrl = cswUrl;
    }

    /**
     * @param pushIntervalSeconds
     *        the pushIntervalSeconds to set
     */
    public void setPushInterval(int pushInterval) {
        this.pushInterval = pushInterval;
    }

}
