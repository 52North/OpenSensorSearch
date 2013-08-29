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
