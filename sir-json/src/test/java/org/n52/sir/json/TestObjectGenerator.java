/**
 * Copyright 2013 52°North Initiative for Geospatial Open Source Software GmbH
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
package org.n52.sir.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class TestObjectGenerator {

    public static SimpleSensorDescription getSensorDescription() {
        return new SimpleSensorDescription("http://domain.tld:port/path/001",
                                     "This text describes the sensor.",
                                     getBoundingBox());
    }

    private static BoundingBox getBoundingBox() {
        return new BoundingBox(1, 2, 3, 4);
    }

    public static SearchResultElement getSearchResultElement() {
        SearchResultElement resultElement = new SearchResultElement();
        resultElement.setSensorId("001");
        Collection<ServiceReference> references = new ArrayList<ServiceReference>();
        references.add(getServiceReference());
        resultElement.setServiceReferences(references);
        resultElement.setLastUpdate(new Date());
        resultElement.setSensorDescription(TestObjectGenerator.getSensorDescription());

        return resultElement;
    }

    public static SearchResultElement getSearchResultElement2() {
        SearchResultElement resultElement = new SearchResultElement();
        resultElement.setSensorId("002");
        Collection<ServiceReference> references = new ArrayList<ServiceReference>();
        references.add(getServiceReference());
        resultElement.setServiceReferences(references);
        resultElement.setLastUpdate(new Date());
        resultElement.setSensorDescription(TestObjectGenerator.getSensorDescription());

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
