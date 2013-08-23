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

package org.n52.sir.datastructure.detailed;

import java.util.Collection;
import java.util.Date;

import org.n52.sir.datastructure.SirSensorDescription;

public class SirDetailedSensorDescription extends SirSensorDescription {
	private String id;
	private Collection<Object> keywords;
	private Date begineDate;
	private String location;
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	private String description;
	private double bbox_x;

	public double getbbox_x() {
		return this.bbox_x;
	}

	public void setbbox_x(double bbox_x) {
		this.bbox_x = bbox_x;
	}

	public double getbbox_y() {
		return this.bbox_y;
	}

	public void setbbox_y(double bbox_y) {
		this.bbox_y = bbox_y;
	}

	private double bbox_y;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getBegineDate() {
		return begineDate;
	}

	public void setBegineDate(Date begineDate) {
		this.begineDate = begineDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	private Date endDate;
	private Collection<Object> classifiers;
	private Collection<? extends Object> identifiers;
	private Collection<String> contacts;
	private Collection<String> inputs;
	private Collection<String> outputs;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Collection<Object> getKeywords() {
		return this.keywords;
	}

	public void setKeywords(Collection<Object> keywords) {
		this.keywords = keywords;
	}

	public SirDetailedSensorDescription() {
	}

	public void setClassifiers(Collection<Object> classifiers) {
		this.classifiers = classifiers;
	}

	public Collection<Object> getClassifiers() {
		return this.classifiers;
	}

	public Collection<? extends Object> getIdentifiers() {
		return this.identifiers;
	}

	public void setIdentifiers(Collection<? extends Object> Identifiers) {
		this.identifiers = Identifiers;
	}

	public void setContacts(Collection<String> contacts) {
		this.contacts = contacts;
	}

	public Collection<String> getContacts() {
		return this.contacts;

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
