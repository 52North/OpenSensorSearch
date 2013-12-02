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

package org.n52.sir.listener.harvest;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.request.SirHarvestServiceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tools {

    private static Logger log = LoggerFactory.getLogger(Tools.class);

    public static URI url2Uri(SirHarvestServiceRequest requestP) throws OwsExceptionReport {
        URI uri;
        try {
            uri = new URL(requestP.getServiceUrl()).toURI();
        }
        catch (MalformedURLException e) {
            String msg = "Error creating URI from given service url '" + requestP.getServiceUrl()
                    + "', cannot proceed with harvesting!";
            log.error(msg, e);
            throw new OwsExceptionReport(ExceptionCode.InvalidParameterValue,
                                         "ServiceURL",
                                         "Service url is not a valid URL.");
        }
        catch (URISyntaxException e) {
            String msg = "Error creating URI from given service url '" + requestP.getServiceUrl()
                    + "', cannot proceed with harvesting!";
            log.error(msg, e);
            throw new OwsExceptionReport(ExceptionCode.InvalidParameterValue,
                                         "ServiceURL",
                                         "Service url is not a valid URL.");
        }
        return uri;
    }

}
