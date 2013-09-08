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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.n52.sir.SirConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import com.wordnik.swagger.annotations.Api;

@Path("/api/v1/statistics")
@RequestScoped
public class StatisticsResource {
        protected static Logger log = LoggerFactory.getLogger(UserAccessResource.class);
        private SirConfigurator config;

        @Inject
        public StatisticsResource(SirConfigurator config){
            this.config=config;
        }

        @GET
        @Path("/sensors")
        public Response getSensors() {
            //TODO :Daniel implement this to return the number of sensors
            return Response.ok("{sensors:0}").build();
        }
        @GET
        @Path("/phenomena")
        public Response getNumberOfPhenomena() {
            //TODO :Daniel implement this to return the number of phenomena
            return Response.ok("{phenomena:0}").build();
        }
        @GET
        @Path("/services")
        public Response getNumberOfServices() {
            //TODO :Daniel implement this to return the number of services
            return Response.ok("{services:0}").build();
        }
       
}
