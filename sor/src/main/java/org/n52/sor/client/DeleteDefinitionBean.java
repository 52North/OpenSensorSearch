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
import org.x52North.sor.DeleteDefinitionRequestDocument;
import org.x52North.sor.DeleteDefinitionRequestDocument.DeleteDefinitionRequest;

/**
 * @author Jan Schulte
 * 
 */
public class DeleteDefinitionBean extends AbstractBean {

    private static Logger log = LoggerFactory.getLogger(DeleteDefinitionBean.class);

    private String definitionURI = "";

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.client.AbstractBean#buildRequest()
     */
    @Override
    public void buildRequest() {
        DeleteDefinitionRequestDocument requestDoc = DeleteDefinitionRequestDocument.Factory.newInstance();
        DeleteDefinitionRequest request = requestDoc.addNewDeleteDefinitionRequest();
        request.setService(PropertiesManager.getInstance().getService());
        request.setVersion(PropertiesManager.getInstance().getServiceVersion());

        // definition URI
        if ( !this.definitionURI.isEmpty()) {
            request.setDefinitionURI(this.definitionURI);
        }
        else {
            this.requestString = "Please enter a Definition URI!";
        }

        if ( !requestDoc.validate()) {
            log.warn("Request is NOT valid, service may return error!\n"
                    + XmlTools.validateAndIterateErrors(requestDoc));
        }

        this.requestString = requestDoc.toString();
    }

    /**
     * @return the definitionURI
     */
    public String getDefinitionURI() {
        return this.definitionURI;
    }

    /**
     * @param definitionURI
     *        the definitionURI to set
     */
    public void setDefinitionURI(String definitionURI) {
        this.definitionURI = definitionURI;
    }
}