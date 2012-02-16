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

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.opengis.sensorML.x101.SensorMLDocument;
import net.sf.saxon.Transform;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.xml.IProfileValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.LocatorImpl;

/**
 * @author Daniel Nüst
 * 
 */
public class SensorML4DiscoveryValidatorImpl implements IProfileValidator {

    protected static Logger log = LoggerFactory.getLogger(SensorML4DiscoveryValidatorImpl.class);

    protected static File tempXSLFile = null;

    private static final String tempDir = System.getProperty("java.io.tmpdir") + "/";

    protected static final String QNAME_FAILED_ASSERT = "failed-assert";

    protected static final String QNAME_FIRED_RULE = "fired-rule";

    protected static final String QNAME_ACTIVE_PATTERN = "active-pattern";

    protected static final String ATTRIBUTE_NAME_CONTEXT = "context";

    protected static final String ATTRIBUTE_NAME_TEST = "test";

    protected static final String ATTRIBUTE_NAME_ID = "id";

    protected static final String ATTRIBUTE_NAME_NAME = "name";

    protected static final String ATTRIBUTE_NAME_LOCATION = "location";

    private List<String> activatedPatterns = new ArrayList<String>();

    private List<String> firedRules = new ArrayList<String>();

    private List<String> assertionFailures = new ArrayList<String>();

    private Transformer transformer;

    private static StreamSource source;

    protected static boolean first = true;

    private static TransformerFactory tFactory = TransformerFactory.newInstance();

    /**
     * 
     * @param profileFile
     * @param svrlFile
     * @throws TransformerConfigurationException
     * @throws TransformerFactoryConfigurationError
     * @throws ParserConfigurationException
     */
    public SensorML4DiscoveryValidatorImpl(File profileFile, File svrlFile) throws TransformerConfigurationException,
            TransformerFactoryConfigurationError,
            ParserConfigurationException {
        initializeTempXSLFile(profileFile, svrlFile);

        this.transformer = tFactory.newTransformer(source);
        // this.db = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        if (log.isDebugEnabled())
            log.debug("NEW SensorML4DiscoveryValidatorImpl");
    }

