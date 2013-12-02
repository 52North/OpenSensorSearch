
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

}
