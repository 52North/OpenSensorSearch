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

package org.n52.sor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Nüst
 * 
 */
public abstract class SorTestCase extends TestCase {

    public static final String REQUEST_EXAMPLES_GET_FILE = "RequestExamples_GET.txt";

    private static Logger log = LoggerFactory.getLogger(SorTestCase.class);

    /**
     * 
     */
    private static Properties examples;

    /**
     * 
     * @param name
     * @return
     */
    protected static String loadGetRequestExample(String name) {
        return examples.getProperty(name);
    }
    
    /**
     * 
     * @throws IOException
     */
    @Override
    protected void setUp() throws Exception {
        if (PropertiesManager.getInstance() == null) {
            String basepath = "/home/daniel/workspace/SOR/WebContent";
            InputStream configStream = new FileInputStream(basepath + "/WEB-INF/conf/sor.properties");
            PropertiesManager.getInstance(configStream, basepath);

            InputStream exampleStream = new FileInputStream(PropertiesManager.getInstance().getTestRequestPath()
                    + REQUEST_EXAMPLES_GET_FILE);
            examples = new Properties();
            // load properties
            try {
                examples.load(exampleStream);
            }
            catch (IOException e) {
                log.error("Loading examples properties failed");
            }

            configStream.close();
            exampleStream.close();

            log.info("Instatiated PropertiesManager");
        }
    }
}