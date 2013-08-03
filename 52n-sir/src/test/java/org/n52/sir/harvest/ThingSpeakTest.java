package org.n52.sir.harvest;

import java.io.File;
import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;
import org.n52.sir.harvest.exec.IJSExecute;
import org.n52.sir.harvest.exec.impl.RhinoJSExecute;
import org.n52.sir.ows.OwsExceptionReport;

public class ThingSpeakTest {
	@Test
	public void harvestJSFile() throws OwsExceptionReport, SolrServerException, IOException {
		File harvestScript = new File(ClassLoader.getSystemResource(
				"Requests/harvestThingSpeak.js").getFile());
		IJSExecute execEngine = new RhinoJSExecute();
		String id = execEngine.execute(harvestScript);
		System.out.println(id);
	}

}
