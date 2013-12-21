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

/**
 * @created 20-Okt-2008 16:44:48
 * @version 1.0
 */
public class SorGetMatchingDefinitionsRequest implements ISorRequest {

    /**
     * The requested URI for the getMatchingDefinitionsRequest
     */
    private String inputURI;

    /**
     * The type of matching algorithm used.
     */
    private SorMatchingType matchingType;

    /**
     * The depth the algorithm searches to.
     */
    private String searchDepth = "0";

    /**
     * Constructor
     */
    public SorGetMatchingDefinitionsRequest() {
        //
    }

    /**
     * Constructor
     */
    public SorGetMatchingDefinitionsRequest(String inputURI, SorMatchingType matchingType, String searchDepth) {
        this.inputURI = inputURI;
        this.matchingType = matchingType;
        this.searchDepth = searchDepth;
    }

    /**
     * Get the input URI
     * 
     * @return the inputURI
     */
    public String getInputURI() {
        return this.inputURI;
    }

    /**
     * Get the matching type
     * 
     * @return the matchingType
     */
    public SorMatchingType getMatchingType() {
        return this.matchingType;
    }

    /**
     * Get the depth of the search
     * 
     * @return the searchDepth
     */
    public String getSearchDepth() {
        return this.searchDepth;
    }

    /**
     * Set the input URI
     * 
     * @param inputURI
     *        the inputURI to set
     */
    public void setInputURI(String inputURI) {
        this.inputURI = inputURI;
    }

    /**
     * Set the matching type
     * 
     * @param matchingType
     *        the matching type to set
     */
    public void setMatchingType(SorMatchingType matchingType) {
        this.matchingType = matchingType;
    }

    /**
     * Set the depth of the search
     * 
     * @param searchDepth
     *        the depth of the search
     */
    public void setSearchDepth(String searchDepth) {
        this.searchDepth = searchDepth;
    }

    @Override
    public String toString() {
        return "getMatchingDefinitions request: InputURI: " + this.inputURI + ", MatchingType: " + this.matchingType
                + ", Depth of the search: " + this.searchDepth;
    }

}