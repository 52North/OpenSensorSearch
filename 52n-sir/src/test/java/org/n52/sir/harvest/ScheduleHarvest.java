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

package org.n52.sir.harvest;

import java.util.Date;

import org.junit.Test;
import org.n52.sir.scheduler.HarvestJob;
import org.n52.sir.scheduler.QuartzConstants;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class ScheduleHarvest {

    @Test
    public void harvestAtTime() throws Exception {
        // TODO replace the value with a returned value
        SchedulerFactory factory = new StdSchedulerFactory();
        JobDetail detail = JobBuilder.newJob(HarvestJob.class).withIdentity("_J59").usingJobData(QuartzConstants.SENSOR_ID_HARVEST_JOB_DATA,
                                                                                                 "59").build();

        try {
            Trigger tr = TriggerBuilder.newTrigger().withIdentity("_T59").withSchedule(CronScheduleBuilder.cronSchedule("0/10 * * * * ?")).startAt(new Date()).build();
            Scheduler sch = factory.getScheduler();
            sch.scheduleJob(detail, tr);
            sch.start();
            Thread.sleep(10000);
            // TODO check that the sensors are added here
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

}
