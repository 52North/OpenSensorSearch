package org.n52.oss.opensearch.listeners.feed;

import org.n52.oss.opensearch.listeners.OpenSearchListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

public class ListenerModule extends AbstractModule {
    
    private static Logger log = LoggerFactory.getLogger(ListenerModule.class);
    
    @Override
    protected void configure() {
        Multibinder<OpenSearchListener> uriBinder = Multibinder.newSetBinder(binder(), OpenSearchListener.class);
        uriBinder.addBinding().to(AtomListener.class);
        uriBinder.addBinding().to(RssListener.class);
        
        log.debug("Configured {}", this);
    }

}