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

import net.opengis.kml.x22.AbstractFeatureType;
import net.opengis.kml.x22.KmlDocument;
import net.opengis.kml.x22.KmlType;

import org.n52.oss.opensearch.OpenSearchConfigurator;
import org.n52.oss.opensearch.OpenSearchConstants;
import org.n52.oss.opensearch.listeners.OpenSearchListener;
import org.n52.oss.opensearch.listeners.feed.AbstractFeedListener;
import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.util.XmlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class KmlListener implements OpenSearchListener {

    protected static final Logger log = LoggerFactory.getLogger(AbstractFeedListener.class);

    public static final String MIME_TYPE = OpenSearchConstants.APPLICATION_VND_KML;

    private static final String NAME = "KML";

    private OpenSearchConfigurator conf;

    @Inject
    public KmlListener(OpenSearchConfigurator configurator) {
        this.conf = configurator;
        this.conf.addResponseFormat(this);
    }

    @Override
    public Response createResponse(final Collection<SirSearchResultElement> searchResult,
                                   final MultivaluedMap<String, String> params) throws OwsExceptionReport {

        log.debug("Creating streamed response...");
        final String query = params.getFirst(OpenSearchConstants.QUERY_PARAM);

        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                log.debug("Creating KML response for {} with {} results.", query, searchResult.size());

                KmlDocument doc = KmlDocument.Factory.newInstance();
                KmlType kml = doc.addNewKml();
                
                AbstractFeatureType abstractFeatureGroup = kml.addNewAbstractFeatureGroup();
                abstractFeatureGroup.addNewAuthor().addName("Open Sensor Search");

                // TODO add kml content

                doc.save(os, XmlTools.xmlOptionsForNamespaces());

                log.debug("Done with XML response.");
            }
        };

        return Response.ok(stream).header("Content-Disposition",
                                          "attachment; filename=" + query + "_Open-Sensor-Search.kml").build();
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
