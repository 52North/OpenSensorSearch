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

import java.net.URI;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@Path(ApiPaths.API)
@Api(value = ApiPaths.API, description = "Base resource of the API linking the different versions and endpoints.")
@Singleton
public class ApiResource {

    private static Logger log = LoggerFactory.getLogger(ApiResource.class);

    private URI baseUri;

    @Inject
    public ApiResource(@Context
    UriInfo uri) {
        this.baseUri = uri.getBaseUri();

        log.info("NEW {} @ {}", this, this.baseUri);
    }

    @GET
    @ApiOperation(value = "List of available API versions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getApiRoot() {
        StringBuilder sb = new StringBuilder();
        sb.append(" { ");
        sb.append("\"currentVersion\" : \"");
        sb.append(this.baseUri);
        sb.append(ApiPaths.API_PATH);
        sb.append("\"");
        sb.append(" , ");
        sb.append("\"v1\" : \"");
        sb.append(this.baseUri);
        sb.append(ApiPaths.API_PATH);
        sb.append("\"");
        sb.append(" } ");

        return Response.ok(sb.toString()).build();
    }

    @GET
    @Path(ApiPaths.API_VERSION)
    @ApiOperation(value = "List of available endpoints of this version")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVersionRoot() {
        StringBuilder sb = new StringBuilder();
        sb.append(" { ");

        // TODO this must work dynamically, i.e. the respective modules must register themselves!

        sb.append("\"statistics\" : \"");
        sb.append(this.baseUri);
        sb.append(ApiPaths.STATISTICS_PATH);
        sb.append("\"");
        sb.append(" , ");

        sb.append("\"sensors\" : \"");
        sb.append(this.baseUri);
        sb.append(ApiPaths.SENSORS_PATH);
        sb.append("\"");
        sb.append(" , ");

        sb.append("\"conversion\" : \"");
        sb.append(this.baseUri);
        sb.append(ApiPaths.TRANSFORMATION_PATH);
        sb.append("\"");
        sb.append(" , ");

        sb.append("\"check\" : \"");
        sb.append(this.baseUri);
        sb.append(ApiPaths.CHECK_PATH);
        sb.append("\"");
        sb.append(" , ");

        sb.append("\"opensearch\" : \"");
        sb.append(this.baseUri);
        sb.append(ApiPaths.OPENSEARCH_PATH);
        sb.append("\"");

        sb.append(" } ");

        return Response.ok(sb.toString()).build();
    }

}
