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

package org.n52.oss;

import org.n52.oss.guice.log.InjectLogger;
import org.n52.oss.guice.log.Slf4jTypeListener;
import org.slf4j.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.matcher.Matchers;

public class InjectionLogger {

    // @BeforeClass
    public static void configure() {
        Guice.createInjector(new AbstractModule() {

            @Override
            protected void configure() {
                bindListener(Matchers.any(), new Slf4jTypeListener());
            }
        });
    }

    // @Test
    public void loggerIsInjected() {
        ClassWithLogger cwl = new ClassWithLogger();
        cwl.doSomething();

        // TODO somehow check if the log is correctly added to stdout
    }

    private static class ClassWithLogger {

        @InjectLogger
        private static Logger log;

        public ClassWithLogger() {
            log.debug("NEW {}", this);
        }

        public void doSomething() {
            log.info("Do something with a logger...");

        }

    }

}
