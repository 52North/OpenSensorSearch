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

import org.n52.oss.sir.SirConfig;
import org.n52.sir.SirConfigurator;
import org.n52.sir.SirConstants;
import org.n52.sir.ds.IDAOFactory;
import org.n52.sir.ds.IInsertSensorStatusDAO;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.request.AbstractSirRequest;
import org.n52.sir.request.SirInsertSensorStatusRequest;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.n52.sir.response.SirInsertSensorStatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schulte
 * 
 */
public class InsertSensorStatusListener implements ISirRequestListener {

    /**
     * the logger, used to log exceptions and additionally information
     */
    private static Logger log = LoggerFactory.getLogger(InsertSensorStatusListener.class);

    private static final String OPERATION_NAME = SirConstants.Operations.InsertSensorStatus.name();

    /**
     * the data access object for the insertSensorStatus operation
     */
    private IInsertSensorStatusDAO insSensStatDao;

    public InsertSensorStatusListener() throws OwsExceptionReport {
        SirConfig configurator = SirConfigurator.getInstance();

        IDAOFactory factory = configurator.getFactory();

        try {
            this.insSensStatDao = factory.insertSensorStatusDAO();
        }
        catch (OwsExceptionReport se) {
            log.error("Error while creating the insertSensorStatusDAO", se);
            throw se;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.ISirRequestListener#getOperationName()
     */
    @Override
    public String getOperationName() {
        return InsertSensorStatusListener.OPERATION_NAME;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.n52.sir.ISirRequestListener#receiveRequest(org.n52.sir.request. AbstractSirRequest)
     */
    @Override
    public ISirResponse receiveRequest(AbstractSirRequest request) {
        SirInsertSensorStatusRequest insSensStatReq = (SirInsertSensorStatusRequest) request;

        SirInsertSensorStatusResponse response = new SirInsertSensorStatusResponse();

        try {
            String sensorIdInSir = this.insSensStatDao.insertSensorStatus(insSensStatReq.getSensIdent(),
                                                                          insSensStatReq.getStatus());
            if (sensorIdInSir != null) {
                response.setSensorIdInSir(sensorIdInSir);
            }
            else {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(ExceptionCode.InvalidParameterValue, null, "Unknown identification!");
                if (log.isDebugEnabled())
                    log.debug("Unknown identification: " + insSensStatReq.getSensIdent());
                throw se;
            }
        }
        catch (OwsExceptionReport se) {
            return new ExceptionResponse(se.getDocument());
        }
        response.setStatusUpdateSuccessful(true);
        return response;
    }
}
