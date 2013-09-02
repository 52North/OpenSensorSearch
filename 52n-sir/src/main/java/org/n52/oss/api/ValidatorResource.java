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

import java.io.IOException;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.xml.IProfileValidator;
import org.n52.sir.xml.IValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import com.sun.jersey.multipart.FormDataParam;

@Path("/check")
@RequestScoped
public class ValidatorResource {
	private static Logger log = LoggerFactory
			.getLogger(ValidatorResource.class);
	private IProfileValidator validator;

	@Inject
	public ValidatorResource(IValidatorFactory validatorFactory)
			throws OwsExceptionReport {
		this.validator = validatorFactory
				.getSensorMLProfile4DiscoveryValidator();

	}

	private boolean validateSensorMLDocument(String s) throws XmlException, IOException {
		boolean b = this.validator.validate(XmlObject.Factory.parse(s));
		return b;
	}

	private String returnJSON(String s) {
		try {
			boolean b = validateSensorMLDocument(s);
			if (b)
				return "{status:'valid'}";

			String details = this.validator.getValidationFailuresAsString();
			return "{status:'invalid',error:" + details + "}";

		} catch (XmlException | IOException exception) {
			return "{error:" + exception.getMessage() + "}";
		}
	}

	@POST
	@Path("/sensorML")
	public Response check(@FormParam("sensor") String sensor,
			@FormDataParam("format") String format) {
		if(format.equals("json")){
			return Response.ok(returnJSON(sensor)).build();
		}else{
			//TODO yakoub USA8 for xml
			//
			return Response.ok().build();
		}

	}
}
