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

package org.n52.oss.guice;

import org.n52.oss.opensearch.OpenSearchServlet;
import org.n52.sir.harvest.exec.IJSExecute;
import org.n52.sir.harvest.exec.impl.RhinoJSExecute;
import org.n52.sir.script.HarvestResource;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class OssServletModule extends JerseyServletModule {

    private static Logger log = LoggerFactory.getLogger(OssServletModule.class);

    public OssServletModule() {
        super();
    }

    @Override
    protected void configureServlets() {
        String basepath = getServletContext().getRealPath("/");
        bindConstant().annotatedWith(Names.named("context.basepath")).to(basepath);

        // install(new FactoryModuleBuilder().implement(ApplicationConstants.class,
        // PropertyApplicationConstants.class).build(ConfigFactory.class));

        bind(HarvestResource.class);
        bind(IJSExecute.class).to(RhinoJSExecute.class);
        bind(SchedulerFactory.class).to(StdSchedulerFactory.class).in(Singleton.class);
        bind(com.sun.jersey.spi.container.servlet.ServletContainer.class).in(Singleton.class);

        serve("/harvest/*").with(GuiceContainer.class);

        // http://code.google.com/p/google-guice/wiki/ServletModule

        serve("/autocomplete*").with(GuiceContainer.class);

        // TODO Daniel: get opensearchservlet to run with guice
        // bind(ISearchSensorDAO.class).to(PGSQLSearchSensorDAO.class);

        serve("/opensearch*").with(OpenSearchServlet.class);

        log.info("configured {} with context {}", this, getServletContext());
    }

}
