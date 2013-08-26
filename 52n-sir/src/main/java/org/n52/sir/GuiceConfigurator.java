<<<<<<< HEAD
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
package org.n52.sir;

import org.n52.sir.harvest.exec.IJSExecute;
import org.n52.sir.harvest.exec.impl.RhinoJSExecute;
import org.n52.sir.script.HarvestResource;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.servlet.GuiceServletContextListener;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

@Singleton
public class GuiceConfigurator extends GuiceServletContextListener {

	@Override
	protected Injector getInjector() {
		return Guice.createInjector(new JerseyServletModule() {
			@Override
			protected void configureServlets() {
				bind(HarvestResource.class);
				bind(IJSExecute.class).to(RhinoJSExecute.class);
				bind(SchedulerFactory.class).to(StdSchedulerFactory.class).in(Singleton.class);
				bind(
						com.sun.jersey.spi.container.servlet.ServletContainer.class)
						.in(Singleton.class);
				serve("/harvest/*").with(GuiceContainer.class);
			}

		});
	}
}
=======
package org.n52.sir;

import java.util.HashMap;
import java.util.Map;

import org.n52.sir.api.IdentifierGenerator;
import org.n52.sir.ds.IInsertSensorInfoDAO;
import org.n52.sir.ds.solr.SOLRInsertSensorInfoDAO;
import org.n52.sir.harvest.exec.IJSExecute;
import org.n52.sir.harvest.exec.impl.RhinoJSExecute;
import org.n52.sir.script.HarvestResource;
import org.n52.sir.util.ShortAlphanumericIdentifierGenerator;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.servlet.GuiceServletContextListener;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.spi.container.servlet.ServletContainer;

@Singleton
public class GuiceConfigurator extends GuiceServletContextListener {

	@Override
	protected Injector getInjector() {
		return Guice.createInjector(new JerseyServletModule() {
			@Override
			protected void configureServlets() {
				bind(HarvestResource.class);
				bind(IJSExecute.class).to(RhinoJSExecute.class);
				bind(IdentifierGenerator.class).to(ShortAlphanumericIdentifierGenerator.class);
				//TODO Daniel : change this to allow for multiple binding for the same interface item:COD9
				bind(IInsertSensorInfoDAO.class).to(SOLRInsertSensorInfoDAO.class);
				bind(SchedulerFactory.class).to(StdSchedulerFactory.class).in(Singleton.class);
				bind(
						com.sun.jersey.spi.container.servlet.ServletContainer.class)
						.in(Singleton.class);
				Map<String,String> map = new HashMap<String	, String>();
				map.put(ServletContainer.JSP_TEMPLATES_BASE_PATH, "/WEB-INF/jsp/");
				map.put(ServletContainer.FEATURE_FILTER_FORWARD_ON_404, "true");
				serve("/harvest/*").with(GuiceContainer.class,map);
			}

		});
	}
}
>>>>>>> harvestCallback
