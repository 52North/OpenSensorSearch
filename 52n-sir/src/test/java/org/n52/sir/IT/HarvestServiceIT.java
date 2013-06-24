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
package org.n52.sir.IT;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;

import org.apache.xmlbeans.XmlObject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.sir.Util;
import org.n52.sir.client.Client;
import org.n52.sir.client.HarvestServiceBean;
import org.x52North.sir.x032.HarvestServiceRequestDocument;
import org.x52North.sir.x032.HarvestServiceResponseDocument;

/**
 * 
 * @author Yakoub
 */
public class HarvestServiceIT {

	private String serviceURL = "http://v-swe.uni-muenster.de:8080/WeatherSOS/sos";

	private String serviceType = "SOS";
    
	private static Client c = null;
    
    @BeforeClass
    public static void setUpClient() throws MalformedURLException {
        c  = new Client(Util.getSIREndpointForIT());
    }
    
	@Test
	public void harvestWeatherServiceBean() throws Exception {
		// buildRequest
		HarvestServiceBean hsb = new HarvestServiceBean(this.serviceURL,
				this.serviceType);

		hsb.buildRequest();

		// send request
		String response = c.sendPostRequest(hsb.getRequestString());

		// parse and validate response
		HarvestServiceResponseDocument cd = HarvestServiceResponseDocument.Factory
				.parse(response);
			assertTrue(cd.validate());
	}

	@Test
	public void harvestWeatherServiceDoc() throws Exception {
		File f = new File(ClassLoader.getSystemResource(
				"Requests/HarvestService_WeatherSOS.xml").getFile());
		HarvestServiceRequestDocument hsrd = HarvestServiceRequestDocument.Factory
				.parse(f);

		XmlObject response = c.xSendPostRequest(hsrd);

		// parse and validate response
		HarvestServiceResponseDocument cd = HarvestServiceResponseDocument.Factory
				.parse(response.getDomNode());
		assertTrue(cd.validate());
	}

}