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
import java.util.Arrays;
import java.util.Collection;

import org.n52.sor.util.XmlTools;
import org.x52North.sor.x031.GetMatchingDefinitionsResponseDocument;
import org.x52North.sor.x031.GetMatchingDefinitionsResponseDocument.GetMatchingDefinitionsResponse;

/**
 * @created 20-Okt-2008 16:44:48
 * @version 1.0
 */
public class SorGetMatchingDefinitionsResponse implements ISorResponse {

    /**
     * All matching results to the input URI
     */
    private Collection<String> matchingURI;

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
        GetMatchingDefinitionsResponseDocument getMatchDefRespDoc = GetMatchingDefinitionsResponseDocument.Factory.newInstance();
        GetMatchingDefinitionsResponse getMatchDefResp = getMatchDefRespDoc.addNewGetMatchingDefinitionsResponse();

        // add matching urns
        for (String match : this.matchingURI) {
            getMatchDefResp.addMatchingURI(match);
        }

        XmlTools.insertAttributesForValidationSOR(getMatchDefResp);

        getMatchDefRespDoc.save(baos, XmlTools.DEFAULT_OPTIONS);
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
     * @return the matchingURI
     */
    public Collection<String> getMatchingURI() {
        return this.matchingURI;
    }

    /**
     * 
     * @param matchingURI
     *        the matchingURI to set
     */
    public void setMatchingURI(Collection<String> matchingURI) {
        this.matchingURI = matchingURI;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SorGetMatchingDefinitionsResponse: ");
        sb.append("MatchingURIs: " + Arrays.toString(this.matchingURI.toArray()));
        return sb.toString();
    }

}