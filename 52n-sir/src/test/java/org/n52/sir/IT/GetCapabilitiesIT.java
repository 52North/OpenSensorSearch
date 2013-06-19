/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */

/**
 * @author Yakoub
 */

package org.n52.sir.IT;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;

import javax.servlet.UnavailableException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import net.opengis.sensorML.x101.CapabilitiesDocument.Capabilities;

import org.junit.Before;
import org.junit.Test;
import org.n52.sir.SirConfigurator;
import org.n52.sir.client.Client;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.xml.impl.SensorML4DiscoveryValidatorImpl;
import org.x52North.sir.x032.CapabilitiesDocument;

public class GetCapabilitiesIT {
	private void failIfFileNotExists(File f) {
		if (!f.exists())
			fail(f.getName() + " Is missing!");
	}

	private void failIfURLNull(String resource) {
		if (ClassLoader.getSystemResource(resource) == null)
			fail(resource + " Is missing");
	}
	@Before
	public void setup(){
		failIfURLNull("prop/db.PROPERTIES");
		failIfURLNull("prop/sir.PROPERTIES");
		
		if(SirConfigurator.getInstance() == null){
			InputStream dbStream = ClassLoader.getSystemResourceAsStream("prop/db.PROPERTIES");
			InputStream sirStream  = ClassLoader.getSystemResourceAsStream("prop/sir.PROPERTIES");
			try {
				//Read configurator if null 
				SirConfigurator.getInstance(sirStream, dbStream, null,null);
			} catch (UnavailableException | OwsExceptionReport e) {
				// TODO Auto-generated catch block
				fail(e.toString());
			}
		}
		
	}

	@Test
	public void readFile() {

		failIfURLNull("Requests/GetCapabilities.xml");

		File f = new File(ClassLoader.getSystemResource("Requests/GetCapabilities.xml")
				.getFile());
		
		StringBuilder request = new StringBuilder();
		try{
		BufferedReader br = new BufferedReader(new FileReader(f));
		
		String k = "";
		
		System.out.println(SirConfigurator.getInstance() == null);
		
		while((k=br.readLine())!=null)request.append(k);
		
		String response = Client.sendPostRequest(request.toString());
		CapabilitiesDocument doc = CapabilitiesDocument.Factory.parse(response);
		
		if(doc.validate()==false)
			fail("Invalid validator");
		
		} catch(Exception e){
			e.printStackTrace();
			fail(e.toString());
		}
	}
}
