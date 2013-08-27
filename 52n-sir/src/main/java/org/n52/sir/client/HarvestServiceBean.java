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
import org.x52North.sir.x032.HarvestServiceRequestDocument;
import org.x52North.sir.x032.HarvestServiceRequestDocument.HarvestServiceRequest;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class HarvestServiceBean extends TestClientBean {

    private String harvestInterval = "";

    private String serviceType = "";

    private String serviceUrl = "";

    /**
     * 
     */
    public HarvestServiceBean() {
        // empty constructor required for JSPs
    }

    /**
     * @param serviceUrl
     * @param serviceType
     */
    public HarvestServiceBean(String serviceUrl, String serviceType) {
        this.serviceUrl = serviceUrl;
        this.serviceType = serviceType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.client.beans.IBean#buildRequest()
     */
    @Override
    public void buildRequest() {
        this.responseString = "";

        HarvestServiceRequestDocument requestDoc = HarvestServiceRequestDocument.Factory.newInstance();
        HarvestServiceRequest request = requestDoc.addNewHarvestServiceRequest();
        request.setService(SirConstants.SERVICE_NAME);
        request.setVersion(SirConfigurator.getInstance().getServiceVersionEnum());

        // url
        if (this.serviceUrl != null && !this.serviceUrl.equals("")) {
            request.setServiceURL(this.serviceUrl);
        }
        // type
        if (this.serviceType != null && !this.serviceType.equals("")) {
            request.setServiceType(this.serviceType);
        }
        // interval
        if (this.harvestInterval != null && !this.harvestInterval.equals("")) {
            request.setHarvestIntervalSeconds(Integer.parseInt(this.harvestInterval));
        }

        XmlTools.addSirAndSensorMLSchemaLocation(request);

        if (requestDoc.validate())
            this.requestString = requestDoc.xmlText(XmlTools.xmlOptionsForNamespaces());
        else
            this.requestString = XmlTools.validateAndIterateErrors(requestDoc);
    }

    /**
     * @return the harvestInterval
     */
    public String getHarvestInterval() {
        return this.harvestInterval;
    }

    /**
     * @return the serviceType
     */
    public String getServiceType() {
        return this.serviceType;
    }

    /**
     * @return the serviceUrl
     */
    public String getServiceUrl() {
        return this.serviceUrl;
    }

    /**
     * @param harvestInterval
     *        the harvestInterval to set
     */
    public void setHarvestInterval(String harvestInterval) {
        this.harvestInterval = harvestInterval;
    }

    /**
     * @param serviceType
     *        the serviceType to set
     */
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    /**
     * @param serviceUrl
     *        the serviceUrl to set
     */
    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

}
