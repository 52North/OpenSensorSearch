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
