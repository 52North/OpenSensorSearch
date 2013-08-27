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

package org.n52.sir.ds.solr;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.n52.sir.api.IdentifierGenerator;
import org.n52.sir.datastructure.SirSensor;
import org.n52.sir.datastructure.SirSensorIdentification;
import org.n52.sir.datastructure.SirServiceReference;
import org.n52.sir.datastructure.SirTimePeriod;
import org.n52.sir.ds.IInsertSensorInfoDAO;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.util.ShortAlphanumericIdentifierGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class SOLRInsertSensorInfoDAO implements IInsertSensorInfoDAO {
	private IdentifierGenerator idenGen = new ShortAlphanumericIdentifierGenerator();
	private static Logger log = LoggerFactory
			.getLogger(SOLRInsertSensorInfoDAO.class);

	@Override
	public String addNewReference(SirSensorIdentification sensIdent,
			SirServiceReference servRef) throws OwsExceptionReport {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String deleteReference(SirSensorIdentification sensIdent,
			SirServiceReference servRef) throws OwsExceptionReport {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String deleteSensor(SirSensorIdentification sensIdent)
			throws OwsExceptionReport {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String insertSensor(SirSensor sensor) throws OwsExceptionReport {
		// TODO Auto-generated method stub
		// Get the keywords first

		// TODO moh-yakoub: implement a mechanism to get access to the various
		// identifiers
		// Map<String, String> futherIds = sensor.getIdentifiers();

		Collection<String> keywords = sensor.getKeywords();
		// Get the connection of solr Server
		SolrConnection connection = new SolrConnection();
		SolrInputDocument inputDocument = new SolrInputDocument();

		for (String s : keywords) {
			inputDocument.addField(SolrConstants.KEYWORD, s);
		}
		// FIXME use OSS-wide id generator
		String id = this.idenGen.generate();
		inputDocument.addField(SolrConstants.ID, id);
		String longitude = sensor.getLongitude();
		String latitude = sensor.getLatitude();
		if (sensor.getbBox() != null) {
			double[] center = sensor.getbBox().getCenter();
			if (center != null) {
				String center_cords = center[0] + "," + center[1];
				inputDocument.addField(SolrConstants.BBOX_CENTER, center_cords);
			}
		}

		SirTimePeriod timePeriod = sensor.getTimePeriod();
		if (timePeriod != null) {
			Date startDate = timePeriod.getStartTime();
			Date endDate = timePeriod.getEndTime();
			if (startDate != null)
				inputDocument.addField(SolrConstants.START_DATE,
						SolrUtils.getISO8601UTCString(startDate));
			if (endDate != null)
				inputDocument.addField(SolrConstants.END_DATE,
						SolrUtils.getISO8601UTCString(endDate));
		}
		if (longitude != null && latitude != null)
			if (longitude.length() > 0 && latitude.length() > 0)
				inputDocument.addField(SolrConstants.LOCATION, latitude + ","
						+ longitude);
		if (sensor.getDescription() != null)
			inputDocument.addField(SolrConstants.DESCRIPTION,
					sensor.getDescription());

		if (sensor.getClassificationList() != null) {
			Collection<String> classifiers = sensor.getClassificationList();
			for (String classifier : classifiers)
				inputDocument.addField(SolrConstants.CLASSIFIER, classifier);
		}
		if(sensor.getIdentificationsList() != null && sensor.getIdentificationsList().size() !=0){
			Collection<Object> ids = sensor.getIdentificationsList();
			for(Object iden : ids)
				inputDocument.addField(SolrConstants.IDENTIFICATION, iden);
		}
		if(sensor.getContacts()!=null && sensor.getContacts().size()>0)
		{
			Collection<String> contacts = sensor.getContacts();
			for(String contact : contacts)
				inputDocument.addField(SolrConstants.CONTACTS,contact);
		}
		if(sensor.getInterfaces()!=null && sensor.getInterfaces().size() >0){
			Collection<String> interfaces = sensor.getInterfaces();
			for(String interfacestr : interfaces)
				inputDocument.addField(SolrConstants.INTERFACE, interfacestr);
		}
		if(sensor.getInputs()!=null && sensor.getInputs().size() >0){
			Collection<String> inputs = sensor.getInputs();
			for(String inputstr : inputs)
				inputDocument.addField(SolrConstants.INPUT, inputstr);
		}
		if(sensor.getOutputs()!=null && sensor.getOutputs().size() >0){
			Collection<String> outputs = sensor.getOutputs();
			for(String outputstr : outputs)
				inputDocument.addField(SolrConstants.OUTPUT, outputstr);
		}
		try {
			connection.addInputDocument(inputDocument);
			connection.commitChanges();
		} catch (SolrServerException e) {
			log.error("Could not create connection to Solr.", e);
		} catch (IOException e) {
			log.error("IOException", e);
		}

		// TODO add the database ID
		return id;
	}

	@Override
	public String updateSensor(SirSensorIdentification sensIdent,
			SirSensor sensor) throws OwsExceptionReport {
		// TODO Auto-generated method stub
		return null;
	}

}
