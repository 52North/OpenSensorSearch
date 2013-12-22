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

import net.opengis.swe.x101.PhenomenonDocument;

import org.apache.xmlbeans.XmlException;
import org.n52.sor.PropertiesManager;
import org.n52.sor.util.XmlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sor.x031.InsertDefinitionRequestDocument;
import org.x52North.sor.x031.InsertDefinitionRequestDocument.InsertDefinitionRequest;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class InsertDefinitionBean extends AbstractBean {

    private static Logger log = LoggerFactory.getLogger(InsertDefinitionBean.class);

    private String phenomenon = "";

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.client.AbstractBean#buildRequest()
     */
    @Override
    public void buildRequest() {
        InsertDefinitionRequestDocument requestDoc = InsertDefinitionRequestDocument.Factory.newInstance();
        InsertDefinitionRequest request = requestDoc.addNewInsertDefinitionRequest();
        request.setService(PropertiesManager.getInstance().getService());
        request.setVersion(PropertiesManager.getInstance().getServiceVersion());

        // phenomenon dictinary
        if ( !this.phenomenon.isEmpty()) {
            try {
                PhenomenonDocument phenDoc = PhenomenonDocument.Factory.parse(this.phenomenon);

                if ( !phenDoc.validate()) {
                    this.requestString = "The phenomenon is not a valid PhenomenonType!\n\n"
                            + XmlTools.validateAndIterateErrors(phenDoc);
                    return;
                }

                request.setPhenomenon(phenDoc.getPhenomenon());
            }
            catch (XmlException e) {
                this.requestString = "The phenomenon could not be parsed to swe:PhenomenonDocument!";
                return;
            }
        }
        else {
            this.requestString = "Phenomenon is mandatory.";
            return;
        }

        if ( !requestDoc.validate()) {
            log.warn("Request is NOT valid, service may return error!\n"
                    + XmlTools.validateAndIterateErrors(requestDoc));
        }

        this.requestString = requestDoc.toString();
    }

    /**
     * @return the phenomenon
     */
    public String getPhenomenon() {
        return this.phenomenon;
    }

    /**
     * @param phenomenon
     *        the phenomenon to set
     */
    public void setPhenomenon(String phenomenon) {
        this.phenomenon = phenomenon;
    }
}