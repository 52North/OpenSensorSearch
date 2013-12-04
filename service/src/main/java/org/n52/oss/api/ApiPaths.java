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

package org.n52.oss.api;

public abstract class ApiPaths {

    public static final String API = "api";

    public static final String API_VERSION = "v1";

    public static final String API_PATH = API + "/" + API_VERSION;

    public static final String STATISTICS_PATH = API_PATH + "/statistics";

    public static final String STATS_SENSORS = "/sensors";

    public static final String STATS_SERVICES = "/services";

    public static final String STATS_PHENOMENA = "/phenomena";

    public static final String TRANSFORMATION_PATH = API_PATH + "/convert";

    public static final String USER_PATH = API_PATH + "/user";

    public static final String CHECK_PATH = API_PATH + "/check";

    public static final String CHECK_SENSORML = "/sml";

    public static final String SENSORS_PATH = API_PATH + "/sensors";

    public static final String OPENSEARCH_PATH = API_PATH + "/search";

    public static final String AUTOSUGGEST_PATH = API_PATH + "/suggest";

}
