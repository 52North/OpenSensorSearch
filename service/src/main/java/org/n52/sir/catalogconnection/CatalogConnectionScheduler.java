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
package org.n52.sir.catalogconnection;

import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.catalog.ICatalogConnection;

/**
 * 
 * This interface encapsulates the submission of catalog connections using
 * {@link CatalogConnectionScheduler#submit(ICatalogConnection)}.
 * 
 * An {@link ICatalogConnection} can be submitted for (repeated) execution. It contains an identifier that can
 * be used to abort a (persistently saved) task using {@link CatalogConnectionScheduler#cancel(String)}.
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public interface CatalogConnectionScheduler {

    /**
     * Cancels the task with the given identifier.
     * 
     * @param identifier
     */
    public abstract void cancel(String identifier);

    /**
     * Submits the given catalog connection for (repeated) execution. There is no guarantee for a specific
     * execution time or maximum delay of the operations that are scheduled.
     * 
     * @param iCatalogConnection
     * @return true if submission was successful
     * @throws OwsExceptionReport
     */
    public abstract boolean submit(ICatalogConnection iCatalogConnection);

}