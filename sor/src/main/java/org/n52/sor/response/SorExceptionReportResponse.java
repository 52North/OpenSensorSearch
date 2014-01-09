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

import net.opengis.ows.x11.ExceptionReportDocument;

import org.n52.sor.ISorResponse;
import org.n52.sor.util.XmlTools;

/**
 * Report of an exception
 * 
 * @author Jan Schulte
 * 
 */
public class SorExceptionReportResponse implements ISorResponse {

    /** the exception report document */
    private ExceptionReportDocument erd;

    public SorExceptionReportResponse(ExceptionReportDocument erd) {
        this.erd = erd;
    }

    /**
     * @return Returns the response as byte[]
     * @throws IOException
     *         if getting the byte[] failed
     * 
     */
    @Override
    public byte[] getByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        this.erd.save(baos, XmlTools.DEFAULT_OPTIONS);
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
     * @return info as string
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SorExceptionReportResponse: ");
        sb.append(this.erd.getExceptionReport().getExceptionArray(0).getExceptionCode());
        return sb.toString();
    }
}