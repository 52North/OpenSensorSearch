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

package org.n52.sir.util;

import java.util.ArrayList;
import java.util.Collection;

import org.n52.sir.SirConfigurator;
import org.n52.sir.datastructure.SirSearchCriteria_Phenomenon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * 
 * @author Daniel Nüst
 * 
 */
public class SORTools {

    private static Logger log = LoggerFactory.getLogger(SORTools.class);

    @Inject
    SORClient client;

    /**
     * @param phenomena
     * @return
     */
    public Collection<SirSearchCriteria_Phenomenon> getMatchingPhenomena(Collection<SirSearchCriteria_Phenomenon> phenomena) {
        Collection<SirSearchCriteria_Phenomenon> newPhenomena = new ArrayList<>();

        for (SirSearchCriteria_Phenomenon p : phenomena) {
            if (p.usesSOR()) {
                if (log.isDebugEnabled())
                    log.debug("Try to resolve matching phenomena based on " + p);

                Collection<SirSearchCriteria_Phenomenon> currentNewPhenomena = null;
                try {
                    currentNewPhenomena = this.client.getMatchingTypes(p,
                                                                       SirConfigurator.getInstance().isValidateRequests());
                }
                catch (Exception e) {
                    log.error("Could not get matching phenomena for " + p);
                }
                if (currentNewPhenomena != null) {
                    if (log.isDebugEnabled())
                        log.debug("Matched " + currentNewPhenomena.size() + " phenomena based on " + p);
                    newPhenomena.addAll(currentNewPhenomena);
                }
            }
        }
        return newPhenomena;
    }

}
