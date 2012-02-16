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
package org.n52.sir.client;

import org.n52.sir.SirConfigurator;
import org.n52.sir.SirConstants;
import org.n52.sir.util.XmlTools;

import de.uniMuenster.swsl.sir.HarvestServiceRequestDocument;
import de.uniMuenster.swsl.sir.HarvestServiceRequestDocument.HarvestServiceRequest;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class HarvestServiceBean extends AbstractBean {

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
