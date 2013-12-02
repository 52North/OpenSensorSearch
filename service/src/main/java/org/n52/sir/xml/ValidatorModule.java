/**
 * ﻿Copyright (C) 2012 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
