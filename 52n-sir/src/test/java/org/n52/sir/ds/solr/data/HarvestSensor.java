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
package org.n52.sir.ds.solr.data;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import javax.servlet.UnavailableException;

import net.opengis.sensorML.x101.AbstractProcessType;
import net.opengis.sos.x10.RegisterSensorDocument.RegisterSensor.SensorDescription;

import org.apache.http.HttpException;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Before;
import org.junit.Test;
import org.n52.sir.SirConfigurator;
import org.n52.sir.client.Client;
import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.datastructure.SirSensor;
import org.n52.sir.ds.solr.SOLRSearchSensorDAO;
import org.n52.sir.json.SearchResultElement;
import org.n52.sir.ows.OwsExceptionReport;
import org.x52North.sir.x032.HarvestServiceRequestDocument;
import org.x52North.sir.x032.HarvestServiceRequestDocument.HarvestServiceRequest;
import org.x52North.sir.x032.HarvestServiceResponseDocument;
import org.x52North.sir.x032.HarvestServiceResponseDocument.HarvestServiceResponse.InsertedSensor;
import org.x52North.sir.x032.InsertSensorInfoRequestDocument;
import org.x52North.sir.x032.InsertSensorInfoRequestDocument.InsertSensorInfoRequest;
import org.x52North.sir.x032.SimpleSensorDescriptionDocument.SimpleSensorDescription;
import org.x52North.sir.x032.SearchSensorRequestDocument;
import org.x52North.sir.x032.SearchSensorResponseDocument;

public class HarvestSensor {
	private String serviceURL = "http://v-swe.uni-muenster.de:8080/WeatherSOS/sos";

	private String serviceType = "SOS";

	@Before
	public void prepare() throws UnavailableException, OwsExceptionReport {

		if (SirConfigurator.getInstance() == null) {
			InputStream dbStream = ClassLoader
					.getSystemResourceAsStream("prop/db.PROPERTIES");
			InputStream sirStream = ClassLoader
					.getSystemResourceAsStream("prop/sir.PROPERTIES");
			// Read configurator if null
			SirConfigurator.getInstance(sirStream, dbStream, null, null);

		}
	}


	@Test
	public void harvestService() throws IOException, OwsExceptionReport,
			HttpException, XmlException, URISyntaxException {
		File f = new File(ClassLoader.getSystemResource(
				"Requests/HarvestService_WeatherSOS.xml").getFile());
		HarvestServiceRequestDocument doc = HarvestServiceRequestDocument.Factory
				.parse(f);
		XmlObject resp = Client.xSendPostRequest(doc, new URI(serviceURL));
		System.out.println(resp);

		HarvestServiceResponseDocument respDoc = HarvestServiceResponseDocument.Factory
				.parse(resp.getDomNode());
		InsertedSensor[] sensors = respDoc.getHarvestServiceResponse()
				.getInsertedSensorArray();
		for (int i = 0; i < sensors.length; i++) {
			String id = sensors[i].getSensorIDInSIR();
			SOLRSearchSensorDAO dao = new SOLRSearchSensorDAO();
			Collection<SirSearchResultElement> elements = dao.searchByID(id);
			assertTrue(elements.size() > 0);
		}
	}
}
