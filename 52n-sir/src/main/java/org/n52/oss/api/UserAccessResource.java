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
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.n52.sir.SirConfigurator;
import org.n52.sir.ds.IUserAccountDAO;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;

@Path("/api/user")
@RequestScoped
public class UserAccessResource {

	private SirConfigurator config;

	@Inject
	public UserAccessResource(SirConfigurator config) {
		this.config = config;
	}

	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	public Response authenticate(@FormParam("username") String user,
			@FormParam("password") String password) {
		try {
			String token = this.config.getFactory().userAccountDAO()
					.authenticateUser(user, password);
			if (token == null)
				return Response.ok("{status:fail}").build();
			else
				return Response.ok("{auth_token:" + token + "}").build();
		} catch (Exception e) {
			return Response.status(500).build();
		}
	}

}
