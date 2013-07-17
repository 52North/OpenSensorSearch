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
package org.n52.sir.datastructure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.xmlbeans.XmlObject;

/**
 * represents an SIR internal sensor
 * 
 * @author Jan Schulte
 * 
 */
public class SirSensor {

	/**
	 * bounding box of the observed area
	 */
	private SirBoundingBox bBox;

	/**
	 * time period
	 */
	private Date lastUpdate;

	/**
	 * observed phenomenon
	 */
	private Collection<SirPhenomenon> phenomenon;

	/**
	 * sensor ID in SIR
	 */
	private String sensorIDInSIR;

	/**
	 * sensorML document
	 */
	private XmlObject sensorMLDocument;

	/**
	 * service descriptions
	 */
	private Collection<SirServiceReference> servDescs;

	/**
	 * searchwords for this sensor
	 */
	private Collection<String> text;

	/**
	 * time period
	 */
	private SirTimePeriod timePeriod;

	private Collection<String> keywords = new ArrayList<String>();

	private String longitude;

	private String description;

	private String latitude;

	private Collection<String> classifications;

	private Collection<Object> identificationList;

	private Collection<String> contactsList;

	private Collection<String> interfaces;

	private Collection<String> inputs;

	private Collection<String> outputs;

	/**
	 * @return the bBox
	 */
	public SirBoundingBox getbBox() {
		return this.bBox;
	}

	/**
	 * @return the lastUpdate
	 */
	public Date getLastUpdate() {
		return this.lastUpdate;
	}

	/**
	 * @return the phenomenon
	 */
	public Collection<SirPhenomenon> getPhenomenon() {
		return this.phenomenon;
	}

	/**
	 * @return the sensorIDInSIR
	 */
	public String getSensorIDInSIR() {
		return this.sensorIDInSIR;
	}

	/**
	 * @return the sensorMLDocument
	 */
	public XmlObject getSensorMLDocument() {
		return this.sensorMLDocument;
	}

	/**
	 * @return the servDescs
	 */
	public Collection<SirServiceReference> getServDescs() {
		return this.servDescs;
	}

	/**
	 * @return the text
	 */
	public Collection<String> getText() {
		return this.text;
	}

	/**
	 * @return the timePeriod
	 */
	public SirTimePeriod getTimePeriod() {
		return this.timePeriod;
	}

	/**
	 * @param bBox
	 *            the bBox to set
	 */
	public void setbBox(SirBoundingBox bBox) {
		this.bBox = bBox;
	}

	/**
	 * @param lastUpdate
	 *            the lastUpdate to set
	 */
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	/**
	 * @param phenomenon
	 *            the phenomenon to set
	 */
	public void setPhenomenon(Collection<SirPhenomenon> phenomenon) {
		this.phenomenon = phenomenon;
	}

	/**
	 * @param sensorIDInSIR
	 *            the sensorIDInSIR to set
	 */
	public void setSensorIDInSIR(String sensorIDInSIR) {
		this.sensorIDInSIR = sensorIDInSIR;
	}

	/**
	 * @param sensorMLDocument
	 *            the sensorMLDocument to set
	 */
	public void setSensorMLDocument(XmlObject sensorMLDocument) {
		this.sensorMLDocument = sensorMLDocument;
	}

	/**
	 * @param servDescs
	 *            the servDescs to set
	 */
	public void setServDescs(Collection<SirServiceReference> servDescs) {
		this.servDescs = servDescs;
	}

	/**
	 * @param text
	 *            the text to set
	 */
	public void setText(Collection<String> text) {
		this.text = text;
	}

	/**
	 * @param timePeriod
	 *            the timePeriod to set
	 */
	public void setTimePeriod(SirTimePeriod timePeriod) {
		this.timePeriod = timePeriod;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("SirSensor: ");
		sb.append("SensorID in SIR: " + this.sensorIDInSIR);
		sb.append(", Service description: " + this.servDescs);
		sb.append(", " + this.phenomenon);
		sb.append(", " + this.bBox);
		sb.append(", Timeperiod: " + this.timePeriod);
		sb.append(", Last update: " + this.lastUpdate);
		return sb.toString();
	}

	public Collection<String> getKeywords() {
		return this.keywords;
	}

	public void setKeywords(Collection<String> keywords) {
		this.keywords = keywords;
	}

	public void addKeyword(String keyword) {
		this.keywords.add(keyword);
	}

	public String getLongitude() {
		return this.longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return this.latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public void setDescription(Object description) {
		this.description = description.toString();
	}

	public String getDescription() {
		return this.description;
	}

	@SuppressWarnings("unchecked")
	public void setClassificationList(Object classificationList) {
		this.classifications = (Collection<String>) classificationList;
	}

	public Collection<String> getClassificationList() {
		return this.classifications;
	}

	public void setIdentificationsList(Collection<Object> identificationList) {
		this.identificationList = identificationList;

	}

	public Collection<Object> getIdentificationsList() {
		return this.identificationList;
	}

	public void setContacts(Collection<String> contacts) {
		this.contactsList = contacts;
	}

	public Collection<String> getContacts() {
		return this.contactsList;
	}

	public void setInterfaces(Collection<String> interfaces) {
		this.interfaces = interfaces;
	}

	public Collection<String> getInterfaces() {
		return this.interfaces;
	}

	public void setInputs(Collection<String> inputs) {
		this.inputs = inputs;
	}

	public Collection<String> getInputs() {
		return this.inputs;
	}

	public void setOutputs(Collection<String> outputs) {
		this.outputs = outputs;
	}

	public Collection<String> getOutputs() {
		return this.outputs;
	}
}
