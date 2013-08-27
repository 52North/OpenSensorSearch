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
package org.n52.sir.IT.solr;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import org.apache.http.HttpException;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.sir.IT.GuiceUtil;
import org.n52.sir.client.Client;
import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.ds.solr.SOLRSearchSensorDAO;
import org.n52.sir.ows.OwsExceptionReport;
import org.x52North.sir.x032.HarvestServiceRequestDocument;
import org.x52North.sir.x032.HarvestServiceResponseDocument;
import org.x52North.sir.x032.HarvestServiceResponseDocument.HarvestServiceResponse.InsertedSensor;

public class Harvesting {
	
    private static Client client;

    private String serviceURL = "http://sensorweb.demo.52north.org/EO2HeavenSOS/sos";

	private String serviceType = "SOS";

	@BeforeClass
    public static void setUp() {
        client = GuiceUtil.configureSirClient();
    }

	@Test
	public void harvestService() throws IOException, OwsExceptionReport,
			HttpException, XmlException, URISyntaxException {
		File f = new File(ClassLoader.getSystemResource(
				"Requests/HarvestService_WeatherSOS.xml").getFile());
		HarvestServiceRequestDocument doc = HarvestServiceRequestDocument.Factory
				.parse(f);
		XmlObject resp = client.xSendPostRequest(doc, new URI(this.serviceURL));
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
