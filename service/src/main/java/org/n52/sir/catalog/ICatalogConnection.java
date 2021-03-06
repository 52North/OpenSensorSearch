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
package org.n52.sir.catalog;

import java.net.URL;

/**
 * 
 * Class encapsulates all information for persistent storage of a connection to a catalog.
 * 
 * If the connetion is only scheduled for single execution the parameters
 * {@link ICatalogConnection#NO_PUSH_INTERVAL} and {@link ICatalogConnection#UNSAVED_CONNECTION_ID} shall be
 * used for instantiation.
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public interface ICatalogConnection {

    /**
     * The string that is written into the status field if there is no real status yet.
     */
    public static final String NEW_CONNECTION_STATUS = "NEW";

    /**
     * Push interval value if not push interval is set.
     */
    public static final int NO_PUSH_INTERVAL = Integer.MIN_VALUE;

    /**
     * String that is used as the connectionID for unsaved connections.
     */
    public static final String UNSAVED_CONNECTION_ID = "[unsaved_connection!]";

    /**
     * 
     * @return The URL pointing to the catalog service
     */
    public abstract URL getCatalogURL();

    /**
     * 
     * @return The internal identifier, can be used to cancel a connection
     */
    public abstract String getConnectionID();

    /**
     * 
     * This parameter is optional. It can be checked via comparison with {@link #NO_PUSH_INTERVAL}.
     * 
     * @return The temporal interval at which updates are sent to this catalog in seconds
     */
    public abstract int getPushIntervalSeconds();

    /**
     * 
     * @return A textual description of the connection's status. This should be human readable.
     */
    public abstract String getStatus();
}
