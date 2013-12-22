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