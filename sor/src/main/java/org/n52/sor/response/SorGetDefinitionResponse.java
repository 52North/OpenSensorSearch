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

import net.opengis.swe.x101.PhenomenonType;

import org.apache.xmlbeans.XmlOptions;
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