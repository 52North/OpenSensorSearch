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

package org.n52.oss.badges;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.n52.oss.api.ApiPaths;
import org.n52.oss.api.StatisticsResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

@Path(ApiPaths.BADGES_PATH)
@Singleton
public class BadgeResource {

    private static Logger log = LoggerFactory.getLogger(StatisticsResource.class);

    private String baseUrl;

    private BadgeDAO dao;

    private UriInfo baseUriInfo;

    @Inject
    public BadgeResource(BadgeDAO dao, @Context
    UriInfo uri) {
        this.dao = dao;
        this.baseUrl = uri.getBaseUri() + ApiPaths.BADGES_PATH;
        this.baseUriInfo = uri;

        log.debug("NEW {} @ {}", this, this.baseUrl);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBadges() {
        Collection<Badge> allBadges = this.dao.getAllBadges();
        return Response.ok(allBadges).build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBadge(@PathParam("id")
    String id) {
        Badge b = this.dao.getBadge(id);
        return Response.ok(b).build();
    }

    @GET
    @Path("{id}/" + ApiPaths.SUB_SENSORS)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBearingSensors(@PathParam("id")
    String id) {
        Badge b = this.dao.getBadge(id);
        Collection<String> bearingSensors = this.dao.getBearingSensors(b);
        UriBuilder sensor = this.baseUriInfo.getBaseUriBuilder().path(ApiPaths.SENSORS_PATH);
        return returnUriList(bearingSensors, sensor);
    }

    @GET
    @Path("{id}/" + ApiPaths.SUB_SERVICES)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBearingServices(@PathParam("id")
    String id) {
        Badge b = this.dao.getBadge(id);
        Collection<String> bearingServices = this.dao.getBearingServices(b);
        UriBuilder sensor = this.baseUriInfo.getBaseUriBuilder().path(ApiPaths.SERVICES_PATH);
        return returnUriList(bearingServices, sensor);
    }

    private Response returnUriList(Collection<String> ids, UriBuilder builder) {
        ArrayList<URI> uris = new ArrayList<>();

        for (String s : ids) {
            URI uri = builder.path(s).build();
            uris.add(uri);
        }

        return Response.ok(uris).build();
    }

}