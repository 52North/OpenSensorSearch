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
import java.util.Arrays;
import java.util.Collection;

import org.n52.oss.sir.SirConstants;
import org.n52.oss.sir.api.InternalSensorID;
import org.n52.oss.sir.api.SirSearchCriteria_Phenomenon;
import org.n52.oss.sir.api.SirSensorIdentification;
import org.n52.oss.sir.api.SirServiceReference;
import org.n52.oss.sir.api.SirStatusDescription;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.ds.IGetSensorStatusDAO;
import org.n52.sir.request.AbstractSirRequest;
import org.n52.sir.request.SirGetSensorStatusRequest;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.n52.sir.response.SirGetSensorStatusResponse;
import org.n52.sir.util.SORTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author Jan Schulte
 * 
 */
public class GetSensorStatusListener implements ISirRequestListener {

    private static Logger log = LoggerFactory.getLogger(GetSensorStatusListener.class);

    private static final String OPERATION_NAME = SirConstants.Operations.GetSensorStatus.name();

    private IGetSensorStatusDAO getSensStatDao;

    @Inject
    public GetSensorStatusListener(IGetSensorStatusDAO dao) {
        this.getSensStatDao = dao;

        log.info("NEW {}", this);
    }

    @Override
    public String getOperationName() {
        return GetSensorStatusListener.OPERATION_NAME;
    }

    @Override
    public ISirResponse receiveRequest(AbstractSirRequest request) {
        SirGetSensorStatusRequest getSensStatReq = (SirGetSensorStatusRequest) request;
        SirGetSensorStatusResponse response = new SirGetSensorStatusResponse();
        ArrayList<SirStatusDescription> statDescs = new ArrayList<>();

        if (getSensStatReq.getSensIdent() != null) {
            // search by sensorIdentification
            for (SirSensorIdentification sensIdent : getSensStatReq.getSensIdent()) {
                if (sensIdent instanceof InternalSensorID) {
                    // sensorID in SIR
                    InternalSensorID sensorId = (InternalSensorID) sensIdent;
                    try {
                        statDescs = (ArrayList<SirStatusDescription>) this.getSensStatDao.getSensorStatusBySensorID(sensorId,
                                                                                                                    getSensStatReq.getPropertyFilter());
                    }
                    catch (OwsExceptionReport e) {
                        return new ExceptionResponse(e);
                    }
                }
                else {
                    // service description
                    SirServiceReference servDesc = (SirServiceReference) sensIdent;
                    try {
                        statDescs = (ArrayList<SirStatusDescription>) this.getSensStatDao.getSensorStatusByServiceDescription(servDesc,
                                                                                                                              getSensStatReq.getPropertyFilter());
                    }
                    catch (OwsExceptionReport e) {
                        return new ExceptionResponse(e);
                    }
                }
            }
        }
        else {
            // search by Criteria
            try {
                // utilize SOR if information is given
                if (getSensStatReq.getSearchCriteria().isUsingSOR()) {
                    // request the information from SOR and extend the search criteria with the result
                    Collection<SirSearchCriteria_Phenomenon> phenomena = getSensStatReq.getSearchCriteria().getPhenomena();

                    SORTools sor = new SORTools();
                    Collection<SirSearchCriteria_Phenomenon> newPhenomena = sor.getMatchingPhenomena(phenomena);

                    // add all found phenomena to search criteria
                    if (log.isDebugEnabled())
                        log.debug("Adding phenomena to search criteria: " + Arrays.toString(newPhenomena.toArray()));
                    phenomena.addAll(newPhenomena);
                }

                statDescs = (ArrayList<SirStatusDescription>) this.getSensStatDao.getSensorStatusBySearchCriteria(getSensStatReq.getSearchCriteria(),
                                                                                                                  getSensStatReq.getPropertyFilter());
            }
            catch (OwsExceptionReport e) {
                return new ExceptionResponse(e);
            }
        }
        response.setStatusDescs(statDescs);
        return response;
    }

}
