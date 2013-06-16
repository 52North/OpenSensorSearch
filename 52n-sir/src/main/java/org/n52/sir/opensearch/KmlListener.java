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
package org.n52.sir.opensearch;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.opengis.kml.x22.AbstractFeatureType;
import net.opengis.kml.x22.KmlDocument;
import net.opengis.kml.x22.KmlType;

import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.util.XmlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class KmlListener implements IOpenSearchListener {

    protected static final Logger log = LoggerFactory.getLogger(AbstractFeedListener.class);

    public static final String MIME_TYPE = "application/vnd.google-earth.kml+xml";

    private static final String NAME = "KML";

    private OpenSearchConfigurator conf;

    public KmlListener(OpenSearchConfigurator configurator) {
        this.conf = configurator;
        this.conf.addResponseFormat(this);
    }

    @Override
    public void createResponse(HttpServletRequest req,
                               HttpServletResponse resp,
                               Collection<SirSearchResultElement> searchResult,
                               PrintWriter writer,
                               String searchText) throws OwsExceptionReport {
        resp.setContentType(MIME_TYPE);

        resp.setHeader("Content-Disposition", "attachment; filename=" + searchText + "_Open-Sensor-Search.kml");

        KmlDocument doc = KmlDocument.Factory.newInstance();
        KmlType kml = doc.addNewKml();
        // simpleExtensionGroup.setStringValue("RESPONSE TYPE NOT IMPLEMENTED!");

        AbstractFeatureType abstractFeatureGroup = kml.addNewAbstractFeatureGroup();
        abstractFeatureGroup.addNewAuthor().addName("Open Sensor Search");

        // TODO add kml content

        try {
            doc.save(writer, XmlTools.xmlOptionsForNamespaces());
        }
        catch (IOException e) {
            log.error("Error outputting feed to writer", e);
            throw new OwsExceptionReport(ExceptionCode.NoApplicableCode, "service", "Error outputting feed to writer");
        }
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
