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
		try {
			QueryResponse response = connection.SolrQuery(params);
			SolrDocumentList list = response.getResults();
			return encodeResult(list);

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
			return encodeResult(list);
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
			return encodeResult(list);
		} catch (SolrServerException e) {
			log.error("Cannot search by query", e);
			return null;
		}

	}

	private List<SirSearchResultElement> encodeResult(SolrDocumentList doc) {
		List<SirSearchResultElement> results = new ArrayList<SirSearchResultElement>();
		for (int i = 0; i < doc.size(); i++) {
			SirSearchResultElement element = new SirSearchResultElement();
			SirSolrSensorDescription solrDescription = new SirSolrSensorDescription();
			SolrDocument solrresult = doc.get(i);
			Collection<Object> keywords = solrresult
					.getFieldValues(SolrConstants.KEYWORD);
			if(solrresult.getFieldValue(SolrConstants.ID)!=null)solrDescription.setId(solrresult.get(SolrConstants.ID).toString());
			solrDescription.setKeywords(keywords);
			if(solrresult.getFieldValue(SolrConstants.START_DATE)!=null)solrDescription.setBegineDate(Long.parseLong(solrresult
					.getFieldValue(SolrConstants.START_DATE).toString()));
			if(solrresult.getFieldValue(SolrConstants.END_DATE)!=null)solrDescription.setEndDate(Long.parseLong(solrresult.getFieldValue(
					SolrConstants.END_DATE).toString()));
			if(solrresult.getFieldValue(SolrConstants.DESCRIPTION)!=null)solrDescription.setDescription(solrresult.get(
					SolrConstants.DESCRIPTION).toString());
			if(solrresult.getFieldValues(SolrConstants.CLASSIFIER)!=null)solrDescription.setClassifiers(solrresult
					.getFieldValues(SolrConstants.CLASSIFIER));
			if(solrresult.getFieldValues(SolrConstants.IDENTIFICATION)!=null)solrDescription.setIdentifiers(solrresult
					.getFieldValues(SolrConstants.IDENTIFICATION));
			if (solrresult.getFieldValues(SolrConstants.CONTACTS) != null) {
				Iterator<Object> it = solrresult.getFieldValues(
						SolrConstants.CONTACTS).iterator();
				Collection<String> results_contacts = new ArrayList<String>();
				while (it.hasNext())
					results_contacts.add(it.next().toString());
				solrDescription.setContacts(results_contacts);
			}
			if(solrresult.getFieldValues(SolrConstants.INPUT)!=null){
				Iterator<Object> it = solrresult.getFieldValues(SolrConstants.INPUT).iterator();
				Collection<String> inputs = new ArrayList<String>();
				while(it.hasNext())
					inputs.add(it.next().toString());
				solrDescription.setInputs(inputs);
				
			}
			if(solrresult.getFieldValues(SolrConstants.OUTPUT)!=null){
				Iterator<Object> it = solrresult.getFieldValues(SolrConstants.OUTPUT).iterator();
				Collection<String> outputs = new ArrayList<String>();
				while(it.hasNext())
					outputs.add(it.next().toString());
				solrDescription.setOutputs(outputs);
				
			}


			element.setSensorDescription(solrDescription);
			results.add(element);
		}
		return results;
	}

	public Collection<SirSearchResultElement> searchByDescription(
			String description) {
		SolrConnection connection = new SolrConnection();
		ModifiableSolrParams params = new ModifiableSolrParams();
		StringBuilder builder = new StringBuilder();
		builder.append(SolrConstants.DESCRIPTION);
		builder.append(":");
		builder.append('"');
		builder.append(description);
		builder.append('"');
		params.set("q", builder.toString());
		try {
			QueryResponse response = connection.SolrQuery(params);
			SolrDocumentList list = response.getResults();
			return encodeResult(list);
		} catch (Exception e) {
			log.error("Solr exception", e);
			return null;
		}

	}

	public Collection<SirSearchResultElement> searchByClassifer(String classifer) {
		SolrConnection connection = new SolrConnection();
		ModifiableSolrParams params = new ModifiableSolrParams();
		StringBuilder builder = new StringBuilder();
		builder.append(SolrConstants.CLASSIFIER);
		builder.append(":");
		builder.append('"');
		builder.append(classifer);
		builder.append('"');
		params.set("q", builder.toString());
		try {
			QueryResponse response = connection.SolrQuery(params);
			SolrDocumentList list = response.getResults();
			return encodeResult(list);
		} catch (Exception e) {
			log.error("Solr exception", e);
			return null;
		}

	}

	public Collection<SirSearchResultElement> searchByContact(String contact) {
		SolrConnection connection = new SolrConnection();
		ModifiableSolrParams params = new ModifiableSolrParams();
		StringBuilder builder = new StringBuilder();
		builder.append(SolrConstants.CONTACTS);
		builder.append(":");
		builder.append('"');
		builder.append(contact);
		builder.append('"');
		params.set("q", builder.toString());
		System.out.println(params);
		try {
			QueryResponse response = connection.SolrQuery(params);
			SolrDocumentList list = response.getResults();
			return encodeResult(list);
		} catch (Exception e) {
			log.error("Solr exception", e);
			return null;
		}

	}

	public Collection<SirSearchResultElement> searchByIdentification(
			String identifier) {
		SolrConnection connection = new SolrConnection();
		ModifiableSolrParams params = new ModifiableSolrParams();
		StringBuilder builder = new StringBuilder();
		builder.append(SolrConstants.IDENTIFICATION);
		builder.append(":");
		builder.append('"');
		builder.append(identifier);
		builder.append('"');
		params.set("q", builder.toString());
		try {
			QueryResponse response = connection.SolrQuery(params);
			SolrDocumentList list = response.getResults();
			return encodeResult(list);
		} catch (Exception e) {
			log.error("Solr exception", e);
			return null;
		}
	}

	public Collection<SirSearchResultElement> searchByInput(String input) {
		SolrConnection connection = new SolrConnection();
		ModifiableSolrParams params = new ModifiableSolrParams();
		StringBuilder builder = new StringBuilder();
		builder.append(SolrConstants.INPUT);
		builder.append(":");
		builder.append('"');
		builder.append(input);
		builder.append('"');
		params.set("q", builder.toString());
		try {
			QueryResponse response = connection.SolrQuery(params);
			SolrDocumentList list = response.getResults();
			return encodeResult(list);
		} catch (Exception e) {
			log.error("Solr exception", e);
			return null;
		}
	}
	public Collection<SirSearchResultElement> searchByOutput(String output) {
		SolrConnection connection = new SolrConnection();
		ModifiableSolrParams params = new ModifiableSolrParams();
		StringBuilder builder = new StringBuilder();
		builder.append(SolrConstants.OUTPUT);
		builder.append(":");
		builder.append('"');
		builder.append(output);
		builder.append('"');
		params.set("q", builder.toString());
		try {
			QueryResponse response = connection.SolrQuery(params);
			SolrDocumentList list = response.getResults();
			return encodeResult(list);
		} catch (Exception e) {
			log.error("Solr exception", e);
			return null;
		}
	}
	public Collection<SirSearchResultElement> searchByID(String ID){
		SolrConnection connection = new SolrConnection();
		ModifiableSolrParams params = new ModifiableSolrParams();
		StringBuilder builder = new StringBuilder();
		builder.append(SolrConstants.ID);
		builder.append(":");
		builder.append('"');
		builder.append(ID);
		builder.append('"');
		params.set("q", builder.toString());
		try {
			QueryResponse response = connection.SolrQuery(params);
			SolrDocumentList list = response.getResults();
			return encodeResult(list);
		} catch (Exception e) {
			log.error("Solr exception", e);
			return null;
		}
	}
	public Collection<SirSearchResultElement> searchByAll(String query){
		SolrConnection connection = new SolrConnection();
		ModifiableSolrParams params = new ModifiableSolrParams();
		StringBuilder builder = new StringBuilder();
		builder.append('"');
		builder.append(query);
		builder.append('"');
		params.set("q", builder.toString());
		params.set("defType", "dismax");
		params.set("qf",SolrConstants.EDISMAX);
		try {
			QueryResponse response = connection.SolrQuery(params);
			SolrDocumentList list = response.getResults();
			return encodeResult(list);
		} catch (Exception e) {
			log.error("Solr exception", e);
			return null;
		}
		
	}

}
