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
 * author Yakoub
 */
package org.n52.sir.IT;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;


import javax.servlet.UnavailableException;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.xmlbeans.XmlObject;
import org.junit.Before;
import org.junit.Test;
import org.n52.sir.client.Client;
import org.n52.sir.ows.OwsExceptionReport;
import org.x52North.sir.x032.DescribeSensorRequestDocument;

public class DescribeSensorIT {

	private String sensorIDinSIR = "1";

	public void failIfURLNull(String resource) {
		assertNotNull (resource + " Is missing",ClassLoader.getSystemResource(resource));
			
	}

	@Before
	public void setup() throws UnavailableException, OwsExceptionReport {
		failIfURLNull("Requests/DescribeSensor.xml");
		failIfURLNull("prop/db.PROPERTIES");
		failIfURLNull("prop/sir.PROPERTIES");

	}

	@Test
	public void describeSensor() throws Exception {

		DescribeSensorRequestDocument doc = DescribeSensorRequestDocument.Factory
				.newInstance();
		doc.addNewDescribeSensorRequest().setSensorIDInSIR(sensorIDinSIR);
		XmlObject response = null;

		response = Client.xSendPostRequest(doc);
		// parse and validate response
		SensorMLDocument sml = SensorMLDocument.Factory.parse(response
				.getDomNode());
		boolean isValid = sml.validate();
		assertTrue("Not a valid sensorML returned",isValid);
	}
}
