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

package org.n52.sir.ds.solr;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.commons.collections.map.HashedMap;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.xmlbeans.XmlException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.datastructure.SirSensor;
import org.n52.sir.datastructure.detailed.SirDetailedSensorDescription;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.sml.SensorMLDecoder;

public class SearchByQueryTest {

	public String id;

	@Before
	public void insertSensor() throws XmlException, IOException,
			OwsExceptionReport {
		File sensor_file = new File(ClassLoader.getSystemResource(
				"Requests/testsensor.xml").getFile());
		SensorMLDocument doc = SensorMLDocument.Factory.parse(sensor_file);
		SirSensor sensor = SensorMLDecoder.decode(doc);
		SOLRInsertSensorInfoDAO dao = new SOLRInsertSensorInfoDAO();
		dao.insertSensor(sensor);
	}

	@Test
	public void keyWordTemporalSearch() {
		SOLRSearchSensorDAO searchDAO = new SOLRSearchSensorDAO();
		// Search by keywords and By StartDate
		Map<String, String> map = new HashMap<String, String>();
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

	@After
	public void deleteSensor() throws SolrServerException, IOException {
		new SolrConnection().deleteByQuery("");

	}
}
