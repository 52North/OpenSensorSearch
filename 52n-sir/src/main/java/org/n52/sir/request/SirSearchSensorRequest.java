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

package org.n52.sir.request;

import java.util.Collection;

import org.n52.sir.datastructure.SirSearchCriteria;
import org.n52.sir.datastructure.SirSensorIdentification;

/**
 * Internal request to search sensors
 * 
 * @author Jan Schulte
 * 
 */
public class SirSearchSensorRequest extends AbstractSirRequest {

    /**
     * Search Criteria
     */
    private SirSearchCriteria searchCriteria;

    /**
     * Collection of sensor identifications
     */
    private Collection<SirSensorIdentification> sensIdent;

    /**
     * if a simple response is requested
     */
    private boolean simpleResponse = false;

    /**
     * @return the searchCriteria
     */
    public SirSearchCriteria getSearchCriteria() {
        return this.searchCriteria;
    }

    /**
     * @return the sensIdent
     */
    public Collection<SirSensorIdentification> getSensIdent() {
        return this.sensIdent;
    }

    /**
     * @return the simpleResponse
     */
    public boolean isSimpleResponse() {
        return this.simpleResponse;
    }

    /**
     * @param searchCriteria
     *        the searchCriteria to set
     */
    public void setSearchCriteria(SirSearchCriteria searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    /**
     * @param sensIdent
     *        the sensIdent to set
     */
    public void setSensIdent(Collection<SirSensorIdentification> sensIdent) {
        this.sensIdent = sensIdent;
    }

    /**
     * @param simpleResponse
     *        the simpleResponse to set
     */
    public void setSimpleResponse(boolean simpleResponse) {
        this.simpleResponse = simpleResponse;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SearchSensorRequest: ");
        sb.append(" " + this.sensIdent);
        sb.append(", SimpleResponse: " + this.simpleResponse);
        return sb.toString();
    }
}
