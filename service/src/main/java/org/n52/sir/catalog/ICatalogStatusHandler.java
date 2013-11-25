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
package org.n52.sir.catalog;

import java.util.Collection;

/**
 * 
 * Interface for viewing the status of a saved catalog connection.
 * 
 * TODO move this into a module and as a service endpoint /catalog
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
