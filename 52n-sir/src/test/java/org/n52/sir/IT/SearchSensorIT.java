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
import static org.junit.Assert.*;
import java.io.File;
import java.io.InputStream;


import org.apache.xmlbeans.XmlObject;
import org.junit.Before;
import org.junit.Test;
import org.n52.sir.SirConfigurator;
import org.n52.sir.client.Client;
import org.x52North.sir.x032.SearchSensorRequestDocument;
import org.x52North.sir.x032.SearchSensorResponseDocument;

public class SearchSensorIT {


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

		if (SirConfigurator.getInstance() == null) {
			InputStream dbStream = ClassLoader
					.getSystemResourceAsStream("prop/db.PROPERTIES");
			InputStream sirStream = ClassLoader
					.getSystemResourceAsStream("prop/sir.PROPERTIES");

			// Read configurator if null
			SirConfigurator.getInstance(sirStream, dbStream, null, null);

		}

	}

	public void searchSensor(String file, String description) throws Exception {
		System.out.println(description);
		File f = new File((ClassLoader.getSystemResource(file).getFile()));
		SearchSensorRequestDocument doc = SearchSensorRequestDocument.Factory
				.parse(f);

		XmlObject response = null;

		response = Client.xSendPostRequest(doc);
		// parse and validate response
		SearchSensorResponseDocument resp_doc = SearchSensorResponseDocument.Factory
				.parse(response.getDomNode());
		// validate the respo_doc

		assertTrue("Invalid  Sensor Response", resp_doc.validate());

		int send = doc.getSearchSensorRequest().getSensorIdentificationArray().length;
		int response_count = resp_doc.getSearchSensorResponse()
				.sizeOfSearchResultElementArray();
		assertEquals(send, response_count);

	}

	@Test
	public void searchSensorbyIDInSIR() throws Exception {
		searchSensor("Requests/SearchSensor_bySensorIDInSIR.xml",
				"Search Sensor by ID in SIR");

	}

	@Test
	public void searchSensorByDescription() throws Exception {

		searchSensor("Requests/SearchSensor_byServiceDescription.xml",
				"Search Sensor by service Description");

	}

}
