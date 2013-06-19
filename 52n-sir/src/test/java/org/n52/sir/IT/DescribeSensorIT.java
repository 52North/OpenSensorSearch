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

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;

import net.opengis.ows.ExceptionDocument;
import net.opengis.ows.ExceptionReportDocument;
import net.opengis.ows.ExceptionReportDocument.ExceptionReport;
import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Before;
import org.junit.Test;
import org.n52.sir.SirConfigurator;
import org.n52.sir.client.Client;
import org.n52.sir.client.DescribeSensorBean;
import org.n52.sir.ows.OWSConstants;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.util.XmlTools;
import org.x52North.sir.x032.DescribeSensorRequestDocument;

public class DescribeSensorIT {

	private String sensorIDinSIR = "1";

	
	public void failIfURLNull(String resource) {
		if (ClassLoader.getSystemResource(resource) == null)
			fail(resource + " Is missing");
	}

	@Before
	public void setup() {
		failIfURLNull("Requests/DescribeSensor.xml");
		failIfURLNull("prop/db.PROPERTIES");
		failIfURLNull("prop/sir.PROPERTIES");

		if (SirConfigurator.getInstance() == null) {
			InputStream dbStream = ClassLoader
					.getSystemResourceAsStream("prop/db.PROPERTIES");
			InputStream sirStream = ClassLoader
					.getSystemResourceAsStream("prop/sir.PROPERTIES");
			try {
				// Read configurator if null
				SirConfigurator.getInstance(sirStream, dbStream, null, null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				fail(e.toString());
			}
		}

	}

	@Test
	public void describeSensor() throws Exception {

		DescribeSensorRequestDocument doc = DescribeSensorRequestDocument.Factory.newInstance();
		doc.addNewDescribeSensorRequest().setSensorIDInSIR("1");
		XmlObject response=null;
		try {
			
			response = Client.xSendPostRequest(doc);
			System.out.println(response);
			// parse and validate response
			SensorMLDocument sml = SensorMLDocument.Factory.parse(response.getDomNode());
			boolean isValid = sml.validate();
			System.out.println(isValid);
		} catch (Exception e) {
			if(response == null)
				fail("Null response");
			ExceptionReportDocument r = ExceptionReportDocument.Factory.parse(response.getDomNode());
			if(r.validate())
				fail("No sensor returned - Check db");
			else fail(e.toString());
		}
	}
}
