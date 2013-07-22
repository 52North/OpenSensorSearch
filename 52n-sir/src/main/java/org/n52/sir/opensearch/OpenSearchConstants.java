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
package org.n52.sir.opensearch;

import java.util.ArrayList;
import java.util.Collection;

/**
 * TODO see what is constant, and what should be in {@link OpenSearchConfigurator}.
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class OpenSearchConstants {

    public static final String ACCEPT_PARAMETER = "httpAccept";

    public static final String BOX_PARAM = "box";

    public static final String CDATA_END_TAG = "]";

    public static final String CDATA_START_TAG = "![CDATA[";

    public static final double DEFAULT_RADIUS = 1000.0d;

    public static final double EARTH_RADIUS_METERS = 6.3675 * 1000000;

    public static final String GEOMETRY_PARAM = "geometry";

    public static final String LAT_PARAM = "lat";

    public static final String LON_PARAM = "lon";

    public static final int MAX_GET_URL_CHARACTER_COUNT = 2000; // http://stackoverflow.com/questions/417142/what-is-the-maximum-length-of-a-url

    public static final String MIME_TYPE_PLAIN = "text/plain";

    public static final String NAME_PARAM = "name";

    public static final String QUERY_PARAMETER = "q";
    
    public static final String TIME_START_PARAMETER = "dtstart";
    
    public static final String TIME_END_PARAMETER = "dtend";

    public static final String RADIUS_PARAM = "radius";

    public static Collection<String> TIME_SERIES_SERVICE_TYPES = new ArrayList<String>();

    public static final String X_DEFAULT_MIME_TYPE = "text/html";

    static {
        TIME_SERIES_SERVICE_TYPES.add("SOS");
        TIME_SERIES_SERVICE_TYPES.add("OGC:SOS");
    }

}
