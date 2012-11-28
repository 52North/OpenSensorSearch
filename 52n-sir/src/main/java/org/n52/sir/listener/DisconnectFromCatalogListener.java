/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */

package org.n52.sir.listener;

import org.n52.sir.SirConfigurator;
import org.n52.sir.SirConstants;
import org.n52.sir.catalog.ICatalogStatusHandler;
import org.n52.sir.ds.IDAOFactory;
import org.n52.sir.ds.IDisconnectFromCatalogDAO;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.request.AbstractSirRequest;
import org.n52.sir.request.SirDisconnectFromCatalogRequest;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.n52.sir.response.SirDisconnectFromCatalogResponse;
import org.n52.sir.util.jobs.IJobScheduler;
import org.n52.sir.util.jobs.IJobSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schulte
 * 
 */
public class DisconnectFromCatalogListener implements ISirRequestListener {

    private static Logger log = LoggerFactory.getLogger(DisconnectFromCatalogListener.class);

    private static final String OPERATION_NAME = SirConstants.Operations.DisconnectFromCatalog.name();

    private IDisconnectFromCatalogDAO disconFromCatDao;

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
        IJobSchedulerFactory schedulerFact = SirConfigurator.getInstance().getJobSchedulerFactory();
        IJobScheduler scheduler = schedulerFact.getJobScheduler();

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
            scheduler.cancel(connectionID);

            response.setCatalogUrl(disconFromCatReq.getCswURL());

            // set runtime status info
            ICatalogStatusHandler runtimeHandler = SirConfigurator.getInstance().getCatalogStatusHandler();
            runtimeHandler.setStatus(connectionID, "Disconnected from catalog " + disconFromCatReq.getCswURL());

            return response;
        }
        catch (OwsExceptionReport e) {
            return new ExceptionResponse(e.getDocument());
        }
    }

}
