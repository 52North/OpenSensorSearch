package org.n52.sir.IT;

import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import javax.servlet.UnavailableException;

import org.junit.Test;
import org.n52.sir.SirConfigurator;
import org.n52.sir.ds.IInsertHarvestScriptDAO;
import org.n52.sir.ows.OwsExceptionReport;

public class IInsertHarvestScriptIT {

	/**TODO convert this to the other configuration implemented by Daniel
	 * 
	 */
	@Test
	public void insertScript() throws UnavailableException, OwsExceptionReport {

		if (SirConfigurator.getInstance() == null) {
			InputStream dbStream = ClassLoader
					.getSystemResourceAsStream("prop/db.PROPERTIES");
			InputStream sirStream = ClassLoader
					.getSystemResourceAsStream("prop/sir.PROPERTIES");
			// Read configurator if null
			IInsertHarvestScriptDAO dao = SirConfigurator.getInstance(sirStream, dbStream, null, null).getFactory().insertHarvestScriptDAO();
			String id = dao.insertScript("/scripts/one.js","test", 1);
			assertNotNull(id);
			
		}
		

	}
}
