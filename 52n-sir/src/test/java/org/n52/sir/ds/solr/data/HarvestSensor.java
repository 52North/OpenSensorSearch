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
