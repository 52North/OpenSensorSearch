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
package org.n52.oss.json;

import java.util.ArrayList;
import java.util.Collection;

import org.n52.oss.opensearch.listeners.OpenSearchTools;
import org.n52.oss.sir.api.SirBoundingBox;
import org.n52.oss.sir.api.SirDetailedSensorDescription;
import org.n52.oss.sir.api.SirSearchResultElement;
import org.n52.oss.sir.api.SirSensor;
import org.n52.oss.sir.api.SirService;
import org.n52.oss.sir.api.SirServiceReference;
import org.n52.oss.sir.api.SirSimpleSensorDescription;
import org.n52.sir.json.BoundingBox;
import org.n52.sir.json.SearchResultElement;
import org.n52.sir.json.Service;
import org.n52.sir.json.ServiceReference;
import org.n52.sir.json.SimpleSensorDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Converter {

    private static Logger log = LoggerFactory.getLogger(Converter.class);

    public Converter() {
        //
    }

    public SearchResultElement convert(SirSearchResultElement sirSearchResultElement, boolean detailed) {
        if (detailed && sirSearchResultElement.getSensorDescription() instanceof SirDetailedSensorDescription) {
            SirDetailedSensorDescription desc = (SirDetailedSensorDescription) sirSearchResultElement.getSensorDescription();

            return createDetailedResult(desc);
        }

        return createResult(sirSearchResultElement);
    }

    private SearchResultElement createDetailedResult(SirDetailedSensorDescription desc) {
        log.debug("Adding a detailed sensor profile");

        SearchResultElement element = new SearchResultElement();

        element.setSensorId(desc.getId());
        log.trace("Begin date:" + desc.getBegineDate());
        element.setBeginDate(desc.getBegineDate());
        element.setEndDate(desc.getEndDate());

        if (desc.getInputs() != null)
            element.setInputs(desc.getInputs());
        if (desc.getOutputs() != null)
            element.setOutputs(desc.getOutputs());
        if (desc.getIdentifiers() != null)
            element.setIdentifiers(desc.getIdentifiers());
        if (desc.getClassifiers() != null)
            element.setClassifiers(desc.getClassifiers());
        if (desc.getContacts() != null)
            element.setContacts(desc.getContacts());
        if (desc.getKeywords() != null)
            element.setKeywords(desc.getKeywords());

        return element;
    }

    private SearchResultElement createResult(SirSearchResultElement sirSearchResultElement) {
        SearchResultElement sre = new SearchResultElement();

        sre.setSensorId(sirSearchResultElement.getSensorId());
        sre.setLastUpdate(sirSearchResultElement.getLastUpdate());

        Collection<ServiceReference> sr = new ArrayList<>();
        Collection<SirServiceReference> serviceReferences = sirSearchResultElement.getServiceReferences();

        if (serviceReferences != null) {
            for (SirServiceReference sirServiceReference : serviceReferences) {
                SirService service = sirServiceReference.getService();
                sr.add(new ServiceReference(new Service(service.getUrl(), service.getType()),
                                            sirServiceReference.getServiceSpecificSensorId()));
            }
            sre.setServiceReferences(sr);
        }

        if (sirSearchResultElement.getSensorDescription() instanceof SirSimpleSensorDescription) {
            SirSimpleSensorDescription d = (SirSimpleSensorDescription) sirSearchResultElement.getSensorDescription();

            SirBoundingBox b = d.getBoundingBox();
            BoundingBox bbox = null;
            if (b != null) {
                bbox = new BoundingBox(b.getEast(), b.getSouth(), b.getWest(), b.getNorth());
                bbox.setSrid(b.getSrid());
            }

            String text = OpenSearchTools.extractDescriptionText(d);
            SimpleSensorDescription sd = new SimpleSensorDescription(d.getSensorDescriptionURL(), text, bbox);
            sre.setSensorDescription(sd);
        }

        return sre;
    }

    public SearchResultElement convert(SirSensor decoded, boolean detailed) {
        SearchResultElement sre = new SearchResultElement();

        sre.setSensorId(decoded.getInternalSensorID());
        // sre.setLastUpdate(.getLastUpdate());

        Collection<ServiceReference> sr = new ArrayList<>();
        // Collection<SirServiceReference> serviceReferences = decoded.get

        // if (serviceReferences != null) {
        // for (SirServiceReference sirServiceReference : serviceReferences) {
        // SirService service = sirServiceReference.getService();
        // sr.add(new ServiceReference(new Service(service.getUrl(), service.getType()),
        // sirServiceReference.getServiceSpecificSensorId()));
        // }
        // sre.setServiceReferences(sr);
        // }

        // if (sirSearchResultElement.getSensorDescription() instanceof SirSimpleSensorDescription) {
        // SirSimpleSensorDescription d = (SirSimpleSensorDescription)
        // sirSearchResultElement.getSensorDescription();
        //
        // SirBoundingBox b = d.getBoundingBox();
        // BoundingBox bbox = null;
        // if (b != null) {
        // bbox = new BoundingBox(b.getEast(), b.getSouth(), b.getWest(), b.getNorth());
        // bbox.setSrid(b.getSrid());
        // }
        //
        // String text = OpenSearchTools.extractDescriptionText(d);
        // SimpleSensorDescription sd = new SimpleSensorDescription(d.getSensorDescriptionURL(), text, bbox);
        // sre.setSensorDescription(sd);
        // }

        // TODO implement conversion to Json

        return sre;
    }

}
