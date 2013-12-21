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

package org.n52.sor.request;

import org.n52.sor.PropertiesManager;

/**
 * @created 20-Okt-2008 16:44:48
 * @version 1.0
 */
public class SorGetDefinitionURIsRequest implements ISorRequest {

    private int maxNumberOfResults = Integer.MAX_VALUE;

    private int startResultElement = 0;

    private String searchString = null;

    public SorGetDefinitionURIsRequest() {
        this.maxNumberOfResults = PropertiesManager.getInstance().getDefaultMaximumNumberOfResults();
    }

    /**
     * @return the maxNumberOfResults
     */
    public int getMaxNumberOfResults() {
        return this.maxNumberOfResults;
    }

    /**
     * @return the searchString
     */
    public String getSearchString() {
        return this.searchString;
    }

    /**
     * @return the startResultElement
     */
    public int getStartResultElement() {
        return this.startResultElement;
    }

    /**
     * @param maxNumberOfResults
     *        the maxNumberOfResults to set
     */
    public void setMaxNumberOfResults(int maxNumberOfResults) {
        this.maxNumberOfResults = maxNumberOfResults;
    }

    /**
     * @param searchString
     *        the searchString to set
     */
    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    /**
     * @param startResultElement
     *        the startResultElement to set
     */
    public void setStartResultElement(int startResultElement) {
        this.startResultElement = startResultElement;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("GetDefinitionURIsRequest: ");
        sb.append(" Max number of results: " + this.maxNumberOfResults);
        sb.append(", Start result element: " + this.startResultElement);
        sb.append(" , Search string: " + this.searchString);
        return sb.toString();
    }

}