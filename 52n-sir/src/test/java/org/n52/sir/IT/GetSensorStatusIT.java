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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.io.File;

import org.apache.xmlbeans.XmlObject;
import org.junit.Before;
import org.junit.Test;
import org.n52.sir.OpenSearchSIR;
import org.n52.sir.client.Client;
import org.n52.sir.opensearch.JsonListener;
import org.n52.sir.opensearch.OpenSearchConfigurator;
import org.x52North.sir.x032.GetSensorStatusRequestDocument;
import org.x52North.sir.x032.GetSensorStatusResponseDocument;

public class GetSensorStatusIT {

	public void failIfURLNull(String resource) {
		assertNotNull(resource + " Is missing",
				(ClassLoader.getSystemResource(resource)));
	}

	@Before
	public void setup() throws Exception {
		failIfURLNull("Requests/SearchSensor_bySensorIDInSIR.xml");
		failIfURLNull("Requests/SearchSensor_byServiceDescription.xml");

		failIfURLNull("prop/db.PROPERTIES");
		failIfURLNull("prop/sir.PROPERTIES");

	}

	public void getSensorStatus(String file, String description)
			throws Exception {
		File f = new File((ClassLoader.getSystemResource(file).getFile()));
		GetSensorStatusRequestDocument doc = GetSensorStatusRequestDocument.Factory
				.parse(f);
		XmlObject response = null;

		// try {

		response = Client.xSendPostRequest(doc);
		// parse and validate response
		GetSensorStatusResponseDocument resp_doc = GetSensorStatusResponseDocument.Factory
				.parse(response.getDomNode());
		// validate the respo_doc

		assertTrue("Invalid  Sensor status", resp_doc.validate());

		/*
		 * } catch (Exception e) {
		 * 
		 * if (response == null) fail("Null response"); ExceptionReportDocument
		 * r = ExceptionReportDocument.Factory .parse(response.getDomNode()); if
		 * (r.validate())
		 * fail("Couldnot retrieve sensor data,check the db contain valid sensors"
		 * ); else fail(e.toString()); }
		 */

	}

	@Test
	public void getSensorStatusSearchCriteria() throws Exception {
		getSensorStatus("Requests/GetSensorStatus_bySearchCriteria.xml",
				"Getting sensor status by Search criteria");

	}

	@Test
	public void getSensorStatusSearchID() throws Exception {
		getSensorStatus("Requests/GetSensorStatus_bySensorIDInSIR",
				"Getting sensor status by Sensor ID in SIR");

	}

	@Test
	public void getSensorStatusSearchServiceDescription() throws Exception {
		getSensorStatus("Requests/GetSensorStatus_byServiceDescription.xml",
				"Getting sensor status by sensor service description");

	}

}
