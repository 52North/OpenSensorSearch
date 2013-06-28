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
