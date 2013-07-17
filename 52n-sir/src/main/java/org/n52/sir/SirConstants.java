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

/**
 * @author Jan Schulte
 * 
 */
public class SirConstants {

    /**
     * enum with parameter names for GetCapabilities HTTP GET request
     */
    public enum GetCapGetParams {
        ACCEPTFORMATS, ACCEPTVERSIONS, REQUEST, SECTIONS, SERVICE, UPDATESEQUENCE;
    }

    /**
     * enum with parameters of DescribeSensor HTTP GET request
     */
    public enum GetDescSensorParams {
        REQUEST, SENSORIDINSIR;
    }

    /**
     * enum for all supported request operations by the SIR
     */
    public enum Operations {
        CancelSensorStatusSubscription, ConnectToCatalog, DeleteSensorInfo, DescribeSensor, DisconnectFromCatalog, GetCapabilities, GetSensorStatus, HarvestService, InsertSensorInfo, InsertSensorStatus, RenewSensorStatusSubscription, SearchSensor, SubscribeSensorStatus, UpdateSensorDescription
    }

    public static final String CHARSET_NAME = "UTF-8";

    /**
     * Constant for the content type of the response
     */
    public static final String CONTENT_TYPE_XML = "text/xml";

    /**
     * Name of the DescribeSensor operation in a capabitilities document
     */
    public static final String DESCRIBE_SENSOR_OPERATION_NAME = "DescribeSensor";

    /**
     * Constant for the http get request REQUEST parameter
     */
    public static final String GETREQUESTPARAM = "REQUEST";

    /**
     * 
     */
    public static final String GETVERSIONPARAM = "version";

    /**
     * Service type for IOOSCatalog XML sensor description file
     */
    public static final String IOOSCATAL0G_SERVICE_TYPE = "IOOSCatalog";

    /**
     * 
     */
    public static final String REQUEST_CONTENT_CHARSET = CHARSET_NAME;

    public static final String REQUEST_CONTENT_TYPE = "text/xml";

    /**
     * 
     */
    public static final String RESPONSE_CONTENT_CHARSET = CHARSET_NAME;

    /**
     * Constant for the service name of the SIR
     */
    public static final String SERVICE_NAME = "SIR";

    /**
     * 
     */
    public static final String SERVICE_VERSION_0_3_0 = "0.3.0";

    /**
     * 
     */
    public static final String SERVICE_VERSION_0_3_1 = "0.3.1";

    /**
     * 
     */
    public static final String SERVICE_VERSION_0_3_2 = "0.3.2";

    /**
     * 
     */
    public static final Object SERVICEPARAM = "service";

    /**
     * Service type for Sensor Observation Service
     */
    public static final String SOS_SERVICE_TYPE = "SOS";

    /**
     * 
     */
    public static final String SOS_VERSION = "1.0.0";

    /**
     * Service type for Sensor Planning Service
     */
    public static final String SPS_SERVICE_TYPE = "SPS";

}