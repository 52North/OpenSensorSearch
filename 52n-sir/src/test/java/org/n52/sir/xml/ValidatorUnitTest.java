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
/**
 * @author Yakoub
 */

package org.n52.sir.xml;

import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.junit.Test;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.xml.impl.SensorML4DiscoveryValidatorImpl;

/*
 * The file name to be changed to ValidatorTest
 */
public class ValidatorUnitTest {
	@Test
    public void readFile() throws TransformerConfigurationException,
            TransformerFactoryConfigurationError,
            ParserConfigurationException,
            OwsExceptionReport {

        File f = new File(ClassLoader.getSystemResource("AirBase-test.xml").getFile());

        // Read schema
        File schematronFile = new File(ClassLoader.getSystemResource("SensorML_Profile_for_Discovery.sch").getFile());
        // Read svrl
        File svrlFile = new File(ClassLoader.getSystemResource("xslt/iso_svrl_for_xslt2.xsl").getFile());

        // Now validate
        SensorML4DiscoveryValidatorImpl validator;

//        validator = new SensorML4DiscoveryValidatorImpl(schematronFile, svrlFile);
  //      boolean v = validator.validate(f);
    //    assertTrue(v);
    }
}
