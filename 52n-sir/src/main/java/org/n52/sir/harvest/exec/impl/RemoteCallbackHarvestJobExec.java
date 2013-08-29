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
