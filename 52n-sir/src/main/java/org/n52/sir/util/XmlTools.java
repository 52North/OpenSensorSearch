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
package org.n52.sir.util;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.opengis.cat.csw.x202.BriefRecordType;
import net.opengis.cat.csw.x202.InsertResultType;
import net.opengis.cat.csw.x202.TransactionResponseType;
import net.opengis.cat.csw.x202.TransactionSummaryType;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.n52.sir.SirConfigurator;
import org.n52.sir.sml.SMLConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import x0.oasisNamesTcEbxmlRegrepXsdRim3.AssociationType1;
import x0.oasisNamesTcEbxmlRegrepXsdRim3.ClassificationNodeDocument;
import x0.oasisNamesTcEbxmlRegrepXsdRim3.ClassificationNodeType;
import x0.oasisNamesTcEbxmlRegrepXsdRim3.ClassificationSchemeType;
import x0.oasisNamesTcEbxmlRegrepXsdRim3.ClassificationType;
import x0.oasisNamesTcEbxmlRegrepXsdRim3.ExtrinsicObjectDocument;
import x0.oasisNamesTcEbxmlRegrepXsdRim3.ExtrinsicObjectType;
import x0.oasisNamesTcEbxmlRegrepXsdRim3.IdentifiableType;
import x0.oasisNamesTcEbxmlRegrepXsdRim3.InternationalStringType;
import x0.oasisNamesTcEbxmlRegrepXsdRim3.LocalizedStringType;
import x0.oasisNamesTcEbxmlRegrepXsdRim3.OrganizationType;
import x0.oasisNamesTcEbxmlRegrepXsdRim3.PersonNameType;
import x0.oasisNamesTcEbxmlRegrepXsdRim3.PersonType;
import x0.oasisNamesTcEbxmlRegrepXsdRim3.RegistryObjectListType;
import x0.oasisNamesTcEbxmlRegrepXsdRim3.RegistryPackageType;
import x0.oasisNamesTcEbxmlRegrepXsdRim3.ServiceBindingType;
import x0.oasisNamesTcEbxmlRegrepXsdRim3.ServiceType;
import x0.oasisNamesTcEbxmlRegrepXsdRim3.SlotDocument;
import x0.oasisNamesTcEbxmlRegrepXsdRim3.SlotType1;

