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