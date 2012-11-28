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

package org.n52.sir.xml.impl;

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
        ITransformer t = new SMLtoEbRIMTransformer(this.xsltDir);
        t.setValidating(IS_TRANSFORMER_VALIDATING);
        return t;
    }

}