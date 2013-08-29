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

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.xmlbeans.XmlException;

import com.google.gson.Gson;
import com.google.inject.servlet.RequestScoped;
import com.sun.jersey.multipart.FormDataParam;

@Path("/convert")
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
