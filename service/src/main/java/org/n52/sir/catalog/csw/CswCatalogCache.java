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
