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

import java.net.MalformedURLException;
import java.net.URL;

public class Util {

    private static final String SERVICE_URL_PROPERTY = "service.url";

    private static final String SIR_URL_PROPERTY_IT = "sir.url.it";

    private static final String OPENSEARCH_URL_PROPERTY_IT = "opensearch.url.it";

    public static URL getServiceURL() throws MalformedURLException {
        String s = System.getProperty(SERVICE_URL_PROPERTY);
        URL url = new URL(s);
        return url;
    }

    public static URL getSIREndpointForIT() throws MalformedURLException {
        String s = System.getProperty(SIR_URL_PROPERTY_IT);
        URL url = new URL(s);
        return url;
    }
    
    public static URL getOpenSearchEndpointForIT() throws MalformedURLException {
        String s = System.getProperty(OPENSEARCH_URL_PROPERTY_IT);
        URL url = new URL(s);
        return url;
    }

}
