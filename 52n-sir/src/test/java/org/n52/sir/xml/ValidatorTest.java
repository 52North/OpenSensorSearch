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
package org.n52.sir.xml;

import java.io.File;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.n52.sir.ows.OwsExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Nüst
 * 
 */
@SuppressWarnings("unused")
public class ValidatorTest {

    private static Logger log = LoggerFactory.getLogger(ValidatorTest.class);

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
        // File f = new
        // File("D:/52n/OpenSensorSearch/52n-sir/target/test-classes/transformation/AirBase-test.xml");

        // validateFileToConsole(f);
    }

    private static void testIfgicam() throws OwsExceptionReport,
            TransformerConfigurationException,
            TransformerFactoryConfigurationError,
            ParserConfigurationException {
        // File f = new
        // File("D:/52n/OpenSensorSearch/52n-sir/target/test-classes/transformation/ifgicam-discoveryprofile.xml");

        // validateFileToConsole(f);
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
        // File schematronFile = new
        // File("D:/52n/OpenSensorSearch/52n-sir/target/SIR/WEB-INF/classes/SensorML_Profile_for_Discovery.sch");
        // File svrlFile = new
        // File("D:/52n/OpenSensorSearch/52n-sir/target/SIR/WEB-INF/classes/xslt/iso_svrl_for_xslt2.xsl");
        // SensorML4DiscoveryValidatorImpl validator = new SensorML4DiscoveryValidatorImpl(schematronFile,
        // svrlFile);

        // boolean v = validator.validate(f);

        /*
         * System.out.println("Patterns:"); for (String string : validator.getActivatedPatterns()) {
         * System.out.println(string); } System.out.println("Rules:"); for (String string :
         * validator.getFiredRules()) { System.out.println(string); }
         * 
         * if ( !v) { System.out.println("Failures:"); for (String string : validator.getValidationFailures())
         * { System.out.println(string); } } else { System.out.println("VALID!!!11"); }
         */
    }

    private static void test01() throws OwsExceptionReport,
            TransformerConfigurationException,
            TransformerFactoryConfigurationError,
            ParserConfigurationException {
        /*
         * File schematronFile = new
         * File("/home/daniel/workspace/SIR/WebContent/WEB-INF/conf/SensorML_Profile_for_Discovery.sch"); File
         * svrlFile = new File("/home/daniel/workspace/SIR/WebContent/WEB-INF/conf/iso_svrl_for_xslt2.xsl");
         * 
         * SensorML4DiscoveryValidatorImpl validator = new SensorML4DiscoveryValidatorImpl(schematronFile,
         * svrlFile);
         * 
         * boolean v = validator.validate(new
         * File("/home/daniel/workspace/SIR/data/transformation/SensorML_Profile_for_Discovery_Example.xml"));
         * if ( !v) { for (String string : validator.getValidationFailures()) { System.out.println(string); }
         * } else { System.out.println("VALID!!!11"); }
         * 
         * v = validator.validate(new
         * File("/home/daniel/workspace/SIR/data/transformation/IFGI_HWS1-discoveryprofile.xml"));
         * 
         * if ( !v) { for (String string : validator.getValidationFailures()) { System.out.println(string); }
         * } else { System.out.println("VALID!!!11"); }
         */
    }

    /**
     * 
     * @param inputFile
     * @param transformationDir
     * @param dataDir
     * @throws InstantiationError
     */
    private static void test02() throws InstantiationError {
        /*
         * String inputFile =
         * "D:/52n/OpenSensorSearch/52n-sir/target/test-classes/transformation/IFGI_HWS1-discoveryprofile.xml"
         * ; log.info("Transforming " + inputFile);
         * 
         * try { Transformer transformer = TransformerFactory.newInstance().newTransformer(new
         * StreamSource("/home/daniel/workspace/SIR/data/discovery.xsl"));
         * 
         * // test the input document FileReader inputReader = new FileReader(inputFile); SensorMLDocument
         * smlDoc = SensorMLDocument.Factory.parse(inputReader);
         * log.info(XmlTools.validateAndIterateErrors(smlDoc));
         * 
         * // encapsulate input document in a Source Source input = new DOMSource(smlDoc.getDomNode());
         * 
         * // create output string StringWriter sw = new StringWriter(); StreamResult output = new
         * StreamResult(sw);
         * 
         * // do the transformation transformer.transform(input, output);
         * 
         * // create output document String outputString = output.getWriter().toString();
         * 
         * System.out.println(outputString);
         * 
         * // clean up input = null; sw = null; output = null; outputString = null; } catch
         * (FileNotFoundException e) { log.error("", e); } catch (TransformerException e) { log.error("", e);
         * } catch (IOException e) { log.error("", e); } catch (XmlException e) { log.error("", e); }
         */
    }

}
