/**
 * Copyright 2013 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * @author Yakoub
 */

package org.n52.oss.sir.xml;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileReader;

import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.oss.util.GuiceUtil;
import org.n52.oss.util.XmlTools;
import org.n52.sir.xml.impl.SMLtoEbRIMTransformer;

import x0.oasisNamesTcEbxmlRegrepXsdRim3.RegistryPackageDocument;
import x0.oasisNamesTcEbxmlRegrepXsdRim3.RegistryPackageType;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

public class Transformer {

    private static String xsltDir;

    private void failIfURLNull(String resource) {
        if (ClassLoader.getSystemResource(resource) == null)
            fail(resource + " Is missing");
    }

    @BeforeClass
    public static void prepare() {
        Injector i = GuiceUtil.configurePropertiesFiles();
        xsltDir = i.getInstance(Key.get(String.class, Names.named("oss.transform.xsltDir")));
    }

    @Test
    public void testTransform() {
        String[] s = new String[] {"IFGI_HWS1-discoveryprofile.xml",
                                   "FH_HWS1-discoveryprofile.xml",
                                   "FH_HWS1-discoveryprofile.xml"};

        for (String str : s)
            failIfURLNull("transformation/" + str);

        File transformations = new File(ClassLoader.getSystemResource("transformation/").getFile());

        for (int i = 0; i < s.length; i++) {
            File file = new File(ClassLoader.getSystemResource("transformation/" + s[i]).getFile());
            testTransformation(file.getName(), xsltDir, transformations.getAbsolutePath() + "/");
        }

    }

    private static void testTransformation(String inputFile, String transformationDir, String dataDir) throws InstantiationError {

        SMLtoEbRIMTransformer transformer = new SMLtoEbRIMTransformer(transformationDir, false);

        try {
            // test the input document
            FileReader inputReader = new FileReader(dataDir + inputFile);
            SensorMLDocument smlDoc = SensorMLDocument.Factory.parse(inputReader);

            transformer.setValidatingInputAndOutput(false);

            Result r = transformer.transform(dataDir + inputFile);
            StreamResult sr = (StreamResult) r;

            String outputString = sr.getWriter().toString();

            RegistryPackageDocument rpd = RegistryPackageDocument.Factory.parse(outputString);
            RegistryPackageType rp = rpd.getRegistryPackage();

            String eoInfo = XmlTools.inspect(rp);
        }
        catch (Exception e) {
            fail(e.toString());
        }
    }

}
