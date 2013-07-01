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
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.joda.time.DateTime;
import org.n52.sir.datastructure.SirBoundingBox;
import org.n52.sir.datastructure.SirSearchCriteria;
import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.datastructure.SirServiceReference;
import org.n52.sir.datastructure.solr.SirSolrSensorDescription;
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
		wordslist.append(SolrConstants.KEYWORD + ":" + iter.next());
		while (iter.hasNext()) {
			wordslist.append("+");
			wordslist.append(iter.next());
		}
		// prepare the request
		SolrConnection connection = new SolrConnection();
		ModifiableSolrParams params = new ModifiableSolrParams();
		params.set("q", wordslist.toString());
		System.out.println("Parameters:" + params.toString());
		try {
			QueryResponse response = connection.SolrQuery(params);
			SolrDocumentList list = response.getResults();

			List<SirSearchResultElement> results = new ArrayList<SirSearchResultElement>();
			for (int i = 0; i < list.size(); i++) {
				// create a new result element
				SirSearchResultElement element = new SirSearchResultElement();
				SirSolrSensorDescription solrDescription = new SirSolrSensorDescription();

				/*
				 * The results now just takes the keywords and the id TODO
				 * extends the SirSensorDescription to optimize with the Solr
				 * results.
				 */
				SolrDocument solrresult = list.get(i);

				Collection<Object> keywords = solrresult
						.getFieldValues(SolrConstants.KEYWORD);

				solrDescription.setId(solrresult.get(SolrConstants.ID)
						.toString());
				solrDescription.setKeywords(keywords);

				element.setSensorDescription(solrDescription);
				results.add(element);
			}

			return results;

		} catch (SolrServerException solrexception) {
			log.error("SolrException:" + solrexception.getLocalizedMessage());
			solrexception.printStackTrace();
			return null;
		}
	}

	/**
	 * @param lat
	 *            : Latitude of the desired point
	 * @param lng
	 *            : longitude of the desired point
	 * @param kms
	 *            : The distance of kms to match against
	 * 
	 */
	private Collection<SirSearchResultElement> spatialSearch(String lat,
			String lng, double kms, String column) {
		// prepare the request
		SolrConnection connection = new SolrConnection();
		ModifiableSolrParams params = new ModifiableSolrParams();
		params.set("q", "*:*");
		params.set("fq", "{!geofilt sfield=" + column + "}");
		params.set("pt", lat + "," + lng);
		params.set("d", kms + "");
		System.out.println("Params:" + params);
		try {
			QueryResponse response = connection.SolrQuery(params);
			SolrDocumentList list = response.getResults();

			List<SirSearchResultElement> results = new ArrayList<SirSearchResultElement>();
			for (int i = 0; i < list.size(); i++) {
				// create a new result element
				SirSearchResultElement element = new SirSearchResultElement();
				SirSolrSensorDescription solrDescription = new SirSolrSensorDescription();

				/*
				 * The results now just takes the keywords and the id TODO
				 * extends the SirSensorDescription to optimize with the Solr
				 * results.
				 */
				SolrDocument solrresult = list.get(i);

				Collection<Object> keywords = solrresult
						.getFieldValues(SolrConstants.KEYWORD);

				solrDescription.setId(solrresult.get(SolrConstants.ID)
						.toString());
				solrDescription.setKeywords(keywords);

				element.setSensorDescription(solrDescription);
				results.add(element);
			}

			return results;
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			return null;
		}
	}

	public Collection<SirSearchResultElement> searchSensorByBoundingBox(
			SirBoundingBox bbox) {
		double[] center = bbox.getCenter();
		return spatialSearch(center[0] + "", center[1] + "", 10,
				SolrConstants.BBOX_CENTER);
	}

	public Collection<SirSearchResultElement> searchSensorByLocation(
			String lat, String lng, double kms) {
		return spatialSearch(lat, lng, kms, SolrConstants.LOCATION);
	}

	public Collection<SirSearchResultElement> searchByValidTimeRange(
			Date start, Date end) {
		long startMillis = start.getTime();
		long endMillis = end.getTime();
		StringBuilder query = new StringBuilder();
		query.append(SolrConstants.START_DATE);
		query.append(":[");
		query.append(startMillis);
		query.append(" TO *]");
		query.append(" AND ");
		query.append(SolrConstants.END_DATE);
		query.append(":[* TO ");
		query.append(endMillis);
		query.append("]");
		System.out.println(query);
		SolrConnection connection = new SolrConnection();
		ModifiableSolrParams params = new ModifiableSolrParams();
		params.set("q", query.toString());
		QueryResponse response;
		try {
			response = connection.SolrQuery(params);
			SolrDocumentList list = response.getResults();

			List<SirSearchResultElement> results = new ArrayList<SirSearchResultElement>();
			for (int i = 0; i < list.size(); i++) {
				// create a new result element
				SirSearchResultElement element = new SirSearchResultElement();
				SirSolrSensorDescription solrDescription = new SirSolrSensorDescription();
				SolrDocument solrresult = list.get(i);
				Collection<Object> keywords = solrresult
						.getFieldValues(SolrConstants.KEYWORD);
				solrDescription.setId(solrresult.get(SolrConstants.ID)
						.toString());
				solrDescription.setKeywords(keywords);

				element.setSensorDescription(solrDescription);
				results.add(element);
			}

			return results;
		} catch (SolrServerException e){
			log.error("Cannot search by query", e);
			return null;
		}

	}
}
