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

import java.io.FileNotFoundException;
import java.io.StringWriter;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.opengis.sensorML.x101.AbstractProcessType;
import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.sensorML.x101.SystemType;
import net.sf.saxon.StandardErrorListener;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sir.SirConfigurator;
import org.n52.sir.datastructure.SirSensorDescription;
import org.n52.sir.datastructure.SirXmlSensorDescription;
import org.n52.sir.sml.SMLConstants;
import org.n52.sir.util.XmlTools;
import org.n52.sir.xml.ITransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import x0.oasisNamesTcEbxmlRegrepXsdRim3.RegistryPackageDocument;

/**
 * 
 * A transformer for the transformation of SensorML (http://www.opengeospatial.org/standards/sensorml)
 * documents to a set of ebRIM (http://www.oasis-open.org/specs/index.php#ebxmlrimv3.0) ExtrinsicObjects
 * according the the SensorML Profile for Discovery (Ongoing work, see OWS-6 SensorML Profile for Discovery
 * Engineering Report, OGC 09-033 (http://portal.opengeospatial.org/files/?artifact_id=33284)).
 * 
 * The transformation is based on the file {@link SMLtoEbRIMTransformer#TRANSFORMATION_FILE_NAME} and uses
 * XSLT 2.0 (http://www.w3.org/TR/xslt20/).
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class SMLtoEbRIMTransformer implements ITransformer {

    private static Logger log = LoggerFactory.getLogger(SMLtoEbRIMTransformer.class);

    private boolean validating = ITransformer.IS_VALIDATING_DEFAULT;

    private Transformer transformer;

    private static TransformerFactory tFactory = TransformerFactory.newInstance();

    private StreamSource xsltSource;

    public static final String TRANSFORMATION_FILE_NAME = "SensorML-to-ebRIM.xsl";

    public static final Object TRANSFORMED_DOCS_LISTING_ELEMENT_BEFORE = "<rim:RegistryObjectList xmlns:rim=\"urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0\">";

    public static final Object TRANSFORMED_DOCS_LISTING_ELEMENT_AFTER = "</rim:RegistryObjectList>";

    private static final String INDENT_OUTPUT_PROPERTY_VALUE = "yes";

    private static final String TESTING_ENCODING = "UTF-8";

    /**
     * @throws InstantiationException
     * 
     */
    public SMLtoEbRIMTransformer(String xsltDir) throws InstantiationError {
        this.xsltSource = new StreamSource(xsltDir + TRANSFORMATION_FILE_NAME);

        try {
            this.transformer = tFactory.newTransformer(this.xsltSource);
            this.transformer.setOutputProperty(OutputKeys.INDENT, INDENT_OUTPUT_PROPERTY_VALUE);
            SirConfigurator configurator = SirConfigurator.getInstance();
            if (configurator != null) {
                this.transformer.setOutputProperty(OutputKeys.ENCODING,
                                                   SirConfigurator.getInstance().getCharacterEncoding());
            }
            else {
                this.transformer.setOutputProperty(OutputKeys.ENCODING, TESTING_ENCODING);
            }
        }
        catch (TransformerConfigurationException e) {
            throw new InstantiationError("Could not instantiate Transformer from file " + this.xsltSource);
        }
    }

    @Override
    public Result transform(String file) throws FileNotFoundException, TransformerException {
        StreamSource input = new StreamSource(file);
        return transform(input);
    }

    @Override
    public XmlObject transform(SensorMLDocument smlDoc) throws XmlException, TransformerException {
        return actualTransform(smlDoc);
    }

    @Override
    public XmlObject transform(SirSensorDescription sensor) throws XmlException, TransformerException {
        if (sensor instanceof SirXmlSensorDescription) {
            SirXmlSensorDescription sensorDescription = (SirXmlSensorDescription) sensor;
            XmlObject o = sensorDescription.getDescription();

            if (o instanceof SystemType) {
                SystemType st = (SystemType) o;
                return transform(st);
            }
            else if (o instanceof SensorMLDocument) {
                SensorMLDocument smlDoc = (SensorMLDocument) o;
                return actualTransform(smlDoc);
            }

            return o;
        }
        log.error("'transform' can only handle SiXmlSensorDescriptions, because it requires the complete sensor description!");
        return null;
    }

    @Override
    public XmlObject transform(SystemType st) throws XmlException, TransformerException {
        SensorMLDocument smlDoc = SensorMLDocument.Factory.newInstance();
        AbstractProcessType abstractProcess = smlDoc.addNewSensorML().addNewMember().addNewProcess();
        SystemType systemType = (SystemType) abstractProcess.substitute(new QName(SMLConstants.NAMESPACE, "System"),
                                                                        SystemType.type);
        systemType.set(st);
        return actualTransform(smlDoc);
    }

    /*
     * (non-Javadoc)
     * @see org.n52.sir.xml.ITransformer#transform(javax.xml.transform.Source)
     */
    @Override
    public Result transform(Source input) throws TransformerException, FileNotFoundException {
        if (this.validating) {
            return validateAndTransform(new InputSource(input.getSystemId()));
        }

        log.info("Transformation of " + input.getSystemId());

        ErrorListener errL = new StandardErrorListener();
        this.transformer.setErrorListener(errL);

        // create output string
        StringWriter sw = new StringWriter();
        StreamResult output = new StreamResult(sw);

        // do transform
        this.transformer.transform(input, output);

        if (log.isDebugEnabled())
            log.debug("Transformation Done.");
        
        return output;
    }

    /**
     * 
     * Method does the actual transformation of the given SensorML document: a {@link DOMSource} is created
     * and the transformation is writes to a {@link StringWriter}. The outpot is parsed as a
     * {@link RegistryPackageDocument} and returned.
     * 
     * @param smlDoc
     * @return
     * @throws XmlException
     * @throws TransformerException
     */
    private RegistryPackageDocument actualTransform(SensorMLDocument smlDoc) throws XmlException, TransformerException {
        if (log.isDebugEnabled())
            log.debug("Start transforming SensorMLDocument: " + XmlTools.inspect(smlDoc));

        // encapsulate input document in a Source
        Source input = new DOMSource(smlDoc.getDomNode());

        // create output string
        StringWriter sw = new StringWriter();
        StreamResult output = new StreamResult(sw);

        // do the transformation
        this.transformer.transform(input, output);

        // create output document
        String outputString = output.getWriter().toString();

        RegistryPackageDocument regPackDoc = RegistryPackageDocument.Factory.parse(outputString);

        // clean up
        input = null;
        sw = null;
        output = null;
        outputString = null;

        if (SirConfigurator.getInstance().isValidateRequests()) {
            if ( !regPackDoc.validate()) {
                log.warn("Created RegistryPackageDocument is not valid!");
            }
        }

        if (log.isDebugEnabled())
            log.debug("Finished transformation: " + XmlTools.inspect(regPackDoc));

        return regPackDoc;
    }

    /**
     * 
     * Method validates the documents during transformation and relies on SAX features (which must be
     * installed, naturally).
     * 
     * @param input
     * @return
     * @throws TransformerConfigurationException
     */
    private Result validateAndTransform(InputSource input) throws TransformerConfigurationException {
        // Since we're going to use a SAX feature, the transformer must support
        // input in the form of a SAXSource.
        if (tFactory.getFeature(SAXSource.FEATURE)) {
            SAXParserFactory pfactory = SAXParserFactory.newInstance();
            pfactory.setNamespaceAware(true);
            // Turn on validation.
            pfactory.setValidating(true);

            // ...
            pfactory.setXIncludeAware(true);

            // pfactory.setSchema(schema)

            // Get an XMLReader.
            XMLReader reader;
            try {
                reader = pfactory.newSAXParser().getXMLReader();
            }
            catch (SAXException e) {
                e.printStackTrace();
                return null;
            }
            catch (ParserConfigurationException e) {
                e.printStackTrace();
                return null;
            }

            // Instantiate an error handler (see the Handler inner class below) that will report any
            // errors or warnings that occur as the XMLReader is parsing the XML input.
            ParseExceptionHandler handler = new ParseExceptionHandler();
            reader.setErrorHandler(handler);

            Transformer t = tFactory.newTransformer(this.xsltSource);

            // Specify a SAXSource that takes both an XMLReader and a URL.
            SAXSource source = new SAXSource(reader, input);

            // Transform input to a string.
            StringWriter sw = new StringWriter();
            StreamResult output = new StreamResult(sw);
            try {
                t.transform(source, output);
            }
            catch (TransformerException te) {
                // The TransformerException wraps someting other than a SAXParseException
                // warning or error, either of which should be "caught" by the Handler.
                log.error("Not a SAXParseException warning or error: " + te);
            }

            log.info("=====Done=====");
            return output;
        }
        log.error("TransformerFactory does not support required SAXSource.FEATURE");
        throw new TransformerConfigurationException("TransformerFactory does not support required SAXSource.FEATURE");
    }

    @Override
    public boolean isValidating() {
        return this.validating;
    }

    @Override
    public void setValidating(boolean b) {
        this.validating = b;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SensorMLToEbRIMTransformer [validating: ");
        sb.append(this.validating);
        sb.append(", xsltSource: ");
        sb.append(this.xsltSource);
        sb.append(", transformation file: ");
        sb.append(TRANSFORMATION_FILE_NAME);
        return sb.toString();
    }

    /**
     * 
     * Class catches {@link SAXParseException}s and is used during validated transformation.
     * 
     * @author Daniel Nüst
     * 
     */
    protected static class ParseExceptionHandler extends DefaultHandler {

        @SuppressWarnings("synthetic-access")
        @Override
        public void warning(SAXParseException spe) throws SAXException {
            log.error(" [ParseExceptionHandler] SAXParseException warning: " + spe);
        }

        @SuppressWarnings("synthetic-access")
        @Override
        public void error(SAXParseException spe) throws SAXException {
            log.error(" [ParseExceptionHandler] SAXParseException error: " + spe.getMessage());
        }
    }

}