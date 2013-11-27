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
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.transform.TransformerException;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.oss.json.Converter;
import org.n52.oss.sir.api.SirSensor;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.json.SearchResultElement;
import org.n52.sir.sml.SensorMLDecoder;
import org.n52.sir.xml.ITransformer;
import org.n52.sir.xml.ITransformer.TransformableFormat;
import org.n52.sir.xml.TransformerModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * @author Yakoub, Daniel
 * 
 */
@Path(ApiPaths.TRANSFORMATION_PATH)
@Api(value = "/" + ApiPaths.TRANSFORMATION_PATH, description = "Conversion of SensorML document to different formats")
@RequestScoped
public class TransformationResource {

    private static Logger log = LoggerFactory.getLogger(TransformationResource.class);

    private SensorMLDecoder decoder;

    private Converter converter;

    private Set<ITransformer> transformers;

    @Inject
    public TransformationResource(Set<ITransformer> transformers) {
        this.transformers = transformers;

        this.decoder = new SensorMLDecoder();
        this.converter = new Converter();
    }

    // private String toJsonString(String sensorML) throws XmlException {
    // SensorMLDocument document = SensorMLDocument.Factory.parse(sensorML);
    // return this.gson.toJson(document);
    // }

    // public Response toJson(String sensor) {
    // try {
    // String response = toJsonString(sensor);
    // return Response.ok(response).build();
    // }
    // catch (XmlException e) {
    // return Response.ok("{ \"error\" : \"Cannot parse sensorML\" }").build();
    // }
    // }

    @GET
    @ApiOperation(value = "index of the available transformations")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIndex() {
        StringBuilder sb = new StringBuilder();

        sb.append("{ \"transformations\" : [");
        sb.append(" { \"input\" : ");
        sb.append("\"text/xml;subtype='sensorML/1.0.0'\"");
        sb.append(" , \"output\" : ");
        sb.append("\"text/xml;subtype='EbRIM/1.0.1'\"");
        sb.append(" } ");
        sb.append("] }");

        return Response.ok(sb.toString()).build();
    }

    @POST
    @ApiOperation(value = "Convert sensor description to a specific form", notes = "The output can be json or ebrim.")
    @Produces(MediaType.APPLICATION_JSON)
    public Response convertSmlToJson(String data) {
        log.debug("Transforming to json: {}", data);

        try {
            SensorMLDocument sml = SensorMLDocument.Factory.parse(data);

            SirSensor decoded = this.decoder.decode(sml);

            SearchResultElement converted = this.converter.convert(decoded, true);

            return Response.ok(converted).build();
        }
        catch (XmlException e) {
            log.error("Could not *parse* SensorML for transformation.", e);
            return Response.serverError().entity("{\"error\" : \"" + e.getMessage() + "\" } ").build();
        }
        catch (OwsExceptionReport e) {
            log.error("Could not *decode* SensorML for transformation.", e);
            return Response.serverError().entity("{\"error\" : \"" + e.getMessage() + "\" } ").build();
        }
    }

    @POST
    @ApiOperation(value = "Convert sensor description to a specific form", notes = "The output can be json or ebrim.")
    @Produces(MediaType.APPLICATION_XML)
    public Response convertSmlToEbrim(String data) {
        log.debug("Transforming to xml: {}", data.substring(0, 1000));
        SensorMLDocument sensorMLDocument;
        try {
            sensorMLDocument = SensorMLDocument.Factory.parse(data);
        }
        catch (XmlException e) {
            log.error("Could not parse SensorML: " + data, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\": \"Cannot parse sensorML\"; \"reason\":\" "
                    + e.getMessage() + "\" }").build();
        }

        log.debug("Transforming SML to EbRim... SML: {} [...]", sensorMLDocument.xmlText().substring(0, 300));

        ITransformer transformer = TransformerModule.getFirstMatchFor(this.transformers,
                                                                      TransformableFormat.SML,
                                                                      TransformableFormat.EBRIM);

        try {
            XmlObject transformed = transformer.transform(sensorMLDocument);
            Document doc = (Document) transformed.getDomNode();
            return Response.ok(doc).build();
        }
        catch (XmlException | TransformerException | IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\": \"Cannot parse sensorML\"; \"reason\":\" "
                    + e.getMessage() + "\" }").build();
        }
    }

}
