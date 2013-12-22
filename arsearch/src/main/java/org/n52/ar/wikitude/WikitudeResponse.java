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

    protected String providerId = "geoviqua"; // TODO set right value
    protected String providerName = "geoviqua";

    WikitudeResponse() {

    }

    WikitudeResponse(Collection< ? extends SirPOI> collection) {
        this.pois = (Collection<WikitudePOI>) collection;
    }

    void toARML(StringBuilder sb) {
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<kml xmlns=\"http://www.opengis.net/kml/2.2\"" + " xmlns:ar=\"http://www.openarml.org/arml/1.0\""
                + " xmlns:wikitude=\"http://www.openarml.org/wikitude/1.0\">");

        sb.append("<Document>");

        sb.append("<ar:provider id=\"" + providerId + "\">");

        sb.append("<ar:name>" + "OpenSensorSearch" + "</ar:name>");
        sb.append("<wikitude:logo>" + WikitudeResponse.logoURL + "</wikitude:logo>");
        sb.append("<wikitude:icon>" + WikitudeResponse.iconURL + "</wikitude:icon>");

        sb.append("</ar:provider>");

        System.out.println("" + pois.size());
        for (WikitudePOI p : pois)
            p.appendARML(sb);

        sb.append("</Document>");
        sb.append("</kml>");
    }

    protected void addPoi(WikitudePOI poi) {
        this.pois.add(poi);
    }
}
