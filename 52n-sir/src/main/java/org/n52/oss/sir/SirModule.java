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

package org.n52.oss.sir;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import javax.servlet.UnavailableException;

import org.n52.sir.SIR;
import org.n52.sir.SirConfigurator;
import org.n52.sir.ds.IDAOFactory;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.util.jobs.IJobSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.google.inject.servlet.ServletModule;

/**
 * 
 * @author Daniel
 * 
 */
public class SirModule extends ServletModule {

    private static Logger log = LoggerFactory.getLogger(SirModule.class);

    private static final String PROPERTIES_FILE_NAME = "/sir.properties";

    private static final String SIR_ENDPOINT = "SIR_ENDPOINT";

    private static Properties props;

    @Override
    protected void configureServlets() {
        log.debug("Configure servlets...");

        props = new Properties();
        try (InputStream in = SirModule.class.getResourceAsStream(PROPERTIES_FILE_NAME)) {
            props.load(in);
        }
        catch (IOException e) {
            log.error("Could not load properties", e);
        }

        Names.bindProperties(binder(), props);

        log.trace("Bound properties: {}", Arrays.deepToString(props.entrySet().toArray()));

        // serveRegex(".+(?<!\\.(html|css|png|jpg))").with(StaticFileServlet.class);

        String path = props.getProperty(SIR_ENDPOINT);
        serve("/" + path + "*").with(SIR.class);
        // filter("/sir*").through(SIR.class); // a filter might be the solution to serving static content
        // with default servlet

        // bind(IDAOFactory.class).to(PGSQLDAOFactory.class); // conflicts with @Provides
    }

    @Provides
    public SirConfig provideConfig() {
        try {
            SirConfig c = SirConfigurator.getInstance(props);
            return c;
        }
        catch (UnavailableException | OwsExceptionReport e) {
            log.error("Could not create instance of SirConfig", e);
            return null;
        }
    }

    @Provides
    public IDAOFactory provideDaoFactory() {
        IDAOFactory f = provideConfig().getFactory();
        return f;
    }

    @Provides
    public IJobSchedulerFactory provideJobSchedulerFactory() {
        IJobSchedulerFactory f = provideConfig().getJobSchedulerFactory();
        return f;
    }

}
