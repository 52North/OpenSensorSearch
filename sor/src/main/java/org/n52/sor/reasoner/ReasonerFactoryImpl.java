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
package org.n52.sor.reasoner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * 
 * Singleton providing wrapped access to the currently used reasoner as a singleton to avoid multiple
 * downloads of dictionaries that are most probably online.
 * 
 * @author Daniel Nüst
 * 
 */
public class ReasonerFactoryImpl implements IReasonerFactory {

    private static Logger log = LoggerFactory.getLogger(ReasonerFactoryImpl.class);

    /**
     * instance to be a singleton
     */
    private static IReasoner instance;

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sor.reasoner.IReasonerFactory#getReasoner()
     */
    @Override
    public IReasoner getReasoner() {
        if (instance == null) {
            if (log.isDebugEnabled())
                log.debug("No instance of reasoner given yet, creating new one...");
            instance = new JenaReasoner();
        }
        return instance;
    }

}
