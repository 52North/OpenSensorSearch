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

/**
 * @author Yakoub, Daniel Nüst
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.n52.oss.opensearch.listeners.OpenSearchTools;
import org.n52.oss.sir.api.SirDetailedSensorDescription;
import org.n52.oss.sir.api.SirSearchCriteria;
import org.n52.oss.sir.api.SirSearchResultElement;
import org.n52.oss.sir.api.SirSimpleSensorDescription;
import org.n52.oss.sir.api.SirXmlSensorDescription;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.ds.ISearchSensorDAO;
import org.n52.sir.ds.solr.SOLRSearchSensorDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.inject.servlet.RequestScoped;

@Path(ApiPaths.AUTOSUGGEST_PATH)
@RequestScoped
public class AutoCompleteResource {

    private static Logger log = LoggerFactory.getLogger(SOLRSearchSensorDAO.class);

    private static final String REQUEST_PARAM_AUTOCOMPLETE = "q";

    private ISearchSensorDAO searchSensorDao;

    @Inject
    public AutoCompleteResource(@Named(ISearchSensorDAO.AUTOCOMPLETE)
    ISearchSensorDAO dao) {
        this.searchSensorDao = dao;

        log.debug("NEW {}", this);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response doGet(@QueryParam(REQUEST_PARAM_AUTOCOMPLETE)
    String query) {
        log.debug("Autocomplete request: '{}'", query);

        if (query == null || query.isEmpty())
            return Response.status(Status.BAD_REQUEST).entity(" { error: \"Query parameter "
                    + REQUEST_PARAM_AUTOCOMPLETE + " must be given.\" }").build();

        Collection<String> results = new HashSet<>();

        SirSearchCriteria crit = new SirSearchCriteria();
        Collection<String> text = new ArrayList<>();
        text.add(query);
        crit.setSearchText(text);

        Collection<SirSearchResultElement> searchResults;
        try {
            searchResults = this.searchSensorDao.searchSensor(crit, true);
        }
        catch (OwsExceptionReport e) {
            return Response.serverError().entity(e).build();
        }

        for (SirSearchResultElement element : searchResults) {
            if (element.getSensorDescription() instanceof SirDetailedSensorDescription) {
                SirDetailedSensorDescription detailedSD = (SirDetailedSensorDescription) element.getSensorDescription();

                results.addAll(detailedSD.getKeywords());

                if (detailedSD.getContacts() != null)
                    results.addAll(detailedSD.getContacts());
                if (detailedSD.getDescription() != null)
                    results.add(detailedSD.getDescription().toString());
                if (detailedSD.getInputs() != null)
                    results.addAll(detailedSD.getInputs());
                if (detailedSD.getOutputs() != null)
                    results.addAll(detailedSD.getOutputs());
                if (detailedSD.getIdentifiers() != null)
                    results.addAll(detailedSD.getIdentifiers());
                if (detailedSD.getClassifiers() != null)
                    results.addAll(detailedSD.getClassifiers());
            }
            else if (element.getSensorDescription() instanceof SirSimpleSensorDescription) {
                SirSimpleSensorDescription simpleSD = (SirSimpleSensorDescription) element.getSensorDescription();

                if (simpleSD.getDescriptionText() != null) {
                    String descriptionText = OpenSearchTools.extractDescriptionText(simpleSD);
                    results.add(descriptionText);
                }
            }
            else if (element.getSensorDescription() instanceof SirXmlSensorDescription) {
                SirXmlSensorDescription xmlSD = (SirXmlSensorDescription) element.getSensorDescription();
                // do nothing, takes too long to handle this...
            }
        }

        String result = null;
        if (results.size() == 0)
            result = "{ \"results\" : [ ] }";
        else {
            StringBuilder sb = new StringBuilder();
            sb.append("{ \"results\" : [");
            for (String s : results) {
                // check again if the keyword is contained - could also disable this!
                if (s.toLowerCase().contains(query.toLowerCase())) {
                    sb.append("\"");
                    sb.append(s);
                    sb.append("\", ");
                }
                else
                    log.debug("Got a result that is not contained in the query: {}", s);
            }
            if (sb.length() > 4)
                sb.replace(sb.length() - 2, sb.length(), ""); // remove last comma
            sb.append("] } ");

            result = sb.toString();
        }

        log.debug("Done serving autocomplete, response: {}", result);

        return Response.status(200).entity(result).header(HttpHeaders.CONTENT_LENGTH, result.length()).build();
    }

}
