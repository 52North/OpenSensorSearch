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

import java.io.File;
import java.io.IOException;

import net.opengis.sensorML.x101.AbstractProcessType;
import net.opengis.sos.x10.RegisterSensorDocument.RegisterSensor.SensorDescription;

import org.apache.http.HttpException;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Test;
import org.n52.sir.client.Client;
import org.n52.sir.datastructure.SirSensor;
import org.n52.sir.json.SearchResultElement;
import org.n52.sir.ows.OwsExceptionReport;
import org.x52North.sir.x032.HarvestServiceRequestDocument;
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

	@Test
	public void harvestService() throws IOException, OwsExceptionReport,
			HttpException, XmlException {
		File f = new File(ClassLoader.getSystemResource(
				"Requests/HarvestService_WeatherSOS.xml").getFile());

		HarvestServiceRequestDocument doc = HarvestServiceRequestDocument.Factory
				.parse(f);
		XmlObject resp = Client.xSendPostRequest(doc);
		HarvestServiceResponseDocument respDoc = HarvestServiceResponseDocument.Factory.parse(resp.getDomNode());
		InsertedSensor [] sensors = respDoc.getHarvestServiceResponse().getInsertedSensorArray();
		for(int i=0;i<sensors.length;i++){
			String id = sensors[i].getSensorIDInSIR();
			SearchSensorRequestDocument requestDoc = SearchSensorRequestDocument.Factory.newInstance();
			requestDoc.addNewSearchSensorRequest().addNewSensorIdentification().setSensorIDInSIR(id);
			
			XmlObject searchResponse = Client.xSendPostRequest(requestDoc);
			SearchSensorResponseDocument respDocument = SearchSensorResponseDocument.Factory.parse(searchResponse.getDomNode());
			org.x52North.sir.x032.SearchSensorResponseDocument.SearchSensorResponse.SearchResultElement[] resultElements = respDocument.getSearchSensorResponse().getSearchResultElementArray();
			for(int j=0;j<resultElements.length;j++){
				SirSensor sensor = new SirSensor();
				AbstractProcessType description = resultElements[j].getSensorDescription();
				sensor.setDescription(description);
				sensor.setSensorIDInSIR(id);
			}
		}
	}
}
