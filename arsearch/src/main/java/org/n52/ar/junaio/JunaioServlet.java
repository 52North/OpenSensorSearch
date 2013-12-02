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

package org.n52.ar.junaio;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

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

/**
 * 
 * 
 * @author Arne de Wall
 */
public class JunaioServlet extends SirCallbackServlet {

	
	private String layerName = "139554";
    /**
     * 
     */
    private static final long serialVersionUID = 8327259238862283021L;
    
    private static Logger log = LoggerFactory.getLogger(JunaioServlet.class);

    public JunaioServlet() {
        log.debug("NEW {}", this);
    }

    @Override
    protected SirPOI createPOI(SearchResultElement sre) {
        JunaioPOI p = new JunaioPOI();
        
		ArrayList<ServiceReference> references = (ArrayList<ServiceReference>) sre
				.getServiceReferences();
		p.title = references.get(0).getServiceSpecificSensorId();
		
        p.id = sre.getSensorIdInSir();
        
        // location paramters
        double[] latLon = sre.getSensorDescription().getBoundingBox().getCenter();
        p.lat = latLon[0];
        p.lon = latLon[1];
        p.alt = 0;
        
        // Description 
        p.description = sre.getSensorDescription().getText();
        
        return p;
    }

    /**
     * http://www.junaio.com/develop/quickstart/tutorials/lbs-quickstart/lbs-001-hello-world/
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userAgent = request.getHeader("User-agent");
        log.debug("User-agent={}", userAgent);
        
        // TODO implement callback URL
        double latitude = 51.965344, longitude = 7.600003;
        
        // get location parameters
        String lParameter = request.getParameter("l");
        if(lParameter != null){
        	StringTokenizer st = new StringTokenizer(lParameter, ",");
        	latitude = Double.parseDouble(st.nextToken());
        	longitude = Double.parseDouble(st.nextToken());
        }
        double[] center = new double[]{latitude, longitude};
        
        float radius = 10000;
        // get radius
        String pParameter = request.getParameter("p");
        if(pParameter != null){
        	radius = Float.parseFloat(pParameter);
        }
         
        int poiMaximum = Integer.MAX_VALUE;
        // get poi maximum
        String mProperty = request.getParameter("m");
        if(mProperty != null){
        	poiMaximum = Integer.parseInt(mProperty);
        }
        
        response.setContentType("application/xml; charset=utf-8");
        // query SIR
        Collection<SirPOI> pois = querySIR(center, radius, CONTENT_TYPE);

        // write response
        Writer out = response.getWriter();
        response.setContentType(CONTENT_TYPE_XML);
        
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<results>");
        
        for(SirPOI p : pois)
        	((JunaioPOI) p).appendXML(sb); 
        
        sb.append("</results>");
        out.write(sb.toString());
       
        // clean up 
        response.flushBuffer();
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig); 

        log.info("Initialized " + this);
    }
    
    public String getLayerName(){
    	return layerName; 
    }
}