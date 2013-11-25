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
import org.n52.sir.SirConfigurator;
import org.n52.sir.catalog.ICatalogStatusHandler;
import org.n52.sir.catalogconnection.CatalogConnectionScheduler;
import org.n52.sir.ds.IDAOFactory;
import org.n52.sir.ds.IDisconnectFromCatalogDAO;
import org.n52.sir.request.AbstractSirRequest;
import org.n52.sir.request.SirDisconnectFromCatalogRequest;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.n52.sir.response.SirDisconnectFromCatalogResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author Jan Schulte
 * 
 */
public class DisconnectFromCatalogListener implements ISirRequestListener {

    private static Logger log = LoggerFactory.getLogger(DisconnectFromCatalogListener.class);

    private static final String OPERATION_NAME = SirConstants.Operations.DisconnectFromCatalog.name();

    private IDisconnectFromCatalogDAO disconFromCatDao;

    @Inject
    CatalogConnectionScheduler scheduler;

    public DisconnectFromCatalogListener() throws OwsExceptionReport {
        SirConfigurator configurator = SirConfigurator.getInstance();
        IDAOFactory factory = configurator.getFactory();
        try {
            this.disconFromCatDao = factory.disconnectFromCatalogDAO();
        }
        catch (OwsExceptionReport se) {
            log.error("Error while creating the disconnectFromCatalogDao", se);
            throw se;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.listener.ISirRequestListener#getOperationName()
     */
    @Override
    public String getOperationName() {
        return DisconnectFromCatalogListener.OPERATION_NAME;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.listener.ISirRequestListener#receiveRequest(org.n52.sir.request .AbstractSirRequest)
     */
    @Override
    public ISirResponse receiveRequest(AbstractSirRequest request) {
        SirDisconnectFromCatalogRequest disconFromCatReq = (SirDisconnectFromCatalogRequest) request;
        SirDisconnectFromCatalogResponse response = new SirDisconnectFromCatalogResponse();

        String connectionID;
        try {
            // delete database entry
            connectionID = this.disconFromCatDao.deleteConnection(disconFromCatReq.getCswURL());

            if (connectionID == null) {
                // connection not in SIR
                OwsExceptionReport oer = new OwsExceptionReport();
                oer.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                      null,
                                      "No connection with given url (" + disconFromCatReq.getCswURL() + ") saved!");
                return new ExceptionResponse(oer.getDocument());
            }
            // delete timer with connectionID
            this.scheduler.cancel(connectionID);

            response.setCatalogUrl(disconFromCatReq.getCswURL());

            // set runtime status info
            ICatalogStatusHandler runtimeHandler = SirConfigurator.getInstance().getCatalogStatusHandler();
            runtimeHandler.setStatus(connectionID, "Disconnected from catalog " + disconFromCatReq.getCswURL());

            return response;
        }
        catch (OwsExceptionReport e) {
            return new ExceptionResponse(e);
        }
    }

}
