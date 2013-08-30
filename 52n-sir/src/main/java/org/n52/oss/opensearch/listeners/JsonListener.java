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

package org.n52.oss.opensearch.listeners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.n52.oss.opensearch.OpenSearchConfigurator;
import org.n52.oss.opensearch.OpenSearchConstants;
import org.n52.sir.datastructure.SirBoundingBox;
import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.datastructure.SirService;
import org.n52.sir.datastructure.SirServiceReference;
import org.n52.sir.datastructure.SirSimpleSensorDescription;
import org.n52.sir.datastructure.detailed.SirDetailedSensorDescription;
import org.n52.sir.json.BoundingBox;
import org.n52.sir.json.MapperFactory;
import org.n52.sir.json.SearchResult;
import org.n52.sir.json.SearchResultElement;
import org.n52.sir.json.SensorDescription;
import org.n52.sir.json.Service;
import org.n52.sir.json.ServiceReference;
import org.n52.sir.ows.OwsExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

public class JsonListener implements OpenSearchListener {

    private static final Logger log = LoggerFactory.getLogger(JsonListener.class);

    public static final String MIME_TYPE = MediaType.APPLICATION_JSON;

    private static final String NAME = "JSON";

    private OpenSearchConfigurator conf;

    private ObjectMapper mapper;

    @Inject
    public JsonListener(OpenSearchConfigurator configurator) {
        this.conf = configurator;
        this.conf.addResponseFormat(this);

        this.mapper = MapperFactory.getMapper();

        log.info("NEW {}", this);
    }

    @Override
    public Response createResponse(Collection<SirSearchResultElement> searchResult,
                                   MultivaluedMap<String, String> params) throws OwsExceptionReport {
        log.debug("Creating response for {} search results with params {}", searchResult.size(), params);

        String searchText = params.getFirst(OpenSearchConstants.QUERY_PARAM);

        String responseDescription = "These are the search hits for the keyword(s) '" + searchText
                + "' from Open Sensor Search (" + this.conf.getFullServicePath().toString() + ").";
        String responseURL = this.conf.getFullOpenSearchPath() + "?" + OpenSearchConstants.QUERY_PARAM + "="
                + searchText + "&" + OpenSearchConstants.FORMAT_PARAM + "=" + MIME_TYPE;

        // build the response object
        SearchResult result = new SearchResult(this.conf.getFullServicePath().toString(),
                                               searchText,
                                               responseURL,
                                               responseDescription,
                                               "52°North",
                                               new Date());

        for (SirSearchResultElement sirSearchResultElement : searchResult) {
            SearchResultElement element;
            if (sirSearchResultElement.getSensorDescription() instanceof SirDetailedSensorDescription) {
                SirDetailedSensorDescription desc = (SirDetailedSensorDescription) sirSearchResultElement.getSensorDescription();

                element = createDetailedResult(desc);
            }
            else {
                element = createResult(sirSearchResultElement);
            }

            result.addResult(element);
        }

        // try {
        // this.mapper.writeValue(writer, result);
        // }
        // catch (JsonGenerationException e) {
        // log.error("Json Exception", e);
        // return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build();
        // }
        // catch (JsonMappingException e) {
        // log.error("Json Exception", e);
        // return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build();
        // }
        // catch (IOException e) {
        // log.error("Error outputting feed to writer", e);
        // return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build();
        // }

        return Response.ok(result).build();
    }

    private SearchResultElement createResult(SirSearchResultElement sirSearchResultElement) {
        SearchResultElement sre = new SearchResultElement();

        sre.setSensorIdInSir(sirSearchResultElement.getSensorIdInSir());
        sre.setLastUpdate(sirSearchResultElement.getLastUpdate());

        Collection<ServiceReference> sr = new ArrayList<>();
        Collection<SirServiceReference> serviceReferences = sirSearchResultElement.getServiceReferences();

        for (SirServiceReference sirServiceReference : serviceReferences) {
            SirService service = sirServiceReference.getService();
            sr.add(new ServiceReference(new Service(service.getUrl(), service.getType()),
                                        sirServiceReference.getServiceSpecificSensorId()));
        }
        sre.setServiceReferences(sr);

        if (sirSearchResultElement.getSensorDescription() instanceof SirSimpleSensorDescription) {
            SirSimpleSensorDescription d = (SirSimpleSensorDescription) sirSearchResultElement.getSensorDescription();
            SirBoundingBox b = d.getBoundingBox();
            BoundingBox bbox = new BoundingBox(b.getEast(), b.getSouth(), b.getWest(), b.getNorth());
            bbox.setSrid(b.getSrid());
            SensorDescription sd = new SensorDescription(d.getSensorDescriptionURL(),
                                                         Tools.extractDescriptionText(d),
                                                         bbox);
            sre.setSensorDescription(sd);

        }

        return sre;
    }

    private SearchResultElement createDetailedResult(SirDetailedSensorDescription desc) {
        log.debug("Adding a detailed sensor profile");

        SearchResultElement element = new SearchResultElement();

        element.setSensorIdInSir(desc.getId());
        log.trace("Begin date:" + desc.getBegineDate());
        element.setBeginDate(desc.getBegineDate());
        element.setEndDate(desc.getEndDate());
        if (desc.getInputs() != null)
            element.setInputs(desc.getInputs());
        if (desc.getOutputs() != null)
            element.setOutputs(desc.getOutputs());
        if (desc.getIdentifiers() != null)
            element.setIdentifiers(desc.getIdentifiers());
        if (desc.getClassifiers() != null)
            element.setClassifiers(desc.getClassifiers());
        if (desc.getContacts() != null)
            element.setContacts(desc.getContacts());
        if (desc.getKeywords() != null)
            element.setKeywords(desc.getKeywords());

        return element;
    }

    @Override
    public String getMimeType() {
        return MIME_TYPE;
    }

    @Override
    public String getName() {
        return NAME;
    }

}
