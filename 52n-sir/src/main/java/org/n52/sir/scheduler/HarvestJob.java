package org.n52.sir.scheduler;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HarvestJob implements Job {
	private static Logger log = LoggerFactory.getLogger(HarvestJob.class);
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		JobDetail details = arg0.getJobDetail();
		//get sensor id from here
		System.out.println("Executed");
		log.info("Executed at : "+new Date().getTime());
		try {
			arg0.getScheduler().unscheduleJob(arg0.getTrigger().getKey());
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			log.error("Cannot unscedule ",e);
		}
		
	}
	
}