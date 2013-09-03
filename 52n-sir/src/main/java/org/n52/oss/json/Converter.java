
package org.n52.oss.json;

import java.util.ArrayList;
import java.util.Collection;

import org.n52.oss.opensearch.listeners.Tools;
import org.n52.sir.datastructure.SirBoundingBox;
import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.datastructure.SirService;
import org.n52.sir.datastructure.SirServiceReference;
import org.n52.sir.datastructure.SirSimpleSensorDescription;
import org.n52.sir.datastructure.detailed.SirDetailedSensorDescription;
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

        element.setSensorIdInSir(desc.getId());
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

        sre.setSensorIdInSir(sirSearchResultElement.getSensorIdInSir());
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

            String text = Tools.extractDescriptionText(d);
            SimpleSensorDescription sd = new SimpleSensorDescription(d.getSensorDescriptionURL(), text, bbox);
            sre.setSensorDescription(sd);
        }

        return sre;
    }

}