/**
 * 
 * Class for helper methods dealing with XML, especially deeper inspection of XML-Documents respectively beans
 * (i.e. instances of (subclasses of) {@link XmlObject}).
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class XmlTools {

    private static final int DEFAULT_INDENT = 4;

    public static final XmlOptions DEFAULT_OPTIONS = new XmlOptions();

    private static final HashMap<String, String> FIXED_SUGGESTED_PREFIXES;

    public static final String GML_NAMESPACE_PREFIX = "gml";

    public static final String GML_NAMESPACE_URI = "http://www.opengis.net/gml";

    private static final int MAX_INSPECT_STRING_LENGTH = 142;

    public static final String OGC_NAMESPACE_PREFIX = "ogc";

    public static final String OGC_NAMESPACE_URI = "http://www.opengis.net/ogc";

    public static final String OM_1_0_NAMESPACE_URI = "http://www.opengis.net/om/1.0";

    public static final String OM_NAMESPACE_PREFIX = "om";

    public static final String OWS_1_1_NAMESPACE_URI = "http://www.opengis.net/ows/1.1";

    public static final String OWS_NAMESPACE_PREFIX = "ows";

    public static final XmlOptions PRETTY_PRINT_OPTIONS = new XmlOptions().setSavePrettyPrint().setSavePrettyPrintIndent(DEFAULT_INDENT);

    private static final Random rand = new Random();

    public static final String SA_1_0_NAMESPACE_URI = "http://www.opengis.net/sampling/1.0";

    public static final String SA_NAMESPACE_PREFIX = "sa";

    public static final QName SCHEMA_LOCATION_ATTRIBUTE_QNAME = new QName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
                                                                          "schemaLocation");

    private static final String SIR_NAMESPACE_PREFIX = "sir";

    private static final String SIR_NAMESPACE_URI = "http://52north.org/sir/0.3.2";

    public static final String SML_1_0_1_NAMESPACE_URI = "http://www.opengis.net/sensorML/1.0.1";

    public static final String SML_NAMESPACE_PREFIX = "sml";

    public static final String SML_SCHEMA_LOCATION = "http://schemas.opengis.net/sensorML/1.0.1/sensorML.xsd";

    public static final String SOS_1_0_NAMESPACE_URI = "http://www.opengis.net/sos/1.0";

    public static final String SOS_NAMESPACE_PREFIX = "sos";

    public static final String SWE_1_0_1_NAMESPACE_URI = "http://www.opengis.net/swe/1.0.1";

    public static final String SWE_NAMESPACE_PREFIX = "swe";

    public static final String XLINK_NAMESPACE_PREFIX = "xlink";

    public static final String XLINK_NAMESPACE_URI = "http://www.w3.org/1999/xlink";

    private static final String XML_OPTIONS_CHARACTER_ENCODING = "UTF-8";

    public static final String XSI_NAMESPACE_PREFIX = "xsi";

    static {
        FIXED_SUGGESTED_PREFIXES = new HashMap<String, String>();
        FIXED_SUGGESTED_PREFIXES.put(SOS_1_0_NAMESPACE_URI, SOS_NAMESPACE_PREFIX);
        FIXED_SUGGESTED_PREFIXES.put(SWE_1_0_1_NAMESPACE_URI, SWE_NAMESPACE_PREFIX);
        FIXED_SUGGESTED_PREFIXES.put(OM_1_0_NAMESPACE_URI, OM_NAMESPACE_PREFIX);
        FIXED_SUGGESTED_PREFIXES.put(SA_1_0_NAMESPACE_URI, SA_NAMESPACE_PREFIX);
        FIXED_SUGGESTED_PREFIXES.put(GML_NAMESPACE_URI, GML_NAMESPACE_PREFIX);
        FIXED_SUGGESTED_PREFIXES.put(OWS_1_1_NAMESPACE_URI, OWS_NAMESPACE_PREFIX);
        FIXED_SUGGESTED_PREFIXES.put(SML_1_0_1_NAMESPACE_URI, SML_NAMESPACE_PREFIX);
        FIXED_SUGGESTED_PREFIXES.put(SMLConstants.NAMESPACE, SMLConstants.NAMESPACE_PREFIX);
        FIXED_SUGGESTED_PREFIXES.put(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, XSI_NAMESPACE_PREFIX);

        DEFAULT_OPTIONS.setSaveNamespacesFirst();
        DEFAULT_OPTIONS.setSavePrettyPrint();
        DEFAULT_OPTIONS.setSaveSuggestedPrefixes(FIXED_SUGGESTED_PREFIXES);
        DEFAULT_OPTIONS.setSaveAggressiveNamespaces();
        DEFAULT_OPTIONS.setSavePrettyPrintIndent(DEFAULT_INDENT);
    }

    /**
     * 
     * @param request
     */
    public static void addSirAndSensorMLSchemaLocation(XmlObject xml) {
        XmlCursor c = xml.newCursor();
        c.setAttributeText(SCHEMA_LOCATION_ATTRIBUTE_QNAME, XmlTools.getSirSchemaLocation() + " "
                + SMLConstants.SCHEMA_LOCATION);
        c.dispose();
    }

    public static void addSirSchemaLocation(XmlObject xml) {
        XmlCursor c = xml.newCursor();
        c.setAttributeText(SCHEMA_LOCATION_ATTRIBUTE_QNAME, XmlTools.getSirSchemaLocation());
        c.dispose();
    }

    /**
     * 
     * @param o
     * @return
     */
    public static String generateGmlID(XmlObject o) {
        return "id" + Long.toString(rand.nextInt(1000));
    }

    /**
     * 
     * @return
     */
    public static Map<String, String> getFixedSuggestedPrefixes() {
        return FIXED_SUGGESTED_PREFIXES;
    }

    /**
     * 
     * @return
     */
    public static String getSensorMLSchemaLocation() {
        return SML_1_0_1_NAMESPACE_URI + " " + SML_SCHEMA_LOCATION;
    }

    /**
     * 
     * @return
     */
    public static String getSirSchemaLocation() {
        return SirConfigurator.getInstance().getNamespaceUri() + " " + SirConfigurator.getInstance().getSchemaUrl();
    }

    /**
     * 
     * @param at
     * @return
     */
    public static String inspect(AssociationType1 at) {
        StringBuilder sb = new StringBuilder();
        sb.append("AssociationType [id: ");
        sb.append(at.getId());
        sb.append(", associationType: ");
        sb.append(at.getAssociationType());
        sb.append(", sourceObject: ");
        sb.append(at.getSourceObject());
        sb.append(", targetObject: ");
        sb.append(at.getTargetObject());
        sb.append("]");
        return sb.toString();
    }

    /**
     * 
     * @param br
     * @return
     */
    public static String inspect(BriefRecordType br) {
        StringBuilder sb = new StringBuilder();
        sb.append("BriefRecordType [ ");
        sb.append(br.xmlText());
        sb.append("]");
        return sb.toString();
    }

    /**
     * 
     * @param cnd
     * @return
     */
    public static String inspect(ClassificationNodeDocument cnd) {
        StringBuilder sb = new StringBuilder();
        sb.append("ClassificationNodeDocument [ ");
        sb.append(inspect(cnd.getClassificationNode()));
        sb.append(" ]");
        return sb.toString();
    }

    /**
     * 
     * @param cnt
     * @return
     */
    public static String inspect(ClassificationNodeType cnt) {
        StringBuilder sb = new StringBuilder();
        sb.append("ClassificationNodeType [id: ");
        sb.append(cnt.getId());
        sb.append(", parent: ");
        sb.append(cnt.getParent());
        sb.append(", code: ");
        sb.append(cnt.getCode());
        sb.append(", path: ");
        sb.append(cnt.getPath());
        sb.append("]");
        return sb.toString();
    }

    /**
     * 
     * @param cst
     * @return
     */
    public static String inspect(ClassificationSchemeType cst) {
        StringBuilder sb = new StringBuilder();
        sb.append("ClassificationSchemeType [id: ");
        sb.append(cst.getId());
        sb.append(", isInternal: ");
        sb.append(cst.getIsInternal());
        sb.append(", name: ");
        sb.append(cst.getName());
        sb.append(", description: ");
        sb.append(cst.getDescription());
        sb.append("]");
        return sb.toString();
    }

    /**
     * 
     * @param ct
     * @return
     */
    public static String inspect(ClassificationType ct) {
        StringBuilder sb = new StringBuilder();
        sb.append("ClassificationType [id: ");
        sb.append(ct.getId());
        sb.append(", classificationScheme: ");
        sb.append(ct.getClassificationScheme());
        sb.append(", classificationNode: ");
        sb.append(ct.getClassificationNode());
        sb.append(", classifiedObject: ");
        sb.append(ct.getClassifiedObject());
        sb.append("]");
        return sb.toString();
    }

    public static String inspect(Document document) {
        return document.getNodeValue();
    }

    /**
     * 
     * @param eod
     * @return
     */
    public static String inspect(ExtrinsicObjectDocument eod) {
        StringBuilder sb = new StringBuilder();
        sb.append("ExtrinsicObjectDocument [ ");
        sb.append(inspect(eod.getExtrinsicObject()));
        sb.append(" ]");
        return sb.toString();
    }

    /**
     * 
     * @param eo
     * @return
     */
    public static String inspect(ExtrinsicObjectType eo) {
        StringBuilder sb = new StringBuilder();
        sb.append("ExtrinsicObjectType [id: ");
        sb.append(eo.getId());
        sb.append(", lid: ");
        sb.append(eo.getLid());
        InternationalStringType descr = eo.getDescription();

        if (descr != null) { // is optional
            sb.append(", description: ");
            LocalizedStringType[] locS = descr.getLocalizedStringArray();
            sb.append(locS[0].getValue());
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * 
     * Method makes <code>instaceof</code> checks to use the most appropriate subclass of
     * {@link IdentifiableType}. See other methods of this class for available subclasses.
     * 
     * @param it
     * @return
     */
    public static Object inspect(IdentifiableType it) {
        StringBuilder sb = new StringBuilder();

        if (it instanceof ExtrinsicObjectType) {
            ExtrinsicObjectType eo = (ExtrinsicObjectType) it;
            return inspect(eo);
        }

        if (it instanceof SlotType1) {
            SlotType1 st = (SlotType1) it;
            return inspect(st);
        }

        if (it instanceof SlotDocument) {
            SlotDocument sd = (SlotDocument) it;
            return inspect(sd);
        }

        if (it instanceof ClassificationNodeDocument) {
            ClassificationNodeDocument t = (ClassificationNodeDocument) it;
            return inspect(t);
        }

        if (it instanceof ClassificationSchemeType) {
            ClassificationSchemeType t = (ClassificationSchemeType) it;
            return inspect(t);
        }

        if (it instanceof ClassificationNodeType) {
            ClassificationNodeType t = (ClassificationNodeType) it;
            return inspect(t);
        }

        if (it instanceof ClassificationType) {
            ClassificationType t = (ClassificationType) it;
            return inspect(t);
        }

        if (it instanceof PersonType) {
            PersonType t = (PersonType) it;
            return inspect(t);
        }

        if (it instanceof PersonNameType) {
            PersonNameType t = (PersonNameType) it;
            return inspect(t);
        }

        if (it instanceof OrganizationType) {
            OrganizationType t = (OrganizationType) it;
            return inspect(t);
        }

        if (it instanceof RegistryPackageType) {
            RegistryPackageType t = (RegistryPackageType) it;
            return inspect(t);
        }

        if (it instanceof AssociationType1) {
            AssociationType1 t = (AssociationType1) it;
            return inspect(t);
        }

        if (it instanceof ServiceType) {
            ServiceType t = (ServiceType) it;
            return inspect(t);
        }

        if (it instanceof ServiceBindingType) {
            ServiceBindingType t = (ServiceBindingType) it;
            return inspect(t);
        }

        if (it instanceof TransactionResponseType) {
            TransactionResponseType t = (TransactionResponseType) it;
            return inspect(t);
        }

        if (it instanceof TransactionSummaryType) {
            TransactionSummaryType t = (TransactionSummaryType) it;
            return inspect(t);
        }

        if (it instanceof InsertResultType) {
            InsertResultType t = (InsertResultType) it;
            return inspect(t);
        }

        if (it instanceof BriefRecordType) {
            BriefRecordType t = (BriefRecordType) it;
            return inspect(t);
        }

        sb.append("IdentifiableType [id: ");
        sb.append(it.getId());
        sb.append(", DomNode: ");
        sb.append(xmlToString(it.getDomNode()));
        sb.append("]");
        return sb.toString();
    }

    /**
     * 
     * @param iR
     * @return
     */
    public static String inspect(InsertResultType iR) {
        StringBuilder sb = new StringBuilder();
        sb.append("InsertResultType [ brief records: ");
        BriefRecordType[] records = iR.getBriefRecordArray();
        for (int i = 0; i < records.length; i++) {
            sb.append(inspect(records[i]));
            sb.append(", ");
        }
        sb.replace(sb.length() - 2, sb.length(), "");
        sb.append("]");
        return sb.toString();
    }

    /**
     * 
     * @param ist
     * @return
     */
    public static String inspect(InternationalStringType ist) {
        StringBuilder sb = new StringBuilder();
        sb.append("InternationalStringType [ ");

        LocalizedStringType[] locStrings = ist.getLocalizedStringArray();
        for (LocalizedStringType localizedStringType : locStrings) {
            sb.append(inspect(localizedStringType));
            sb.append(", ");
        }
        sb.replace(sb.length() - 2, sb.length(), "");
        sb.append("]");
        return sb.toString();
    }

    /**
     * 
     * @param lst
     * @return
     */
    public static String inspect(LocalizedStringType lst) {
        StringBuilder sb = new StringBuilder();
        sb.append(lst.getValue());
        sb.append(" {");
        sb.append(lst.getLang());
        sb.append("}");
        return sb.toString();
    }

    /**
     * 
     * @param ot
     * @return
     */
    public static String inspect(OrganizationType ot) {
        StringBuilder sb = new StringBuilder();
        sb.append("OrganizationType [id: ");
        sb.append(ot.getId());
        sb.append(", name: ");
        sb.append(inspect(ot.getName()));
        sb.append(", primaryContact: ");
        sb.append(ot.getPrimaryContact());
        sb.append("]");
        return sb.toString();
    }

    /**
     * 
     * @param pnt
     * @return
     */
    public static String inspect(PersonNameType pnt) {
        StringBuilder sb = new StringBuilder();
        sb.append("PersonNameType [firstName: ");
        sb.append(pnt.getFirstName());
        sb.append(", middleName: ");
        sb.append(pnt.getMiddleName());
        sb.append(", lastName: ");
        sb.append(pnt.getLastName());
        sb.append("]");
        return sb.toString();
    }

    /**
     * 
     * @param pt
     * @return
     */
    public static String inspect(PersonType pt) {
        StringBuilder sb = new StringBuilder();
        sb.append("PersonType [id: ");
        sb.append(pt.getId());
        sb.append(", personName: ");
        sb.append(inspect(pt.getPersonName()));
        sb.append("]");
        return sb.toString();
    }

    /**
     * 
     * @param rol
     * @return
     */
    public static String inspect(RegistryObjectListType rol) {
        StringBuilder sb = new StringBuilder();
        IdentifiableType[] identifiables = rol.getIdentifiableArray();
        sb.append("RegistryObjectListType (");
        sb.append(identifiables.length);
        sb.append(" elements):\n");
        for (IdentifiableType identifiableType : identifiables) {
            sb.append("\t");
            sb.append(inspect(identifiableType));
            sb.append(",\n");
        }
        sb.replace(sb.length() - 2, sb.length(), "");
        return sb.toString();
    }

    /**
     * 
     * @param rp
     * @return
     */
    public static String inspect(RegistryPackageType rp) {
        StringBuilder sb = new StringBuilder();
        sb.append("RegistryPackageType [ ");
        sb.append(inspect(rp.getRegistryObjectList()));
        sb.append(" ]");
        return sb.toString();
    }

    /**
     * 
     * @param sbt
     * @return
     */
    public static String inspect(ServiceBindingType sbt) {
        StringBuilder sb = new StringBuilder();
        sb.append("ServiceBindingType [id: ");
        sb.append(sbt.getId());
        sb.append(", accessURI: ");
        sb.append(sbt.getAccessURI());
        sb.append(", service: ");
        sb.append(sbt.getService());
        sb.append("]");
        return sb.toString();
    }

    /**
     * 
     * @param st
     * @return
     */
    public static String inspect(ServiceType st) {
        StringBuilder sb = new StringBuilder();
        sb.append("ServiceType [id: ");
        sb.append(st.getId());
        sb.append(", service bindings: ");
        ServiceBindingType[] bindings = st.getServiceBindingArray();
        for (ServiceBindingType serviceBindingType : bindings) {
            sb.append(inspect(serviceBindingType));
            sb.append(", ");
        }
        sb.replace(sb.length() - 2, sb.length(), "");
        sb.append("]");
        return sb.toString();
    }

    /**
     * 
     * @param sd
     * @return
     */
    public static String inspect(SlotDocument sd) {
        StringBuilder sb = new StringBuilder();
        sb.append("SlotDocument [Slot: ");
        sb.append(inspect(sd.getSlot()));
        sb.append("]");
        return sb.toString();
    }

    /**
     * 
     * @param st
     * @return
     */
    public static String inspect(SlotType1 st) {
        StringBuilder sb = new StringBuilder();
        sb.append("SlotType [name: ");
        sb.append(st.getName());
        sb.append(", slotType: ");
        sb.append(st.getSlotType());
        sb.append(", ValueList: ");
        sb.append(st.getValueList().xmlText());
        sb.append("]");
        return sb.toString();
    }

    /**
     * 
     * @param transactionResponse
     * @return
     */
    public static String inspect(TransactionResponseType transactionResponse) {
        StringBuilder sb = new StringBuilder();
        sb.append("TransactionResponseType [version: ");
        sb.append(transactionResponse.getVersion());
        sb.append(", summary: ");
        sb.append(inspect(transactionResponse.getTransactionSummary()));
        sb.append(", insertResults: ");
        InsertResultType[] results = transactionResponse.getInsertResultArray();
        for (InsertResultType insertResultType : results) {
            sb.append(inspect(insertResultType));
            sb.append(", ");
        }
        sb.replace(sb.length() - 2, sb.length(), "");
        sb.append("]");
        return sb.toString();
    }

    /**
     * 
     * @param transactionSummary
     * @return
     */
    public static String inspect(TransactionSummaryType transactionSummary) {
        StringBuilder sb = new StringBuilder();
        sb.append("TransactionSummaryType [requestId: ");
        sb.append(transactionSummary.getRequestId());
        sb.append(", total inserted: ");
        sb.append(transactionSummary.getTotalInserted());
        sb.append(", total updated: ");
        sb.append(transactionSummary.getTotalUpdated());
        sb.append(", total deleted: ");
        sb.append(transactionSummary.getTotalDeleted());
        sb.append("]");
        return sb.toString();
    }

    /**
     * 
     * Returns {@link XmlObject#xmlText()}, but only a maximum of {@link XmlTools#MAX_INSPECT_STRING_LENGTH}
     * characters.
     * 
     * @param xmlObj
     * @return
     */
    public static String inspect(XmlObject xmlObj) {
        String s = xmlObj.xmlText();

        // remove comment at start
        if (s.startsWith("<!--")) {
            int commentEnd = s.indexOf("-->") + 3;
            s = s.substring(commentEnd, s.length() - commentEnd);
        }

        if (s.length() <= MAX_INSPECT_STRING_LENGTH)
            return s;
        return s.substring(0, MAX_INSPECT_STRING_LENGTH) + "... ( " + (s.length() - MAX_INSPECT_STRING_LENGTH)
                + " more characters)";
    }

    /**
     * 
     * @param source
     */
    public static void sensorMLNameSpaceOptions(XmlObject source) {
        XmlCursor cursor = source.newCursor();
        cursor.toFirstContentToken();
        cursor.toLastAttribute();
        cursor.insertNamespace(OM_NAMESPACE_PREFIX, OM_1_0_NAMESPACE_URI);
        cursor.insertNamespace(SA_NAMESPACE_PREFIX, SA_1_0_NAMESPACE_URI);
        cursor.insertNamespace(SWE_NAMESPACE_PREFIX, SWE_1_0_1_NAMESPACE_URI);
        cursor.insertNamespace(SOS_NAMESPACE_PREFIX, SOS_1_0_NAMESPACE_URI);
        cursor.insertNamespace(OWS_NAMESPACE_PREFIX, OWS_1_1_NAMESPACE_URI);
        cursor.insertNamespace(GML_NAMESPACE_PREFIX, GML_NAMESPACE_URI);
        cursor.insertNamespace(SML_NAMESPACE_PREFIX, SML_1_0_1_NAMESPACE_URI);
        cursor.insertNamespace(XSI_NAMESPACE_PREFIX, XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
        cursor.insertNamespace(XLINK_NAMESPACE_PREFIX, XLINK_NAMESPACE_URI);
        cursor.dispose();
        XmlOptions options = new XmlOptions();
        options.setCharacterEncoding(XML_OPTIONS_CHARACTER_ENCODING);
        options.setUseDefaultNamespace();
        options.setSaveNamespacesFirst();
        options.setSavePrettyPrint();
        options.setSaveAggressiveNamespaces();
        source.xmlText(options);
    }

    /**
     * does the same stuff that {@link XmlTools#xmlOptionsForNamespaces()} does, but without depending on an
     * instanciation of {@link SirConfigurator}. The following values are NOT used from sir config file:
     * character encoding, sir namespace url, and sir namespace prefix.
     * 
     * @return
     */
    public static XmlOptions unconfiguredXmlOptionsForNamespaces() {
        XmlOptions options = new XmlOptions();
        options.setCharacterEncoding(XML_OPTIONS_CHARACTER_ENCODING);

        options.setUseDefaultNamespace();
        options.setSaveNamespacesFirst();
        options.setSavePrettyPrint();
        options.setSavePrettyPrintIndent(4);
        options.setSaveAggressiveNamespaces();

        HashMap<String, String> suggestedPrefixes = new HashMap<String, String>();
        suggestedPrefixes.putAll(XmlTools.getFixedSuggestedPrefixes());
        suggestedPrefixes.put(SIR_NAMESPACE_URI, SIR_NAMESPACE_PREFIX);
        options.setSaveSuggestedPrefixes(suggestedPrefixes);

        return options;
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
            sb.append("XmlObject of class <");
            sb.append(xml.getClass().getSimpleName());
            sb.append(" /> is NOT valid! The validation errors are:");

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

    /**
     * gives {@link XmlOptions} to set the default namespaces via {@link XmlObject}.xmlText(setOptions()).
     * 
     * @return
     */
    public static XmlOptions xmlOptionsForNamespaces() {
        XmlOptions options = new XmlOptions();
        options.setCharacterEncoding(SirConfigurator.getInstance().getCharacterEncoding());

        options.setUseDefaultNamespace();
        options.setSaveNamespacesFirst();
        options.setSavePrettyPrint();
        options.setSavePrettyPrintIndent(DEFAULT_INDENT);
        options.setSaveAggressiveNamespaces();

        HashMap<String, String> suggestedPrefixes = new HashMap<String, String>();
        suggestedPrefixes.putAll(FIXED_SUGGESTED_PREFIXES);
        suggestedPrefixes.put(SirConfigurator.getInstance().getNamespaceUri(),
                              SirConfigurator.getInstance().getNamespacePrefix());
        options.setSaveSuggestedPrefixes(suggestedPrefixes);

        return options;
    }

    /**
     * 
     * 
     * 
     * @param node
     * @return
     */
    public static String xmlToString(Node node) {
        try {
            Source source = new DOMSource(node);
            StringWriter stringWriter = new StringWriter();
            Result result = new StreamResult(stringWriter);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();

            // if you want pretty pring, take a look at http://xml.apache.org/xalan-j/usagepatterns.html and
            // http://www.chipkillmar.net/2009/03/25/pretty-print-xml-from-a-dom/
            // transformer.setOutputProperties(oformat);
            transformer.transform(source, result);

            return stringWriter.getBuffer().toString();
        }
        catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        catch (TransformerException e) {
            e.printStackTrace();
        }
        return "ERROR transforming given Node";
    }
}