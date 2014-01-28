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
package org.n52.sir.catalogconnection.impl;

import org.n52.sir.catalogconnection.CatalogConnectionScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Provider;

/**
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class CatalogConnectionSchedulerProvider implements Provider<CatalogConnectionScheduler> {

    private static Logger log = LoggerFactory.getLogger(CatalogConnectionSchedulerProvider.class);

    public CatalogConnectionSchedulerProvider() {
        log.info("NEW {}", this);
    }

    @Override
    public CatalogConnectionScheduler get() {
        CatalogConnectionScheduler ccs = new CatalogConnectionSchedulerImpl();
        return ccs;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CatalogConnectionSchedulerFactoryImpl [");
        sb.append("...");
        sb.append("]");
        return sb.toString();
    }
}
