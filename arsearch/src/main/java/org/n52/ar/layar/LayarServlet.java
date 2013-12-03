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
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.n52.ar.SirCallbackServlet;
import org.n52.ar.SirPOI;
import org.n52.sir.json.SearchResultElement;
import org.n52.sir.json.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

/**
 * 
 * 
 * @author <a href="mailto:d.nuest@52north.org">Daniel Nüst</a>
 * 
 */
public class LayarServlet extends SirCallbackServlet {

	private static Logger log = LoggerFactory.getLogger(LayarServlet.class);

	private static final String REQUEST_FORMAT = "application/json";

	/**
     * 
     */
	private static final long serialVersionUID = 3747559074882272526L;

	private JsonFactory factory;

	private String layerName = "opensensorsearch";

	public LayarServlet() {
		log.debug("NEW {}", this);
	}

	@Override
	protected SirPOI createPOI(SearchResultElement sre) {
		Hotspot h = new Hotspot();
		// poi.actions
		// poi.alt = calculate altitude?`
		// poi.attribution
		h.description = sre.getSensorDescription().getText();
		// poi.distance = calculate?
        h.id = sre.getSensorId();
		// poi.imageURL
		double[] latLon = sre.getSensorDescription().getBoundingBox()
				.getCenter();
		h.lat = latLon[0];
		h.lon = latLon[1];
		ArrayList<ServiceReference> references = (ArrayList<ServiceReference>) sre
				.getServiceReferences();
		h.title = references.get(0).getServiceSpecificSensorId(); 

		return h; 
	}

	/**
	 * http://layar.com/documentation/browser/api/getpois-request/
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
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
			// response.sendError(HttpServletResponse.SC_BAD_REQUEST,
			// "Missing parameter 'action'");
			// return;
			log.debug("No action given.");
		}

		String latParam = request.getParameter("lat");
		if (latParam == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Missing parameter 'lat'");
			return;
		}

		String lonParam = request.getParameter("lon");
		if (lonParam == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Missing parameter 'lon'");
			return;
		}

		String radiusParam = request.getParameter("radius");
		if (radiusParam == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Missing parameter 'radius'");
			return;
		}

		/*
		 * optional:
		 */
		String accurary = request.getParameter("accuracy");
		String pageKey = request.getParameter("pageKey");
		// String oauth_consumer_key =
		// request.getParameter("oauth_consumer_key");
		// String oauth_signature_method =
		// request.getParameter("oauth_signature_method");
		String radiolist = request.getParameter("RADIOLIST");
		String searchbox = request.getParameter("SEARCHBOX");
		String custom_slider = request.getParameter("CUSTOM_SLIDER");
		String checkboxlist = request.getParameter("CHECKBOXLIST");
		String localCountryCode = request.getParameter("localCountryCode");
		String alt = request.getParameter("alt"); // current altitude
		String recognizedReferenceImage = request
				.getParameter("recognizedReferenceImage");

		// convert parameters
		float lat = Float.parseFloat(latParam);
		float lon = Float.parseFloat(lonParam);
		double[] center = new double[] { lat, lon };
		float radius = Float.parseFloat(radiusParam);

		// get nearby sensors
		LayarResponse layarResponse = new LayarResponse();
		layarResponse.layer = request.getParameter("layerName");
		if (layarResponse.layer == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Missing parameter 'layerName'");
			return;
		}

		response.setContentType("application/json; charset=utf-8");
		JsonGenerator generator = this.factory.createJsonGenerator(response
				.getWriter());
		generator.useDefaultPrettyPrinter();

		// query SIR
		try {
			Collection<SirPOI> pois = querySIR(center, radius, REQUEST_FORMAT);

			for (SirPOI sirPOI : pois) {
				if (sirPOI instanceof Hotspot) {
					Hotspot h = (Hotspot) sirPOI;
					layarResponse.hotspots.add(h);
				}
			}
		} catch (Exception e) {
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

	public String getLayerName() {
		return this.layerName;
	}

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);

		this.factory = new JsonFactory();

		log.info("Initialized " + this);
	}
}