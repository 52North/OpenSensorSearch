package org.n52.sir.script;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class HarvestApplication extends Application{
	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> s = new HashSet<Class<?>>();
		s.add(HarvestResource.class);
		return s;
	}
}
