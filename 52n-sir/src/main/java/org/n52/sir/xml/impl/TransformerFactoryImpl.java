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
package org.n52.sir.xml.impl;

import java.nio.file.Paths;

import org.n52.sir.xml.ITransformer;
import org.n52.sir.xml.ITransformerFactory;

/**
 * 
 * Factor for {@link ITransformer}s and accessing files from the directory with XSLT documents.
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class TransformerFactoryImpl implements ITransformerFactory {

    private static final boolean IS_TRANSFORMER_VALIDATING = false;

    private String xsltDir;

    /**
     * Creates new transformer with the given directory where they can try to load the XSL definitions.
     * 
     * @param xsltDir
     */
    public TransformerFactoryImpl(String xsltDir) {
        this.xsltDir = xsltDir;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.xml.ITransformerFactory#getSensorMLtoEbRIMTransformer()
     */
    @Override
    public ITransformer getSensorMLtoCatalogXMLTransformer() {
        ITransformer t = new SMLtoEbRIMTransformer(Paths.get(this.xsltDir));
        t.setValidating(IS_TRANSFORMER_VALIDATING);
        return t;
    }

}