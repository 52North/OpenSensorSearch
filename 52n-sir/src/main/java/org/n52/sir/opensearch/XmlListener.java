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
import java.io.Writer;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.response.SirSearchSensorResponse;
import org.n52.sir.util.XmlTools;
import org.restlet.data.CharacterSet;
import org.restlet.engine.io.WriterOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sir.x032.SearchSensorResponseDocument;

public class XmlListener implements IOpenSearchListener {

    private static final Logger log = LoggerFactory.getLogger(XmlListener.class);

    private static final String MIME_TYPE = "application/xml";

    private static final String NAME = "XML";

    private OpenSearchConfigurator conf;

    public XmlListener(OpenSearchConfigurator configurator) {
        this.conf = configurator;
        this.conf.addResponseFormat(this);
    }

    @Override
    public void createResponse(HttpServletRequest req,
                               HttpServletResponse resp,
                               Collection<SirSearchResultElement> searchResult,
                               PrintWriter writer,
                               String searchText) throws OwsExceptionReport {
        log.debug("Creating XML response for {}", searchText);
        
        resp.setContentType(MIME_TYPE);
        SirSearchSensorResponse sssr = new SirSearchSensorResponse();
        sssr.setSearchResultElements(searchResult);
        writeTo(sssr, writer);

        log.debug("Done with XML response.");
    }

    private void writeTo(SirSearchSensorResponse sssr, Writer writer) throws OwsExceptionReport {
        SearchSensorResponseDocument searchSensorRespDoc = sssr.createXml();
        try {
            WriterOutputStream wos = new WriterOutputStream(writer, CharacterSet.UTF_8);
            searchSensorRespDoc.save(wos, XmlTools.xmlOptionsForNamespaces());
            wos.close();
        }
        catch (IOException e) {
            log.error("Could not write response document to writer.", e);
            throw new OwsExceptionReport(ExceptionCode.NoApplicableCode,
                                         "server",
                                         "Could not write response to output writer.");
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
