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