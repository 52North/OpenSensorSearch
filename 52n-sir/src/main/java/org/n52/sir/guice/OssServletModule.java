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

package org.n52.sir.guice;

import org.n52.sir.harvest.exec.IJSExecute;
import org.n52.sir.harvest.exec.impl.RhinoJSExecute;
import org.n52.sir.opensearch.OpenSearchServlet;
import org.n52.sir.script.HarvestResource;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import com.google.inject.Singleton;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class OssServletModule extends JerseyServletModule {

    @Override
    protected void configureServlets() {
        bind(HarvestResource.class);
        bind(IJSExecute.class).to(RhinoJSExecute.class);
        bind(SchedulerFactory.class).to(StdSchedulerFactory.class).in(Singleton.class);
        bind(com.sun.jersey.spi.container.servlet.ServletContainer.class).in(Singleton.class);

        serve("/harvest/*").with(GuiceContainer.class);

        // TODO Daniel: get opensearchservlet to run with guice
        // serve("/opensearch*").with(OpenSearchServlet.class);
    }

}
