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
package org.n52.sir.script;

import java.io.InputStream;

import javax.servlet.UnavailableException;

import org.n52.sir.SirConfigurator;
import org.n52.sir.client.Client;
import org.n52.sir.client.HarvestServiceBean;
import org.n52.sir.ows.OwsExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sir.x032.HarvestServiceResponseDocument;
import org.x52North.sir.x032.HarvestServiceResponseDocument.HarvestServiceResponse;

public class OWSHarvestingRequest {
	private static Logger log = LoggerFactory.getLogger(HarvestResource.class);

	public OWSHarvestingRequest(){
		//TODO remove all of those

		if (SirConfigurator.getInstance() == null) {
			InputStream dbStream = ClassLoader
					.getSystemResourceAsStream("prop/db.PROPERTIES");
			InputStream sirStream = ClassLoader
					.getSystemResourceAsStream("prop/sir.PROPERTIES");
			// Read configurator if null
			try {
				SirConfigurator.getInstance(sirStream, dbStream, null, null);
			} catch (UnavailableException e) {
				log.error("Error on intialization",e);
			} catch (OwsExceptionReport e) {
				log.error("Error on intialization",e);
			}

		}

	}
	
	
	public int harvestOWSService(String url,String serviceType){
		
		HarvestServiceBean harvestBean = new HarvestServiceBean(url, serviceType);
		log.info("Harvesting server at:"+url+" : "+serviceType);
		
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
