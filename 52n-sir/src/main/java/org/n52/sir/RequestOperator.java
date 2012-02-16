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
package org.n52.sir;

import java.util.HashMap;

import org.n52.sir.decode.IHttpGetRequestDecoder;
import org.n52.sir.decode.IHttpPostRequestDecoder;
import org.n52.sir.listener.ConnectToCatalogListener;
import org.n52.sir.listener.DeleteSensorInfoListener;
import org.n52.sir.listener.DescribeSensorListener;
import org.n52.sir.listener.DisconnectFromCatalogListener;
import org.n52.sir.listener.GetCapabilitiesListener;
import org.n52.sir.listener.GetSensorStatusListener;
import org.n52.sir.listener.HarvestServiceListener;
import org.n52.sir.listener.ISirRequestListener;
import org.n52.sir.listener.InsertSensorInfoListener;
import org.n52.sir.listener.InsertSensorStatusListener;
import org.n52.sir.listener.SearchSensorListener;
import org.n52.sir.listener.UpdateSensorDescriptionListener;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.request.AbstractSirRequest;
import org.n52.sir.request.SirConnectToCatalogRequest;
import org.n52.sir.request.SirDeleteSensorInfoRequest;
import org.n52.sir.request.SirDescribeSensorRequest;
import org.n52.sir.request.SirDisconnectFromCatalogRequest;
import org.n52.sir.request.SirGetCapabilitiesRequest;
import org.n52.sir.request.SirGetSensorStatusRequest;
import org.n52.sir.request.SirHarvestServiceRequest;
import org.n52.sir.request.SirInsertSensorInfoRequest;
import org.n52.sir.request.SirInsertSensorStatusRequest;
import org.n52.sir.request.SirSearchSensorRequest;
import org.n52.sir.request.SirSubscriptionRequest;
import org.n52.sir.request.SirUpdateSensorDescriptionRequest;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Jan Schulte, Daniel Nüst (d.nuest@52north.org)
 *
 */
public class RequestOperator {

    private static Logger log = LoggerFactory.getLogger(RequestOperator.class);

    /**
     * container of the requestListener
     */
    private HashMap<String, ISirRequestListener> reqListener;

    /**
     * decoder for http get requests
     */
    private IHttpGetRequestDecoder httpGetDecoder;

    /**
     * decoder for http post requests
     */
    private IHttpPostRequestDecoder httpPostDecoder;

    /**
     * constructor
     */
    public RequestOperator() {
        this.httpGetDecoder = SirConfigurator.getInstance().getHttpGetDecoder();
        this.httpPostDecoder = SirConfigurator.getInstance().getHttpPostDecoder();
    }

    /**
     * adds a requestListener to the listener collection
     * 
     * @param listener
     *        the requestListener which should be added
     */
    public void addRequestListener(ISirRequestListener listener) {
        if (this.reqListener == null) {
            this.reqListener = new HashMap<String, ISirRequestListener>();
        }
        this.reqListener.put(listener.getOperationName(), listener);
    }

    /**
     * Checks the get request query string and returns the related response
     * 
     * @param queryString
     *        the post request string
     * @return the related ISirResponse
     */
    public ISirResponse doGetOperation(String queryString) {
        ISirResponse response = null;

        AbstractSirRequest request = null;

        try {
            request = this.httpGetDecoder.receiveRequest(queryString);
        }
        catch (OwsExceptionReport se) {
            return new ExceptionResponse(se.getDocument());
        }

        // getCapabilities request
        if (request instanceof SirGetCapabilitiesRequest) {
            GetCapabilitiesListener capListener = (GetCapabilitiesListener) this.reqListener.get(SirConstants.Operations.GetCapabilities.name());
            log.info("Listener: " + capListener);
            response = capListener.receiveRequest(request);
        }

        // describeSensor request
        if (request instanceof SirDescribeSensorRequest) {
            DescribeSensorListener descSensListener = (DescribeSensorListener) this.reqListener.get(SirConstants.Operations.DescribeSensor.name());
            log.info("Listener: " + descSensListener);
            response = descSensListener.receiveRequest(request);
        }

        if (request == null) {
            log.error("Invalid Get request!");
            OwsExceptionReport se = new OwsExceptionReport(OwsExceptionReport.ExceptionLevel.DetailedExceptions);
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                 "HttpGetRequestDecoder.receiveRequest()",
                                 "Invalid Get request!");
            return new ExceptionResponse(se.getDocument());
        }

