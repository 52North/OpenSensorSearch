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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.xmlbeans.XmlException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.n52.sir.datastructure.SirSearchCriteria;
import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.datastructure.SirSensor;
import org.n52.sir.datastructure.solr.SirSolrSensorDescription;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.sml.SensorMLDecoder;

public class SearchByKeywordTest {
	
	public String id;

    @Test
    public void searchKeywords() throws OwsExceptionReport, XmlException, IOException {
    	//Inserts the sensor
    	File sensor_file = new File(ClassLoader.getSystemResource("Requests/testsensor.xml").getFile());
        SensorMLDocument doc = SensorMLDocument.Factory.parse(sensor_file);
        SirSensor sensor = SensorMLDecoder.decode(doc);
        System.out.println(sensor.getText().toArray()[0]);
        // probably this will take some configuration - haven't decided yet.
        SOLRInsertSensorInfoDAO dao = new SOLRInsertSensorInfoDAO();
        String id = dao.insertSensor(sensor);
        this.id=id;
    	
        SOLRSearchSensorDAO searchDAO = new SOLRSearchSensorDAO();
        SirSearchCriteria criteria = new SirSearchCriteria();
        /*
         * Prepare the list of keywords
         */
        List<String> keywords = new ArrayList<String>();
        keywords.add("testkeyword");
        keywords.add("test");
        keywords.add("another keyword");

        ArrayList<String> searchkeywords = new ArrayList<String>();
        searchkeywords.add("test");
        criteria.setSearchText(searchkeywords);

        Collection<SirSearchResultElement> results = searchDAO.searchSensor(criteria, true);

        assertNotNull(results);
        assertEquals(results.size(), 1);

        Iterator<SirSearchResultElement> iter = results.iterator();
        SirSearchResultElement result = iter.next();
        // SensorML is stored in the sensor description value
        SirSolrSensorDescription description = (SirSolrSensorDescription) result.getSensorDescription();
        assertNotNull(description);
        assertEquals(description.getKeywords().size(), keywords.size());
        for (String s : keywords)
            assertTrue(description.getKeywords().contains(s));
        //deletes the sensor
        

    }
    /**TODO LET the delete delete only by the given id not all  
     *
     */
   
    @After
    public void deleteSensor() throws SolrServerException, IOException{
        new SolrConnection().deleteByQuery("");   
    } 
}