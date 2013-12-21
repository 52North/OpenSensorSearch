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

package org.n52.sor.client;

import java.util.ArrayList;

import org.n52.sor.OwsExceptionReport;
import org.n52.sor.PhenomenonManager;
import org.n52.sor.datastructures.IDictionaryEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Class for common attributes (and respective accessor methods) among all client page beans.
 * 
 * @author Jan Schulte
 * 
 */
public abstract class AbstractBean {

    private static Logger log = LoggerFactory.getLogger(AbstractBean.class);

    protected String requestString = "";

    protected String responseString = "";

    /**
     * Build the request based on user input, then save it in {@link AbstractBean#requestString}.
     */
    public abstract void buildRequest();

    /**
     * 
     * @return
     */
    public String[] getAvailableURIs() {
        ArrayList<IDictionaryEntry> phenomList;
        try {
            phenomList = (ArrayList<IDictionaryEntry>) PhenomenonManager.getInstance().getPhenomenaList();
        }
        catch (OwsExceptionReport e) {
            log.error("Could not retrieve available phenomena.", e);
            return new String[] {"Could not retrieve available phenomena."};
        }

        String[] uris = new String[phenomList.size()];

        int i = 0;
        for (IDictionaryEntry p : phenomList) {
            uris[i++] = p.getIdentifier();
        }

        return uris;
    }

    /**
     * @return the request
     */
    public String getRequestString() {
        return this.requestString;
    }

    /**
     * @return the response
     */
    public String getResponseString() {
        return this.responseString;
    }

    /**
     * @param request
     *        the request to set
     */
    public void setRequestString(String request) {
        this.requestString = request;
    }

    /**
     * @param response
     *        the response to set
     */
    public void setResponseString(String response) {
        this.responseString = response;
    }

}