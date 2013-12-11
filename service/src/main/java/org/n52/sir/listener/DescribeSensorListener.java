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

import org.apache.xmlbeans.XmlObject;
import org.n52.oss.sir.SirConstants;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.ds.IDescribeSensorDAO;
import org.n52.sir.request.AbstractSirRequest;
import org.n52.sir.request.SirDescribeSensorRequest;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.n52.sir.response.SirDescribeSensorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * @author Jan Schulte
 * 
 */
public class DescribeSensorListener implements ISirRequestListener {

    private static Logger log = LoggerFactory.getLogger(DescribeSensorListener.class);

    private static final String OPERATION_NAME = SirConstants.Operations.DescribeSensor.name();

    private IDescribeSensorDAO descSensDao;

    private boolean validateResponses;

    @Inject
    public DescribeSensorListener(IDescribeSensorDAO dao, @Named("oss.sir.responses.validate")
    boolean validateResponses) {
        this.descSensDao = dao;
        this.validateResponses = validateResponses;
    }

    @Override
    public String getOperationName() {
        return DescribeSensorListener.OPERATION_NAME;
    }

    @Override
    public ISirResponse receiveRequest(AbstractSirRequest request) {
        SirDescribeSensorRequest descSensReq = (SirDescribeSensorRequest) request;

        SirDescribeSensorResponse response = new SirDescribeSensorResponse(this.validateResponses);

        try {
            XmlObject sensorML = this.descSensDao.getSensorDescription(descSensReq.getSensorId());
            if (sensorML != null) {
                response.setSensorML(sensorML);
            }
            else {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(ExceptionCode.InvalidRequest, null, "Unknown sensor ID in Sir! Given ID: "
                        + descSensReq.getSensorId());
                log.debug("Unknown sensor ID in Sir! Given ID: " + descSensReq.getSensorId());
                throw se;
            }
        }
        catch (OwsExceptionReport se) {
            return new ExceptionResponse(se.getDocument());
        }
        return response;
    }
}