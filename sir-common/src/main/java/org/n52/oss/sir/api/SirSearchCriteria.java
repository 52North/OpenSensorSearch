/**
 * Copyright 2013 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.n52.oss.sir.api;

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

    private Collection<SirSearchCriteria_Phenomenon> phenomena = new ArrayList<>();

    private Collection<String> searchText;

    private Collection<SirService> serviceCriteria;

    private String lat;

    private String lng;

    private String radius;

    private Calendar start;

    private boolean indexedTextSearchWithMinimalResult = false;

    public String getLat() {
        return this.lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return this.lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getRadius() {
        return this.radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

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

    public void addPhenomenon(SirSearchCriteria_Phenomenon p) {
        this.phenomena.add(p);
    }

    public SirBoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    public Calendar getEnd() {
        return this.end;
    }

    public Collection<SirSearchCriteria_Phenomenon> getPhenomena() {
        return this.phenomena;
    }

    public Collection<String> getSearchText() {
        return this.searchText;
    }

    public Collection<SirService> getServiceCriteria() {
        return this.serviceCriteria;
    }

    public Calendar getStart() {
        return this.start;
    }

    public Collection<String> getUom() {
        return this.uom;
    }

    public boolean isUsingSOR() {
        for (SirSearchCriteria_Phenomenon p : this.phenomena) {
            if (p.usesSOR())
                return true;
        }
        return false;
    }

    public void setBoundingBox(SirBoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    public void setEnd(Calendar end) {
        this.end = end;
    }

    public void setPhenomena(Collection<SirSearchCriteria_Phenomenon> phenomena) {
        this.phenomena = phenomena;
    }

    public void setSearchText(Collection<String> searchText) {
        this.searchText = searchText;
    }

    public void setServiceCriteria(Collection<SirService> serviceCriteria) {
        this.serviceCriteria = serviceCriteria;
    }

    public void setStart(Calendar start) {
        this.start = start;
    }

    public void setUom(Collection<String> uom) {
        this.uom = uom;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SirSearchCriteria [");
        if (this.boundingBox != null) {
            builder.append("boundingBox=");
            builder.append(this.boundingBox);
            builder.append(", ");
        }
        if (this.end != null) {
            builder.append("end=");
            builder.append(this.end);
            builder.append(", ");
        }
        if (this.phenomena != null) {
            builder.append("phenomena=");
            builder.append(this.phenomena);
            builder.append(", ");
        }
        if (this.searchText != null) {
            builder.append("searchText=");
            builder.append(this.searchText);
            builder.append(", ");
        }
        if (this.serviceCriteria != null) {
            builder.append("serviceCriteria=");
            builder.append(this.serviceCriteria);
            builder.append(", ");
        }
        if (this.lat != null) {
            builder.append("lat=");
            builder.append(this.lat);
            builder.append(", ");
        }
        if (this.lng != null) {
            builder.append("lng=");
            builder.append(this.lng);
            builder.append(", ");
        }
        if (this.radius != null) {
            builder.append("radius=");
            builder.append(this.radius);
            builder.append(", ");
        }
        if (this.start != null) {
            builder.append("start=");
            builder.append(this.start);
            builder.append(", ");
        }
        builder.append("partialTextSearch=");
        builder.append(this.indexedTextSearchWithMinimalResult);
        builder.append(", ");
        if (this.dtstart != null) {
            builder.append("dtstart=");
            builder.append(this.dtstart);
            builder.append(", ");
        }
        if (this.dtend != null) {
            builder.append("dtend=");
            builder.append(this.dtend);
            builder.append(", ");
        }
        if (this.uom != null) {
            builder.append("uom=");
            builder.append(this.uom);
        }
        builder.append("]");
        return builder.toString();
    }

    public boolean isIndexedTextSearchWithMinimalResult() {
        return this.indexedTextSearchWithMinimalResult;
    }

    public void setIndexedTextSearchWithMinimalResult(boolean partialTextSearch) {
        this.indexedTextSearchWithMinimalResult = partialTextSearch;
    }

}
