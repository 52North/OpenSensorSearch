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

import net.opengis.gml.CodeType;
import net.opengis.gml.StringOrRefType;
import net.opengis.swe.x101.PhenomenonType;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class DictionaryEntryImpl implements IDictionaryEntry {

    /**
     * Description of dictonary entry
     */
    private String description;

    /**
     * Name of dictonary entry
     */
    private String name;

    /**
     * Phenomenon type of dictonary entry
     */
    private PhenomenonType phenom;

    /**
     * URL of ontology of dictonary entry
     */
    private String url;

    /**
     * Identifier of dictionary entry
     */
    private String identifier;

    public DictionaryEntryImpl(PhenomenonType phen) {
        this.phenom = phen;

        // url to ontology
        if (phen.isSetDescription()) {
            StringOrRefType d = phen.getDescription();
            if (d.isSetHref()) {
                this.url = d.getHref();
            }
            // description
            this.description = d.getStringValue();
        }

        // identifier or urn
        this.identifier = phen.getId();

        // name
        CodeType[] nameArray = phen.getNameArray();
        if (nameArray.length > 0) {
            this.name = nameArray[0].getStringValue();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.datastructures.DictionaryEntry#getDescription()
     */
    @Override
    public String getDescription() {
        return this.description;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.datastructures.DictionaryEntry#getIdentifier()
     */
    @Override
    public String getIdentifier() {
        return this.identifier;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.datastructures.DictionaryEntry#getName()
     */
    @Override
    public String getName() {
        return this.name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.datastructures.DictionaryEntry#getPhenom()
     */
    @Override
    public PhenomenonType getPhenomenon() {
        return this.phenom;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.datastructures.DictionaryEntry#getUrl()
     */
    @Override
    public String getUrl() {
        return this.url;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.datastructures.DictionaryEntry#setDescription(java.lang.String)
     */
    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.datastructures.DictionaryEntry#setIdentifier(java.lang.String)
     */
    @Override
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.datastructures.DictionaryEntry#setName(java.lang.String)
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.datastructures.DictionaryEntry#setPhenomenonType(net.opengis.swe.x101.PhenomenonType)
     */
    @Override
    public void setPhenomenon(PhenomenonType phenom) {
        this.phenom = phenom;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.datastructures.DictionaryEntry#setUrl(java.lang.String)
     */
    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "DictonaryEntry: Name: " + getName() + " Ident: " + getIdentifier() + " Description: "
                + getDescription() + " URL: " + getUrl();
    }

}