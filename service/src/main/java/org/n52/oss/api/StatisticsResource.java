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
/** @author Yakoub
 */

package org.n52.oss.api;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.ds.IGetCapabilitiesDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@Path(ApiPaths.STATISTICS_PATH)
@Api(value = ApiPaths.STATISTICS_PATH, description = "Endpoint of all of the statistics related to sensors in OSS")
@Singleton
public class StatisticsResource {

    private static Logger log = LoggerFactory.getLogger(UserAccessResource.class);

    private IGetCapabilitiesDAO capabilitiesDao;

    private String baseUrl;

    @Inject
    public StatisticsResource(IGetCapabilitiesDAO dao, @Context
    UriInfo uri) {
        this.capabilitiesDao = dao;
        this.baseUrl = uri.getBaseUri() + ApiPaths.STATISTICS_PATH;

        log.debug("NEW {} @ {}", this, this.baseUrl);
    }

    @GET
    @ApiOperation(value = "List of available statistics")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatisticsIndex() {
        StringBuilder sb = new StringBuilder();
        sb.append(" { ");
        sb.append("\"sensors\" : \"");
        sb.append(this.baseUrl);
        sb.append(ApiPaths.STATS_SENSORS);
        sb.append("\"");
        sb.append(" , ");
        sb.append("\"phenomena\" : \"");
        sb.append(this.baseUrl);
        sb.append(ApiPaths.STATS_PHENOMENA);
        sb.append("\"");
        sb.append(" , ");
        sb.append("\"services\" : \"");
        sb.append(this.baseUrl);
        sb.append(ApiPaths.STATS_SERVICES);
        sb.append("\"");
        sb.append(" } ");

        return Response.ok(sb.toString()).build();
    }

    @GET
    @Path(ApiPaths.STATS_SENSORS)
    @ApiOperation(value = "Find the number of sensors stored in OSS")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNumberOfSensors() {
        StringBuilder sb = new StringBuilder();
        sb.append(" { \"sensors\": ");

        try {
            sb.append(this.capabilitiesDao.getSensorCount());
        }
        catch (OwsExceptionReport e) {
            return Response.serverError().entity(e).build();
        }
        sb.append(" }");

        return Response.ok(sb.toString()).build();
    }

    @GET
    @Path(ApiPaths.STATS_PHENOMENA)
    @ApiOperation(value = "Find the number of phenomena stored in OSS")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNumberOfPhenomena() {
        StringBuilder sb = new StringBuilder();
        sb.append(" { \"phenomena\": ");

        try {
            sb.append(this.capabilitiesDao.getPhenomenonCount());
        }
        catch (OwsExceptionReport e) {
            return Response.serverError().entity(e).build();
        }
        sb.append(" }");

        return Response.ok(sb.toString()).build();
    }

    @GET
    @Path(ApiPaths.STATS_SERVICES)
    @ApiOperation(value = "Find the number of services stored in OSS")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNumberOfServices() {
        StringBuilder sb = new StringBuilder();
        sb.append(" { \"services\": ");

        try {
            sb.append(this.capabilitiesDao.getServiceCount());
        }
        catch (OwsExceptionReport e) {
            return Response.serverError().entity(e).build();
        }
        sb.append(" }");

        return Response.ok(sb.toString()).build();
    }

}
