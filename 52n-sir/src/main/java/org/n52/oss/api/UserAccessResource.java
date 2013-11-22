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

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.n52.sir.SirConfigurator;
import org.n52.sir.ds.IUserAccountDAO;
import org.n52.sir.util.SHA1HashGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;

/**
 * @author Yakoub
 * 
 */
@Path("/api/v1/user")
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
            boolean isValid = dao.isAdmin(user);
            boolean isAdmin  = dao.isValid(user);
            log.debug("Token for user {} is {}", user, token);

            if (token == null)
                return Response.ok("{status:fail}").build();

            return Response.ok("{auth_token:'" + token + "',isValid:'"+isValid+"',isAdmin:'"+isAdmin+"'}").build();
        }
        catch (Exception e) {
            return Response.status(500).entity(e).build();
        }
    }
    @POST
    @Path("/register")
    public Response register(@FormParam("username")
    String user, @FormParam("password")
    String password) {
        log.debug("Authentication requested for user {}", user);

        try {
            IUserAccountDAO dao = this.config.getInstance().getFactory().userAccountDAO();
            String passwordHash = new SHA1HashGenerator().generate(password);
            boolean userExists = dao.nameExists(user);
            if(userExists)return Response.ok("{success:'false',reason:'User exists'}").build();
            
            boolean success  = dao.register(user, passwordHash);
            return Response.ok("{success:'"+success+"'}").build();
        }
        catch (Exception e) {
            return Response.status(500).entity(e).build();
        }
    }
    @POST
    @Path("/validate")
    public Response validate(@FormParam("username")
    String user, @FormParam("auth_token")
    String auth_token) {
        log.debug("Authentication requested for user {}", user);

        try {
            IUserAccountDAO dao = this.config.getInstance().getFactory().userAccountDAO();
            String admin_id = dao.getUserIDForToken(auth_token);
            String admin_user_name = dao.userNameForId(admin_id);
            String user_id = dao.getUserIDForUsername(user);
            boolean isAdmin = dao.isAdmin(admin_user_name);
            if(!isAdmin)return Response.status(403).entity("{status:Fail insufficent permission}").build();
            boolean result =  dao.validate(user_id);
            
            return Response.ok("{status:'"+result+"'}").build();
        }
        catch (Exception e) {
            return Response.status(500).entity(e).build();
        }
    }
    
}
