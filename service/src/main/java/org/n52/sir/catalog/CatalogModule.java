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
package org.n52.sir.catalog;

import org.n52.sir.catalog.csw.CswFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;

/**
 * 
 * @author Daniel
 * 
 */
public class CatalogModule extends AbstractModule {

    private static Logger log = LoggerFactory.getLogger(CatalogModule.class);

    @Override
    protected void configure() {
        bind(ICatalogStatusHandler.class).to(CatalogStatusHandlerImpl.class);
        bind(ICatalogFactory.class).to(CswFactory.class);

        log.debug("Configured {}", this);
    }

}
