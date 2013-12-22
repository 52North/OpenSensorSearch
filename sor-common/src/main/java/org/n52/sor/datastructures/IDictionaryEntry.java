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
package org.n52.sor.datastructures;

import net.opengis.swe.x101.PhenomenonType;


/**
 * @created 20-Okt-2008 16:44:48
 * @version 1.0
 */
public interface IDictionaryEntry {

    /**
     * Get the description.
     * 
     * @return The description
     */
    public String getDescription();

    /**
     * Get the identifier.
     * 
     * @return The identifier
     */
    public String getIdentifier();

    /**
     * Get the name.
     * 
     * @return The name
     */
    public String getName();

    /**
     * Get the phenomenon type.
     * 
     * @return The type of phenomenon
     */
    public PhenomenonType getPhenomenon();

    /**
     * Get the URL to the ontology.
     * 
     * @return The URL to the ontology
     */
    public String getUrl();

    /**
     * Set the description.
     * 
     * @param Description
     *        to set
     */
    public void setDescription(String description);

    /**
     * Set the identifier.
     * 
     * @return The identifier
     */
    public void setIdentifier(String identifier);

    /**
     * Set the Name
     * 
     * @param name
     *        The name to set
     */
    public void setName(String name);

    /**
     * Set the phenomenon type
     * 
     * @param phenom
     *        The type of phenomenon
     */
    public void setPhenomenon(PhenomenonType phenom);

    /**
     * Set the URL to the ontology
     * 
     * @param url
     *        The URL to the ontology to set
     */
    public void setUrl(String url);

}