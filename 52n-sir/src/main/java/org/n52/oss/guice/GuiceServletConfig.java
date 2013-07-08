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

package org.n52.oss.guice;

import java.util.Arrays;

import javax.servlet.annotation.WebListener;

import org.n52.oss.batch.TimerModule;
import org.n52.oss.opensearch.OpenSearchModule;
import org.n52.oss.sir.SirConfig;
import org.n52.oss.sir.SirModule;
import org.n52.oss.sir.operations.OperationsModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * 
 * @author Daniel
 * 
 */
@WebListener
public class GuiceServletConfig extends GuiceServletContextListener {

    private static Logger log = LoggerFactory.getLogger(GuiceServletConfig.class);

    public GuiceServletConfig() {
        super();

        log.debug("NEW {}", this);
    }

    @Override
    protected Injector getInjector() {
        Injector inj = Guice.createInjector(new SirModule(),
                                            new OperationsModule(),
                                            new OpenSearchModule(),
                                            new TimerModule());
        System.out.println("All bindings inj: " + Arrays.toString(inj.getAllBindings().entrySet().toArray()));

        SirConfig instance = inj.getInstance(SirConfig.class);
        System.out.println(instance);

        return inj;
    }

}
