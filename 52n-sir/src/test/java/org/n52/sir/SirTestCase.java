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
package org.n52.sir;

import java.io.File;
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
public abstract class SirTestCase extends TestCase {

    public static final String REQUEST_EXAMPLES_GET_FILE = "RequestExamples_GET.txt";

    private static final String WEB_CONTENT_FOLDER = "/home/daniel/workspace/SIR/WebContent";

    private static final String CONFIG_FILE_IN_WEB_CONTENT = "/WEB-INF/conf/sir.config";

    private static final String DB_CONFIG_FILE_IN_WEB_CONTENT = "/WEB-INF/conf/dbsir.config";

    private static Logger log = LoggerFactory.getLogger(SirTestCase.class);

    /**
     * 
     */
    private static Properties examples;

    public static String insertedSensorId;
    
    /**
     * 
     * @throws IOException
     */
    @Override
    protected void setUp() throws Exception {
        if (SirConfigurator.getInstance() == null) {
            String basepath = WEB_CONTENT_FOLDER;
            InputStream configStream = new FileInputStream(basepath + CONFIG_FILE_IN_WEB_CONTENT);
            InputStream dbConfigStream = new FileInputStream(basepath + DB_CONFIG_FILE_IN_WEB_CONTENT);
            SirConfigurator.getInstance(configStream, dbConfigStream, basepath, null);

            InputStream exampleStream = new FileInputStream(SirConfigurator.getInstance().getTestRequestPath()
                    + REQUEST_EXAMPLES_GET_FILE);
            examples = new Properties();
            // load properties
            try {
                examples.load(exampleStream);
            }
            catch (IOException e) {
                log.error("Loading examples file failed for: " + SirConfigurator.getInstance().getTestRequestPath()
                        + REQUEST_EXAMPLES_GET_FILE);
            }

            configStream.close();
            exampleStream.close();

            log.info("Instatiated SirConfigurator for Testing!");
        }
    }

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
     * @param fileName
     * @return
     */
    protected File getPostExampleFile(String fileName) {
        String path = SirConfigurator.getInstance().getTestRequestPath() + fileName;
        return new File(path);
    }
}