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
package org.n52.sor.request;

import org.n52.sor.ISorRequest;

/**
 * @created 20-Okt-2008 16:44:48
 * @version 1.0
 */
public class SorGetDefinitionRequest implements ISorRequest {

    /**
     * The input URI for the definition
     */
    private String inputURI;

    public SorGetDefinitionRequest(String inputURI) {
        this.inputURI = inputURI;
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
     * Set the input URI
     * 
     * @param inputURI
     *        the inputURI to set
     */
    public void setInputURI(String inputURI) {
        this.inputURI = inputURI;
    }

    @Override
    public String toString() {
        return "GetDefinition request: InputURI: " + this.inputURI;
    }

}