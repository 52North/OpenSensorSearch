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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.transform.Result;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.xmlbeans.XmlException;
import org.n52.sir.util.XmlTools;
import org.n52.sir.xml.impl.SMLtoEbRIMTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import x0.oasisNamesTcEbxmlRegrepXsdRim3.RegistryPackageDocument;
import x0.oasisNamesTcEbxmlRegrepXsdRim3.RegistryPackageType;

/**
 * 
 * Testclass for transformations.
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
@SuppressWarnings("unused")
public class TransformerTest {

    private static Logger log = LoggerFactory.getLogger(TransformerTest.class);

    /**
     * @param args
     */
    public static void main(String[] args) {
        // initialize Logger
        Properties p = new Properties();
        p.setProperty("log4j.rootLogger", "DEBUG, A1");
        p.setProperty("log4j.appender.A1", "org.apache.log4j.ConsoleAppender");
        p.setProperty("log4j.appender.A1.layout", "org.apache.log4j.PatternLayout");
        p.setProperty("log4j.appender.A1.layout.ConversionPattern", "%d{ABSOLUTE} %-5p %-40.40c:%3L - %m %n");

        test01();
    }

    /**
     * Transform a list of .xml files.
     */
    private static void test01() {
        String dataDir = "/home/daniel/workspace/SIR/data/transformation/";
        String dir = "/home/daniel/workspace/SIR/WebContent/WEB-INF/xslt/";

        List<String> files = new ArrayList<String>();
        // files.add("SensorML_Profile_for_Discovery_Example.xml");
        files.add("IFGI_HWS1-discoveryprofile.xml");
        // files.add("generated_01.xml");
        // files.add("FH_HWS1.xml");
        // files.add("singleTestComponent.xml");
        files.add("FH_HWS1-discoveryprofile.xml");
        files.add("ifgicam-discoveryprofile.xml");

        for (String f : files) {
            testTransform(f, dir, dataDir);
        }

    }

    /**
     * 
     * Transform the given input file.
     * 
     * @param inputFile
     * @param transformationDir
     * @throws InstantiationError
     */
    private static void testTransform(String inputFile, String transformationDir, String dataDir) throws InstantiationError {
        log.info("Transforming " + inputFile);

        SMLtoEbRIMTransformer transformer = new SMLtoEbRIMTransformer(transformationDir);

        try {
            // test the input document
            FileReader inputReader = new FileReader(dataDir + inputFile);
            SensorMLDocument smlDoc = SensorMLDocument.Factory.parse(inputReader);
            log.info(XmlTools.validateAndIterateErrors(smlDoc));

            transformer.setValidating(false);

            Result r = transformer.transform(dataDir + inputFile);
            StreamResult sr = (StreamResult) r;

            String outputString = sr.getWriter().toString();

            String outputFileName = dataDir + inputFile.substring(0, inputFile.indexOf(".")) + "_OUT.xml";
            saveStreamToFile(outputFileName, sr);

            RegistryPackageDocument rpd = RegistryPackageDocument.Factory.parse(outputString);
            RegistryPackageType rp = rpd.getRegistryPackage();

            log.info(XmlTools.validateAndIterateErrors(rp));

            String eoInfo = XmlTools.inspect(rp);
            log.info(eoInfo);

            log.info(XmlTools.validateAndIterateErrors(rp));
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

    /**
     * 
     * Helper method for saving a String to a file.
     * 
     * @param filePath
     * @param result
     */
    private static void saveToFile(String filePath, String xmlText) {
        FileWriter fw;
        try {
            fw = new FileWriter(filePath);
            fw.write(xmlText);
            fw.close();
            log.info("Wrote String to file " + filePath);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * Helper method for saving a {@link StreamResult} to a file.
     * 
     * @param file
     * @param sr
     * @throws IOException
     */
    private static void saveStreamToFile(String file, StreamResult sr) throws IOException {
        FileWriter fw = new FileWriter(file);
        fw.write(sr.getWriter().toString());
        fw.close();
        log.info("Wrote StreamResult to file " + file);
    }

}
