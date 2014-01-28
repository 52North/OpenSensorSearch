/**
 * Copyright 2013 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
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
import java.util.Collection;

import org.n52.sor.ISorResponse;
import org.n52.sor.util.XmlTools;
import org.x52North.sor.x031.GetDefinitionURIsResponseDocument;
import org.x52North.sor.x031.GetDefinitionURIsResponseDocument.GetDefinitionURIsResponse;

/**
 * @created 20-Okt-2008 16:44:48
 * @version 1.0
 */
public class SorGetDefinitionURIsResponse implements ISorResponse {

    /**
     * List of all definitions to a requested URI
     */
    private Collection<String> definitionList;

    /**
     * @return Returns the response as byte[]
     * @throws IOException
     *         if getting the byte[] failed
     * 
     */
    @Override
    public byte[] getByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // build response document
        GetDefinitionURIsResponseDocument getAllDefsRespDoc = GetDefinitionURIsResponseDocument.Factory.newInstance();
        GetDefinitionURIsResponse getAllDefsResp = getAllDefsRespDoc.addNewGetDefinitionURIsResponse();

        getAllDefsResp.setTotalNumberOfMatchingURIs(this.definitionList.size());

        // add all definition urns
        for (String phenom : this.definitionList) {
            getAllDefsResp.addDefinitionURI(phenom);
        }

        XmlTools.insertAttributesForValidationSOR(getAllDefsResp);

        getAllDefsRespDoc.save(baos, XmlTools.DEFAULT_OPTIONS);
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
     * @return the definitionList
     */
    public Collection<String> getDefinitionList() {
        return this.definitionList;
    }

    /**
     * 
     * @param definitionList
     *        the definitionList to set
     */
    public void setDefinitionList(Collection<String> definitionList) {
        this.definitionList = definitionList;
    }

    @Override
    public String toString() {
        return "GetDefinitionURIsResponse";
    }

}