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

/**
 * 
 * JSON with Jersey: https://jersey.java.net/documentation/1.18/json.html
 * 
 * @author Yakoub, Daniel Nüst
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.n52.oss.opensearch.listeners.OpenSearchTools;
import org.n52.oss.sir.api.SirDetailedSensorDescription;
import org.n52.oss.sir.api.SirSearchCriteria;
import org.n52.oss.sir.api.SirSearchResultElement;
import org.n52.oss.sir.api.SirSimpleSensorDescription;
import org.n52.oss.sir.api.SirXmlSensorDescription;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.ds.ISearchSensorDAO;
import org.n52.sir.ds.solr.SOLRSearchSensorDAO;
import org.n52.sir.sml.SensorMLStringConverter;
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

    /**
     * include only the text results that actually contain the query
     */
    private boolean includeOnlyWordsContainingQuery = true;

    private static Collection<String> wordsToBeRemoved = new ArrayList<>();

    static {
        wordsToBeRemoved.add(SensorMLStringConverter.CLASSIFICATIONS_PREPEND.trim());
        wordsToBeRemoved.add(SensorMLStringConverter.IDENTIFICATIONS_PREPEND.trim());
        wordsToBeRemoved.add(SensorMLStringConverter.KEYWORDS_PREPEND.trim());
    }

    private static class ResultItem {

        public String description;

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("ResultItem [");
            if (this.description != null) {
                builder.append("description=");
                builder.append(this.description);
            }
            builder.append("]");
            return builder.toString();
        }
    }

    @XmlRootElement
    private static class Result {

        @XmlElement(name = "results")
        public ArrayList<ResultItem> items = new ArrayList<>();

        @Override
        public String toString() {
            final int maxLen = 3;
            StringBuilder builder = new StringBuilder();
            builder.append("Result [");
            if (this.items != null) {
                builder.append("items=");
                builder.append(this.items.subList(0, Math.min(this.items.size(), maxLen)));
            }
            builder.append("]");
            return builder.toString();
        }
    }

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
        crit.setIndexedTextSearchWithMinimalResult(true);

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

                    // split up text
                    String[] words = descriptionText.split(" "); // (" |-|_");
                    Collections.addAll(results, words);
                }
            }
            else if (element.getSensorDescription() instanceof SirXmlSensorDescription) {
                SirXmlSensorDescription xmlSD = (SirXmlSensorDescription) element.getSensorDescription();
                // do nothing, takes too long to handle this...
            }
        }

        // remove undesired artifacts from decoding
        results.removeAll(wordsToBeRemoved);

        Result r = new Result();

        if (results.size() > 0) {
            for (String s : results) {
                if (includeOnlyWordsContainingQuery && s.contains(query)) {
                    ResultItem ri = new ResultItem();
                    ri.description = s.trim();
                    r.items.add(ri);
                }
            }
        }

        log.debug("Done serving autocomplete, response: {}", r);

        return Response.status(200).entity(r).build();
    }

}
