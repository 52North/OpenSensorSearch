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

package org.n52.ar.layar;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.n52.sir.json.MapperFactory;
import org.n52.sir.json.SearchResult;
import org.n52.sir.json.SearchResultElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * @author <a href="mailto:d.nuest@52north.org">Daniel Nüst</a>
 */
public class LayarServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 3747559074882272526L;

    private static final Object REQUEST_FORMAT = "application/json";

    private static Logger log = LoggerFactory.getLogger(LayarServlet.class);

    private String sirURL;

    private String layerName = "geoviquasensorsearch";

    private JsonFactory factory;

    private ObjectMapper mapper;

    private String url;

    public LayarServlet() {
        log.debug("NEW {}", this);
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);

        String sirURLParam = getServletConfig().getInitParameter("sirURL");
        if (sirURLParam != null) {
            this.sirURL = sirURLParam;
        }
        String urlParam = getServletContext().getInitParameter("url");
        if (urlParam != null) {
            this.url = urlParam;
        }

        this.mapper = MapperFactory.getMapper();
        this.factory = new JsonFactory();

        log.info("Initialized " + this);
    }

    /**
     * http://layar.com/documentation/browser/api/getpois-request/
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userAgent = request.getHeader("User-agent");
        log.debug("User-agent={}", userAgent);

        /*
         * mandatory:
         */
        String userId = request.getParameter("userId");
        String version = request.getParameter("version");
        String countryCode = request.getParameter("countryCode");
        String lang = request.getParameter("lang");

        String action = request.getParameter("action");
        if (action == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameter 'action'");
            return;
        }

        String latParam = request.getParameter("lat");
        if (latParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameter 'lat'");
            return;
        }

        String lonParam = request.getParameter("lon");
        if (lonParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameter 'lon'");
            return;
        }

        String radiusParam = request.getParameter("radius");
        if (radiusParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameter 'radius'");
            return;
        }

        /*
         * optional:
         */
        String accurary = request.getParameter("accuracy");
        String pageKey = request.getParameter("pageKey");
        // String oauth_consumer_key = request.getParameter("oauth_consumer_key");
        // String oauth_signature_method = request.getParameter("oauth_signature_method");
        String radiolist = request.getParameter("RADIOLIST");
        String searchbox = request.getParameter("SEARCHBOX");
        String custom_slider = request.getParameter("CUSTOM_SLIDER");
        String checkboxlist = request.getParameter("CHECKBOXLIST");
        String localCountryCode = request.getParameter("localCountryCode");
        String alt = request.getParameter("alt"); // current altitude
        String recognizedReferenceImage = request.getParameter("recognizedReferenceImage");

        // convert parameters
        float lat = Float.parseFloat(latParam);
        float lon = Float.parseFloat(lonParam);
        double[] center = new double[] {lat, lon};
        float radius = Float.parseFloat(radiusParam);

        // get nearby sensors
        LayarResponse layarResponse = new LayarResponse();
        layarResponse.layer = request.getParameter("layerName");
        if (layarResponse.layer == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameter 'layerName'");
            return;
        }

        response.setContentType("application/json; charset=utf-8");
        JsonGenerator generator = this.factory.createJsonGenerator(response.getWriter());
        generator.useDefaultPrettyPrinter();

        try {
            Collection<Hotspot> hotspots = querySIR(center, radius);
            layarResponse.hotspots = hotspots;
        }
        catch (Exception e) {
            log.error("Error querying SIR.", e);
            layarResponse.errorCode = LayarResponse.CODE_ERROR;
            layarResponse.errorString = e.toString() + ": " + e.getMessage();
            layarResponse.showMessage = "Server side exception!";
        }

        // write response
        layarResponse.toJSON(generator);

        // clean up
        generator.close();
        response.flushBuffer();
    }

    /**
     * 
     * @param center
     * @param radius
     * @return
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    private Collection<Hotspot> querySIR(double[] center, float radius) throws JsonParseException,
            JsonMappingException,
            IOException {
        List<Hotspot> hotspots = new ArrayList<Hotspot>();
        StringBuilder sb = new StringBuilder();

        sb.append(this.sirURL);
        sb.append("?q=");

        // TODO use keyword entered by user
        // sb.append("muenster");

        sb.append("&httpAccept=");
        sb.append(REQUEST_FORMAT);

        // append location
        sb.append("&lat=");
        sb.append(center[0]);
        sb.append("&lon=");
        sb.append(center[1]);
        sb.append("&radius=");
        sb.append(radius);

        String request = sb.toString();
        log.info("Requesting {}", request);

        DefaultHttpClient client = new DefaultHttpClient();

        HttpGet get = new HttpGet(request);

        HttpResponse response = client.execute(get);
        InputStream responseStream = response.getEntity().getContent();
        int responseCode = response.getStatusLine().getStatusCode();
        log.debug("Response Code: " + responseCode);

        SearchResult result = this.mapper.readValue(responseStream, SearchResult.class);
        log.debug("Got {} search results.", Integer.valueOf(result.getResults().size()));

        // convert search result to a list of AugmentedPOIs
        Collection<SearchResultElement> results = result.getResults();
        for (SearchResultElement sre : results) {
            Hotspot h = new Hotspot();
            // poi.actions
            // poi.alt = calculate altitude?`
            // poi.attribution
            h.description = sre.getSensorDescription().getText();
            // poi.distance = calculate?
            h.id = sre.getSensorIdInSir();
            // poi.imageURL
            double[] latLon = sre.getSensorDescription().getBoundingBox().getCenter();
            h.lat = latLon[0];
            h.lon = latLon[1];
            h.title = sre.getSensorDescription().getUrl();

            hotspots.add(h);
        }

        responseStream.close();
        client = null;

        return hotspots;
    }

    public String getUrl() {
        return this.url;
    }
}