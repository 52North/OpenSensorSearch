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

package org.n52.oss.opensearch.listeners.xml;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.n52.oss.opensearch.OpenSearchConfigurator;
import org.n52.oss.opensearch.OpenSearchConstants;
import org.n52.oss.opensearch.listeners.OpenSearchListener;
import org.n52.oss.sir.api.SirSearchResultElement;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.util.XmlTools;
import org.n52.sir.response.SirSearchSensorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sir.x032.SearchSensorResponseDocument;

import com.google.inject.Inject;

public class XmlListener implements OpenSearchListener {

    private static final Logger log = LoggerFactory.getLogger(XmlListener.class);

    private static final String MIME_TYPE = "application/xml";

    private static final String NAME = "XML";

    private OpenSearchConfigurator conf;

    @Inject
    public XmlListener(OpenSearchConfigurator configurator) {
        this.conf = configurator;
        this.conf.addResponseFormat(this);
    }

    @Override
    public Response createResponse(final Collection<SirSearchResultElement> searchResult,
                                   final MultivaluedMap<String, String> params) throws OwsExceptionReport {
        log.debug("Creating streamed response...");
        
        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                String query = params.getFirst(OpenSearchConstants.QUERY_PARAM);

                log.debug("Creating XML response for {} with {} results.", query, searchResult.size());

                SirSearchSensorResponse sssr = new SirSearchSensorResponse();
                sssr.setSearchResultElements(searchResult);

                SearchSensorResponseDocument searchSensorRespDoc = sssr.createXml();

                searchSensorRespDoc.save(os, XmlTools.xmlOptionsForNamespaces());

                log.debug("Done with XML response.");
            }
        };

        return Response.ok(stream).build();
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
