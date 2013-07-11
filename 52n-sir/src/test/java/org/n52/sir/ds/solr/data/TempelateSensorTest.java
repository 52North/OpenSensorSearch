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

package org.n52.sir.ds.solr.data;

/**
 * @author Yakoub
 */
import static org.junit.Assert.assertNotEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.UnavailableException;

import net.opengis.sensorML.x101.KeywordsDocument.Keywords.KeywordList;
import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.http.HttpException;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.n52.sir.SirConfigurator;
import org.n52.sir.client.Client;
import org.n52.sir.datastructure.SirSensor;
import org.n52.sir.datastructure.SirTimePeriod;
import org.n52.sir.ds.solr.SOLRInsertSensorInfoDAO;
import org.n52.sir.ds.solr.data.ds.JSONSensor;
import org.n52.sir.ds.solr.data.ds.JSONSensorsCollection;
import org.n52.sir.ows.OwsExceptionReport;
import org.x52North.sir.x032.InsertSensorInfoRequestDocument;
import org.x52North.sir.x032.InsertSensorInfoResponseDocument;

import com.google.gson.Gson;

public class TempelateSensorTest {

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

	public void parseJsonSensorsAndInsert() throws IOException,
			OwsExceptionReport, XmlException, HttpException {

		File sensor_file = new File(ClassLoader.getSystemResource(
				"data/randomSensors.json").getFile());
		File sensor_temp = new File(ClassLoader.getSystemResource(
				"AirBase-test.xml").getFile());
		SensorMLDocument DOC = SensorMLDocument.Factory.parse(sensor_temp);
		Gson gson = new Gson();
		StringBuilder builder = new StringBuilder();
		String s;
		BufferedReader reader = new BufferedReader(
				(new FileReader(sensor_file)));
		while ((s = reader.readLine()) != null)
			builder.append(s);
		JSONSensorsCollection collection = gson.fromJson(builder.toString(),
				JSONSensorsCollection.class);
		Iterator<JSONSensor> sensors = collection.sensors.iterator();
		while (sensors.hasNext()) {
			SirSensor sensor = new SirSensor();
			JSONSensor jsensor = sensors.next();
			sensor.setKeywords(jsensor.keywords);
			SirTimePeriod period = new SirTimePeriod();
			DateTime begin = DateTime.parse(jsensor.beginPosition);
			DateTime end = DateTime.parse(jsensor.endPosition);
			// fix because we need start < end , and the data is randomly
			// generated
			if (begin.getMillis() > end.getMillis()) {
				DateTime temp = begin;
				end = begin;
				begin = temp;
			}
			period.setStartTime(begin.toDate());
			period.setEndTime(end.toDate());
			sensor.setTimePeriod(period);
			sensor.setIdentificationsList(jsensor.Identifiers);
			List<String> contacts = new ArrayList<String>();
			contacts.add(jsensor.contacts);
			sensor.setContacts(contacts);
			
			/*
			 * Add to SIRPQSQL
			 */
			KeywordList klist = KeywordList.Factory.newInstance();
			klist.setKeywordArray(jsensor.keywords.toArray(new String[]{}));
			DOC.getSensorML().getMemberArray(0).getProcess().getKeywordsArray(0).setKeywordList(klist);
			InsertSensorInfoRequestDocument req = InsertSensorInfoRequestDocument.Factory
					.newInstance();
			req.addNewInsertSensorInfoRequest()
					.addNewInfoToBeInserted()
					.setSensorDescription(
							DOC.getSensorML().getMemberArray(0).getProcess());
			XmlObject res = Client.xSendPostRequest(req);
			InsertSensorInfoResponseDocument resp = InsertSensorInfoResponseDocument.Factory
					.parse(res.getDomNode());
			assertNotEquals("Failed to insert sensor",
					resp.getInsertSensorInfoResponse()
							.getNumberOfInsertedSensors(), 0);
			/*
			 * Insert into apache solr
			 */
			// SOLRInsertSensorInfoDAO dao = new SOLRInsertSensorInfoDAO();
			// dao.insertSensor(sensor);
		}

	}

}
