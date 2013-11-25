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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.ds.IGetCapabilitiesDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@Path("/api/v1/statistics")
@Api(value = "/api/v1/statistics", description = "Endpoint of all of the statistics related to sensors in OSS")
@Singleton
public class StatisticsResource {

    private static final String SENSORS_PATH = "/sensors";

    private static final String SERVICES_PATH = "/services";

    private static final String PHENOMENA_PATH = "/phenomena";

    protected static Logger log = LoggerFactory.getLogger(UserAccessResource.class);

    private IGetCapabilitiesDAO capabilitiesDao;

    private String baseUrl;

    @Inject
    public StatisticsResource(IGetCapabilitiesDAO dao, @Named("oss.serviceurl")
    String baseUrl) {
        this.capabilitiesDao = dao;
        this.baseUrl = baseUrl;

        log.debug("NEW {}", this);
    }

    @GET
    @Path("/")
    @ApiOperation(value = "List of available statistics")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatisticsIndex() {
        StringBuilder sb = new StringBuilder();
        sb.append(" { ");
        sb.append("\"sensors\" : \"" + this.baseUrl + "/api/v1/statistics" + SENSORS_PATH + "\"");
        sb.append(" , ");
        sb.append("\"phenomena\" : \"" + this.baseUrl + "/api/v1/statistics" + PHENOMENA_PATH + "\"");
        sb.append(" , ");
        sb.append("\"services\" : \"" + this.baseUrl + "/api/v1/statistics" + SERVICES_PATH + "\"");
        sb.append(" } ");

        return Response.ok(sb.toString()).build();
    }

    @GET
    @Path(SENSORS_PATH)
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
    @Path(PHENOMENA_PATH)
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
    @Path(SERVICES_PATH)
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
