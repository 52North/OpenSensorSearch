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

    private Calendar start;

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
