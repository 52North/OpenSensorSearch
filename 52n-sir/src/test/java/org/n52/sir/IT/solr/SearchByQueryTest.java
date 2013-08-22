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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.xmlbeans.XmlException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.datastructure.SirSensor;
import org.n52.sir.datastructure.detailed.SirDetailedSensorDescription;
import org.n52.sir.ds.solr.SOLRInsertSensorInfoDAO;
import org.n52.sir.ds.solr.SOLRSearchSensorDAO;
import org.n52.sir.ds.solr.SolrConnection;
import org.n52.sir.ds.solr.SolrConstants;
import org.n52.sir.ds.solr.SolrUtils;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.sml.SensorMLDecoder;

public class SearchByQueryTest {

	public String id;
	public static final double R = 6372.8; // In kilometers

	@Before
	public void insertSensor() throws XmlException, IOException,
			OwsExceptionReport {
		String basePath = (this.getClass().getResource("/Requests").getFile());
		File sensor_file = new File(basePath+"/testSensor.xml");
		SensorMLDocument doc = SensorMLDocument.Factory.parse(sensor_file);
		SirSensor sensor = SensorMLDecoder.decode(doc);
		SOLRInsertSensorInfoDAO dao = new SOLRInsertSensorInfoDAO();
		dao.insertSensor(sensor);
	}

	@Test
	public void keywordTemporalSearch() {
		SOLRSearchSensorDAO searchDAO = new SOLRSearchSensorDAO();
		// Search by keywords and By StartDate
		Map<String, String> map = new HashMap<>();
		Calendar c = Calendar.getInstance();
		c.set(2009, 11, 31);
		Date start = c.getTime();

		c = Calendar.getInstance();
		c.set(2012, 0, 30);
		Date end = c.getTime();

		map.put("keyword", "TEST");
		map.put("dtstart", SolrUtils.getISO8601UTCString(start));
		map.put("dtend", SolrUtils.getISO8601UTCString(end));

		Collection<SirSearchResultElement> results = searchDAO.searchByQuery(
				map, SolrConstants.OR_OP);
		assertTrue(results.size() > 0);
		assertNotNull(results);
		Iterator<SirSearchResultElement> iterator = results.iterator();
		while (iterator.hasNext()) {
			SirSearchResultElement result = iterator.next();
			SirDetailedSensorDescription desc = (SirDetailedSensorDescription) result
					.getSensorDescription();

			assertTrue(desc.getKeywords().contains("TEST")
					|| (((desc.getBegineDate().getTime() >= start.getTime()) && (desc
							.getBegineDate().getTime() <= end.getTime())) && ((desc
							.getEndDate().getTime() >= start.getTime()) && (desc
							.getEndDate().getTime() <= end.getTime()))));
		}

	}

	@Test
	public void keywordSpatialSearch() {
		SOLRSearchSensorDAO searchDAO = new SOLRSearchSensorDAO();
		// Search by keywords and By StartDate
		Map<String, String> map = new HashMap<>();
		map.put("keyword", "test");
		map.put("lat", "1.5");
		map.put("lng", "3.49");
		map.put("radius", "2");

		Collection<SirSearchResultElement> results = searchDAO.searchByQuery(
				map, SolrConstants.OR_OP);
		assertTrue(results.size() > 0);
		assertNotNull(results);
		Iterator<SirSearchResultElement> iterator = results.iterator();
		while (iterator.hasNext()) {
			SirSearchResultElement result = iterator.next();
			SirDetailedSensorDescription desc = (SirDetailedSensorDescription) result
					.getSensorDescription();
			String loc = desc.getLocation();
			String[] latlng = loc.split(",");
			String lat = latlng[0];
			String lng = latlng[1];
			double dist = (haversine(1.5, 3.49, Double.parseDouble(lat),
					Double.parseDouble(lng)));
			assertTrue(desc.getKeywords().contains("test") && dist < 2);
		}

	}

