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
package org.n52.sir.util.jobs.impl;

import org.n52.sir.util.jobs.IJobScheduler;
import org.n52.sir.util.jobs.IJobSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * 
 * Factory for creating instances of {@link IJobScheduler} that work with the timer given in the constructor.
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class JobSchedulerFactoryImpl implements IJobSchedulerFactory {

    private static Logger log = LoggerFactory.getLogger(JobSchedulerFactoryImpl.class);

    @Inject
    private TimerServlet timerServlet;

    public JobSchedulerFactoryImpl() {
        log.info("NEW {}", this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.util.jobs.IJobSchedulerFactory#getJobScheduler()
     */
    @Override
    public IJobScheduler getJobScheduler() {
        return new JobSchedulerImpl(this.timerServlet);
    }

    /*
     * (non-Javadoc)
     * 
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
