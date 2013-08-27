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

package org.n52.sir;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

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
import org.n52.sir.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * 
 * @author Jan Schulte, Daniel Nüst (d.nuest@52north.org)
 * 
 */
@Singleton
public class RequestOperator {

    private static Logger log = LoggerFactory.getLogger(RequestOperator.class);

    private IHttpGetRequestDecoder httpGetDecoder;

    private IHttpPostRequestDecoder httpPostDecoder;

    private HashMap<String, ISirRequestListener> reqListener = new HashMap<>();

    @Inject
    public RequestOperator(SirConfigurator config, Set<ISirRequestListener> listeners) {
        this.httpGetDecoder = SirConfigurator.getInstance().getHttpGetDecoder();
        this.httpPostDecoder = SirConfigurator.getInstance().getHttpPostDecoder();

        for (ISirRequestListener listener : listeners) {
            addRequestListener(listener);
        }

        ArrayList<String> listenerClassnames = config.getInstance().getListenerClassnames();
        addListenersByClassname(listenerClassnames);

        log.info("NEW {}", this);
    }

    private void addRequestListener(ISirRequestListener listener) {
        String name = listener.getOperationName();
        
        if(this.reqListener.containsKey(name))
            log.warn("Replacing listener for {}", name);
        
        this.reqListener.put(name, listener);

        log.debug("Added new request listener for operation {}: {}",
                  listener.getOperationName(),
                  listener.getClass().getName());
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
        catch (OwsExceptionReport e) {
            return new ExceptionResponse(e.getDocument());
        }
        catch (IllegalArgumentException e) {
            log.error("Illegal argument in request: ", e);

            OwsExceptionReport owser = new OwsExceptionReport();
            owser.addCodedException(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                    e.getClass().toString(),
                                    "The request contained an illeagal argument: " + e.getMessage() + "\n\n"
                                            + Tools.getStackTrace(e));
            return new ExceptionResponse(owser.getDocument());
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

    private void addListenersByClassname(Collection<String> listeners) {
        for (String classname : listeners) {
            log.debug("Loading {} by classname", classname);

            try {
                // get Class of the Listener
                @SuppressWarnings("unchecked")
                Class<ISirRequestListener> listenerClass = (Class<ISirRequestListener>) Class.forName(classname);

                Class< ? >[] constrArgs = {};

                Object[] args = {};

                // get Constructor of this class with matching parameter types
                Constructor<ISirRequestListener> constructor = listenerClass.getConstructor(constrArgs);

                addRequestListener(constructor.newInstance(args));
            }
            catch (ClassNotFoundException cnfe) {
                log.error("Error while loading RequestListeners, required class could not be loaded: "
                        + cnfe.toString());
            }
            catch (NoSuchMethodException nsme) {
                log.error("Error while loading RequestListeners," + " no required constructor available: "
                        + nsme.toString());
            }
            catch (InvocationTargetException ite) {
                log.error("The instatiation of a RequestListener failed: " + ite.toString());
            }
            catch (InstantiationException ie) {
                log.error("The instatiation of a RequestListener failed: " + ie.toString());
            }
            catch (IllegalAccessException iace) {
                log.error("The instatiation of a RequestListener failed: " + iace.toString());
            }
        }
    }
}