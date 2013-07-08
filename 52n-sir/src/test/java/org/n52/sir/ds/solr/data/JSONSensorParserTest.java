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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Iterator;

import org.apache.solr.client.solrj.SolrServerException;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.n52.sir.datastructure.SirSensor;
import org.n52.sir.datastructure.SirTimePeriod;
import org.n52.sir.ds.solr.SOLRInsertSensorInfoDAO;
import org.n52.sir.ds.solr.SolrConnection;
import org.n52.sir.ds.solr.data.ds.JSONSensor;
import org.n52.sir.ds.solr.data.ds.JSONSensorsCollection;
import org.n52.sir.ows.OwsExceptionReport;

import com.google.gson.Gson;

public class JSONSensorParserTest {
	
	@Test
	public void parseJsonSensorsAndInsert() throws IOException, OwsExceptionReport {
	/*	File sensor_file = new File(ClassLoader.getSystemResource(
				"data/randomSensors.json").getFile());
		Gson gson = new Gson();
		StringBuilder builder = new StringBuilder();
		String s;
		BufferedReader reader = new BufferedReader(
				(new FileReader(sensor_file)));
		while ((s = reader.readLine()) != null)
			builder.append(s);
		JSONSensorsCollection collection = gson
				.fromJson(builder.toString(), JSONSensorsCollection.class);
		Iterator<JSONSensor> sensors = collection.sensors.iterator();
		while(sensors.hasNext()){
			SirSensor sensor = new SirSensor();
			JSONSensor jsensor = sensors.next();
			sensor.setKeywords(jsensor.keywords);
			SirTimePeriod period = new SirTimePeriod();
			DateTime begin = DateTime.parse(jsensor.beginPosition);
			DateTime end = DateTime.parse(jsensor.endPosition);
			//fix because we need start < end , and the data is randomly generated
			if(begin.getMillis() > end.getMillis()){
				DateTime temp = begin;
				end = begin;
				begin=temp;
			}
			period.setStartTime(begin.toDate());
			period.setEndTime(end.toDate());
			SOLRInsertSensorInfoDAO dao = new SOLRInsertSensorInfoDAO();
			dao.insertSensor(sensor);
		}
		*/
	}
	
}
