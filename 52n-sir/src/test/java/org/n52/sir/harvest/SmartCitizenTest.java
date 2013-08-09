package org.n52.sir.harvest;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;
import org.n52.sir.harvest.exec.IJSExecute;
import org.n52.sir.harvest.exec.impl.RhinoJSExecute;
import org.n52.sir.ows.OwsExceptionReport;

public class SmartCitizenTest {
	@Test
	public void harvestJSFile() throws OwsExceptionReport, SolrServerException, IOException {
		File harvestScript = new File(ClassLoader.getSystemResource(
				"Requests/harvestSmartCitizen.js").getFile());
		IJSExecute execEngine = new RhinoJSExecute();
		String id = execEngine.execute(harvestScript);
		assertFalse(id.equals("-1"));
		System.out.println(id);
		
				
	}

}
