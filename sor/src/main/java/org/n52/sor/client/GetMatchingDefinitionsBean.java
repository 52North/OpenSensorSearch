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
/*******************************************************************************
Copyright (C) 2010
by 52 North Initiative for Geospatial Open Source Software GmbH

Contact: Andreas Wytzisk
52 North Initiative for Geospatial Open Source Software GmbH
Martin-Luther-King-Weg 24
48155 Muenster, Germany
info@52north.org

This program is free software; you can redistribute and/or modify it under 
the terms of the GNU General Public License version 2 as published by the 
Free Software Foundation.

This program is distributed WITHOUT ANY WARRANTY; even without the implied
WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License along with
this program (see gnu-gpl v2.txt). If not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
visit the Free Software Foundation web page, http://www.fsf.org.

Authors: Jan Schulte, Daniel Nüst
 
 ******************************************************************************/

package org.n52.sor.client;

import org.n52.sor.OwsExceptionReport;
import org.n52.sor.PropertiesManager;
import org.n52.sor.request.ISorRequest.SorMatchingType;
import org.n52.sor.request.SorGetMatchingDefinitionsRequest;
import org.n52.sor.util.XmlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sor.GetMatchingDefinitionsRequestDocument;
import org.x52North.sor.GetMatchingDefinitionsRequestDocument.GetMatchingDefinitionsRequest;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class GetMatchingDefinitionsBean extends AbstractBean {

    private static Logger log = LoggerFactory.getLogger(GetMatchingDefinitionsBean.class);

    private String inputURI = "";

    private String matchingTypeString;

    private int searchDepth;

    @Override
    public void buildRequest() {
        GetMatchingDefinitionsRequestDocument requestDoc = GetMatchingDefinitionsRequestDocument.Factory.newInstance();
        GetMatchingDefinitionsRequest request = requestDoc.addNewGetMatchingDefinitionsRequest();
        request.setService(PropertiesManager.getInstance().getService());
        request.setVersion(PropertiesManager.getInstance().getServiceVersion());

        // inputURI
        if ( !this.inputURI.isEmpty()) {
            request.setInputURI(this.inputURI);
        }
        else {
            this.requestString = "Please choose an input URI!";
            return;
        }

        // matchingType
        try {
            request.setMatchingType(SorMatchingType.getSorMatchingType(this.matchingTypeString).getSchemaMatchingType());
        }
        catch (OwsExceptionReport e) {
            log.warn("Matching type NOT supported!");
            this.requestString = "The matching type is not supported!\n\n" + e.getDocument();
        }

        // searchDepth
        request.setSearchDepth(this.searchDepth);

        if ( !requestDoc.validate()) {
            log.warn("Request is NOT valid, service may return error!\n"
                    + XmlTools.validateAndIterateErrors(requestDoc));
        }

        this.requestString = requestDoc.toString();
    }

    /**
     * @return the inputURI
     */
    public String getInputURI() {
        return this.inputURI;
    }

    /**
     * 
     * @return
     */
    public SorMatchingType[] getMatchingTypes() {
        return SorGetMatchingDefinitionsRequest.SorMatchingType.values();
    }

    /**
     * @return the matchingTypeString
     */
    public String getMatchingTypeString() {
        return this.matchingTypeString;
    }

    /**
     * @return the searchDepth
     */
    public int getSearchDepth() {
        return this.searchDepth;
    }

    /**
     * @param inputURI
     *        the inputURI to set
     */
    public void setInputURI(String inputURI) {
        this.inputURI = inputURI;
    }

    /**
     * @param matchingTypeString
     *        the matchingTypeString to set
     */
    public void setMatchingTypeString(String matchingTypeString) {
        this.matchingTypeString = matchingTypeString;
    }

    /**
     * @param searchDepth
     *        the searchDepth to set
     */
    public void setSearchDepth(int searchDepth) {
        this.searchDepth = searchDepth;
    }

}