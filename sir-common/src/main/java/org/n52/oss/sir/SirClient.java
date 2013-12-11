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
package org.n52.oss.sir;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class SirClient extends Client {

    private static Logger log = LoggerFactory.getLogger(SirClient.class);
    private String sirVersion;

    @Inject
    public SirClient(@Named("oss.sir.sirClient.url")
    String sirUrl, @Named("oss.sir.version")
    String sirVersion) {
        super(sirUrl);
        this.sirVersion = sirVersion;
    }

    /**
     * 
     * creates a GET request to retrieve the sensor description of the given sensor,
     * 
     * @param sensorId
     * @param encodeURLs
     *        for usage in XML documents
     * @return
     * @throws UnsupportedEncodingException
     */
    public String createDescribeSensorURL(String sensorId, boolean encodeURLs) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        sb.append(this.uri);
        sb.append("?");
        sb.append(SirConstants.SERVICEPARAM);
        sb.append("=");
        sb.append(SirConstants.SERVICE_NAME);
        sb.append("&");
        sb.append(SirConstants.GETVERSIONPARAM);
        sb.append("=");
        sb.append(this.sirVersion);
        sb.append("&");
        sb.append(SirConstants.GETREQUESTPARAM);
        sb.append("=");
        sb.append(SirConstants.Operations.DescribeSensor.name());
        sb.append("&");
        sb.append(SirConstants.GetDescSensorParams.SENSORIDINSIR.name());
        sb.append("=");
        sb.append(sensorId);

        log.debug("Created description URL for sensor {}: {}", sensorId, sb.toString());

        // URL must be encoded for usage in XML documents
        if (encodeURLs)
            return URLEncoder.encode(sb.toString(), "UTF-8");

        return sb.toString();
    }

}
