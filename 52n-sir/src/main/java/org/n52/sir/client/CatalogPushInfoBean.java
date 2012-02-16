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
package org.n52.sir.client;

import java.util.Collection;

import org.n52.sir.SirConfigurator;
import org.n52.sir.catalog.ICatalogStatusHandler;

/**
 * @author Daniel Nüst (d.nuest@52north.org)
 *
 */
public class CatalogPushInfoBean extends AbstractBean {

    private ICatalogStatusHandler catalogStatusHandler;

    /**
     * 
     */
    public CatalogPushInfoBean() {
        this.catalogStatusHandler = SirConfigurator.getInstance().getCatalogStatusHandler();
    }

    /* (non-Javadoc)
     * @see org.n52.sir.client.beans.AbstractBean#buildRequest()
     */
    @Override
    public void buildRequest() {
        throw new UnsupportedOperationException("Method not implemented!");
    }
    
    /**
     * 
     * @return
     */
    public String getInfoString() {
        StringBuilder sb = new StringBuilder();
        Collection<String> currentList = this.catalogStatusHandler.getRuntimeInfo();
        for (String s : currentList) {
            sb.append(s);
            sb.append("\n\n");
        }
        return sb.toString();
    }
    
    /**
     * 
     * @return
     */
    public int getMaxEventsCount() {
        return this.catalogStatusHandler.getMaximumInfolistSize();
    }

}
