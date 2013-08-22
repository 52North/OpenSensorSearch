
package org.n52.sir;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.n52.oss.config.ConfigModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class SirModule extends AbstractModule {

    private static Logger log = LoggerFactory.getLogger(SirModule.class);

    @Override
    protected void configure() {
        try {
            Properties sirProps = loadProperties("/prop/sir.properties");
            Names.bindProperties(binder(), sirProps);

            Properties dbProps = loadProperties("/prop/db.properties");
            Names.bindProperties(binder(), dbProps);

            log.debug("Loaded and bound properties: \n    {}\n    {}", sirProps, dbProps);
        }
        catch (IOException e) {
            log.error("Could not load properties.", e);
        }
    }

    private static Properties loadProperties(String name) throws IOException {
        log.trace("Loading properties for {}", name);

        Properties properties = new Properties();
        ClassLoader loader = ConfigModule.class.getClassLoader();
        URL url = loader.getResource(name);
        properties.load(url.openStream());

        log.trace("Loaded properties: {}", properties);
        return properties;
    }
}
