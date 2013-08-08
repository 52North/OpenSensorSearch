package org.n52.sir.harvest;

import static org.junit.Assert.assertFalse;

import java.io.File;

import org.junit.Test;
import org.n52.sir.harvest.exec.IJSExecute;
import org.n52.sir.harvest.exec.impl.RhinoJSExecute;

public class OWSHarvestTest {

//	@Test
	public void harvestOWS(){
		File harvestScript = new File(ClassLoader.getSystemResource(
				"Requests/harvestOWS.js").getFile());
		IJSExecute execEngine = new RhinoJSExecute();
		String sensors = execEngine.execute(harvestScript);
		assertFalse(sensors.equals("-1"));
		
	}
}
