/**
 * ﻿Copyright (C) 2013 52°North Initiative for Geospatial Open Source Software GmbH
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
package org.n52.oss.ui.beans;

import org.n52.oss.util.XmlTools;
import org.x52North.sir.x032.DescribeSensorRequestDocument;
import org.x52North.sir.x032.DescribeSensorRequestDocument.DescribeSensorRequest;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class DescribeSensorBean extends TestClientBean {

    private String sensorId = "";

    public DescribeSensorBean() {
        //
    }

    public DescribeSensorBean(String sensorId) {
        this.sensorId = sensorId;
    }

    @Override
    public void buildRequest() {
        this.responseString = "";

        DescribeSensorRequestDocument requestDoc = DescribeSensorRequestDocument.Factory.newInstance();
        DescribeSensorRequest request = requestDoc.addNewDescribeSensorRequest();
        request.setService(ClientConstants.SERVICE_NAME);
        request.setVersion(ClientConstants.getServiceVersionEnum());

        this.sensorId = this.sensorId.trim();

        if ( !this.sensorId.isEmpty()) {
            request.setSensorIDInSIR(this.sensorId);

            if (requestDoc.validate())
                this.requestString = requestDoc.xmlText(XmlTools.xmlOptionsForNamespaces());
            else
                this.requestString = XmlTools.validateAndIterateErrors(requestDoc);
        }
        else
            this.requestString = "Please enter an ID!";
    }

    public String getSensorId() {
        return this.sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }
}