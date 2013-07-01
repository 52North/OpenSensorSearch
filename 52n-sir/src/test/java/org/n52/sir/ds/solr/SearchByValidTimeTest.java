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

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.Collection;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.xmlbeans.XmlException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.datastructure.SirSensor;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.sml.SensorMLDecoder;

public class SearchByValidTimeTest {

	@Before
	public void insertTestSensor() throws XmlException, IOException,
			OwsExceptionReport {
		File sensor_file = new File(ClassLoader.getSystemResource(
				"Requests/testsensor.xml").getFile());
		SensorMLDocument doc = SensorMLDocument.Factory.parse(sensor_file);
		SirSensor sensor = SensorMLDecoder.decode(doc);
		SOLRInsertSensorInfoDAO dao = new SOLRInsertSensorInfoDAO();
		dao.insertSensor(sensor);
	}

	@Test
	public void searchByValidTime() {
		SOLRSearchSensorDAO dao = new SOLRSearchSensorDAO();
		Collection<SirSearchResultElement> results = dao.searchByValidTimeRange(new Date(2009, 12, 31), new java.util.Date(
				2010, 1, 30));
		
	}

	/**
	 * TODO LET the delete delete only by the given id not all
	 * 
	 */
	/*
	 * @After public void deleteSensor() throws SolrServerException,
	 * IOException{ new SolrConnection().deleteByQuery("");
	 * 
	 * }
	 */
}
