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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.n52.oss.util.XmlTools;
import org.n52.sir.SirConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sir.x032.InsertSensorInfoResponseDocument;
import org.x52North.sir.x032.InsertSensorInfoResponseDocument.InsertSensorInfoResponse;
import org.x52North.sir.x032.InsertSensorInfoResponseDocument.InsertSensorInfoResponse.InsertedSensors;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class SirInsertSensorInfoResponse extends AbstractXmlResponse {

    private static Logger log = LoggerFactory.getLogger(SirInsertSensorInfoResponse.class);

    private Collection<String> insertedSensors = new ArrayList<String>();

    private int numberOfInsertedSensors = 0;

    private int numberOfInsertedServiceReferences = 0;

    @Override
    public InsertSensorInfoResponseDocument createXml() {
        InsertSensorInfoResponseDocument document = InsertSensorInfoResponseDocument.Factory.newInstance();
        InsertSensorInfoResponse insSensInfoResp = document.addNewInsertSensorInfoResponse();

        insSensInfoResp.setNumberOfInsertedSensors(this.numberOfInsertedSensors);
        insSensInfoResp.setNumberOfInsertedServiceReferences(this.numberOfInsertedServiceReferences);
        InsertedSensors insertedSensor = insSensInfoResp.addNewInsertedSensors();
        for (String inSens : this.insertedSensors) {
            insertedSensor.addSensorIDInSIR(inSens);
        }

        XmlTools.addSirAndSensorMLSchemaLocation(insSensInfoResp);

        if (SirConfigurator.getInstance().isValidateResponses()) {
            if ( !document.validate())
                log.warn("Service created invalid document!\n" + XmlTools.validateAndIterateErrors(document));
        }

        return document;
    }

    /**
     * @return the insertedSensors
     */
    public Collection<String> getInsertedSensors() {
        return this.insertedSensors;
    }

    /**
     * @return the numberOfNewSensors
     */
    public int getNumberOfNewSensors() {
        return this.numberOfInsertedSensors;
    }

    /**
     * @return the numberOfNewServiceReferences
     */
    public int getNumberOfNewServiceReferences() {
        return this.numberOfInsertedServiceReferences;
    }

    /**
     * @param insertedSensors
     *        the insertedSensors to set
     */
    public void setInsertedSensors(Collection<String> insertedSensors) {
        this.insertedSensors = insertedSensors;
    }

    /**
     * @param numberOfNewSensors
     *        the numberOfNewSensors to set
     */
    public void setNumberOfNewSensors(int numberOfNewSensors) {
        this.numberOfInsertedSensors = numberOfNewSensors;
    }

    /**
     * @param numberOfNewServiceReferences
     *        the numberOfNewServiceReferences to set
     */
    public void setNumberOfNewServiceReferences(int numberOfNewServiceReferences) {
        this.numberOfInsertedServiceReferences = numberOfNewServiceReferences;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SirInsertSensorInfoResponse: ");
        sb.append(" Inserted sensors: " + this.numberOfInsertedSensors);
        sb.append(" New service references: " + this.numberOfInsertedServiceReferences);
        sb.append(" Inserted Sensors: " + Arrays.toString(this.insertedSensors.toArray()));
        return sb.toString();
    }

}
