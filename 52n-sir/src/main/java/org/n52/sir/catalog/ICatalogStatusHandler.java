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
package org.n52.sir.catalog;

import java.util.Collection;

/**
 * 
 * Interface for updating the status of a saved catalog connection.
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public interface ICatalogStatusHandler {

    /**
     * If the catalog connections are executed in different threads this identifier shall be used to
     * save/access the implementation.
     */
    public static final String NAME_IN_CONTEXT = "CatalogStatusHandler";

    /**
     * 
     * @return The maximum number of events that are available.
     */
    public int getMaximumInfolistSize();

    /**
     * 
     * Returns a list of Strings that give information about the most recent catalog status events that
     * happened during runtime.
     * 
     * @return a collection of status descriptions
     */
    public abstract Collection<String> getRuntimeInfo();

    /**
     * 
     * Updates the status of the catalog saved under the given identifier with the given message and saves the
     * information for runtime querying (the former only if the push is scheduled for repetition).
     * 
     * @param identifier
     * @param statusMessage
     */
    public abstract void setStatus(String identifier, String statusMessage);

}
