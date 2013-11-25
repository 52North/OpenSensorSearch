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

package org.n52.sir.response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import net.opengis.ows.ExceptionReportDocument;
import net.opengis.ows.ExceptionReportDocument.ExceptionReport;
import net.opengis.ows.ExceptionType;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlOptions;
import org.n52.oss.sir.SirConstants;
import org.n52.oss.sir.ows.OWSConstants;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.oss.util.XmlTools;
import org.n52.sir.SirConfigurator;

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
     * @param erd
     *        the exception report document for which the exception response should be created
     */
    public ExceptionResponse(ExceptionReportDocument erd) {
        this.erd = erd;
        addNamespace();
    }

    public ExceptionResponse(OwsExceptionReport oer) {
        this(oer.getDocument());
    }

    /**
     * 
     * @param e
     *        a regular exception to be wrapped in a exception response
     */
    public ExceptionResponse(Exception e) {
        this.erd = ExceptionReportDocument.Factory.newInstance();
        ExceptionReport exceptionReport = this.erd.addNewExceptionReport();
        ExceptionType exception = exceptionReport.addNewException();
        exception.addExceptionText(e.getMessage());
        exception.setExceptionCode(ExceptionCode.NoApplicableCode.toString());

        addNamespace();
    }

    private void addNamespace() {
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

        HashMap<String, String> suggestedPrefixes = new HashMap<>();
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