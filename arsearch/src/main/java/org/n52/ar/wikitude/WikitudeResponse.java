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
package org.n52.ar.wikitude;

import java.util.Collection;

import org.n52.ar.SirPOI;

/**
 * 
 * @author Arne de Wall
 *
 */
public class WikitudeResponse {
	protected static String logoURL = "http://52north.org/templates/52n/images/52n-logo.gif";
	protected static String iconURL = "http://localhost:9090/ar/images/icon_high.png";
	
	
	private Collection<WikitudePOI> pois;
	
	protected String providerId = "geoviqua"; //TODO set right value
	protected String providerName = "geoviqua";
	

	
	WikitudeResponse(){
		
	}
	WikitudeResponse(Collection<? extends SirPOI> collection){
		this.pois = (Collection<WikitudePOI>) collection;
	} 
	
	void toARML(StringBuilder sb){
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<kml xmlns=\"http://www.opengis.net/kml/2.2\""
					+	" xmlns:ar=\"http://www.openarml.org/arml/1.0\""
					+	" xmlns:wikitude=\"http://www.openarml.org/wikitude/1.0\">");
		
		sb.append("<Document>");
		
		sb.append("<ar:provider id=\"" + providerId + "\">");
		
		sb.append("<ar:name>" + "OpenSensorSearch"+ "</ar:name>");
		sb.append("<wikitude:logo>" + WikitudeResponse.logoURL + "</wikitude:logo>");
		sb.append("<wikitude:icon>" + WikitudeResponse.iconURL + "</wikitude:icon>");
		
		sb.append("</ar:provider>"); 
		
		System.out.println("" + pois.size());
		for(WikitudePOI p : pois)
			p.appendARML(sb);
		
		sb.append("</Document>");
		sb.append("</kml>");
	}
	
	protected void addPoi(WikitudePOI poi){
		this.pois.add(poi);
	}
}
