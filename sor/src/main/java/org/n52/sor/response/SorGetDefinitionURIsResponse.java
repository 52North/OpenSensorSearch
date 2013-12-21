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
import java.util.Collection;

import org.n52.sor.util.XmlTools;
import org.x52North.sor.GetDefinitionURIsResponseDocument;
import org.x52North.sor.GetDefinitionURIsResponseDocument.GetDefinitionURIsResponse;

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