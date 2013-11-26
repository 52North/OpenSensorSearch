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

package org.n52.oss.sir.xml;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

import javax.xml.transform.Result;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;

import org.apache.xmlbeans.XmlException;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.NodeDetail;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.oss.util.XmlTools;
import org.n52.sir.xml.impl.SMLtoEbRIMTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import x0.oasisNamesTcEbxmlRegrepXsdRim3.RegistryPackageDocument;

/**
 * 
 * Testclass for transformations.
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class Sml2EbrimTransformation {

    private static Logger log = LoggerFactory.getLogger(Sml2EbrimTransformation.class);

    private static String xsltDir = "/xslt";

    private SMLtoEbRIMTransformer transformer;

    private static String discoveryProfileTestFile_sml = "/transformation/SensorML_Profile_for_Discovery_Example.xml";

    private static String discoveryProfileTestFile_ebrim = "/transformation/SensorML_Profile_for_Discovery_Example.ebrim.xml";

    private static String fhHwsTestFile_sml = "/transformation/FH_HWS1-discoveryprofile.xml";

    private static String fhHwsTestFile_ebrim = "/transformation/FH_HWS1-discoveryprofile.ebrim.xml";

    private static String ifgiHwsTestFile_sml = "/transformation/IFGI_HWS1-discoveryprofile.xml";

    private static String ifgiHwsTestFile_ebrim = "/transformation/IFGI_HWS1-discoveryprofile.ebrim.xml";

    private static String ifgicamTestFile_sml = "/transformation/ifgicam-discoveryprofile.xml";

    private static String ifgicamTestFile_ebrim = "/transformation/ifgicam-discoveryprofile.ebrim.xml";

    @BeforeClass
    public static void setUpXmlUnit() {
        XMLUnit.setNormalizeWhitespace(true);
        XMLUnit.setIgnoreComments(true);
    }

    @Before
    public void createTransformer() throws URISyntaxException {
        URI uri = Sml2EbrimTransformation.class.getResource(xsltDir).toURI();
        Path path = Paths.get(uri);

        this.transformer = new SMLtoEbRIMTransformer(path);
        this.transformer.setValidatingInputAndOutput(false);
    }

    @Test
    public void discoveryProfileTestFilesAreSimilar() throws Exception {
        compareConvertedSmlAndEbrim(discoveryProfileTestFile_sml, discoveryProfileTestFile_ebrim);
    }

    @Test
    public void ifgicamTestFilesAreSimilar() throws Exception {
        compareConvertedSmlAndEbrim(ifgicamTestFile_sml, ifgicamTestFile_ebrim);
    }

    @Test
    public void ifgiWeatherStationTestFilesAreSimilar() throws Exception {
        compareConvertedSmlAndEbrim(ifgiHwsTestFile_sml, ifgiHwsTestFile_ebrim);
    }

    @Test
    public void fhWeatherStationTestFilesAreSimilar() throws Exception {
        compareConvertedSmlAndEbrim(fhHwsTestFile_sml, fhHwsTestFile_ebrim);
    }

    @Test
    public void transformedDPTestDocumentIsValid() throws Exception {
        transformAndValidate(discoveryProfileTestFile_sml);
    }

    @Test
    public void transformedIfgiWeatherStationTestDocumentIsValid() throws Exception {
        transformAndValidate(ifgiHwsTestFile_sml);
    }

    @Test
    public void transformedIfgicamTestDocumentIsValid() throws Exception {
        transformAndValidate(ifgicamTestFile_sml);
    }

    public void transformAndValidate(String sml) throws Exception {
        URL resource = Sml2EbrimTransformation.class.getResource(sml);
        Path path = Paths.get(resource.toURI());

        Result r = this.transformer.transform(path.toFile().getPath());

        StreamResult sr = (StreamResult) r;
        String transformationOutput = sr.getWriter().toString();
        RegistryPackageDocument transformed = RegistryPackageDocument.Factory.parse(transformationOutput);

        boolean valid = transformed.validate();
        if ( !valid)
            log.trace("Transformed document NOT valid:\n{}", XmlTools.validateAndIterateErrors(transformed));

        assertThat("XMLBeans says document is valid", valid, is(true));
        // XMLAssert.assertXMLValid(transformed.xmlText()); // works only with doctype!
    }

    private void compareConvertedSmlAndEbrim(String sml, String ebrim) throws URISyntaxException,
            FileNotFoundException,
            TransformerException,
            XmlException,
            IOException,
            SAXException {
        log.info("Transforming {} and comparing with {}", sml, ebrim);

        URL resource = Sml2EbrimTransformation.class.getResource(sml);
        Path path = Paths.get(resource.toURI());

        Result r = this.transformer.transform(path.toFile().getPath());

        StreamResult sr = (StreamResult) r;
        String transformationOutput = sr.getWriter().toString();
        RegistryPackageDocument test = RegistryPackageDocument.Factory.parse(transformationOutput);
        // RegistryPackageType rp = rpd.getRegistryPackage();
        // log.info(XmlTools.validateAndIterateErrors(test));

        resource = Sml2EbrimTransformation.class.getResource(ebrim);
        path = Paths.get(resource.toURI());
        RegistryPackageDocument control = RegistryPackageDocument.Factory.parse(path.toFile());

        Diff diff = new Diff(control.xmlText(), test.xmlText());
        Diff myDiff = new TransformationLaxDifference(diff);

        // XMLAssert.assertXMLEqual(control.xmlText(), test.xmlText());
        // assertThat("control and test XML are similar", myDiff.similar(), is(true));

        XMLAssert.assertXMLEqual(myDiff, true);
    }

    protected static class TransformationLaxDifference extends Diff {

        private static final Collection<String> identifiersWithRandomComponent = Arrays.asList(new String[] {"id",
                                                                                                             "classificationNode",
                                                                                                             "targetObject",
                                                                                                             "service",
                                                                                                             "classifiedObject",
                                                                                                             "primaryContact"});

        protected TransformationLaxDifference(Diff prototype) {
            super(prototype);
        }

        @Override
        public int differenceFound(Difference difference) {
            NodeDetail cn = difference.getControlNodeDetail();
            NodeDetail tn = difference.getTestNodeDetail();

            if (cn.getNode().getNodeName().equals(tn.getNode().getNodeName())
                    && identifiersWithRandomComponent.contains(cn.getNode().getNodeName())) {
                log.trace("Comparing identifiers: {}", difference.toString());

                String controlId = cn.getNode().getNodeValue();
                String testId = tn.getNode().getNodeValue();

                // pass if difference is only in the generated part of the id
                String nonRandomPartOfControlId = controlId.substring(0, controlId.lastIndexOf(":"));
                String nonRandomPartOfTestId = testId.substring(0, controlId.lastIndexOf(":"));
                if (nonRandomPartOfControlId.equals(nonRandomPartOfTestId))
                    return RETURN_IGNORE_DIFFERENCE_NODES_SIMILAR;
            }
            else if ( (cn.getXpathLocation().endsWith("Envelope[1]/lowerCorner[1]/text()[1]")
                    || cn.getXpathLocation().endsWith("Envelope[1]/upperCorner[1]/text()[1]") || cn.getXpathLocation().endsWith("Point[1]/pos[1]/text()[1]"))
                    && cn.getNode().getNodeName().equals(tn.getNode().getNodeName())
                    && cn.getNode().getNodeName().equals("#text")) {
                log.trace("Comparing coordinates: {}", difference.toString());

                String[] controlCoords = cn.getNode().getNodeValue().split(" ");
                String[] testCoords = tn.getNode().getNodeValue().split(" ");

                // pass if coordinates are just switched
                if (controlCoords[0].equals(testCoords[1]) && controlCoords[1].equals(testCoords[0]))
                    return RETURN_IGNORE_DIFFERENCE_NODES_SIMILAR;
            }
            else if (cn.getNode().getNodeName().equals(tn.getNode().getNodeName())
                    && cn.getXpathLocation().endsWith("Point[1]/@id")) {
                log.trace("Comparing point ids: {}", difference.toString());
                return RETURN_IGNORE_DIFFERENCE_NODES_SIMILAR;
            }

            return super.differenceFound(difference);
        }
    }

}
