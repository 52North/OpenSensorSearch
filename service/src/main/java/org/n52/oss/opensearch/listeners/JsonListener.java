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

import java.net.URI;
import java.util.Collection;
import java.util.Date;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.n52.oss.json.Converter;
import org.n52.oss.opensearch.OpenSearchConfigurator;
import org.n52.oss.opensearch.OpenSearchConstants;
import org.n52.oss.sir.api.SirSearchResultElement;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.json.SearchResult;
import org.n52.sir.json.SearchResultElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class JsonListener implements OpenSearchListener {

    private static Logger log = LoggerFactory.getLogger(JsonListener.class);

    public static final String MIME_TYPE = MediaType.APPLICATION_JSON;

    private static final String NAME = "JSON";

    private OpenSearchConfigurator conf;

    private Converter converter;

    private URI openSearchEndpoint;

    private URI homeUri;

    @Inject
    public JsonListener(OpenSearchConfigurator configurator) {
        this.conf = configurator;
        this.conf.addResponseFormat(this);

        this.converter = new Converter();

        log.info("NEW {}", this);
    }

    @Override
    public Response createResponse(Collection<SirSearchResultElement> searchResult,
                                   MultivaluedMap<String, String> params) throws OwsExceptionReport {
        log.debug("Creating response for {} search results with params {}", searchResult.size(), params);

        String website = this.homeUri.toString();
        String searchUri = this.openSearchEndpoint.toString();

        String searchText = params.getFirst(OpenSearchConstants.QUERY_PARAM);

        String responseDescription = "These are the search hits for the keyword(s) '" + searchText
                + "' from Open Sensor Search (" + website + ").";
        String responseURL = searchUri + "?" + OpenSearchConstants.QUERY_PARAM + "="
                + searchText + "&" + OpenSearchConstants.FORMAT_PARAM + "=" + MIME_TYPE;

        // build the response object
        SearchResult result = new SearchResult(website,
                                               searchText,
                                               responseURL,
                                               responseDescription,
                                               this.conf.getResponseAuthor(),
                                               new Date());

        for (SirSearchResultElement sirSearchResultElement : searchResult) {
            SearchResultElement element = this.converter.convert(sirSearchResultElement, true);
            result.addResult(element);
        }

        return Response.ok(result).build();
    }

    @Override
    public String getMimeType() {
        return MIME_TYPE;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void setOpenSearchEndpoint(URI uri) {
        this.openSearchEndpoint = uri;
    }

    @Override
    public void setHomeURI(URI uri) {
        this.homeUri = uri;
    }

}