	@Test
	public void temporalSpatialSearch() {
		SOLRSearchSensorDAO searchDAO = new SOLRSearchSensorDAO();
		// Search by keywords and By StartDate
		Map<String, String> map = new HashMap<>();
		Calendar c = Calendar.getInstance();
		c.set(2009, 11, 31);
		Date start = c.getTime();

		c = Calendar.getInstance();
		c.set(2012, 0, 30);
		Date end = c.getTime();

		map.put("lat", "1.5");
		map.put("lng", "3.49");
		map.put("radius", "2");
		map.put("dtstart", SolrUtils.getISO8601UTCString(start));
		map.put("dtend", SolrUtils.getISO8601UTCString(end));
		Collection<SirSearchResultElement> results = searchDAO.searchByQuery(
				map, SolrConstants.OR_OP);
		assertTrue(results.size() > 0);
		assertNotNull(results);
		Iterator<SirSearchResultElement> iterator = results.iterator();
		while (iterator.hasNext()) {
			SirSearchResultElement result = iterator.next();
			SirDetailedSensorDescription desc = (SirDetailedSensorDescription) result
					.getSensorDescription();
			String loc = desc.getLocation();
			String[] latlng = loc.split(",");
			String lat = latlng[0];
			String lng = latlng[1];
			double dist = (haversine(1.5, 3.49, Double.parseDouble(lat),
					Double.parseDouble(lng)));
			
			assertTrue((((desc.getBegineDate().getTime() >= start.getTime()) && (desc
					.getBegineDate().getTime() <= end.getTime())) && ((desc
					.getEndDate().getTime() >= start.getTime()) && (desc
					.getEndDate().getTime() <= end.getTime()))));
			assertTrue(dist<2);
		}

	}

	@Test
	public void keywordTemporalSpatialSearch() {
		SOLRSearchSensorDAO searchDAO = new SOLRSearchSensorDAO();
		// Search by keywords and By StartDate
		Map<String, String> map = new HashMap<>();
		Calendar c = Calendar.getInstance();
		c.set(2009, 11, 31);
		Date start = c.getTime();

		c = Calendar.getInstance();
		c.set(2012, 0, 30);
		Date end = c.getTime();

		map.put("keyword", "TEST");
		map.put("lat", "1.5");
		map.put("lng", "3.49");
		map.put("radius", "2");
		map.put("dtstart", SolrUtils.getISO8601UTCString(start));
		map.put("dtend", SolrUtils.getISO8601UTCString(end));
		Collection<SirSearchResultElement> results = searchDAO.searchByQuery(
				map, SolrConstants.OR_OP);
		assertTrue(results.size() > 0);
		assertNotNull(results);
		Iterator<SirSearchResultElement> iterator = results.iterator();
		while (iterator.hasNext()) {
			SirSearchResultElement result = iterator.next();
			SirDetailedSensorDescription desc = (SirDetailedSensorDescription) result
					.getSensorDescription();
			String loc = desc.getLocation();
			String[] latlng = loc.split(",");
			String lat = latlng[0];
			String lng = latlng[1];
			double dist = (haversine(1.5, 3.49, Double.parseDouble(lat),
					Double.parseDouble(lng)));
			
			assertTrue((((desc.getBegineDate().getTime() >= start.getTime()) && (desc
					.getBegineDate().getTime() <= end.getTime())) && ((desc
					.getEndDate().getTime() >= start.getTime()) && (desc
					.getEndDate().getTime() <= end.getTime()))));
			assertTrue(dist<2);
		}

	}

	public double haversine(double lat1, double lon1, double lat2, double lon2) {
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);
		lat1 = Math.toRadians(lat1);
		lat2 = Math.toRadians(lat2);

		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2)
				* Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
		double c = 2 * Math.asin(Math.sqrt(a));
		return R * c;
	}

	@After
	public void deleteSensor() throws SolrServerException, IOException {
		new SolrConnection().deleteByQuery("");

	}

}
