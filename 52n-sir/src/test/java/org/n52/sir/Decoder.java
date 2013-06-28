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
package org.n52.sir;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.xmlbeans.XmlException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.sir.datastructure.SirSensor;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.sml.SensorMLDecoder;

public class Decoder {
    
    private static SensorMLDocument doc = null;
    
    @BeforeClass
    public static void loadDocuments() throws XmlException, IOException {
        File sensor_file = new File(ClassLoader.getSystemResource("Requests/testsensor.xml").getFile());
        doc = SensorMLDocument.Factory.parse(sensor_file);
    }
    
    @Test
    public void keywordsAreDecoded() throws OwsExceptionReport {
        SirSensor sensor = SensorMLDecoder.decode(doc);
        
        Collection<String> keywords = sensor.getKeywords();
        ArrayList<String> actual = new ArrayList<String>();
        actual.addAll(keywords);
        
        assertThat(actual, hasItems("testkeyword", "test", "another keyword"));
    }
    
}
