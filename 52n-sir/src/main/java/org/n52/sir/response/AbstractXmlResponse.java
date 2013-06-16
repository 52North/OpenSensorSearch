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

import javax.xml.transform.TransformerException;

import org.apache.xmlbeans.XmlObject;
import org.n52.sir.SirConstants;
import org.n52.sir.util.XmlTools;

/**
 * @author Daniel Nüst
 * 
 */
public abstract class AbstractXmlResponse implements ISirResponse {

    public abstract XmlObject createXml();

    @Override
    public byte[] getByteArray() throws IOException, TransformerException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XmlObject process = createXml();
        process.save(baos, XmlTools.xmlOptionsForNamespaces());
        byte[] bytes = baos.toByteArray();
        return bytes;
    }

    @Override
    public int getContentLength() throws IOException, TransformerException {
        return getByteArray().length;
    }

    @Override
    public String getContentType() {
        return SirConstants.CONTENT_TYPE_XML;
    }

}
