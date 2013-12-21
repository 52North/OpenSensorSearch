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

package org.n52.sor.response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.n52.sor.util.XmlTools;
import org.x52North.sor.InsertDefinitionResponseDocument;
import org.x52North.sor.InsertDefinitionResponseDocument.InsertDefinitionResponse;

/**
 * @author Jan Schulte
 * 
 */
public class SorInsertDefinitionResponse implements ISorResponse {

    private String definitionURI;

    private boolean insertSuccessful;

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.response.ISorResponse#getByteArray()
     */
    @Override
    public byte[] getByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // build response document
        InsertDefinitionResponseDocument insDefRespDoc = InsertDefinitionResponseDocument.Factory.newInstance();
        InsertDefinitionResponse insDefResp = insDefRespDoc.addNewInsertDefinitionResponse();
        // add URI
        insDefResp.setDefinitionURI(this.definitionURI);

        XmlTools.insertAttributesForValidationSOR(insDefResp);

        insDefRespDoc.save(baos, XmlTools.DEFAULT_OPTIONS);
        byte[] bytes = baos.toByteArray();
        return bytes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.response.ISorResponse#getContentLength()
     */
    @Override
    public int getContentLength() throws IOException {
        return getByteArray().length;
    }

    /**
     * @return the definitionURI
     */
    public String getDefinitionURI() {
        return this.definitionURI;
    }

    /**
     * @return the updateSuccessfull
     */
    public boolean isInsertSuccessful() {
        return this.insertSuccessful;
    }

    /**
     * @param definitionURI
     *        the definitionURI to set
     */
    public void setDefinitionURI(String definitionURI) {
        this.definitionURI = definitionURI;
    }

    /**
     * @param updateSuccessfull
     *        the updateSuccessfull to set
     */
    public void setInsertSuccessful(boolean updateSuccessfull) {
        this.insertSuccessful = updateSuccessfull;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SorInsertDefinitionResponse [insert successful: ");
        sb.append(this.insertSuccessful);
        sb.append("; definition URI: ");
        sb.append(this.definitionURI);
        sb.append("]");
        return sb.toString();
    }

}