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

import org.n52.sir.ows.OwsExceptionReport;

/**
 * 
 * Factory for objects that access catalogue services, {@link ICatalog}. The created catalogs use the url that
 * is available via {@link ICatalogFactory#getCatalogUrl()} as their endpoint.
 * 
 * The method {@link ICatalogFactory#getCatalogConnection(String, URL, int, String)} shall be used to create
 * {@link ICatalogConnection} instances that contain the neccessary parameters to create and save a connection
 * to a catalog.
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public interface ICatalogFactory {

    /**
     * 
     * @return a new instance of an ICatalog
     * @throws OwsExceptionReport 
     */
    public abstract ICatalog getCatalog() throws OwsExceptionReport;

    /**
     * 
     * @return the URL used by the instatiated catalogs
     */
    public abstract URL getCatalogUrl();

    /**
     * 
     * Method creates objects that encapsulate all information that is needed to persitently save a connection
     * to a catalog.
     * 
     * @param connectionID
     * @param url
     * @param pushInterval
     * @param newConnectionStatus
     * @return
     */
    public abstract ICatalogConnection getCatalogConnection(String connectionID,
                                                            URL url,
                                                            int pushInterval,
                                                            String newConnectionStatus);

}
