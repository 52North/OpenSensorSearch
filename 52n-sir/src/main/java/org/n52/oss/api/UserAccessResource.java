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

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.n52.sir.SirConfigurator;
import org.n52.sir.ds.IUserAccountDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;

@Path("/api/user")
@RequestScoped
public class UserAccessResource {
    protected static Logger log = LoggerFactory.getLogger(UserAccessResource.class);
    private SirConfigurator config;

    @Inject
    public UserAccessResource(SirConfigurator config) {
        this.config = config;
    }

    @POST
    @Path("/login")
    public Response authenticate(@FormParam("username")
    String user, @FormParam("password")
    String password) {
        log.debug("Authentication requested for user {}", user);

        try {
            IUserAccountDAO dao = this.config.getInstance().getFactory().userAccountDAO();
            String token = dao.authenticateUser(user, password);
            log.debug("Token for user {} is {}", user, token);

            if (token == null)
                return Response.ok("{status:fail}").build();

            return Response.ok("{auth_token:'" + token + "'}").build();
        }
        catch (Exception e) {
            return Response.status(500).entity(e).build();
        }
    }

}
