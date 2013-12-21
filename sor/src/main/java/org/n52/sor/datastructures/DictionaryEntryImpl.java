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