
package org.n52.sir.harvest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;
import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.datastructure.detailed.SirDetailedSensorDescription;
import org.n52.sir.ds.solr.SOLRSearchSensorDAO;
import org.n52.sir.harvest.exec.IJSExecute;
import org.n52.sir.harvest.exec.impl.RhinoJSExecute;
import org.n52.sir.ows.OwsExceptionReport;

public class ThingSpeakTest {
    @Test
    public void harvestJSFile() throws OwsExceptionReport, SolrServerException, IOException {
        File harvestScript = new File(ClassLoader.getSystemResource("Requests/harvestThingSpeak.js").getFile());
        IJSExecute execEngine = new RhinoJSExecute();
        String id = execEngine.execute(harvestScript);
        assertFalse(id.equals("-1"));

        // FIXME unit test cannot rely on database running
        // TODO inject a dummy database to RhinoJSExecute, e.g. using http://code.google.com/p/mockito/, and
        // as that dummy object if the sensors were harvested

        SOLRSearchSensorDAO dao = new SOLRSearchSensorDAO();
        Collection<SirSearchResultElement> elements = dao.searchByID(id);
        assertNotEquals(elements.size(), 0);

        SirSearchResultElement element = elements.iterator().next();
        SirDetailedSensorDescription description = (SirDetailedSensorDescription) element.getSensorDescription();

        assertTrue(description.getDescription().equals("Data logger for a light sensor"));

        assertTrue(description.getLocation().equals("40.4126,-79.6493"));

    }

}
