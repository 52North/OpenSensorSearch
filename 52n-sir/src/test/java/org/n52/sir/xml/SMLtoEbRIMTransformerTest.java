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
package org.n52.sir.xml;


import java.io.File;
import java.io.IOException;

import javax.xml.transform.Result;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;

import org.apache.xmlbeans.XmlException;
import org.junit.Test;
import org.n52.sir.util.XmlTools;
import org.n52.sir.xml.impl.SMLtoEbRIMTransformer;

import x0.oasisNamesTcEbxmlRegrepXsdRim3.RegistryPackageDocument;
import x0.oasisNamesTcEbxmlRegrepXsdRim3.RegistryPackageType;

public class SMLtoEbRIMTransformerTest {


	@Test
	public void testTransform() throws InstantiationError, XmlException, IOException, TransformerException {
		String[] s = new String[] { "IFGI_HWS1-discoveryprofile.xml",
				"FH_HWS1-discoveryprofile.xml", "FH_HWS1-discoveryprofile.xml" };

		
		
		File xslt_dir = new File(ClassLoader.getSystemResource("xslt/")
				.getFile());
		File transformations = new File(ClassLoader.getSystemResource("transformation/").getFile());
		
		for (int i = 0; i < s.length; i++){
			File file = new File(ClassLoader.getSystemResource(
					"transformation/" + s[i]).getFile());
			testTransformation(file.getName(), transformations.getAbsolutePath()+"/", xslt_dir.getAbsolutePath()+"/");
		}

	}



	private static void testTransformation(String inputFile,
			String transformationDir, String xsltDir) throws InstantiationError, XmlException, IOException, TransformerException {
		SMLtoEbRIMTransformer transformer = new SMLtoEbRIMTransformer(
				xsltDir);

		
			// test the input document
			

			transformer.setValidating(false);

			Result r = transformer.transform(transformationDir + inputFile);
			StreamResult sr = (StreamResult) r;

			String outputString = sr.getWriter().toString();

			RegistryPackageDocument rpd = RegistryPackageDocument.Factory
					.parse(outputString);
			RegistryPackageType rp = rpd.getRegistryPackage();

			
			System.out.println(XmlTools.validateAndIterateErrors(rp));
			
		
	}

}
