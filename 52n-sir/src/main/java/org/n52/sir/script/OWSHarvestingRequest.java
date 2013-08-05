package org.n52.sir.script;

import org.n52.sir.client.Client;
import org.n52.sir.client.HarvestServiceBean;
import org.n52.sir.datastructure.SirSensor;
import org.x52North.sir.x032.HarvestServiceResponseDocument;
import org.x52North.sir.x032.HarvestServiceResponseDocument.HarvestServiceResponse;
import org.x52North.sir.x032.HarvestServiceResponseDocument.HarvestServiceResponse.InsertedSensor;

public class OWSHarvestingRequest {
	
	public int harvestOWSService(String url,String serviceType){
		
		HarvestServiceBean harvestBean = new HarvestServiceBean(url, serviceType);
		
		harvestBean.buildRequest();
		try{
		
		String response = Client.sendPostRequest(harvestBean.getRequestString());
		HarvestServiceResponseDocument respDoc = HarvestServiceResponseDocument.Factory.parse(response);
		
		HarvestServiceResponse harvestResponse = respDoc.getHarvestServiceResponse();
		
		return harvestResponse.getNumberOfInsertedSensors();
		
		}catch(Exception e){
			return -1;
		}
		
		
	}

}
