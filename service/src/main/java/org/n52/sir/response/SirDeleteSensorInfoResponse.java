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
import org.x52North.sir.x032.DeleteSensorInfoResponseDocument;
import org.x52North.sir.x032.DeleteSensorInfoResponseDocument.DeleteSensorInfoResponse;
import org.x52North.sir.x032.DeleteSensorInfoResponseDocument.DeleteSensorInfoResponse.DeletedSensors;

/**
 * @author Daniel Nüst
 * 
 */
public class SirDeleteSensorInfoResponse extends AbstractXmlResponse {

    private static Logger log = LoggerFactory.getLogger(SirDeleteSensorInfoResponse.class);

    private Collection<String> deletedSensors = new ArrayList<String>();

    private int numberOfDeletedSensors = 0;

    private int numberOfDeletedServiceReferences = 0;

    @Override
    public DeleteSensorInfoResponseDocument createXml() {
        DeleteSensorInfoResponseDocument document = DeleteSensorInfoResponseDocument.Factory.newInstance();
        DeleteSensorInfoResponse delteSensInfoResp = document.addNewDeleteSensorInfoResponse();

        delteSensInfoResp.setNumberOfDeletedSensors(this.numberOfDeletedSensors);
        delteSensInfoResp.setNumberOfDeletedServiceReferences(this.numberOfDeletedServiceReferences);
        DeletedSensors deletedSensor = delteSensInfoResp.addNewDeletedSensors();
        for (String inSens : this.deletedSensors) {
            deletedSensor.addSensorIDInSIR(inSens);
        }

        XmlTools.addSirAndSensorMLSchemaLocation(delteSensInfoResp);

        if (SirConfigurator.getInstance().isValidateResponses()) {
            if ( !document.validate())
                log.warn("Service created invalid document!\n" + XmlTools.validateAndIterateErrors(document));
        }

        return document;
    }

    /**
     * @return the deletedSensors
     */
    public Collection<String> getDeletedSensors() {
        return this.deletedSensors;
    }

    /**
     * @return the numberOfNewSensors
     */
    public int getNumberOfDeletedSensors() {
        return this.numberOfDeletedSensors;
    }

    /**
     * @return the numberOfNewServiceReferences
     */
    public int getNumberOfDeletedServiceReferences() {
        return this.numberOfDeletedServiceReferences;
    }

    /**
     * @param deletedSensors
     *        the deletedSensors to set
     */
    public void setDeletedSensors(Collection<String> insertedSensors) {
        this.deletedSensors = insertedSensors;
    }

    /**
     * @param numberOfDeletedSensors
     *        the numberOfDeletedSensors to set
     */
    public void setNumberOfDeletedSensors(int numberOfDeletedSensors) {
        this.numberOfDeletedSensors = numberOfDeletedSensors;
    }

    /**
     * @param numberOfDeletedServiceReferences
     *        the numberOfDeletedServiceReferences to set
     */
    public void setNumberOfDeletedServiceReferences(int numberOfDeletedServiceReferences) {
        this.numberOfDeletedServiceReferences = numberOfDeletedServiceReferences;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SirDeleteSensorInfoResponse: ");
        sb.append("\n# of deleted sensors: " + this.numberOfDeletedSensors);
        sb.append("\nDeleted service references: " + this.numberOfDeletedServiceReferences);
        sb.append("\nDeleted Sensors: " + Arrays.toString(this.deletedSensors.toArray()));
        return sb.toString();
    }

}
