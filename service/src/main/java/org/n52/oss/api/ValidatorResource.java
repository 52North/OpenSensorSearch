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
package org.n52.oss.api;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.xmlbeans.XmlException;
import org.codehaus.jackson.io.JsonStringEncoder;
import org.n52.sir.xml.IProfileValidator;
import org.n52.sir.xml.IProfileValidator.ValidatableFormatAndProfile;
import org.n52.sir.xml.ValidationResult;
import org.n52.sir.xml.ValidatorModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * @author Yakoub
 * 
 */
@Path(ApiPaths.CHECK_PATH)
@Api(value = "/" + ApiPaths.CHECK_PATH, description = "validation of SensorML for future harvesting")
@RequestScoped
public class ValidatorResource {

    private static Logger log = LoggerFactory.getLogger(ValidatorResource.class);

    private Set<IProfileValidator> validators;

    private String idSmlDpVersion1 = "sml_dp_1";

    private static URI baseUri;

    @Inject
    public ValidatorResource(Set<IProfileValidator> validators, @Context
    UriInfo uri) {
        this.validators = validators;
        baseUri = uri.getBaseUri();

        log.info("NEW {}", this);
    }

    @GET
    @ApiOperation(value = "index of the available validations")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIndex() {
        StringBuilder sb = new StringBuilder();

        sb.append("{ \"validations\" : [");
        sb.append(" { \"id\" : \"");
        sb.append(this.idSmlDpVersion1);
        sb.append("\", ");
        sb.append("\"input\" : \"text/xml;subtype='sensorML/1.0.0'\", ");
        sb.append("\"description\" : \"conformity with discovery profile\", ");
        sb.append("\"version\" : 1, ");
        sb.append("\"endpoint\" : \"");
        sb.append(baseUri);
        sb.append(ApiPaths.CHECK_PATH);
        sb.append(ApiPaths.CHECK_SENSORML);
        sb.append("\" } ");
        sb.append("] }");

        return Response.ok(sb.toString()).build();
    }

    @POST
    @Path(ApiPaths.CHECK_SENSORML)
    @ApiOperation(value = "Validates a given SensorML Document")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkReturnJSON(String data, @QueryParam("version")
    @DefaultValue("1")
    String version) {
        log.debug("Checking SensorML (validator version: {}: {}",
                  version,
                  data.substring(0, Math.min(data.length(), 1000)));

        StringBuilder response = new StringBuilder();

        try {
            IProfileValidator smlValidator = getValidator(version);
            ValidationResult vr = validate(data, smlValidator);

            if (vr.isValidated())
                response.append("{\"status\": \"valid\"}");
            else {
                JsonStringEncoder encoder = new JsonStringEncoder();

                response.append("{\"status\" : \"invalid\", \"errors\" : [ ");
                List<String> errors = new ArrayList<>(vr.getValidationFailures());
                for (int i = 0; i < errors.size(); i++) {
                    response.append("{ \"message");
                    response.append(i);
                    response.append("\" : \"");
                    char[] quoted = encoder.quoteAsString(errors.get(i));
                    response.append(quoted);
                    response.append("\" }");
                    if (i < errors.size() - 1)
                        response.append(", ");
                }

                response.append("] }");
            }
        }
        catch (XmlException | IOException exception) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{ \"error\" : \""
                    + exception.getMessage() + "\" }").build();
        }

        return Response.ok(response.toString()).build();
    }

    @POST
    @Path("/sml")
    @ApiOperation(value = "Validates a given SensorML Document")
    @Produces(MediaType.APPLICATION_XML)
    public Response checkReturnXML(String data, @QueryParam("version")
    @DefaultValue("1")
    String version) {
        log.debug("Checking SensorML (validator version: {}: {}",
                  version,
                  data.substring(0, Math.min(data.length(), 1000)));

        String response = null;
        try {
            IProfileValidator smlValidator = getValidator(version);
            ValidationResult vr = validate(data, smlValidator);

            if (vr.isValidated())
                response = "<result><status>valid</status></result>";
            else {
                String details = vr.getValidationFailuresAsString();
                response = "<result><status>invalid</status><errors>" + details + "</errors></result>";
            }
        }
        catch (XmlException | IOException exception) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("<error>" + exception.getMessage()
                    + "</error>").build();
        }

        return Response.ok(response).build();
    }

    private ValidationResult validate(String data, IProfileValidator smlValidator) throws XmlException, IOException {
        if (data.isEmpty())
            return new ValidationResult(false, "Provided data is empty");

        SensorMLDocument doc = SensorMLDocument.Factory.parse(data);
        ValidationResult vr = smlValidator.validate(doc);
        return vr;
    }

    private IProfileValidator getValidator(String version) {
        IProfileValidator smlValidator = ValidatorModule.getFirstMatchFor(this.validators,
                                                                          ValidatableFormatAndProfile.SML_DISCOVERY);
        return smlValidator;
    }

}
