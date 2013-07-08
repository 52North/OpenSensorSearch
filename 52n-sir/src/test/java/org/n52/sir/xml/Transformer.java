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

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.TransformerException;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Test;
import org.n52.sir.util.XmlTools;
import org.n52.sir.xml.impl.SMLtoEbRIMTransformer;
import org.xml.sax.SAXException;

import x0.oasisNamesTcEbxmlRegrepXsdRim3.RegistryPackageDocument;
import x0.oasisNamesTcEbxmlRegrepXsdRim3.RegistryPackageType;

public class Transformer {

    @Test
    public void sensorML2ebRIM() throws XmlException, TransformerException, IOException, SAXException {
        // test the input document
        try (InputStream in = Transformer.class.getResourceAsStream("/transformation/SensorML_Profile_for_Discovery_Example.xml")) {
            SensorMLDocument smlDoc = SensorMLDocument.Factory.parse(in);

            String result = XmlTools.validateAndIterateErrors(smlDoc);
            assertThat(result, containsString("is valid"));

            ITransformer transformer = new SMLtoEbRIMTransformer("/xslt/");
            transformer.setValidating(false);

            XmlObject transformed = transformer.transform(smlDoc);

            RegistryPackageDocument rpd = RegistryPackageDocument.Factory.parse(transformed.xmlText());
            RegistryPackageType rp = rpd.getRegistryPackage();

            result = XmlTools.validateAndIterateErrors(rp);
            assertTrue(result.isEmpty()); // TODO use hamcrest matcher

            RegistryPackageDocument control = RegistryPackageDocument.Factory.parse(Transformer.class.getResourceAsStream("SensorML_Profile_for_Discovery_Example_OUT.xml"));

            assertXMLEqual(control.xmlText(), rpd.xmlText());
        }
    }
    // @Test
    // public static void moreFiles() {
    // String[] s = new String[] {"IFGI_HWS1-discoveryprofile.xml",
    // "FH_HWS1-discoveryprofile.xml",
    // "FH_HWS1-discoveryprofile.xml"};
    //
    // assertTrue(true);
    // }

}
