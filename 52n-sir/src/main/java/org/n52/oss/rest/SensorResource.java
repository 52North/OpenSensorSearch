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

package org.n52.oss.rest;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.n52.oss.json.Converter;
import org.n52.oss.sir.api.SirSearchResultElement;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.ds.ISearchSensorDAO;
import org.n52.sir.json.SearchResultElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.inject.servlet.RequestScoped;

/**
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 */
@Path("/api/v1/sensors")
@RequestScoped
public class SensorResource {

    private static Logger log = LoggerFactory.getLogger(SensorResource.class);
    private ISearchSensorDAO dao;
    private Converter converter;

    @Inject
    public SensorResource(@Named("full")
    ISearchSensorDAO searchDao) {
        log.info("NEW {}", this);

        this.dao = searchDao;
        this.converter = new Converter();
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getSensor(@PathParam("id")
    String id, @QueryParam("detailed")
    @DefaultValue("false")
    boolean detailed) {
        log.debug("GET sensor with id {}, response is extended: {}", id, detailed);

        try {
            SirSearchResultElement sensor = this.dao.getSensorBySensorID(id, !detailed);
            SearchResultElement converted = this.converter.convert(sensor, detailed);

            return Response.ok(converted).build();
        }
        catch (OwsExceptionReport e) {
            return Response.serverError().entity(e).build();
        }
    }

}
