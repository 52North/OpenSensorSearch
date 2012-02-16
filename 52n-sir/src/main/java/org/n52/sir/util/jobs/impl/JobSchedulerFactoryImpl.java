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
package org.n52.sir.util.jobs.impl;

import org.n52.sir.util.jobs.IJobScheduler;
import org.n52.sir.util.jobs.IJobSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Factory for creating instances of {@link IJobScheduler} that work with the timer given in the constructor.
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class JobSchedulerFactoryImpl implements IJobSchedulerFactory {

    private static Logger log = LoggerFactory.getLogger(JobSchedulerFactoryImpl.class);

    private TimerServlet timerServlet;

    public JobSchedulerFactoryImpl(TimerServlet timer) {
        this.timerServlet = timer;
        log.info("NEW " + this);
    }

    /*
     * (non-Javadoc)
     * @see org.n52.sir.util.jobs.IJobSchedulerFactory#getJobScheduler()
     */
    @Override
    public IJobScheduler getJobScheduler() {
        return new JobSchedulerImpl(this.timerServlet);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("JobSchedulerFactory [timerServlet=");
        sb.append(this.timerServlet);
        sb.append("]");
        return sb.toString();
    }
}
