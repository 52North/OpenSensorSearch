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

import org.n52.oss.sir.SirConstants;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class ListenersTools {

    private static Logger log = LoggerFactory.getLogger(ListenersTools.class);

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

}