        return response;
    }

    /**
     * Checks the post request query string and returns the related response
     * 
     * @param inputString
     *        the post request document
     * @return Returns the related ISirResponse
     */
    public ISirResponse doPostOperation(String inputString) {
        ISirResponse response = null;
        AbstractSirRequest request = null;

        try {
            request = this.httpPostDecoder.receiveRequest(inputString);
        }
        catch (OwsExceptionReport se) {
            return new ExceptionResponse(se.getDocument());
        }

        // getCapabilities request
        if (request instanceof SirGetCapabilitiesRequest) {
            GetCapabilitiesListener capListener = (GetCapabilitiesListener) this.reqListener.get(SirConstants.Operations.GetCapabilities.name());
            response = capListener.receiveRequest(request);
            if ( ! (response instanceof ExceptionResponse)) {
                if (log.isDebugEnabled())
                    log.debug("GetCapabilities operation executed successfully!");
            }
        }

        // harvestService request
        else if (request instanceof SirHarvestServiceRequest) {
            HarvestServiceListener harvServListener = (HarvestServiceListener) this.reqListener.get(SirConstants.Operations.HarvestService.name());
            response = harvServListener.receiveRequest(request);
            if ( ! (response instanceof ExceptionResponse)) {
                if (log.isDebugEnabled())
                    log.debug("HarvestService operation executed successfully!");
            }
        }

        // updatesensordescription request
        else if (request instanceof SirUpdateSensorDescriptionRequest) {
            UpdateSensorDescriptionListener updSensDescrListener = (UpdateSensorDescriptionListener) this.reqListener.get(SirConstants.Operations.UpdateSensorDescription.name());
            response = updSensDescrListener.receiveRequest(request);
            if ( ! (response instanceof ExceptionResponse)) {
                if (log.isDebugEnabled())
                    log.debug("UpdateSensorDescription operation executed successfully!");
            }
        }

        // describeSensor request
        else if (request instanceof SirDescribeSensorRequest) {
            DescribeSensorListener descSensListener = (DescribeSensorListener) this.reqListener.get(SirConstants.Operations.DescribeSensor.name());
            response = descSensListener.receiveRequest(request);
            if ( ! (response instanceof ExceptionResponse)) {
                if (log.isDebugEnabled())
                    log.debug("DescribeSensor operation executed successfully!");
            }
        }

        // insertSensorStatus request
        else if (request instanceof SirInsertSensorStatusRequest) {
            InsertSensorStatusListener insSensStatListener = (InsertSensorStatusListener) this.reqListener.get(SirConstants.Operations.InsertSensorStatus.name());
            response = insSensStatListener.receiveRequest(request);
            if ( ! (response instanceof ExceptionResponse)) {
                if (log.isDebugEnabled())
                    log.debug("InsertSensorStatus operation executed successfully!");
            }
        }

        // insertSensorInfo request
        else if (request instanceof SirInsertSensorInfoRequest) {
            InsertSensorInfoListener insSensInfoListener = (InsertSensorInfoListener) this.reqListener.get(SirConstants.Operations.InsertSensorInfo.name());
            response = insSensInfoListener.receiveRequest(request);
            if ( ! (response instanceof ExceptionResponse)) {
                if (log.isDebugEnabled())
                    log.debug("InsertSensorInfo operation executed successfully!");
            }
        }

        // deleteSensorInfo request
        else if (request instanceof SirDeleteSensorInfoRequest) {
            DeleteSensorInfoListener deleteSensorInfoListener = (DeleteSensorInfoListener) this.reqListener.get(SirConstants.Operations.DeleteSensorInfo.name());
            response = deleteSensorInfoListener.receiveRequest(request);
            if ( ! (response instanceof ExceptionResponse)) {
                if (log.isDebugEnabled())
                    log.debug("DeleteSensorInfo operation executed successfully!");
            }
        }

        // searchSensor request
        else if (request instanceof SirSearchSensorRequest) {
            SearchSensorListener searchSensListener = (SearchSensorListener) this.reqListener.get(SirConstants.Operations.SearchSensor.name());
            response = searchSensListener.receiveRequest(request);
            if ( ! (response instanceof ExceptionResponse)) {
                if (log.isDebugEnabled())
                    log.debug("SearchSensor operation executed successfully!");
            }
        }

        // getSensorStatus request
        else if (request instanceof SirGetSensorStatusRequest) {
            GetSensorStatusListener getSensStatListener = (GetSensorStatusListener) this.reqListener.get(SirConstants.Operations.GetSensorStatus.name());
            response = getSensStatListener.receiveRequest(request);
            if ( ! (response instanceof ExceptionResponse)) {
                if (log.isDebugEnabled())
                    log.debug("GetSensorStatus operation executed successfully!");
            }
        }

        // connectToCatalog request
        else if (request instanceof SirConnectToCatalogRequest) {
            ConnectToCatalogListener conCatListener = (ConnectToCatalogListener) this.reqListener.get(SirConstants.Operations.ConnectToCatalog.name());
            response = conCatListener.receiveRequest(request);
            if ( ! (response instanceof ExceptionResponse)) {
                if (log.isDebugEnabled())
                    log.debug("ConnectToCatalog operation executed successfully!");
            }
        }

        // disconnectFromCatalog request
        else if (request instanceof SirDisconnectFromCatalogRequest) {
            DisconnectFromCatalogListener disCatListener = (DisconnectFromCatalogListener) this.reqListener.get(SirConstants.Operations.DisconnectFromCatalog.name());
            response = disCatListener.receiveRequest(request);
            if ( ! (response instanceof ExceptionResponse)) {
                if (log.isDebugEnabled())
                    log.debug("DisconnectionFromCatalog operation executed successfully!");
            }
        }

        // subscription requests wrapper
        else if (request instanceof SirSubscriptionRequest) {
            SirSubscriptionRequest subscription = (SirSubscriptionRequest) request;

            ISirRequestListener subscriptionListener = this.reqListener.get(subscription.getName());
            response = subscriptionListener.receiveRequest(request);
            if ( ! (response instanceof ExceptionResponse)) {
                if (log.isDebugEnabled())
                    log.debug("Subscription operation operation executed, not implemented though!");
            }
        }

        if (request == null) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                 null,
                                 "The request was sent in an unknown format or is invalid!");
            log.error("Request is unknown!", se);
            return new ExceptionResponse(se.getDocument());

        }

        return response;
    }
}