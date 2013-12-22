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

package org.n52.sir.listener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.n52.oss.sir.SirConstants;
import org.n52.oss.sir.api.SirDescriptionToBeUpdated;
import org.n52.oss.sir.api.SirSensor;
import org.n52.oss.sir.api.SirSensorIdentification;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.ds.IInsertSensorInfoDAO;
import org.n52.sir.request.AbstractSirRequest;
import org.n52.sir.request.SirUpdateSensorDescriptionRequest;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.n52.sir.response.SirUpdateSensorDescriptionResponse;
import org.n52.sir.sml.SensorMLDecoder;
import org.n52.sir.xml.IProfileValidator;
import org.n52.sir.xml.ValidationResult;
import org.n52.sir.xml.ValidatorModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author Jan Schulte
 * 
 */
public class UpdateSensorDescriptionListener implements ISirRequestListener {

    private static Logger log = LoggerFactory.getLogger(UpdateSensorDescriptionListener.class);

    private static final String OPERATION_NAME = SirConstants.Operations.UpdateSensorDescription.name();

    private IInsertSensorInfoDAO insertSensorInfoDAO;

    private IProfileValidator validator;

    @Inject
    public UpdateSensorDescriptionListener(IInsertSensorInfoDAO dao, Set<IProfileValidator> validators) {
        this.insertSensorInfoDAO = dao;
        this.validator = ValidatorModule.getFirstMatchFor(validators,
                                                          IProfileValidator.ValidatableFormatAndProfile.SML_DISCOVERY);

        log.info("NEW {}", this);
    }

    @Override
    public String getOperationName() {
        return UpdateSensorDescriptionListener.OPERATION_NAME;
    }

    @Override
    public ISirResponse receiveRequest(AbstractSirRequest request) {

        SirUpdateSensorDescriptionRequest sirRequest = (SirUpdateSensorDescriptionRequest) request;
        SirUpdateSensorDescriptionResponse response = new SirUpdateSensorDescriptionResponse();

        ArrayList<String> updatedSensors = new ArrayList<>();

        SensorMLDecoder decoder = new SensorMLDecoder();

        try {
            for (SirDescriptionToBeUpdated descrToBeUpdated : sirRequest.getDescriptionToBeUpdated()) {

                SirSensorIdentification sensorIdent = descrToBeUpdated.getSensorIdentification();
                XmlObject sensorDescription = descrToBeUpdated.getSensorDescription();

                SirSensor sensor = decoder.decode(sensorIdent, sensorDescription);
                sensor.setLastUpdate(new Date());

                // UPDATE
                updateSensor(response, updatedSensors, sensorIdent, sensor);
            }
        }
        catch (OwsExceptionReport | IOException e) {
            return new ExceptionResponse(e);
        }

        response.setUpdatedSensors(updatedSensors);

        return response;
    }

    private void updateSensor(SirUpdateSensorDescriptionResponse response,
                              ArrayList<String> updatedSensors,
                              SirSensorIdentification sensorIdent,
                              SirSensor sensor) throws OwsExceptionReport, IOException {
        // check SensorML for conformity with profile
        ValidationResult validationResult = this.validator.validate(sensor.getSensorMLDocument());
        boolean isValid = validationResult.isValidated();
        if ( !isValid) {
            String errMsg = "Sensor metadata document of sensor " + sensorIdent
                    + "is not conform with the required profile and cannot be updated!";
            log.error(errMsg);

            throw new OwsExceptionReport(ExceptionCode.InvalidParameterValue,
                                         "SensorDescription",
                                         "The given sensor description is not conform to the required profile of this service: "
                                                 + String.valueOf(validationResult.getValidationFailuresAsString()));
        }

        String sensorId = this.insertSensorInfoDAO.updateSensor(sensorIdent, sensor);
        if (sensorId != null) {
            updatedSensors.add(sensorId);
            response.setNumberOfUpdatedSensorDescriptions(response.getNumberOfUpdatedSensorDescriptions() + 1);

            log.debug("Updated sensor description for sensor {}", sensorId);
        }
    }
}
