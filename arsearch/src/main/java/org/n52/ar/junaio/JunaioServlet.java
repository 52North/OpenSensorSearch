/**
 * Copyright 2012 52°North Initiative for Geospatial Open Source Software GmbH
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
		
        p.id = sre.getSensorId();
        
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
        return this.layerName;
    }
}