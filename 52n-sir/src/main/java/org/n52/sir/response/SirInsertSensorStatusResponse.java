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
package org.n52.sir.response;

import org.n52.sir.SirConfigurator;
import org.n52.sir.util.XmlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sir.x032.InsertSensorStatusResponseDocument;
import org.x52North.sir.x032.InsertSensorStatusResponseDocument.InsertSensorStatusResponse;

/**
 * @author Jan Schulte
 * 
 */
public class SirInsertSensorStatusResponse extends AbstractXmlResponse {

    private static Logger log = LoggerFactory.getLogger(SirInsertSensorStatusResponse.class);

    private String sensorIdInSir;

    private boolean statusUpdateSuccessful;

    @Override
    public InsertSensorStatusResponseDocument createXml() {
        InsertSensorStatusResponseDocument document = InsertSensorStatusResponseDocument.Factory.newInstance();
        InsertSensorStatusResponse insSensStatResp = document.addNewInsertSensorStatusResponse();

        // set sensorID in SIR
        insSensStatResp.setSensorIDInSIR(this.sensorIdInSir);

        XmlTools.addSirAndSensorMLSchemaLocation(insSensStatResp);

        if (SirConfigurator.getInstance().isValidateResponses()) {
            if ( !document.validate())
                log.warn("Service created invalid document!\n" + XmlTools.validateAndIterateErrors(document));
        }

        return document;
    }

    /**
     * @return the sensorIdInSir
     */
    public String getSensorIdInSir() {
        return this.sensorIdInSir;
    }

    /**
     * @return the statusUpdateSuccessful
     */
    public boolean isStatusUpdateSuccessful() {
        return this.statusUpdateSuccessful;
    }

    /**
     * @param sensorIdInSir
     *        the sensorIdInSir to set
     */
    public void setSensorIdInSir(String sensorIdInSir) {
        this.sensorIdInSir = sensorIdInSir;
    }

    /**
     * @param statusUpdateSuccessful
     *        the statusUpdateSuccessful to set
     */
    public void setStatusUpdateSuccessful(boolean statusUpdateSuccessful) {
        this.statusUpdateSuccessful = statusUpdateSuccessful;
    }

}
