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

import org.n52.oss.sir.SirConstants;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.ds.IInsertSensorStatusDAO;
import org.n52.sir.request.AbstractSirRequest;
import org.n52.sir.request.SirInsertSensorStatusRequest;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.n52.sir.response.SirInsertSensorStatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author Jan Schulte
 * 
 */
public class InsertSensorStatusListener implements ISirRequestListener {

    private static Logger log = LoggerFactory.getLogger(InsertSensorStatusListener.class);

    private static final String OPERATION_NAME = SirConstants.Operations.InsertSensorStatus.name();

    private IInsertSensorStatusDAO insSensStatDao;

    @Inject
    public InsertSensorStatusListener(IInsertSensorStatusDAO dao) {
        this.insSensStatDao = dao;

        log.info("NEW {}", this);
    }

    @Override
    public String getOperationName() {
        return InsertSensorStatusListener.OPERATION_NAME;
    }

    @Override
    public ISirResponse receiveRequest(AbstractSirRequest request) {
        SirInsertSensorStatusRequest insSensStatReq = (SirInsertSensorStatusRequest) request;

        SirInsertSensorStatusResponse response = new SirInsertSensorStatusResponse();

        try {
            String sensorId = this.insSensStatDao.insertSensorStatus(insSensStatReq.getSensIdent(),
                                                                     insSensStatReq.getStatus());
            if (sensorId != null) {
                response.setSensorId(sensorId);
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
