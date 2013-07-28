package org.n52.sir;

import org.n52.sir.harvest.exec.IJSExecute;
import org.n52.sir.harvest.exec.impl.RhinoJSExecute;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

public class GuiceConfigurator extends GuiceServletContextListener{

	@Override
	protected Injector getInjector() {
		return Guice.createInjector(new AbstractModule() {
			
			@Override
			protected void configure() {
					bind(IJSExecute.class).to(RhinoJSExecute.class);
			}
		});
	}
}
