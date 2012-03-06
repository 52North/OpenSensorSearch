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

package org.n52.sir.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class Util {

    public static SensorDescription getSensorDescription() {
        return new SensorDescription("http://domain.tld:port/path/001",
                                     "This text describes the sensor.",
                                     getBoundingBox());
    }

    private static BoundingBox getBoundingBox() {
        return new BoundingBox(1, 2, 3, 4);
    }

    public static SearchResultElement getSearchResultElement() {
        SearchResultElement resultElement = new SearchResultElement();
        resultElement.setSensorIdInSir("001");
        Collection<ServiceReference> references = new ArrayList<ServiceReference>();
        references.add(getServiceReference());
        resultElement.setServiceReferences(references);
        resultElement.setLastUpdate(new Date());
        resultElement.setSensorDescription(Util.getSensorDescription());

        return resultElement;
    }

    public static SearchResultElement getSearchResultElement2() {
        SearchResultElement resultElement = new SearchResultElement();
        resultElement.setSensorIdInSir("002");
        Collection<ServiceReference> references = new ArrayList<ServiceReference>();
        references.add(getServiceReference());
        resultElement.setServiceReferences(references);
        resultElement.setLastUpdate(new Date());
        resultElement.setSensorDescription(Util.getSensorDescription());

        return resultElement;
    }

    private static ServiceReference getServiceReference() {
        return new ServiceReference(getService(), "urn:sos:001");
    }

    private static Service getService() {
        return new Service("http://host:port/path", "SOS");
    }

    public static SearchResult getSearchResult() {
        SearchResult sr = new SearchResult("http://service.url",
                                           "temperature",
                                           "http://service.url/search?q=temperature",
                                           "Search results for the keyword 'temperature' from Open Sensor Search.",
                                           "52°North",
                                           new Date());

        sr.addResult(getSearchResultElement());
        sr.addResult(getSearchResultElement2());

        return sr;
    }

}
