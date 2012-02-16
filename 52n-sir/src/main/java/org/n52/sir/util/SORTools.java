/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */
package org.n52.sir.util;

import java.util.ArrayList;
import java.util.Collection;

import org.n52.sir.SirConfigurator;
import org.n52.sir.datastructure.SirSearchCriteria_Phenomenon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Daniel Nüst
 * 
 */
public class SORTools {

    private static Logger log = LoggerFactory.getLogger(SORTools.class);

    /**
     * @param phenomena
     * @return
     */
    public Collection<SirSearchCriteria_Phenomenon> getMatchingPhenomena(Collection<SirSearchCriteria_Phenomenon> phenomena) {
        Collection<SirSearchCriteria_Phenomenon> newPhenomena = new ArrayList<SirSearchCriteria_Phenomenon>();

        for (SirSearchCriteria_Phenomenon p : phenomena) {
            if (p.usesSOR()) {
                if (log.isDebugEnabled())
                    log.debug("Try to resolve matching phenomena based on " + p);

                Collection<SirSearchCriteria_Phenomenon> currentNewPhenomena = null;
                try {
                    currentNewPhenomena = SORClient.getMatchingTypes(p,
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
