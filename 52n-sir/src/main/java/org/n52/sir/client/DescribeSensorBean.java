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

package org.n52.sir.client;

import org.n52.sir.SirConfigurator;
import org.n52.sir.SirConstants;
import org.n52.sir.util.XmlTools;
import org.x52North.sir.x032.DescribeSensorRequestDocument;
import org.x52North.sir.x032.DescribeSensorRequestDocument.DescribeSensorRequest;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class DescribeSensorBean extends AbstractBean {

    private String sensorIdInSir = "";

    /**
     * 
     */
    public DescribeSensorBean() {
        //
    }

    /**
     * @param sensorIdInSir
     */
    public DescribeSensorBean(String sensorIdInSir) {
        this.sensorIdInSir = sensorIdInSir;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.client.beans.AbstractBean#buildRequest()
     */
    @Override
    public void buildRequest() {
        this.responseString = "";

        DescribeSensorRequestDocument requestDoc = DescribeSensorRequestDocument.Factory.newInstance();
        DescribeSensorRequest request = requestDoc.addNewDescribeSensorRequest();
        request.setService(SirConstants.SERVICE_NAME);
        request.setVersion(SirConfigurator.getInstance().getServiceVersionEnum());

        this.sensorIdInSir = this.sensorIdInSir.trim();

        // sensorIdInSir
        if ( !this.sensorIdInSir.isEmpty()) {
            request.setSensorIDInSIR(this.sensorIdInSir);

            if (requestDoc.validate())
                this.requestString = requestDoc.xmlText(XmlTools.xmlOptionsForNamespaces());
            else
                this.requestString = XmlTools.validateAndIterateErrors(requestDoc);
        }
        else
            this.requestString = "Please enter an ID!";
    }

    /**
     * @return the sensorIdInSir
     */
    public String getSensorIdInSir() {
        return this.sensorIdInSir;
    }

    /**
     * @param sensorIdInSir
     *        the sensorIdInSir to set
     */
    public void setSensorIdInSir(String sensorIdInSir) {
        this.sensorIdInSir = sensorIdInSir;
    }
}