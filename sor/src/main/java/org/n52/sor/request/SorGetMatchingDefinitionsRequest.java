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
package org.n52.sor.request;

import org.n52.sor.ISorRequest;
import org.n52.sor.ISorRequest.SorMatchingType;

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