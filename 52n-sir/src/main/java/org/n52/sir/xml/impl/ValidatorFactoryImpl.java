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
package org.n52.sir.xml.impl;

import java.io.File;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.SirConfigurator;
import org.n52.sir.xml.IProfileValidator;
import org.n52.sir.xml.IValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Nüst
 * 
 */
public class ValidatorFactoryImpl implements IValidatorFactory {

    private static Logger log = LoggerFactory.getLogger(ValidatorFactoryImpl.class);

    private File discoveryFile;

    private SensorML4DiscoveryValidatorImpl discoveryValidator = null;

    private File svrlFile;

    /**
     * 
     */
    public ValidatorFactoryImpl() {
        log.info("NEW ValidatorFactoryImpl.");

        this.discoveryFile = new File(SirConfigurator.getInstance().getProfile4Discovery());
        this.svrlFile = new File(SirConfigurator.getInstance().getSvrlSchema());

        // create one (!) discovery validator to initialize the XSL file which is stored in a static variable,
        // because this takes time.
        if (this.discoveryValidator == null) {
            try {
                this.discoveryValidator = new SensorML4DiscoveryValidatorImpl(this.discoveryFile, this.svrlFile);
            }
            catch (TransformerConfigurationException e) {
                log.error("Could not initialize ValidatorFactory!", e);
            }
            catch (TransformerFactoryConfigurationError e) {
                log.error("Could not initialize ValidatorFactory!", e);
            }
        }
    }

    @Override
    public IProfileValidator getSensorMLProfile4DiscoveryValidator() throws OwsExceptionReport {
        try {
            return new SensorML4DiscoveryValidatorImpl(this.discoveryFile, this.svrlFile);
        }
        catch (TransformerConfigurationException e) {
            log.error("Could not initialize SensorML Discovery Profile Validator!", e);
            throw new OwsExceptionReport("Could not create validator!", e);
        }
        catch (TransformerFactoryConfigurationError e) {
            log.error("Could not initialize SensorML Discovery Profile Validator!", e);
            throw new OwsExceptionReport("Could not create validator!", e);
        }
    }

}
