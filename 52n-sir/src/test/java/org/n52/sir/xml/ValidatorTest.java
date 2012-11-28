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

package org.n52.sir.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.ParserConfigurationException;
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

import org.apache.xmlbeans.XmlException;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.util.XmlTools;
import org.n52.sir.xml.impl.SensorML4DiscoveryValidatorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Nüst
 * 
 */
@SuppressWarnings("unused")
public class ValidatorTest {

    private static Logger log = LoggerFactory.getLogger(ValidatorTest.class);

    /**
     * @param args
     * @throws OwsExceptionReport
     * @throws TransformerFactoryConfigurationError
     * @throws TransformerConfigurationException
     * @throws ParserConfigurationException
     */
    public static void main(String[] args) throws OwsExceptionReport,
            TransformerConfigurationException,
            TransformerFactoryConfigurationError,
            ParserConfigurationException {
        // test01();
        // testIfgicam();

        testAirBase();
    }

    private static void testAirBase() throws TransformerConfigurationException,
            TransformerFactoryConfigurationError,
            ParserConfigurationException,
            OwsExceptionReport {
        File f = new File("/home/daniel/workspace/SIR/data/transformation/AirBase-test.xml");

        validateFileToConsole(f);
    }

    private static void testIfgicam() throws OwsExceptionReport,
            TransformerConfigurationException,
            TransformerFactoryConfigurationError,
            ParserConfigurationException {
        File f = new File("/home/daniel/workspace/SIR/data/transformation/ifgicam-discoveryprofile.xml");

        validateFileToConsole(f);
    }

    /**
     * @param f
     * @throws TransformerConfigurationException
     * @throws TransformerFactoryConfigurationError
     * @throws ParserConfigurationException
     * @throws OwsExceptionReport
     */
    private static void validateFileToConsole(File f) throws TransformerConfigurationException,
            TransformerFactoryConfigurationError,
            ParserConfigurationException,
            OwsExceptionReport {
        File schematronFile = new File("/home/daniel/workspace/SIR/WebContent/WEB-INF/conf/SensorML_Profile_for_Discovery.sch");
        File svrlFile = new File("/home/daniel/workspace/SIR/WebContent/WEB-INF/xslt/iso_svrl_for_xslt2.xsl");
        SensorML4DiscoveryValidatorImpl validator = new SensorML4DiscoveryValidatorImpl(schematronFile, svrlFile);

        boolean v = validator.validate(f);

        System.out.println("Patterns:");
        for (String string : validator.getActivatedPatterns()) {
            System.out.println(string);
        }
        System.out.println("Rules:");
        for (String string : validator.getFiredRules()) {
            System.out.println(string);
        }

        if ( !v) {
            System.out.println("Failures:");
            for (String string : validator.getValidationFailures()) {
                System.out.println(string);
            }
        }
        else {
            System.out.println("VALID!!!11");
        }
    }

    private static void test01() throws OwsExceptionReport,
            TransformerConfigurationException,
            TransformerFactoryConfigurationError,
            ParserConfigurationException {
        File schematronFile = new File("/home/daniel/workspace/SIR/WebContent/WEB-INF/conf/SensorML_Profile_for_Discovery.sch");
        File svrlFile = new File("/home/daniel/workspace/SIR/WebContent/WEB-INF/conf/iso_svrl_for_xslt2.xsl");

        SensorML4DiscoveryValidatorImpl validator = new SensorML4DiscoveryValidatorImpl(schematronFile, svrlFile);

        boolean v = validator.validate(new File("/home/daniel/workspace/SIR/data/transformation/SensorML_Profile_for_Discovery_Example.xml"));
        if ( !v) {
            for (String string : validator.getValidationFailures()) {
                System.out.println(string);
            }
        }
        else {
            System.out.println("VALID!!!11");
        }

        v = validator.validate(new File("/home/daniel/workspace/SIR/data/transformation/IFGI_HWS1-discoveryprofile.xml"));

        if ( !v) {
            for (String string : validator.getValidationFailures()) {
                System.out.println(string);
            }
        }
        else {
            System.out.println("VALID!!!11");
        }
    }

    /**
     * 
     * @param inputFile
     * @param transformationDir
     * @param dataDir
     * @throws InstantiationError
     */
    private static void test02() throws InstantiationError {
        String inputFile = "/home/daniel/workspace/SIR/data/transformation/IFGI_HWS1-discoveryprofile.xml";
        log.info("Transforming " + inputFile);

        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource("/home/daniel/workspace/SIR/data/discovery.xsl"));

            // test the input document
            FileReader inputReader = new FileReader(inputFile);
            SensorMLDocument smlDoc = SensorMLDocument.Factory.parse(inputReader);
            log.info(XmlTools.validateAndIterateErrors(smlDoc));

            // encapsulate input document in a Source
            Source input = new DOMSource(smlDoc.getDomNode());

            // create output string
            StringWriter sw = new StringWriter();
            StreamResult output = new StreamResult(sw);

            // do the transformation
            transformer.transform(input, output);

            // create output document
            String outputString = output.getWriter().toString();

            System.out.println(outputString);

            // clean up
            input = null;
            sw = null;
            output = null;
            outputString = null;
        }
        catch (FileNotFoundException e) {
            log.error("", e);
        }
        catch (TransformerException e) {
            log.error("", e);
        }
        catch (IOException e) {
            log.error("", e);
        }
        catch (XmlException e) {
            log.error("", e);
        }
    }

}
