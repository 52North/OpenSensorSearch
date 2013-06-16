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

import java.util.Arrays;
import java.util.Collection;

import org.n52.sir.SirConfigurator;
import org.n52.sir.util.XmlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sir.x032.UpdateSensorDescriptionResponseDocument;
import org.x52North.sir.x032.UpdateSensorDescriptionResponseDocument.UpdateSensorDescriptionResponse;
import org.x52North.sir.x032.UpdateSensorDescriptionResponseDocument.UpdateSensorDescriptionResponse.UpdatedSensors;

/**
 * @author Daniel Nüst
 * 
 */
public class SirUpdateSensorDescriptionResponse extends AbstractXmlResponse {

    private static Logger log = LoggerFactory.getLogger(SirUpdateSensorDescriptionResponse.class);

    private int numberOfUpdatedSensorDescriptions;

    private Collection<String> updatedSensors;

    @Override
    public UpdateSensorDescriptionResponseDocument createXml() {
        UpdateSensorDescriptionResponseDocument document = UpdateSensorDescriptionResponseDocument.Factory.newInstance();
        UpdateSensorDescriptionResponse updSensDescrResp = document.addNewUpdateSensorDescriptionResponse();

        updSensDescrResp.setNumberOfUpdatedSensors(this.numberOfUpdatedSensorDescriptions);
        UpdatedSensors updatedSensorsXB = updSensDescrResp.addNewUpdatedSensors();
        for (String inSens : this.updatedSensors) {
            updatedSensorsXB.addSensorIDInSIR(inSens);
        }

        XmlTools.addSirAndSensorMLSchemaLocation(updSensDescrResp);

        if (SirConfigurator.getInstance().isValidateResponses()) {
            if ( !document.validate())
                log.warn("Service created invalid document!\n" + XmlTools.validateAndIterateErrors(document));
        }

        return document;
    }

    /**
     * @return the numberOfNewSensors
     */
    public int getNumberOfUpdatedSensorDescriptions() {
        return this.numberOfUpdatedSensorDescriptions;
    }

    /**
     * @return the insertedSensors
     */
    public Collection<String> getUpdatedSensors() {
        return this.updatedSensors;
    }

    /**
     * @param numberOfNewSensors
     *        the numberOfNewSensors to set
     */
    public void setNumberOfUpdatedSensorDescriptions(int numberOfNewSensors) {
        this.numberOfUpdatedSensorDescriptions = numberOfNewSensors;
    }

    /**
     * @param insertedSensors
     *        the insertedSensors to set
     */
    public void setUpdatedSensors(Collection<String> updatedSensors) {
        this.updatedSensors = updatedSensors;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SirUpdateSensorDescriptionResponse: ");
        sb.append("\n#Sensors: " + this.numberOfUpdatedSensorDescriptions);
        sb.append("\nUpdated Sensors: " + Arrays.toString(this.updatedSensors.toArray()));
        return sb.toString();
    }

}
