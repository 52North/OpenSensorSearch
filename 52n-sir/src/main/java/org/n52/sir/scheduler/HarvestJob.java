package org.n52.sir.scheduler;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class HarvestJob implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		JobDetail details = arg0.getJobDetail();
		//get sensor id from here
	}
	
}