    /**
     * 
     * @param profileFile
     */
    private synchronized void initializeTempXSLFile(final File profileFile, final File svrlFile) {
        // File temp = new File(tempDir);
        // boolean dir = temp.isDirectory();
        // boolean write = temp.canWrite();

        tempXSLFile = new File(tempDir + profileFile.getName() + ".xsl");
        // tempXSLFile.canWrite();

        if (first | !tempXSLFile.exists()) {
            if (log.isDebugEnabled()) {
                log.debug("Creating XSL from Schematron profile: " + profileFile);
            }

            Executors.newSingleThreadExecutor().submit(new Runnable() {

                /*
                 * (non-Javadoc)
                 * @see java.lang.Runnable#run()
                 */
                @Override
                public void run() {
                    // transform the schematron to XSL,
                    // http://saxon.sourceforge.net/saxon6.5/using-xsl.html#Command-line
                    Transform trans = new Transform();
                    String[] arguments = new String[] {"-x",
                                                       "org.apache.xerces.parsers.SAXParser",
                                                       "-w1",
                                                       "-o",
                                                       tempXSLFile.getAbsolutePath(),
                                                       profileFile.getAbsolutePath(),
                                                       svrlFile.getAbsolutePath(), // "docs/iso_svrl_for_xslt2.xsl",
                                                       "generate-paths=yes"};
                    trans.doTransform(arguments, "java net.sf.saxon.Transform");

                    first = false;
                    log.info("Created XSL file for validation: " + tempXSLFile);
                }
            });
            
            if (source == null) {
                log.debug("Created Stream Source of svrl xsl file.");
                source = new StreamSource(tempXSLFile);
            }
        }
        else if (log.isDebugEnabled()) {
            log.debug("Reusing existing XSL file " + tempXSLFile + " for " + profileFile + ".");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.xml.IProfileValidator#validate(net.opengis.sensorML.x101.SensorMLDocument)
     */
    @Override
    public boolean validate(SensorMLDocument smlDoc) {
        return this.actualValidate(smlDoc);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.xml.IProfileValidator#validate(java.io.File)
     */
    @Override
    public boolean validate(File file) throws OwsExceptionReport {
        try {
            SensorMLDocument smlDoc = SensorMLDocument.Factory.parse(file);
            return validate(smlDoc);
        }
        catch (XmlException e) {
            log.error("XmlException when parsing SensorMLDocument from file.", e);
            throw new OwsExceptionReport("Could not test given file for compliance with profile for discovery!", e);
        }
        catch (IOException e) {
            log.error("IOException when parsing SensorMLDocument from file.", e);
            throw new OwsExceptionReport("Could not test given file for compliance with profile for discovery!", e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.xml.IProfileValidator#validate(org.apache.xmlbeans.XmlObject)
     */
    @Override
    public boolean validate(XmlObject xml) {
        if (xml instanceof SensorMLDocument) {
            SensorMLDocument smlDoc = (SensorMLDocument) xml;
            return validate(smlDoc);
        }
        log.error("The given XmlObject could was not a SensorMLDocument!");

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.xml.IProfileValidator#getValidationFailures()
     */
    @Override
    public List<String> getValidationFailures() {
        return this.getAssertionFailures();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.xml.IProfileValidator#getValidationFailuresAsString()
     */
    @Override
    public String getValidationFailuresAsString() {
        List<String> failures = getValidationFailures();
        StringBuilder sb = new StringBuilder();
        sb.append("The document is NOT valid:\n");
        for (String string : failures) {
            sb.append(string);
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * 
     * @param smlDoc
     * @return
     */
    private boolean actualValidate(SensorMLDocument smlDoc) {
        if (log.isDebugEnabled()) {
            log.debug("Validating SensorMLDocument against Discovery Profile...");
        }

        // encapsulate input document in a Source
        Source input = new DOMSource(smlDoc.getDomNode());

        // create output string
        StringWriter sw = new StringWriter();
        StreamResult output = new StreamResult(sw);

        // do the transformation
        try {
            this.transformer.transform(input, output);

            String outputString = output.getWriter().toString();
            processSVRL(new InputSource(new StringReader(outputString)));
        }
        catch (TransformerException e) {
            log.error("Error transforming SensorML for validation against profile for discovery!", e);
            return false;
        }
        catch (SAXException e) {
            log.error("Error transforming SensorML for validation against profile for discovery!", e);
            return false;
        }
        catch (IOException e) {
            log.error("Error transforming SensorML for validation against profile for discovery!", e);
            return false;
        }
        catch (ParserConfigurationException e) {
            log.error("Error processing SVRL output!", e);
            return false;
        }

        // clean up
        input = null;
        sw = null;
        output = null;

        if (log.isDebugEnabled()) {
            log.debug("Validation result: " + this.getAssertionFailures().size() + " failures, "
                    + this.activatedPatterns.size() + " activated patterns, and " + this.firedRules.size()
                    + " fired rules.");
        }

        return (this.getAssertionFailures().size() == 0) ? true : false;
    }

    /**
     * @param inputSource
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    private void processSVRL(InputSource inputSource) throws SAXException, IOException, ParserConfigurationException {
        /*
         * an extension of DefaultHandler
         */
        DefaultHandler handler = new SchematronResultHandler();
        LocatorImpl locator = new LocatorImpl();
        handler.setDocumentLocator(locator);

        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        parser.parse(inputSource, handler);
        handler = null;
        locator = null;
        parser = null;
    }

    /**
     * 
     * @param assertionFailures
     */
    protected void setAssertionFailures(List<String> assertionFailures) {
        this.assertionFailures = assertionFailures;
    }

    /**
     * 
     * @return
     */
    public List<String> getAssertionFailures() {
        return this.assertionFailures;
    }

    /**
     * 
     * @param firedRules
     */
    public void setFiredRules(List<String> firedRules) {
        this.firedRules = firedRules;
    }

    /**
     * 
     * @return
     */
    public List<String> getFiredRules() {
        return this.firedRules;
    }

    /**
     * 
     * @param patterns
     */
    public void setActivatedPatterns(List<String> patterns) {
        this.activatedPatterns = patterns;
    }

    /**
     * 
     * @return
     */
    public List<String> getActivatedPatterns() {
        return this.activatedPatterns;
    }

    /**
     * 
     * @author Daniel Nüst
     * 
     */
    private class SchematronResultHandler extends DefaultHandler {

        private String failTmp;
        private Locator locator;
        private boolean insideFail = false;
        private String ruleTmp;
        private String patternTmp;

        public SchematronResultHandler() {
            super();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (qName.endsWith(QNAME_FAILED_ASSERT)) {
                this.failTmp = "[line " + this.locator.getLineNumber() + "]\tAssertion error at \""
                        + attributes.getValue(ATTRIBUTE_NAME_TEST) + "\"" + " (location: \""
                        + attributes.getValue(ATTRIBUTE_NAME_LOCATION) + "\"): ";
                this.insideFail = true;
            }
            else if (qName.endsWith(QNAME_FIRED_RULE)) {
                this.ruleTmp = "Fired rule in context \"" + attributes.getValue(ATTRIBUTE_NAME_CONTEXT) + "\" (line "
                        + this.locator.getLineNumber() + ").";
            }
            else if (qName.endsWith(QNAME_ACTIVE_PATTERN)) {
                this.patternTmp = "Active pattern id: \"" + attributes.getValue(ATTRIBUTE_NAME_ID) + " -- name: \""
                        + attributes.getValue(ATTRIBUTE_NAME_NAME) + "\" (line " + this.locator.getLineNumber() + ").";
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String,
         * java.lang.String)
         */
        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (qName.endsWith(QNAME_FAILED_ASSERT)) {
                getAssertionFailures().add(this.failTmp);
                this.failTmp = null;
                this.insideFail = false;
            }
            else if (qName.endsWith(QNAME_FIRED_RULE)) {
                getFiredRules().add(this.ruleTmp);
                this.ruleTmp = null;
            }
            else if (qName.endsWith(QNAME_ACTIVE_PATTERN)) {
                getActivatedPatterns().add(this.patternTmp);
                this.patternTmp = null;
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
         */
        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (this.insideFail) {
                this.failTmp += new String(ch, start, length).trim();
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.xml.sax.helpers.DefaultHandler#setDocumentLocator(org.xml.sax.Locator)
         */
        @Override
        public void setDocumentLocator(Locator locator) {
            this.locator = locator;
        }

    }

}