package org.n52.oss.api;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.xmlbeans.XmlException;

import com.google.gson.Gson;
import com.google.inject.servlet.RequestScoped;
import com.sun.jersey.multipart.FormDataParam;

@Path("/check")
@RequestScoped
public class TransformationResource {

	private Gson gson;

	public TransformationResource() {
		this.gson = new Gson();
	}

	private String toJsonString(String sensorML) throws XmlException {
		SensorMLDocument document = SensorMLDocument.Factory.parse(sensorML);
		return gson.toJson(document);
	}
	

	@POST
	@Path("/toJson")
	public Response toJson(@FormParam("sensor") String sensor) {
		try {
			String response = toJsonString(sensor);
			return Response.ok(response).build();
		} catch (XmlException e) {
			return Response.ok("{error:Cannot parse sensorML}").build();
		}
	}
}
