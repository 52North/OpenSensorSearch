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
import java.util.Calendar;
import java.util.Collection;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class SirSearchCriteria {

    private SirBoundingBox boundingBox;

    private Calendar end;

    private Collection<SirSearchCriteria_Phenomenon> phenomena = new ArrayList<SirSearchCriteria_Phenomenon>();

    private Collection<String> searchText;

    private Collection<SirService> serviceCriteria;
    
    private String lat;
    public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public String getRadius() {
		return radius;
	}

	public void setRadius(String radius) {
		this.radius = radius;
	}

	private String lng;
    private String radius;

    private Calendar start;
    public String getDtstart() {
		return this.dtstart;
	}

	public void setDtstart(String dtstart) {
		this.dtstart = dtstart;
	}

	public String getDtend() {
		return this.dtend;
	}

	public void setDtend(String dtend) {
		this.dtend = dtend;
	}

	private String dtstart;
    private String dtend;

    private Collection<String> uom;

    /**
     * 
     * @param p
     */
    public void addPhenomenon(SirSearchCriteria_Phenomenon p) {
        this.phenomena.add(p);
    }

    /**
     * @return the boundingBox
     */
    public SirBoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    /**
     * @return the end
     */
    public Calendar getEnd() {
        return this.end;
    }

    /**
     * 
     * @return
     */
    public Collection<SirSearchCriteria_Phenomenon> getPhenomena() {
        return this.phenomena;
    }

    /**
     * @return the searchText
     */
    public Collection<String> getSearchText() {
        return this.searchText;
    }

    /**
     * @return the serviceCriteria
     */
    public Collection<SirService> getServiceCriteria() {
        return this.serviceCriteria;
    }

    /**
     * @return the start
     */
    public Calendar getStart() {
        return this.start;
    }

    /**
     * @return the uom
     */
    public Collection<String> getUom() {
        return this.uom;
    }

    /**
     * 
     * @return true if one of the given phenomena uses SOR feature
     */
    public boolean isUsingSOR() {
        for (SirSearchCriteria_Phenomenon p : this.phenomena) {
            if (p.usesSOR())
                return true;
        }
        return false;
    }

    /**
     * @param boundingBox
     *        the boundingBox to set
     */
    public void setBoundingBox(SirBoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    /**
     * @param end
     *        the end to set
     */
    public void setEnd(Calendar end) {
        this.end = end;
    }

    /**
     * 
     * @param phenomena
     */
    public void setPhenomena(Collection<SirSearchCriteria_Phenomenon> phenomena) {
        this.phenomena = phenomena;
    }

    /**
     * @param searchText
     *        the searchText to set
     */
    public void setSearchText(Collection<String> searchText) {
        this.searchText = searchText;
    }

    /**
     * @param serviceCriteria
     *        the serviceCriteria to set
     */
    public void setServiceCriteria(Collection<SirService> serviceCriteria) {
        this.serviceCriteria = serviceCriteria;
    }

    /**
     * @param start
     *        the start to set
     */
    public void setStart(Calendar start) {
        this.start = start;
    }

    /**
     * @param uom
     *        the uom to set
     */
    public void setUom(Collection<String> uom) {
        this.uom = uom;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SearchCriteria: ");
        sb.append("ServiceCriteria: " + this.serviceCriteria);
        sb.append(", SearchText: " + this.searchText);
        sb.append(", Phenomena: " + this.phenomena);
        sb.append(", Uom: " + this.uom);
        sb.append(", BoundingBox: " + this.boundingBox);
        sb.append(", Start: " + this.start);
        sb.append(", End: " + this.end);
        return sb.toString();
    }

}
