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

import net.opengis.swe.x101.PhenomenonType;

import org.apache.xmlbeans.XmlOptions;
import org.n52.sor.ISorResponse;
import org.n52.sor.datastructures.IDictionaryEntry;
import org.n52.sor.util.XmlTools;

/**
 * @created 20-Okt-2008 16:44:48
 * @version 1.0
 */
public class SorGetDefinitionResponse implements ISorResponse {

    /**
     * The requested input URI
     */
    private String inputURI;

    /**
     * The dictionary entry of the phenomenon
     */
    private IDictionaryEntry phenomenonDefinition;

    /**
     * @return Returns the response as byte[]
     * @throws IOException
     *         if getting the byte[] failed
     * 
     */
    @Override
    public byte[] getByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // response document is swe:Phenomenon
        PhenomenonType phen = this.phenomenonDefinition.getPhenomenon();

        XmlTools.insertAttributesForValidationSweGml(phen);

        XmlOptions options = new XmlOptions(XmlTools.DEFAULT_OPTIONS);
        options.setSaveOuter();

        phen.save(baos, options);
        byte[] bytes = baos.toByteArray();
        return bytes;
    }

    /**
     * @return Returns the length of the content in bytes
     * @throws IOException
     *         if getting the content length failed
     */
    @Override
    public int getContentLength() throws IOException {
        return getByteArray().length;
    }

    /**
     * @return the inputURI
     */
    public String getInputURI() {
        return this.inputURI;
    }

    /**
     * @return the phenomenonDefinition
     */
    public IDictionaryEntry getPhenomenonDefinition() {
        return this.phenomenonDefinition;
    }

    /**
     * @param inputURI
     *        the inputURI to set
     */
    public void setInputURI(String inputURI) {
        this.inputURI = inputURI;
    }

    /**
     * @param phenomenonDefinition
     *        the phenomenonDefinition to set
     */
    public void setPhenomenonDefinition(IDictionaryEntry phenomenonDefinition) {
        this.phenomenonDefinition = phenomenonDefinition;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SorGetDefinitionResponse: ");
        sb.append(" inputURI: " + this.inputURI);
        sb.append(" PhenomenonDefinition: " + this.phenomenonDefinition);
        return sb.toString();
    }

}