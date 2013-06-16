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
package org.n52.sir.util;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import org.n52.sir.SirConfigurator;
import org.n52.sir.SirConstants;
import org.n52.sir.ows.OwsExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class ListenersTools {

    private static Logger log = LoggerFactory.getLogger(ListenersTools.class);

    /**
     * 
     * @param service
     * @throws OwsExceptionReport
     */
    public static void checkServiceParameter(String service) throws OwsExceptionReport {
        if (service == null || service.equalsIgnoreCase("NOT_SET")) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.MissingParameterValue,
                                 "service",
                                 "The mandatory parameter 'service' was not found in the request!");
            log.error("Service parameter was not found in the request");
            throw se;
        }

        if ( !service.equalsIgnoreCase(SirConstants.SERVICE_NAME)) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 "service",
                                 "The value of the mandatory parameter 'service' must be '" + SirConstants.SERVICE_NAME
                                         + "'. Delivered value was: " + service);
            log.error("Service parameter was wrong");
            throw se;
        }
    }

    /**
     * 
     * @param version
     * @throws OwsExceptionReport
     */
    public static void checkVersionParameter(String version) throws OwsExceptionReport {
        String[] acceptedServiceVersions = SirConfigurator.getInstance().getAcceptedServiceVersions();
        List<String> versions = Arrays.asList(acceptedServiceVersions);

        if (version == null || !versions.contains(version)) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 "version",
                                 "The Parameter 'version' does not contain the version of this SIR: '"
                                         + SirConfigurator.getInstance().getServiceVersion() + "'");
            log.error("The accepted versions parameter is incorrect.", se);
            throw se;
        }
    }

    /**
     * 
     * creates a GET request to retrieve the sensor description of the given sensor,
     * 
     * @param sensorIdInSir
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String createSensorDescriptionURL(String sensorIdInSir) throws UnsupportedEncodingException {
        SirConfigurator sc = SirConfigurator.getInstance();

        StringBuilder sb = new StringBuilder();
        sb.append(sc.getServiceUrl());
        sb.append("?");
        sb.append(SirConstants.SERVICEPARAM);
        sb.append("=");
        sb.append(SirConstants.SERVICE_NAME);
        sb.append("&");
        sb.append(SirConstants.GETVERSIONPARAM);
        sb.append("=");
        sb.append(sc.getServiceVersion());
        sb.append("&");
        sb.append(SirConstants.GETREQUESTPARAM);
        sb.append("=");
        sb.append(SirConstants.Operations.DescribeSensor.name());
        sb.append("&");
        sb.append(SirConstants.GetDescSensorParams.SENSORIDINSIR.name());
        sb.append("=");
        sb.append(sensorIdInSir);

        return sb.toString();
    }
}
