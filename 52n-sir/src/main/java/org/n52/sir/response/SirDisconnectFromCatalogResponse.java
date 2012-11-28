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

package org.n52.sir.response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.n52.sir.SirConfigurator;
import org.n52.sir.SirConstants;
import org.n52.sir.util.XmlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sir.x032.DisconnectFromCatalogResponseDocument;
import org.x52North.sir.x032.DisconnectFromCatalogResponseDocument.DisconnectFromCatalogResponse;

/**
 * @author Jan Schulte
 * 
 */
public class SirDisconnectFromCatalogResponse implements ISirResponse {

    private static Logger log = LoggerFactory.getLogger(SirDisconnectFromCatalogResponse.class);

    /**
     * the url to the catalog service
     */
    private String catalogUrl;

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.response.ISirResponse#getByteArray()
     */
    @Override
    public byte[] getByteArray() throws IOException, TransformerException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DisconnectFromCatalogResponseDocument disconFromCatRespDoc = parseToResponseDocument();
        disconFromCatRespDoc.save(baos, XmlTools.xmlOptionsForNamespaces());
        byte[] bytes = baos.toByteArray();
        return bytes;
    }

    /**
     * @return the cswUrl
     */
    public String getCatalogUrl() {
        return this.catalogUrl;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.response.ISirResponse#getContentLength()
     */
    @Override
    public int getContentLength() throws IOException, TransformerException {
        return getByteArray().length;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.response.ISirResponse#getContentType()
     */
    @Override
    public String getContentType() {
        return SirConstants.CONTENT_TYPE_XML;
    }

    private DisconnectFromCatalogResponseDocument parseToResponseDocument() {
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
