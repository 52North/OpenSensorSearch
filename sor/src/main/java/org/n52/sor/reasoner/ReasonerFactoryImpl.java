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
