/**
 * Copyright 2013 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.oss.guice.log;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

/**
 * See also https://github.com/artgo/guice-slf4j
 * 
 * @author Daniel
 * 
 */
public class Slf4jTypeListener implements TypeListener {

    private static Logger log = LoggerFactory.getLogger(Slf4jTypeListener.class);

    public Slf4jTypeListener() {
        log.info("NEW {}", this);
    }

    @Override
    public <I> void hear(final TypeLiteral<I> type, final TypeEncounter<I> encounter) {
        Class< ? super I> cls = type.getRawType();

        do {
            for (final Field field : cls.getDeclaredFields()) {
                if ( (field.getType() == Logger.class) && field.isAnnotationPresent(InjectLogger.class)) {
                    log.debug("Adding logger to class {}", cls);
                    encounter.register(new Slf4jMembersInjector<I>(field));
                }
            }

            // Got through all parents as well
            cls = cls.getSuperclass();
        } while (cls != null);
    }
}