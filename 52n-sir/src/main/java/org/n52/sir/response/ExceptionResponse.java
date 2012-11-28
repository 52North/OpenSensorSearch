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
import java.util.HashMap;

import net.opengis.ows.ExceptionReportDocument;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlOptions;
import org.n52.sir.SirConfigurator;
import org.n52.sir.SirConstants;
import org.n52.sir.ows.OWSConstants;
import org.n52.sir.util.XmlTools;

/**
 * Implementation of the <code>ISirResponse</code> interface for OGC service exceptions.
 * 
 * @author Alexander C. Walkowski, Daniel Nüst
 * @version 0.1
 */
public class ExceptionResponse implements ISirResponse {

    /** the exception report Document */
    private ExceptionReportDocument erd;

    /**
     * constructor
     * 
     * @param erd
     *        the exception report document for which the exception response should be created
     */
    public ExceptionResponse(ExceptionReportDocument erd) {
        this.erd = erd;
        XmlCursor cursor = this.erd.newCursor();
        if (cursor.toFirstChild()) {
            cursor.setAttributeText(XmlTools.SCHEMA_LOCATION_ATTRIBUTE_QNAME, OWSConstants.NAMESPACE + " "
                    + OWSConstants.SCHEMA_LOCATION);
        }
    }

    /**
     * @return Returns the response as byte[]
     * @throws IOException
     *         if getting the byte[] failed
     */
    @Override
    public byte[] getByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XmlOptions options = new XmlOptions();
        options.setSaveNamespacesFirst();
        options.setSaveAggressiveNamespaces();
        options.setSavePrettyPrint();
        options.setCharacterEncoding(SirConfigurator.getInstance().getCharacterEncoding());

        HashMap<String, String> suggestedPrefixes = new HashMap<String, String>();
        suggestedPrefixes.put(OWSConstants.NAMESPACE, OWSConstants.NAMESPACE_PREFIX);
        options.setSaveSuggestedPrefixes(suggestedPrefixes);

        this.erd.save(baos, options);
        byte[] bytes = baos.toByteArray();
        return bytes;
    }

    /**
     * @return Returns the the length of the content in bytes
     * @throws IOException
     *         if getting the content length failed
     */
    @Override
    public int getContentLength() throws IOException {
        return getByteArray().length;
    }

    /**
     * @return Returns the content type of this response. The returned value is the constant
     *         ScsConstant.CONTENT_TYPE.
     */
    @Override
    public String getContentType() {
        return SirConstants.CONTENT_TYPE_XML;
    }

}