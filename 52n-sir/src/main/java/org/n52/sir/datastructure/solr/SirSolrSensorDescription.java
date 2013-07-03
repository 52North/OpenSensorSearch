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

package org.n52.sir.datastructure.solr;

import java.util.Collection;

import org.n52.sir.datastructure.SirSensorDescription;

public class SirSolrSensorDescription extends SirSensorDescription {
    private String id;
    private Collection<Object> keywords;
    private long begineDate;
    private String description;
    public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getBegineDate() {
		return begineDate;
	}

	public void setBegineDate(long begineDate) {
		this.begineDate = begineDate;
	}

	public long getEndDate() {
		return endDate;
	}

	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}

	private long endDate;
	private Collection<Object> classifiers;
	private Collection<? extends Object> identifiers;
    

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

    public SirSolrSensorDescription() {
        // TODO Auto-generated constructor stub
    }

	public void setClassifiers(Collection<Object> classifiers) {
		this.classifiers = classifiers;
	}
	public Collection<Object> getClassifiers(){
		return this.classifiers;
	}

	public Collection<? extends Object> getIdentifiers() {
		return this.identifiers;
	}
	public void setIdentifiers(Collection<? extends Object> Identifiers){
		this.identifiers = Identifiers;
	}

}
