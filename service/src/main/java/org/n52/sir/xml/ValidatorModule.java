
package org.n52.sir.xml;

import java.util.HashSet;
import java.util.Set;

import org.n52.sir.xml.IProfileValidator.ValidatableFormatAndProfile;
import org.n52.sir.xml.impl.SensorML4DiscoveryValidatorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

public class ValidatorModule extends AbstractModule {

    private static Logger log = LoggerFactory.getLogger(ValidatorModule.class);

    @Override
    protected void configure() {
        Multibinder<IProfileValidator> listenerBinder = Multibinder.newSetBinder(binder(), IProfileValidator.class);
        listenerBinder.addBinding().to(SensorML4DiscoveryValidatorImpl.class);

        log.debug("configured {}", this);
    }

    public static IProfileValidator getFirstMatchFor(Set<IProfileValidator> validators,
                                                     ValidatableFormatAndProfile profile) {
        Set<IProfileValidator> set = getFor(validators, profile);
        if ( !set.isEmpty())
            return set.iterator().next();

        return null;
    }

    public static Set<IProfileValidator> getFor(Set<IProfileValidator> validators, ValidatableFormatAndProfile profile) {
        Set<IProfileValidator> filtered = new HashSet<>();
        for (IProfileValidator v : validators) {
            if (v.validates(profile))
                filtered.add(v);
        }
        return filtered;
    }

}
