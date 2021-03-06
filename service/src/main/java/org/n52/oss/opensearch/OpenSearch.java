/**
 * Copyright 2013 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.n52.oss.opensearch;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.n52.oss.api.ApiPaths;
import org.n52.oss.config.ApplicationConstants;
import org.n52.oss.opensearch.listeners.OpenSearchListener;
import org.n52.oss.sir.SirConstants;
import org.n52.oss.sir.api.SirBoundingBox;
import org.n52.oss.sir.api.SirSearchCriteria;
import org.n52.oss.sir.api.SirSearchResultElement;
import org.n52.sir.listener.SearchSensorListener;
import org.n52.sir.opensearch.RequestDismantler;
import org.n52.sir.request.SirSearchSensorRequest;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.n52.sir.response.SirSearchSensorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;

/**
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 */
@Path(ApiPaths.OPENSEARCH_PATH)
@RequestScoped
public class OpenSearch {

    private static Logger log = LoggerFactory.getLogger(OpenSearch.class);

    private OpenSearchConfigurator configurator;

    private RequestDismantler dismantler;

    private HashMap<String, OpenSearchListener> listeners;

    private SearchSensorListener sensorSearcher;

    private URI uri;

    @Inject
    public OpenSearch(ApplicationConstants constants,
                      SearchSensorListener listener,
                      OpenSearchConfigurator config,
                      Set<OpenSearchListener> listeners,
                      @Context
                      UriInfo uri) {
        this.uri = uri.getAbsolutePath();

        this.sensorSearcher = listener;
        this.sensorSearcher.setEncodeURLs(false);

        this.configurator = config;
        this.dismantler = new RequestDismantler();

        this.listeners = new HashMap<>();
        for (OpenSearchListener l : listeners) {
            // setting the endpoint and home URL at runtime based on UriInfo - not nice for object
            // instantiation, but less fixed configuration
            l.setOpenSearchEndpoint(this.uri);
            URI homeUri = uri.getBaseUri();
            l.setHomeURI(homeUri);
            this.listeners.put(l.getMimeType(), l);

            log.debug("Added listener for {}:\t{}", l.getMimeType(), l);
        }

        log.info("NEW {} based on {} running at {}", this, constants, this.uri);
    }

    // TODO get parameters using @QueryParam and write own param classes for convenience, see
    // http://jersey.java.net/documentation/latest/user-guide.html#d0e1432
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response json(@HeaderParam(HttpHeaders.ACCEPT)
    String acceptHeader, @Context
    UriInfo uriInfo) {
        // TODO create response subclass for exception reports

        log.debug("****** (GET) Connected: {}", uriInfo.getRequestUri());
        log.debug("Accept header: {}", acceptHeader);
        MultivaluedMap<String, String> params = uriInfo.getQueryParameters();
        OpenSearchListener l = this.listeners.get(MediaType.APPLICATION_JSON);

        try {
            ISirResponse response = search(params);

            if (response instanceof SirSearchSensorResponse) {
                SirSearchSensorResponse sssr = (SirSearchSensorResponse) response;
                Collection<SirSearchResultElement> searchResult = sssr.getSearchResultElements();

                return l.createResponse(searchResult, params);
            }
            else if (response instanceof ExceptionResponse) {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(response).build();
            }
            else {
                log.error("Unhandled response: {}", response);
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(response).build();
            }
        }
        catch (Exception e) {
            log.error("Unhandled exception in doGet: ", e);
            return Response.serverError().entity(e).build();
        }
    }

    // TODO get parameters using @QueryParam and write own param classes for convenience, see
    // http://jersey.java.net/documentation/latest/user-guide.html#d0e1432
    @GET
    @Produces(OpenSearchConstants.APPLICATION_VND_KML)
    public Response kml(@HeaderParam(HttpHeaders.ACCEPT)
    String acceptHeader, @Context
    UriInfo uriInfo) {
        log.debug("****** (GET) Connected: {}", uriInfo.getRequestUri());
        log.debug("Accept header: {}", acceptHeader);
        MultivaluedMap<String, String> params = uriInfo.getQueryParameters();
        OpenSearchListener l = this.listeners.get(OpenSearchConstants.APPLICATION_VND_KML);

        try {
            ISirResponse response = search(params);

            if (response instanceof SirSearchSensorResponse) {
                SirSearchSensorResponse sssr = (SirSearchSensorResponse) response;
                Collection<SirSearchResultElement> searchResult = sssr.getSearchResultElements();

                return l.createResponse(searchResult, params);
            }
            else if (response instanceof ExceptionResponse) {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(response).build();
            }
            else {
                log.error("Unhandled response: {}", response);
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(response).build();
            }
        }
        catch (Exception e) {
            log.error("Unhandled exception in kml: ", e);
            return Response.serverError().entity(e).build();
        }
    }

    // private String detectResponseFormat(String acceptHeader, MultivaluedMap<String, String> params) {
    // String formatParameter = params.getFirst(OpenSearchConstants.FORMAT_PARAM);
    // log.debug("URL format parameter for {} is {}", OpenSearchConstants.FORMAT_PARAM, formatParameter);
    //
    // String responseFormat = null;
    // if (acceptHeader == null || acceptHeader.isEmpty())
    // responseFormat = OpenSearchConstants.X_DEFAULT_MIME_TYPE;
    // else
    // responseFormat = acceptHeader;
    //
    // // allow manual override
    // if (formatParameter != null) {
    // log.debug("Header ({}) is overridden by format parameter ({}).", acceptHeader, formatParameter);
    // responseFormat = formatParameter;
    // }
    //
    // return responseFormat;
    // }

