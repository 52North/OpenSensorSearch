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
import org.x52North.sor.x031.DeleteDefinitionResponseDocument;
import org.x52North.sor.x031.DeleteDefinitionResponseDocument.DeleteDefinitionResponse;

/**
 * @author Jan Schulte
 * 
 */
public class SorDeleteDefinitionResponse implements ISorResponse {

    private String definitionURI;

    private boolean updateSuccessfull;

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.response.ISorResponse#getByteArray()
     */
    @Override
    public byte[] getByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // build response document
        DeleteDefinitionResponseDocument delDefRespDoc = DeleteDefinitionResponseDocument.Factory.newInstance();
        DeleteDefinitionResponse delDefResp = delDefRespDoc.addNewDeleteDefinitionResponse();
        // add DefinitionURI
        delDefResp.setDefinitionURI(this.definitionURI);

        XmlTools.insertAttributesForValidationSOR(delDefResp);

        delDefRespDoc.save(baos, XmlTools.DEFAULT_OPTIONS);
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
     * @return the updateSuccesfull
     */
    public boolean isUpdateSuccessfull() {
        return this.updateSuccessfull;
    }

    /**
     * @param definitionURI
     *        the definitionURI to set
     */
    public void setDefinitionURI(String definitionURI) {
        this.definitionURI = definitionURI;
    }

    /**
     * @param updateSuccesfull
     *        the updateSuccesfull to set
     */
    public void setUpdateSuccessfull(boolean updateSuccessfull) {
        this.updateSuccessfull = updateSuccessfull;
    }

}