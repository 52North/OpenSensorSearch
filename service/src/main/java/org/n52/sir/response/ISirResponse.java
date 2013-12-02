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

import java.io.IOException;

import javax.xml.transform.TransformerException;

/**
 * interface for the SirReponse
 * 
 * @author Jan Schulte
 * 
 */
public interface ISirResponse {

    /**
     * @return Returns the reponse XML document as byte[]
     * @throws IOException
     *         if getting the response as byte[] failed
     * @throws TransformerException
     *         if getting the response as byte[] failed
     */
    public byte[] getByteArray() throws IOException, TransformerException;

    /**
     * @return Returns the length of the content in bytes
     * @throws IOException
     *         if getting the content length failed
     * @throws TransformerException
     *         if getting the content length failed
     */
    public int getContentLength() throws IOException, TransformerException;

    /**
     * @return Returns the content type of this response
     */
    public String getContentType();

}