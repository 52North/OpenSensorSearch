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
