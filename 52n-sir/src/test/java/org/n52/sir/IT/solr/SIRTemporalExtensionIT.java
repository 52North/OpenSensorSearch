package org.n52.sir.IT.solr;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.UnavailableException;

import org.apache.http.HttpException;
import org.apache.xmlbeans.XmlException;
import org.junit.Before;
import org.junit.Test;
import org.n52.sir.SirConfigurator;
import org.n52.sir.client.Client;
import org.n52.sir.ows.OwsExceptionReport;
import org.x52North.sir.x032.SearchCriteriaDocument.SearchCriteria;
import org.x52North.sir.x032.SearchSensorRequestDocument;
import org.x52North.sir.x032.SearchSensorRequestDocument.SearchSensorRequest;

public class SIRTemporalExtensionIT {
	/**
	 * TODO Remove this configruation - replace by automatic configurations
	 * 
	 */
	@Before
	public void configure() throws UnavailableException, OwsExceptionReport {
		if (SirConfigurator.getInstance() == null) {
			InputStream dbStream = ClassLoader
					.getSystemResourceAsStream("prop/db.PROPERTIES");
			InputStream sirStream = ClassLoader
					.getSystemResourceAsStream("prop/sir.PROPERTIES");
			// Read configurator if null
			SirConfigurator.getInstance(sirStream, dbStream, null, null);

		}
	}

	@Test
	public void sendSearchRequest() throws IOException, OwsExceptionReport,
			HttpException, URISyntaxException, XmlException {
		SearchSensorRequestDocument doc = SearchSensorRequestDocument.Factory
				.newInstance();
		SearchSensorRequest request = doc.addNewSearchSensorRequest();
		SearchCriteria crit = SearchCriteria.Factory.newInstance();
		crit.setSearchTextArray(new String[]{"pre"});
		request.setSearchCriteria(crit);
		Client.xSendPostRequest(doc,new URI("http://localhost:8080/SIR"));
	}

}
