package org.n52.oss.opensearch.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

public class ListenerModule extends AbstractModule {
    
    private static Logger log = LoggerFactory.getLogger(ListenerModule.class);
    
    @Override
    protected void configure() {
        Multibinder<OpenSearchListener> uriBinder = Multibinder.newSetBinder(binder(), OpenSearchListener.class);
        uriBinder.addBinding().to(HtmlListener.class);
        uriBinder.addBinding().to(JsonListener.class);
        
        log.debug("Configured {}", this);
    }

}