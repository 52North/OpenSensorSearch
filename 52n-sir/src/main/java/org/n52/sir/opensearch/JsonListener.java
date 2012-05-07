/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */

package org.n52.sir.opensearch;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.n52.sir.datastructure.SirBoundingBox;
import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.datastructure.SirService;
import org.n52.sir.datastructure.SirServiceReference;
import org.n52.sir.datastructure.SirSimpleSensorDescription;
import org.n52.sir.json.BoundingBox;
import org.n52.sir.json.MapperFactory;
import org.n52.sir.json.SearchResult;
import org.n52.sir.json.SearchResultElement;
import org.n52.sir.json.SensorDescription;
import org.n52.sir.json.Service;
import org.n52.sir.json.ServiceReference;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.ows.OwsExceptionReport.ExceptionCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonListener implements IOpenSearchListener {

    private static final Logger log = LoggerFactory.getLogger(JsonListener.class);

    public static final String MIME_TYPE = "application/json";

    private static final String NAME = "JSON";

    private OpenSearchConfigurator conf;

    private ObjectMapper mapper;

    public JsonListener(OpenSearchConfigurator configurator) {
        this.conf = configurator;
        this.conf.addResponseFormat(this);

        this.mapper = MapperFactory.getMapper();
    }

    @Override
    public void createResponse(HttpServletRequest req,
                               HttpServletResponse resp,
                               Collection<SirSearchResultElement> searchResult,
                               PrintWriter writer,
                               String searchText) throws OwsExceptionReport {
        resp.setContentType(MIME_TYPE);

        // TODO move this somewhere where all listeners can use it
        String responseDescription = "These are the search hits for the keyword(s) '" + searchText
                + "' from Open Sensor Search (" + this.conf.getFullServicePath().toString() + ").";
        String responseURL = this.conf.getFullOpenSearchPath() + "?" + OpenSearchConstants.QUERY_PARAMETER + "="
                + searchText + "&" + OpenSearchConstants.ACCEPT_PARAMETER + "=" + MIME_TYPE;

        /*
         * test with gson, works fine, testing jackson with pojos...
         */
        // http://sites.google.com/site/gson/gson-user-guide
        // GsonBuilder gsonBuilder = new
        // GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping();
        // Gson gson = gsonBuilder.create();
        //
        // // creating json object
        // JsonObject response = new JsonObject();
        // response.add("source", new JsonPrimitive(this.configurator.getFullServicePath().toString()));
        // response.add("searchText", new JsonPrimitive(searchText));
        // response.add("searchURL", new JsonPrimitive(responseURL));
        // response.add("searchDescription", new JsonPrimitive(responseDescription));
        // response.add("searchAuthor", new JsonPrimitive("52°North"));
        // response.add("searchDate", new JsonPrimitive(this.permalinkDateFormat.format(new Date())));
        //
        // JsonElement jsonTree = gson.toJsonTree(searchResult);
        // response.add("result", jsonTree);
        //
        // JsonWriter jsonWriter = new JsonWriter(writer);
        // jsonWriter.setHtmlSafe(true);
        // jsonWriter.setIndent("  ");

        // if performance becomes an issue then try using stream writer, see:
        // http://google-gson.googlecode.com/svn/trunk/gson/docs/javadocs/com/google/gson/stream/JsonWriter.html
        // jsonWriter.beginArray();

        SearchResult result = new SearchResult(this.conf.getFullServicePath().toString(),
                                               searchText,
                                               responseURL,
                                               responseDescription,
                                               "52°North",
                                               new Date());
        for (SirSearchResultElement sirSearchResultElement : searchResult) {
            SearchResultElement sre = new SearchResultElement();
            sre.setSensorIdInSir(sirSearchResultElement.getSensorIdInSir());
            sre.setLastUpdate(sirSearchResultElement.getLastUpdate());

            Collection<ServiceReference> sr = new ArrayList<ServiceReference>();
            Collection<SirServiceReference> serviceReferences = sirSearchResultElement.getServiceReferences();
            for (SirServiceReference sirServiceReference : serviceReferences) {
                SirService service = sirServiceReference.getService();
                sr.add(new ServiceReference(new Service(service.getUrl(), service.getType()),
                                            sirServiceReference.getServiceSpecificSensorId()));
            }
            sre.setServiceReferences(sr);

            SirSimpleSensorDescription d = (SirSimpleSensorDescription) sirSearchResultElement.getSensorDescription();
            SirBoundingBox b = d.getBoundingBox();
            BoundingBox bbox = new BoundingBox(b.getEast(), b.getSouth(), b.getWest(), b.getNorth());
            bbox.setSrid(b.getSrid());
            SensorDescription sd = new SensorDescription(d.getSensorDescriptionURL(),
                                                         Tools.extractDescriptionText(d),
                                                         bbox);
            sre.setSensorDescription(sd);

            result.addResult(sre);
        }

        try {
            this.mapper.writeValue(writer, result);
        }
        catch (JsonGenerationException e) {
            log.error("Json Exception", e);
            throw new OwsExceptionReport(ExceptionCode.NoApplicableCode, "service", "Error with Json Generation: "
                    + e.getMessage());
        }
        catch (JsonMappingException e) {
            log.error("Json Exception", e);
            throw new OwsExceptionReport(ExceptionCode.NoApplicableCode, "service", "Error with Json Mapping: "
                    + e.getMessage());
        }
        catch (IOException e) {
            log.error("Error outputting feed to writer", e);
            throw new OwsExceptionReport(ExceptionCode.NoApplicableCode, "service", "Error outputting feed to writer.");
        }

        // gson.toJson(result, writer);
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
