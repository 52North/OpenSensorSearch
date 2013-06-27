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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.solr.client.solrj.SolrResponse;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.n52.sir.datastructure.SirSearchCriteria;
import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.datastructure.SirServiceReference;
import org.n52.sir.datastructure.SirSimpleSensorDescription;
import org.n52.sir.ds.ISearchSensorDAO;
import org.n52.sir.ows.OwsExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SOLRSearchSensorDAO implements ISearchSensorDAO {

	private static Logger log = LoggerFactory
			.getLogger(SOLRSearchSensorDAO.class);

	@Override
	public Collection<SirSearchResultElement> getAllSensors(
			boolean simpleReponse) throws OwsExceptionReport {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SirSearchResultElement getSensorBySensorID(String sensorIdInSir,
			boolean simpleReponse) throws OwsExceptionReport {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SirSearchResultElement getSensorByServiceDescription(
			SirServiceReference servDesc, boolean simpleReponse)
			throws OwsExceptionReport {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<SirSearchResultElement> searchSensor(
			SirSearchCriteria searchCriteria, boolean simpleReponse)
			throws OwsExceptionReport {
		// TODO Auto-generated method stub
		Collection<String> q = searchCriteria.getSearchText();
		StringBuilder wordslist = new StringBuilder();
		Iterator<String> iter = q.iterator();
		wordslist.append(SolrConstants.KEYWORD+":"+iter.next());
		while (iter.hasNext()){
			wordslist.append("+");
			wordslist.append(iter.next());
		}
		// prepare the request
		SolrConnection connection = new SolrConnection();
		ModifiableSolrParams params = new ModifiableSolrParams();
		params.set("q", wordslist.toString());
		System.out.println("Parameters:"+params.toString());
		try {
			QueryResponse response = connection.SolrQuery(params);
			SolrDocumentList list = response.getResults();

			List<SirSearchResultElement> results = new ArrayList<SirSearchResultElement>();
			for (int i = 0; i < list.size(); i++) {
				// create a new result element
				SirSearchResultElement element = new SirSearchResultElement();
				SirSimpleSensorDescription desc = new SirSimpleSensorDescription();
				desc.setDescriptionText(list.get(i).toString());
				element.setSensorDescription(desc);
				results.add(element);
			}

			return results;

		} catch (SolrServerException solrexception) {
			log.error("SolrException:" + solrexception.getLocalizedMessage());
			solrexception.printStackTrace();
			return null;
		}
	}

}
