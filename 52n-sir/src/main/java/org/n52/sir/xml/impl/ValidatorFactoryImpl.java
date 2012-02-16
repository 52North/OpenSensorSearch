/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */
package org.n52.sir.xml.impl;

import java.io.File;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.n52.sir.SirConfigurator;
import org.n52.sir.ows.OwsExceptionReport;
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

    private File svrlFile;

    private SensorML4DiscoveryValidatorImpl discoveryValidator = null;

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
            catch (ParserConfigurationException e) {
                log.error("Could not initialize ValidatorFactory!", e);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.xml.IValidatorFactory#getSensorMLProfile4DiscoveryValidator()
     */
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
        catch (ParserConfigurationException e) {
            log.error("Could not initialize SensorML Discovery Profile Validator!", e);
            throw new OwsExceptionReport("Could not create validator!", e);
        }
    }

}
