package org.n52.sir.harvest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Test;
import org.n52.sir.harvest.exec.IJSExecute;
import org.n52.sir.harvest.exec.impl.RhinoJSExecute;

public class IJSExecuteTest {

	@Test
	public void testSimple() {
		// simple test for basic arithmetics
		IJSExecute exec = new RhinoJSExecute();
		String result = exec.execute("function f(x){return x+1;};f(2);");
		assertEquals(Integer.parseInt(result), 3);
	}

	@Test
	public void testInsertSensor() {
		File f = new File(ClassLoader.getSystemResource("js/IJSExecute.js")
				.getFile());
		IJSExecute exec = new RhinoJSExecute();
		String result = exec.execute(f);
		assertNotNull(result);
	}

}
