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

import java.util.Date;
import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.n52.sir.ows.OwsExceptionReport;

/**
 * 
 * Interface for connections to a catalog. This can be a web service that is regularly updated from a SIR
 * instance. The main method is {@link ICatalog#pushAllDataToCatalog()}, that transfers all available
 * information in a format (that is accepted by the catalog) into the catalog. Methods for checking the
 * capabilites of the encapsulated catalog and the correctness of documents beforehand are available as well.
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public interface ICatalog {

    public static final Date OLDEST_PUSH_DATE = new Date(0l);

    /**
     * Saves all sensor information data that is stored within this instance of the SIR after
     * {@link ICatalog#OLDEST_PUSH_DATE} into this catalog instance. As the push might include a set of
     * transactions so the return type can be a list of errors that occurred.
     * 
     * @throws OwsExceptionReport
     */
    public abstract List<OwsExceptionReport> pushAllDataToCatalog() throws OwsExceptionReport;

    /**
     * 
     * Saves all sensor information data that is stored within this instance of the SIR and was updated after
     * the given Date into this catalog instance. As the push might include a set of transactions so the
     * return type can be a list of errors that occurred.
     * 
     * @param lastPush
     *        The {@link Date} after which the updates must have happened to be included in the push.
     * @return
     * @throws OwsExceptionReport
     */
    List<OwsExceptionReport> pushAllDataToCatalog(Date lastPush) throws OwsExceptionReport;

    /**
     * Checks if this catalog instance is fit for the purpose of storing sensor information, e.g. if the given
     * URL points at a valid catalog service that supports the required document profiles.
     * 
     * @return
     * @throws OwsExceptionReport
     */
    public abstract boolean hasSufficientCapabilities() throws OwsExceptionReport;

    /**
     * 
     * Checks if the catalog instance is fit for the purpose of storing sensor information and tries to update
     * the catalog with the required elements.
     * 
     * @return
     * @throws OwsExceptionReport
     */
    public abstract boolean ensureSufficientCapabilities() throws OwsExceptionReport;

    /**
     * Checks if the given document is conform with the required schema or profile of this catalog.
     * 
     * @param doc
     * @return
     * @throws OwsExceptionReport
     */
    public abstract boolean acceptsDocument(XmlObject doc) throws OwsExceptionReport;

    /**
     * Returns an array with the number of {inserted, updated, deleted} sensors during the last
     * {@link ICatalog#pushAllDataToCatalog()} call.
     * 
     * @return
     */
    public abstract int[] getSummaryOfLastPush();

}
