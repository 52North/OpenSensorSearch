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

/**
 * @author Jan Schulte
 * 
 */
public class SirConstants {

    /**
     * Constant for the content type of the response
     */
    public static final String CONTENT_TYPE_XML = "text/xml";

    /**
     * Constant for the http get request REQUEST parameter
     */
    public static final String GETREQUESTPARAM = "REQUEST";

    /**
     * Constant for the service name of the SIR
     */
    public static final String SERVICE_NAME = "SIR";

    /**
     * Name of the DescribeSensor operation in a capabitilities document
     */
    public static final String DESCRIBE_SENSOR_OPERATION_NAME = "DescribeSensor";

    /**
     * Service type for Sensor Observation Service
     */
    public static final String SOS_SERVICE_TYPE = "SOS";

    /**
     * Service type for Sensor Planning Service
     */
    public static final String SPS_SERVICE_TYPE = "SPS";

    /**
     * Service type for IOOSCatalog XML sensor description file
     */
    public static final String IOOSCATAL0G_SERVICE_TYPE = "IOOSCatalog";

    /**
     * 
     */
    public static final String GETVERSIONPARAM = "version";

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
    public static final String SOS_VERSION = "1.0.0";

    /**
     * enum for all supported request operations by the SIR
     */
    public enum Operations {
        GetCapabilities, DescribeSensor, GetSensorStatus, HarvestService, InsertSensorInfo, DeleteSensorInfo, InsertSensorStatus, SearchSensor, ConnectToCatalog, DisconnectFromCatalog, UpdateSensorDescription, SubscribeSensorStatus, RenewSensorStatusSubscription, CancelSensorStatusSubscription
    }

    /**
     * enum with parameter names for GetCapabilities HTTP GET request
     */
    public enum GetCapGetParams {
        REQUEST, SERVICE, ACCEPTVERSIONS, SECTIONS, UPDATESEQUENCE, ACCEPTFORMATS;
    }

    /**
     * enum with parameters of DescribeSensor HTTP GET request
     */
    public enum GetDescSensorParams {
        REQUEST, SENSORIDINSIR;
    }

}