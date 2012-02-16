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
package org.n52.sir.ds;

import java.net.URL;
import java.util.List;

import org.n52.sir.catalog.ICatalogConnection;
import org.n52.sir.ows.OwsExceptionReport;

/**
 * @author Jan Schulte
 * 
 */
public interface IConnectToCatalogDAO {

    /**
     * insert a connection to a catalog service
     * 
     * @param cswUrl
     *        url to the catalog service
     * @param pushInterval
     *        update interval
     * @return Returns the catalog ID in SIR
     * @throws OwsExceptionReport
     */
    String insertConnection(URL cswUrl, int pushInterval) throws OwsExceptionReport;

    /**
     * checks in database if connection exists. If exists, the ConnectionID is returned, else null
     * 
     * @param url
     *        url to the catalog service
     * @param pushInterval
     *        update interval
     * @return Returns the catalog ID in SIR or null, if not exists
     */
    String getConnectionID(URL url, int pushInterval) throws OwsExceptionReport;

    /**
     * update the connection in database
     * 
     * @param cswUrl
     *        url to the catalog service
     * @param pushInterval
     *        update interval
     * @throws OwsExceptionReport
     */
    void updateConnection(URL cswUrl, int pushInterval) throws OwsExceptionReport;

    /**
     * Returns a list of all catalogs that have a push interval
     * 
     * @return a list of all catalogs that have a push interval
     * @throws OwsExceptionReport
     */
    List<ICatalogConnection> getCatalogConnectionList() throws OwsExceptionReport;

}
