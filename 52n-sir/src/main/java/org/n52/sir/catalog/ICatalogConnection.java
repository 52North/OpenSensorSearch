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

import java.net.URL;

/**
 * 
 * Class encapsulates all information for persistent storage of a connection to a catalog.
 * 
 * If the connetion is only scheduled for single execution the parameters
 * {@link ICatalogConnection#NO_PUSH_INTERVAL} and {@link ICatalogConnection#UNSAVED_CONNECTION_ID} shall
 * be used for instantiation.
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public interface ICatalogConnection {

    /**
     * Push interval value if not push interval is set.
     */
    public static final int NO_PUSH_INTERVAL = Integer.MIN_VALUE;

    /**
     * The string that is written into the status field if there is no real status yet.
     */
    public static final String NEW_CONNECTION_STATUS = "NEW";

    /**
     * String that is used as the connectionID for unsaved connections.
     */
    public static final String UNSAVED_CONNECTION_ID = "[unsaved_connection!]";

    /**
     * 
     * @return The internal identifier, can be used to cancel a connection
     */
    public abstract String getConnectionID();

    /**
     * 
     * @return The URL pointing to the catalog service
     */
    public abstract URL getCatalogURL();

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
