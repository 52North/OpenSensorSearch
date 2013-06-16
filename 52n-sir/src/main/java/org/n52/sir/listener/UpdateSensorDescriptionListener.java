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

import java.util.ArrayList;
import java.util.Date;

import org.apache.xmlbeans.XmlObject;
import org.n52.sir.SirConfigurator;
import org.n52.sir.SirConstants;
import org.n52.sir.datastructure.SirDescriptionToBeUpdated;
import org.n52.sir.datastructure.SirSensor;
import org.n52.sir.datastructure.SirSensorIdentification;
import org.n52.sir.ds.IDAOFactory;
import org.n52.sir.ds.IInsertSensorInfoDAO;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.request.AbstractSirRequest;
import org.n52.sir.request.SirUpdateSensorDescriptionRequest;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.n52.sir.response.SirUpdateSensorDescriptionResponse;
import org.n52.sir.sml.SensorMLDecoder;
import org.n52.sir.xml.IProfileValidator;
import org.n52.sir.xml.IValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schulte
 * 
 */
public class UpdateSensorDescriptionListener implements ISirRequestListener {

    /**
     * the logger, used to log exceptions and additionally information
     */
    private static Logger log = LoggerFactory.getLogger(UpdateSensorDescriptionListener.class);

    private static final String OPERATION_NAME = SirConstants.Operations.UpdateSensorDescription.name();

    private IInsertSensorInfoDAO insertSensorInfoDAO;

    private IValidatorFactory validatorFactory;

    public UpdateSensorDescriptionListener() throws OwsExceptionReport {
        SirConfigurator configurator = SirConfigurator.getInstance();

        IDAOFactory factory = configurator.getFactory();
        this.insertSensorInfoDAO = factory.insertSensorInfoDAO();

        this.validatorFactory = configurator.getValidatorFactory();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.ISirRequestListener#getOperationName()
     */
    @Override
    public String getOperationName() {
        return UpdateSensorDescriptionListener.OPERATION_NAME;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.n52.sir.ISirRequestListener#receiveRequest(org.n52.sir.request. AbstractSirRequest)
     */
    @Override
    public ISirResponse receiveRequest(AbstractSirRequest request) {

        SirUpdateSensorDescriptionRequest sirRequest = (SirUpdateSensorDescriptionRequest) request;
        SirUpdateSensorDescriptionResponse response = new SirUpdateSensorDescriptionResponse();

        ArrayList<String> updatedSensors = new ArrayList<String>();

        try {
            for (SirDescriptionToBeUpdated descrToBeUpdated : sirRequest.getDescriptionToBeUpdated()) {

                SirSensorIdentification sensorIdent = descrToBeUpdated.getSensorIdentification();
                XmlObject sensorDescription = descrToBeUpdated.getSensorDescription();

                SirSensor sensor = SensorMLDecoder.decode(sensorIdent, sensorDescription);
                sensor.setLastUpdate(new Date());

                // UPDATE
                updateSensor(response, updatedSensors, sensorIdent, sensor);
            }
        }
        catch (OwsExceptionReport e) {
            return new ExceptionResponse(e.getDocument());
        }

        response.setUpdatedSensors(updatedSensors);

        return response;
    }

    /**
     * @param response
     * @param updatedSensors
     * @param sensorIdent
     * @param sensorIdent
     * @param sensor
     * @return
     * @throws OwsExceptionReport
     */
    private void updateSensor(SirUpdateSensorDescriptionResponse response,
                              ArrayList<String> updatedSensors,
                              SirSensorIdentification sensorIdent,
                              SirSensor sensor) throws OwsExceptionReport {
        // check SensorML for conformity with profile
        IProfileValidator profileValidator = this.validatorFactory.getSensorMLProfile4DiscoveryValidator();
        boolean isValid = Boolean.valueOf(profileValidator.validate(sensor.getSensorMLDocument())).booleanValue();
        if ( !isValid) {
            String errMsg = "Sensor metadata document of sensor " + sensorIdent
                    + "is not conform with the required profile and cannot be updated!";
            log.error(errMsg);

            throw new OwsExceptionReport(ExceptionCode.InvalidParameterValue,
                                         "SensorDescription",
                                         "The given sensor description is not conform to the required profile of this service: "
                                                 + String.valueOf(profileValidator.getValidationFailuresAsString()));
        }
        profileValidator = null;

        String sensorIdInSIR = this.insertSensorInfoDAO.updateSensor(sensorIdent, sensor);
        if (sensorIdInSIR != null) {
            updatedSensors.add(sensorIdInSIR);
            response.setNumberOfUpdatedSensorDescriptions(response.getNumberOfUpdatedSensorDescriptions() + 1);

            if (log.isDebugEnabled())
                log.debug("Updated sensor description for sensor " + sensorIdInSIR);
        }
    }
}
