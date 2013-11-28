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

package org.n52.oss.sir.xml;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.util.GuiceUtil;
import org.n52.sir.xml.impl.SensorML4DiscoveryValidatorImpl;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

public class Validator {

    private static String schematronFile;

    private static String svrlFile;

    @BeforeClass
    public static void prepare() {
        Injector i = GuiceUtil.configurePropertiesFiles();
        schematronFile = i.getInstance(Key.get(String.class, Names.named("oss.sir.validation.profile.sml.discovery")));
        svrlFile = i.getInstance(Key.get(String.class, Names.named("oss.sir.validation.svrlSchema")));
    }

    @Test
    public void testAirBase() throws OwsExceptionReport, URISyntaxException {
        File f = new File(getClass().getResource("/AirBase-test.xml").getFile());

        SensorML4DiscoveryValidatorImpl validator = new SensorML4DiscoveryValidatorImpl(schematronFile, svrlFile);

        boolean v = validator.validate(f);
        assertThat("tested file is valid", v, is(true));
    }

    @Test
    public void testInvalidAirBase() throws OwsExceptionReport, URISyntaxException {
        File f = new File(getClass().getResource("/AirBase-test-invalid.xml").getFile());

        SensorML4DiscoveryValidatorImpl validator = new SensorML4DiscoveryValidatorImpl(schematronFile, svrlFile);

        boolean v = validator.validate(f);
        assertThat("tested file is INvalid", v, is(false));
        List<String> validationFailures = validator.getValidationFailures();
        assertThat(validationFailures.size(), is(2));
        assertThat(Arrays.toString(validationFailures.toArray()), containsString("gml:description"));
        assertThat(Arrays.toString(validationFailures.toArray()), containsString("sml:validTime"));
    }
}
