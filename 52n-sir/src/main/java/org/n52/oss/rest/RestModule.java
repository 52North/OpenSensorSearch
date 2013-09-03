package org.n52.oss.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;

public class RestModule extends AbstractModule {

    private static Logger log = LoggerFactory.getLogger(RestModule.class);

    @Override
    protected void configure() {
        bind(SensorResource.class);

        log.debug("Configured {}", this);
    }

}
