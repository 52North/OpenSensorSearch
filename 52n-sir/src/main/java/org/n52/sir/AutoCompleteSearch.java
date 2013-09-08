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

package org.n52.sir;

/**
 * @author Yakoub 
 */
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.datastructure.detailed.SirDetailedSensorDescription;
import org.n52.sir.ds.solr.SOLRSearchSensorDAO;
import org.n52.sir.listener.SearchSensorListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import com.sun.jersey.api.core.HttpContext;

@Path("/suggest")
@RequestScoped
public class AutoCompleteSearch {

    private static Logger log = LoggerFactory.getLogger(SOLRSearchSensorDAO.class);

    private static final String REQUEST_PARAM_AUTOCOMPLETE = "q";

    @Context
    HttpServletRequest servletRequest;

    @Context
    private HttpContext servletContext;

    SearchSensorListener searchSensor;

    @Inject
    public AutoCompleteSearch(SearchSensorListener ssl) {
        this.searchSensor = ssl;
        
        log.debug("NEW {}", this);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response doGet(@QueryParam("q")
    String text) {
        // String text =
        // servletRequest.getParameter(REQUEST_PARAM_AUTOCOMPLETE);
        log.trace("new GET request for autocomplete: {}", text);

        // TODO return reasonable error message and status code
        if (text == null)
            throw new RuntimeException("Query parameter " + REQUEST_PARAM_AUTOCOMPLETE + " must be given.");

        // I'm using a set to avoid duplications
        Collection<Object> results = new HashSet<>();
        SOLRSearchSensorDAO dao = new SOLRSearchSensorDAO();
        Collection<SirSearchResultElement> search_results = dao.searchByAll(text, null, null, null, null, null, null);

        Iterator<SirSearchResultElement> it = search_results.iterator();

        while (it.hasNext()) {
            SirSearchResultElement element = it.next();
            SirDetailedSensorDescription desc = (SirDetailedSensorDescription) element.getSensorDescription();
            results.addAll(desc.getKeywords());
            if (desc.getContacts() != null)
                results.addAll(desc.getContacts());
            if (desc.getDescription() != null)
                results.add(desc.getDescription().toString());
            if (desc.getInputs() != null)
                results.addAll(desc.getInputs());
            if (desc.getOutputs() != null)
                results.addAll(desc.getOutputs());
            if (desc.getIdentifiers() != null)
                results.addAll(desc.getIdentifiers());
            if (desc.getClassifiers() != null)
                results.addAll(desc.getClassifiers());
        }

        // returns the result as json array
        String result = null;

        if (results.size() == 0)
            result = "[]";
        else {
            Iterator<Object> iterator = results.iterator();
            StringBuilder res = new StringBuilder();
            String first = iterator.next().toString();
            if (first.contains(text)) {
                res.append(first);
            }
            while (iterator.hasNext()) {
                String n = iterator.next().toString();
                if (n.contains(text)) {
                    if (res.toString().length() > 0)
                        res.append(",");
                    res.append(n);
                }
            }

            result = "[" + res.toString() + "]";
        }

        log.debug("Done serving servlet, response: {}", result);
        return Response.status(200).entity(result).header(HttpHeaders.CONTENT_LENGTH, result.length()).build();
    }

}