    private ISirResponse search(MultivaluedMap<String, String> params) {
        SirSearchCriteria searchCriteria = createSearchCriteria(params);

        SirSearchSensorRequest searchRequest = createSearchRequest(searchCriteria);

        ISirResponse response = this.sensorSearcher.receiveRequest(searchRequest);
        return response;
    }

    private SirSearchSensorRequest createSearchRequest(SirSearchCriteria searchCriteria) {
        SirSearchSensorRequest request = new SirSearchSensorRequest();
        request.setSimpleResponse(true);
        request.setVersion(SirConstants.SERVICE_VERSION_0_3_1);
        request.setSearchCriteria(searchCriteria);

        log.trace("Request: {}", request);
        return request;
    }

    private SirSearchCriteria createSearchCriteria(MultivaluedMap<String, String> params) {
        String query = params.getFirst(OpenSearchConstants.QUERY_PARAM);
        log.debug("Creating search for query '{}' with parameters: {}",
                  query,
                  Arrays.toString(params.entrySet().toArray()));

        SirSearchCriteria searchCriteria = new SirSearchCriteria();

        // handle missing query parameter, can be the case if just using geo extension...
        if (params == null || params.isEmpty()) {
            query = "";
            log.debug("No search text given.");
        }

        /*
         * Geo Extension: http://www.opensearch.org/Specifications/OpenSearch/Extensions/Geo/1.0/Draft_2
         */
        SirBoundingBox boundingBox = null;
        /*
         * if (this.dismantler.requestContainsGeoParameters(req)) { boundingBox =
         * this.dismantler.getBoundingBox(req);
         * log.info("Geo extension used: bounding box {} from query {} (source: {})", new Object[]
         * {boundingBox, req.getQueryString(), req.getRemoteAddr()}); } else
         * log.info("Searching with query {} (source: {})", new Object[] {req.getQueryString(),
         * req.getRemoteAddr()});
         */
        if (params.containsKey(OpenSearchConstants.BOX_PARAM)) {
            String bbox = params.getFirst(OpenSearchConstants.BOX_PARAM);
            String[] s = bbox.split(",");
            boundingBox = new SirBoundingBox(Double.parseDouble(s[2]),
                                             Double.parseDouble(s[1]),
                                             Double.parseDouble(s[0]),
                                             Double.parseDouble(s[3]));
            log.debug("Geo extension used: {}", boundingBox);
        }

        String lat = null;
        String lng = null;
        String radius = null;
        if (params.containsKey(OpenSearchConstants.LAT_PARAM) && params.containsKey(OpenSearchConstants.LON_PARAM)
                && params.containsKey(OpenSearchConstants.RADIUS_PARAM)) {
            lat = params.getFirst(OpenSearchConstants.LAT_PARAM);
            lng = params.getFirst(OpenSearchConstants.LON_PARAM);
            radius = params.getFirst(OpenSearchConstants.RADIUS_PARAM);
        }

        /*
         * Time extension: http://www.opensearch.org/Specifications/OpenSearch/Extensions/Time/1.0/Draft_1
         */
        String start = null;
        String end = null;
        /*
         * if (this.dismantler.requestContainsTime(req)) { Calendar[] startEnd =
         * this.dismantler.getStartEnd(req); start = startEnd[0]; end = startEnd[1];
         * log.debug("Time extension used: {} - {}", start, end); }
         */
        if (params.containsKey(OpenSearchConstants.TIME_START_PARAM)) {
            // contains a temporal query
            log.debug(params.getFirst(OpenSearchConstants.TIME_START_PARAM));
            start = params.getFirst(OpenSearchConstants.TIME_START_PARAM);
            end = params.getFirst(OpenSearchConstants.TIME_END_PARAM);
            log.debug("Time extension used: {} - {}", start, end);
        }

        if ( !query.isEmpty()) {
            ArrayList<String> searchTexts = new ArrayList<>();
            searchTexts.add(query);
            searchCriteria.setSearchText(searchTexts);
        }

        if (boundingBox != null)
            searchCriteria.setBoundingBox(boundingBox);

        if (start != null && end != null) {
            searchCriteria.setDtend(end);
            searchCriteria.setDtstart(start);
        }
        if (lat != null && lng != null && radius != null) {
            searchCriteria.setLat(lat);
            searchCriteria.setLng(lng);
            searchCriteria.setRadius(radius);
        }

        log.debug("Search criteria: {}", searchCriteria);
        return searchCriteria;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("OpenSearch [");
        if (this.configurator != null) {
            builder.append("configurator=");
            builder.append(this.configurator);
            builder.append(", ");
        }
        if (this.dismantler != null) {
            builder.append("dismantler=");
            builder.append(this.dismantler);
            builder.append(", ");
        }
        if (this.listeners != null) {
            builder.append("listeners=");
            builder.append(this.listeners);
            builder.append(", ");
        }
        if (this.sensorSearcher != null) {
            builder.append("sensorSearcher=");
            builder.append(this.sensorSearcher);
            builder.append(", ");
        }
        if (this.uri != null) {
            builder.append("uri=");
            builder.append(this.uri);
        }
        builder.append("]");
        return builder.toString();
    }
}