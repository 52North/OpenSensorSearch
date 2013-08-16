/**
 * ﻿Copyright (C) 2012 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
