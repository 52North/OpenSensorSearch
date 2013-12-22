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
package org.n52.sor.response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.n52.sor.util.XmlTools;
import org.x52North.sor.x031.InsertDefinitionResponseDocument;
import org.x52North.sor.x031.InsertDefinitionResponseDocument.InsertDefinitionResponse;

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