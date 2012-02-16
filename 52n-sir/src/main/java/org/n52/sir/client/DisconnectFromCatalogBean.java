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

import de.uniMuenster.swsl.sir.DisconnectFromCatalogRequestDocument;
import de.uniMuenster.swsl.sir.DisconnectFromCatalogRequestDocument.DisconnectFromCatalogRequest;

/**
 * @author Jan Schulte
 * 
 */
public class DisconnectFromCatalogBean extends AbstractBean {

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

}
