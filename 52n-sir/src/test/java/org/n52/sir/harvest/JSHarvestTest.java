package org.n52.sir.harvest;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;
import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.datastructure.detailed.SirDetailedSensorDescription;
import org.n52.sir.ds.solr.SOLRSearchSensorDAO;
import org.n52.sir.ds.solr.SolrConnection;
import org.n52.sir.harvest.exec.IJSExecute;
import org.n52.sir.harvest.exec.impl.RhinoJSExecute;
import org.n52.sir.ows.OwsExceptionReport;

public class JSHarvestTest {
	@Test
	public void harvestJSFile() throws OwsExceptionReport, SolrServerException, IOException {
		File harvestScript = new File(ClassLoader.getSystemResource(
				"Requests/harvestScript.js").getFile());

		IJSExecute execEngine = new RhinoJSExecute();
		String id = execEngine.execute(harvestScript);

		assertNotNull(id);

		SOLRSearchSensorDAO searchDAO = new SOLRSearchSensorDAO();
		Collection<SirSearchResultElement> elements = searchDAO.searchByID(id);
		assertNotEquals(elements.size(),0);
		
		SirSearchResultElement element = elements.iterator().next();
		SirDetailedSensorDescription description = (SirDetailedSensorDescription)element.getSensorDescription();
		Collection<Object> keywords = description.getKeywords();
		
		assertTrue(keywords.contains("javascript"));
		assertTrue(keywords.contains("harvest"));
		
		assertTrue(description.getLocation().equals("3,1.5"));
		
		Collection<String> contacts = description.getContacts();
		

		assertTrue(contacts.contains("52north"));
		assertTrue(contacts.contains("rhino"));
		
		new SolrConnection().deleteByQuery("id:"+id);

		
	}

}
