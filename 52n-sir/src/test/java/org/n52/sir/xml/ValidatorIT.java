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

import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;
import org.n52.sir.xml.impl.SensorML4DiscoveryValidatorImpl;

public class ValidatorIT {
    private void failIfFileNotExists(File f) {
        if ( !f.exists())
            fail(f.getName() + " Is missing!");
    }

    private void failIfURLNull(String resource) {
        if (ClassLoader.getSystemResource(resource) == null)
            fail(resource + " Is missing");
    }

    @Test
    public void readFile() {

        failIfURLNull("AirBase-test.xml");
        failIfURLNull("SensorML_Profile_for_Discovery.sch");
        failIfURLNull("xslt/iso_svrl_for_xslt2.xsl");

        File f = new File(ClassLoader.getSystemResource("AirBase-test.xml").getFile());

        failIfFileNotExists(f);
        // Read schema
        File schematronFile = new File(ClassLoader.getSystemResource("SensorML_Profile_for_Discovery.sch").getFile());
        failIfFileNotExists(schematronFile);
        // Read svrl
        File svrlFile = new File(ClassLoader.getSystemResource("xslt/iso_svrl_for_xslt2.xsl").getFile());
        failIfFileNotExists(svrlFile);

        // Now validate
        SensorML4DiscoveryValidatorImpl validator;
        try {
            validator = new SensorML4DiscoveryValidatorImpl(schematronFile, svrlFile);
            boolean v = validator.validate(f);
            if ( !v)
                fail("Not a valid test sensor - invalid validator!");

        }
        catch (Exception e) {
            fail(e.toString());
        }
    }
}
