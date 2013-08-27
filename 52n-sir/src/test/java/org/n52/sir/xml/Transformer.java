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
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.transform.Result;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;

import org.apache.xmlbeans.XmlException;
import org.junit.Before;
import org.junit.Test;
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
public class Transformer {

    private static Logger log = LoggerFactory.getLogger(Transformer.class);

    private static String xsltDir = "/xslt";

    private SMLtoEbRIMTransformer transformer;

    @Before
    public void createTransformer() throws URISyntaxException {
        URI uri = Transformer.class.getResource(xsltDir).toURI();
        Path path = Paths.get(uri);

        this.transformer = new SMLtoEbRIMTransformer(path);
        this.transformer.setValidating(false);
    }

    @Test
    public void testDiscoveryProfileExample() throws InstantiationError,
            IOException,
            TransformerException,
            XmlException,
            URISyntaxException {
        testTransformFile("/transformation/SensorML_Profile_for_Discovery_Example.xml");

        // List<String> files = new ArrayList<>();
        // // files.add();
        // files.add("IFGI_HWS1-discoveryprofile.xml");
        // // files.add("generated_01.xml");
        // // files.add("FH_HWS1.xml");
        // // files.add("singleTestComponent.xml");
        // files.add("FH_HWS1-discoveryprofile.xml");
        // files.add("ifgicam-discoveryprofile.xml");
    }

    @Test
    public void smlToEbrim() {
        // TODO implement test
    }

    private void testTransformFile(String inputFile) throws InstantiationError,
            IOException,
            TransformerException,
            XmlException,
            URISyntaxException {
        log.info("Transforming " + inputFile);

        URL resource = Transformer.class.getResource(inputFile);
        Path path = Paths.get(resource.toURI());

        // SensorMLDocument smlDoc = SensorMLDocument.Factory.parse(path.toFile());
        // log.info(XmlTools.validateAndIterateErrors(smlDoc));
        Result r = this.transformer.transform(path.toFile().getPath());
        StreamResult sr = (StreamResult) r;
        File outputFile = saveStreamToTempFile(sr);

        RegistryPackageDocument rpd = RegistryPackageDocument.Factory.parse(outputFile);
        RegistryPackageType rp = rpd.getRegistryPackage();

        // TODO use xmlunit to compare the file
        
        log.info(XmlTools.validateAndIterateErrors(rp));

        String eoInfo = XmlTools.inspect(rp);
        log.info(eoInfo);

        log.info(XmlTools.validateAndIterateErrors(rp));
    }

    private File saveStreamToTempFile(StreamResult sr) throws IOException {
        Path tempFile = Files.createTempFile("transform_", ".xml");

        try (FileWriter fw = new FileWriter(tempFile.toFile())) {
            fw.write(sr.getWriter().toString());
        }

        log.trace("Wrote StreamResult to file " + tempFile);
        return tempFile.toFile();
    }

}
