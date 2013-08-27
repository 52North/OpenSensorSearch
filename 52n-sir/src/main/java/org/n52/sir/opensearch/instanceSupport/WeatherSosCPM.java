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
package org.n52.sir.opensearch.instanceSupport;

import org.n52.sir.opensearch.ICapabilitiesPermalinkMapper;

public class WeatherSosCPM implements ICapabilitiesPermalinkMapper {

    @Override
    public String getServiceURL() {
        return "http://v-swe.uni-muenster.de:8080/WeatherSOS/sos";
    }

    @Override
    public boolean isValidCombinationProcFoi(String procedure, String featureOfInterest) {
        // find and compare IDs
        String procId = procedure.substring(procedure.lastIndexOf(":") + 1, procedure.length());
        String foiId = featureOfInterest.substring(featureOfInterest.lastIndexOf(":") + 1, featureOfInterest.length());

        return procId.equals(foiId);
    }

}
