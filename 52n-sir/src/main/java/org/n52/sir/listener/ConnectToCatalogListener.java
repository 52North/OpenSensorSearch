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

import java.net.URL;

import org.n52.sir.SirConfigurator;
import org.n52.sir.SirConstants;
import org.n52.sir.catalog.ICatalog;
import org.n52.sir.catalog.ICatalogConnection;
import org.n52.sir.catalog.ICatalogFactory;
import org.n52.sir.ds.IConnectToCatalogDAO;
import org.n52.sir.ds.IDAOFactory;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.request.AbstractSirRequest;
import org.n52.sir.request.SirConnectToCatalogRequest;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.n52.sir.response.SirConnectToCatalogResponse;
import org.n52.sir.util.jobs.IJobScheduler;
import org.n52.sir.util.jobs.IJobSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schulte
 * 
 */
public class ConnectToCatalogListener implements ISirRequestListener {

    private static Logger log = LoggerFactory.getLogger(ConnectToCatalogListener.class);

    private static final String OPERATION_NAME = SirConstants.Operations.ConnectToCatalog.name();

    private IConnectToCatalogDAO conToCatDao;

    public ConnectToCatalogListener() throws OwsExceptionReport {
        SirConfigurator configurator = SirConfigurator.getInstance();
        IDAOFactory factory = configurator.getFactory();
        try {
            this.conToCatDao = factory.connectToCatalogDAO();
        }
        catch (OwsExceptionReport se) {
            log.error("Error while creating the connectToCatalogDao", se);
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
        return ConnectToCatalogListener.OPERATION_NAME;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.listener.ISirRequestListener#receiveRequest(org.n52.sir.request .AbstractSirRequest)
     */
    @Override
    public ISirResponse receiveRequest(AbstractSirRequest request) {
        if (log.isDebugEnabled())
            log.debug("Receiving ConnectoToCatalogRequest");

        SirConnectToCatalogRequest conToCatReq = (SirConnectToCatalogRequest) request;
        SirConnectToCatalogResponse response = new SirConnectToCatalogResponse();
        String connectionID;

        int pushInterval = conToCatReq.getPushInterval();
        URL url = conToCatReq.getCswUrl();

        IJobSchedulerFactory schedulerFact = SirConfigurator.getInstance().getJobSchedulerFactory();
        IJobScheduler scheduler = schedulerFact.getJobScheduler();

        try {
            ICatalogFactory catFact = SirConfigurator.getInstance().getCatalogFactory(url);
            ICatalog catalog = catFact.getCatalog();

            // check if csw is capable (with getCapabilities and more...)
            boolean b = catalog.ensureSufficientCapabilities();

            if ( !b) {
                log.warn("Catalog does not have sufficient capabilities!");
                OwsExceptionReport oer = new OwsExceptionReport();
                oer.addCodedException(OwsExceptionReport.ExceptionCode.NoApplicableCode,
                                      url.toString(),
                                      "Catalog capabilities not sufficient! Check the log for details.");
                return new ExceptionResponse(oer.getDocument());
            }

            // catalog not needed anymore, will be created again (and checked once more!) by scheduling
            // system.
            catalog = null;

            // only single push
            if (pushInterval == ICatalogConnection.NO_PUSH_INTERVAL) {
                // start connection with the same connectionID
                ICatalogConnection conn = catFact.getCatalogConnection(ICatalogConnection.UNSAVED_CONNECTION_ID,
                                                                       url,
                                                                       ICatalogConnection.NO_PUSH_INTERVAL,
                                                                       ICatalogConnection.NEW_CONNECTION_STATUS);
                scheduler.submit(conn);
                log.info("Submitted single connection:" + conn);
            }
            // with period
            else {
                // check for existing connection in database
                connectionID = this.conToCatDao.getConnectionID(url, pushInterval);

                if (connectionID == null) {
                    // a completely new task, insert connection to database
                    connectionID = this.conToCatDao.insertConnection(conToCatReq.getCswUrl(),
                                                                     conToCatReq.getPushInterval());

                    // change update sequence
                    SirConfigurator.getInstance().newUpdateSequence();

                    // start connection with the same connectionID
                    ICatalogConnection conn = catFact.getCatalogConnection(connectionID,
                                                                           url,
                                                                           pushInterval,
                                                                           ICatalogConnection.NEW_CONNECTION_STATUS);
                    scheduler.submit(conn);

                    log.info("Inserted new connection into database, connection identifier: " + connectionID);
                }
                else {
                    // cancel existing task with connectionID
                    scheduler.cancel(connectionID);

                    // start connection with the same connectionID
                    ICatalogConnection conn = catFact.getCatalogConnection(connectionID,
                                                                           url,
                                                                           pushInterval,
                                                                           ICatalogConnection.NEW_CONNECTION_STATUS);
                    scheduler.submit(conn);

                    // update connection and start timertaks with same ID
                    this.conToCatDao.updateConnection(conToCatReq.getCswUrl(), conToCatReq.getPushInterval());
                    log.info("Updated connection with identifier: " + connectionID);
                }
            }
            // return url in response to show successful completition
            response.setCatalogUrl(url);

            catFact = null;
        }
        catch (OwsExceptionReport se) {
            return new ExceptionResponse(se.getDocument());
        }

        return response;
    }
}
