
package org.n52.oss.guice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

/**
 * http://docs.oracle.com/javase/6/docs/api/java/util/ServiceLoader.html
 *
 * @author Daniel
 *
 */
public class ServiceLoaderConfigurationModule extends AbstractModule {

    private static Logger log = LoggerFactory.getLogger(ServiceLoaderConfigurationModule.class);

    private static Collection<String> loadedModules = new ArrayList<>();
    
    @Override
    protected void configure() {
        for (Module m : ServiceLoader.load(Module.class)) {
            install(m);
            loadedModules.add(m.toString());
            log.debug("Installed {}", m);
        }
        
        log.info("Configured {}, modules: {}", this, Arrays.toString(loadedModules.toArray()));
    }
}
