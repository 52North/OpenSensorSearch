/**
 * ﻿Copyright (C) 2012 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
public class DescribeSensorBean extends TestClientBean {

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