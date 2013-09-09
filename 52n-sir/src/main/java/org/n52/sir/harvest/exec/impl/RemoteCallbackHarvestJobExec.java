/**
 * ?Copyright (C) 2012 52°North Initiative for Geospatial Open Source Software GmbH
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
package org.n52.sir.harvest.exec.impl;

import java.util.Date;

import org.n52.sir.harvest.exec.IHarvestJobExec;
import org.n52.sir.scheduler.RemoteHarvestJob;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class RemoteCallbackHarvestJobExec implements IHarvestJobExec{

	@Override
	public void execute(JobDataMap map, String identity,String triggerIdentity, Date start) throws SchedulerException {
		JobDetail detail = JobBuilder.newJob(RemoteHarvestJob.class)
				.withIdentity(identity).usingJobData(map).build();

		Trigger tr = TriggerBuilder
				.newTrigger()
				.withIdentity(identity)
				.withSchedule(
						CronScheduleBuilder.cronSchedule("0/10 * * * * ?"))
				.startAt(start).build();
		SchedulerFactory factory = new StdSchedulerFactory();
		Scheduler sch = factory.getScheduler();
		sch.scheduleJob(detail, tr);
		sch.start();
	}
	

}
