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
package org.n52.sir.util.jobs;

import org.n52.sir.catalog.ICatalogConnection;
import org.n52.sir.ows.OwsExceptionReport;

/**
 * 
 * This interface encapsulates the submission of catalog connections using
 * {@link IJobScheduler#submit(ICatalogConnection)}.
 * 
 * An {@link ICatalogConnection} can be submitted for (repeated) execution. It contains an identifier that can
 * be used to abort a (persistently saved) task using {@link IJobScheduler#cancel(String)}.
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public interface IJobScheduler {

    /**
     * Cancels the task with the given identifier.
     * 
     * See {@link java.util.TimerTask#cancel()} for details.
     * 
     * @param identifier
     */
    public abstract void cancel(String identifier);

    /**
     * Submits the given catalog connection for (repeated) execution. There is no guarantee for a specific
     * execution time or maximum delay of the operations that are scheduled.
     * 
     * @param iCatalogConnection
     * @throws OwsExceptionReport
     */
    public abstract void submit(ICatalogConnection iCatalogConnection) throws OwsExceptionReport;

}