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
package org.n52.sir.ds.solr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yakoub 
 *
 */
public class SOLRDeleteSensorInfoDAO  {
	//Delete a sensor identified by ID
	 private static Logger log = LoggerFactory.getLogger(SOLRInsertSensorInfoDAO.class);

	public void deleteSensorById(String id){
		try{
		SolrConnection connection = new SolrConnection();
		connection.deleteSensorWithID(id);
		}catch(Exception e){
			log.error("Error on deleting sensor",e);
		}
	}

}
