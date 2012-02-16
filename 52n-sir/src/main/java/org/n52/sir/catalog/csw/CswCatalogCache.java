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
package org.n52.sir.catalog.csw;

import java.util.HashMap;

import x0.oasisNamesTcEbxmlRegrepXsdRim3.IdentifiableType;

/**
 * 
 * Class caches {@link IdentifiableType} instances that were queried from a catalog. This can be used to avoid
 * repeated querying during the process of updating a transformed document prior to insertion. For example, a
 * classificatio node that should not be inserted again because it is already present in the catalog can be
 * cached here to avoid requesting it again from the catalog. Attention: The equality check is purely based on
 * the attribute "id" of {@link IdentifiableType}.
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class CswCatalogCache {

    private HashMap<String, IdentifiableType> cache;

    public CswCatalogCache() {
        this.cache = new HashMap<String, IdentifiableType>();
    }

    /**
     * 
     * @param cnt
     */
    public void add(IdentifiableType iT) {
        this.cache.put(iT.getId(), iT);
    }

    /**
     * 
     * Uses the id of the given identifiable to check against the cached identifiables.
     * 
     * @param identifiableType
     * @return true if an identifiable with the same id is already cached.
     */
    public boolean contains(IdentifiableType iT) {
        return this.cache.containsKey(iT.getId());
    }

}
