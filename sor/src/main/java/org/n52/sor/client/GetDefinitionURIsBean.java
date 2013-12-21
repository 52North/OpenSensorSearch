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

import org.n52.sor.PropertiesManager;
import org.n52.sor.util.XmlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sor.GetDefinitionURIsRequestDocument;
import org.x52North.sor.GetDefinitionURIsRequestDocument.GetDefinitionURIsRequest;

/**
 * @author Jan Schulte
 * 
 */
public class GetDefinitionURIsBean extends AbstractBean {

    private static Logger log = LoggerFactory.getLogger(GetDefinitionURIsBean.class);

    private String maxNumberOfResults = "";

    private String startResultElement = "";

    private String searchString = "";

    @Override
    public void buildRequest() {
        GetDefinitionURIsRequestDocument requestDoc = GetDefinitionURIsRequestDocument.Factory.newInstance();
        GetDefinitionURIsRequest request = requestDoc.addNewGetDefinitionURIsRequest();
        request.setService(PropertiesManager.getInstance().getService());
        request.setVersion(PropertiesManager.getInstance().getServiceVersion());

        // maxNumberOfResults
        if ( !this.maxNumberOfResults.isEmpty()) {
            try {
                request.setMaxNumberOfResults(Integer.parseInt(this.maxNumberOfResults));
            }
            catch (NumberFormatException nfe) {
                this.requestString = "Please enter numbers into the field 'Maximum number of elements'!";
                return;
            }
        }
        // startResultElement
        if ( !this.startResultElement.isEmpty()) {
            try {
                request.setStartResultElement(Integer.parseInt(this.startResultElement));
            }
            catch (NumberFormatException nfe) {
                this.requestString = "Please enter numbers into the fields 'Start result element'!";
                return;
            }
        }

        // searchString
        if ( !this.searchString.isEmpty()) {
            request.setSearchString(this.searchString);
        }

        if ( !requestDoc.validate()) {
            log.warn("Request is NOT valid, service may return error!\n"
                    + XmlTools.validateAndIterateErrors(requestDoc));
        }

        this.requestString = requestDoc.toString();
    }

    /**
     * @return the maxNumberOfResults
     */
    public String getMaxNumberOfResults() {
        return this.maxNumberOfResults;
    }

    /**
     * @return the searchString
     */
    public String getSearchString() {
        return this.searchString;
    }

    /**
     * @return the startResultElement
     */
    public String getStartResultElement() {
        return this.startResultElement;
    }

    /**
     * @param maxNumberOfResults
     *        the maxNumberOfResults to set
     */
    public void setMaxNumberOfResults(String maxNumberOfResults) {
        this.maxNumberOfResults = maxNumberOfResults;
    }

    /**
     * @param searchString
     *        the searchString to set
     */
    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    /**
     * @param startResultElement
     *        the startResultElement to set
     */
    public void setStartResultElement(String startResultElement) {
        this.startResultElement = startResultElement;
    }
}