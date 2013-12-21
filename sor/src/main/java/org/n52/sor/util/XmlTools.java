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

package org.n52.sor.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;

/**
 * @author Daniel Nüst
 * 
 */
public class XmlTools {

    public static final String SWE_1_0_1_NAMESPACE_URI = "http://www.opengis.net/swe/1.0.1";

    public static final String SWE_NAMESPACE_PREFIX = "swe";

    public static final String GML_NAMESPACE_URI = "http://www.opengis.net/gml/3.2";

    public static final String GML_NAMESPACE_PREFIX = "gml";

    private static final String W3C_XML_SCHEMA_NS_URI = "http://www.w3.org/2001/XMLSchema";

    public static final String XSI_NAMESPACE_PREFIX = "xsi";

    public static final String SOR_NAMESPACE_URI = "http://swsl.uni-muenster.de/sor";

    public static final String SOR_NAMESPACE_PREFIX = "sor";

    public static final String OWS_NAMESPACE_PREFIX = "ows";

    public static final String OWS_1_1_NAMESPACE_URI = "http://www.opengis.net/ows/1.1";

    public static final HashMap<String, String> SUGGESTED_PREFIXES;

    public static final XmlOptions DEFAULT_OPTIONS = new XmlOptions();

    private static final int DEFAULT_INDENT = 4;

    public static final String SWE_GML_SCHEMA_LOCATION = "http://www.opengis.net/swe/1.0/gml32 http://giv-genesis.uni-muenster.de/schemas/sweCommon/1.0.1_gml32/phenomenon.xsd";

    public static final String SOR_SCHEMA_LOCATION = SOR_NAMESPACE_URI
            + " http://giv-genesis.uni-muenster.de/schemas/sor/sorAll.xsd";

    static {
        SUGGESTED_PREFIXES = new HashMap<String, String>();
        SUGGESTED_PREFIXES.put(SWE_1_0_1_NAMESPACE_URI, SWE_NAMESPACE_PREFIX);
        SUGGESTED_PREFIXES.put(GML_NAMESPACE_URI, GML_NAMESPACE_PREFIX);
        SUGGESTED_PREFIXES.put(SOR_NAMESPACE_URI, SOR_NAMESPACE_PREFIX);
        SUGGESTED_PREFIXES.put(OWS_1_1_NAMESPACE_URI, OWS_NAMESPACE_PREFIX);
        SUGGESTED_PREFIXES.put(W3C_XML_SCHEMA_NS_URI, XSI_NAMESPACE_PREFIX);

        DEFAULT_OPTIONS.setSaveNamespacesFirst();
        DEFAULT_OPTIONS.setSavePrettyPrint();
        DEFAULT_OPTIONS.setSaveSuggestedPrefixes(SUGGESTED_PREFIXES);
        DEFAULT_OPTIONS.setSaveAggressiveNamespaces();
        DEFAULT_OPTIONS.setSavePrettyPrintIndent(DEFAULT_INDENT);
    }

    /**
     * 
     * @param cursor
     */
    public static void insertAttributesForValidationSOR(XmlObject obj) {
        XmlCursor cursor = obj.newCursor();
        cursor.setAttributeText(new QName(W3C_XML_SCHEMA_NS_URI, "schemaLocation"),
                                SOR_SCHEMA_LOCATION);
        cursor.toNextToken();
        cursor.insertNamespace("xsi", W3C_XML_SCHEMA_NS_URI);
        cursor.dispose();
    }

    /**
     * 
     * @param cursor
     */
    public static void insertAttributesForValidationSweGml(XmlObject obj) {
        XmlCursor cursor = obj.newCursor();
        cursor.setAttributeText(new QName(W3C_XML_SCHEMA_NS_URI, "schemaLocation"),
                                SWE_GML_SCHEMA_LOCATION);
        cursor.toNextToken();
        cursor.insertNamespace("xsi", W3C_XML_SCHEMA_NS_URI);
        cursor.dispose();
    }

    /**
     * http://xmlbeans.apache.org/docs/2.0.0/guide/conValidationWithXmlBeans.html
     * 
     * @param xml
     * @return
     */
    public static String validateAndIterateErrors(XmlObject xml) {
        ArrayList<XmlError> validationErrors = new ArrayList<XmlError>();
        XmlOptions validationOptions = new XmlOptions();
        validationOptions.setErrorListener(validationErrors);

        boolean isValid = xml.validate(validationOptions);

        StringBuilder sb = new StringBuilder();
        if ( !isValid) {
            sb.append("XmlObject of type ");
            sb.append(xml.getClass().getSimpleName());
            sb.append(" (");
            sb.append(xml.xmlText().substring(0, 60));
            sb.append("...) is NOT valid! The validation errors are:");

            Iterator<XmlError> iter = validationErrors.iterator();
            while (iter.hasNext()) {
                sb.append("\n\t");
                sb.append(iter.next().toString());
            }
        }
        else {
            sb.append("XmlObject ");
            sb.append(xml.getClass().getSimpleName());
            sb.append(" (");
            sb.append(xml.xmlText().substring(0, 40));
            sb.append("...) is valid!");
        }

        return sb.toString();
    }
}