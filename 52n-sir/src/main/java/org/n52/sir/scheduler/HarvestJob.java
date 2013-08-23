package org.n52.sir.scheduler;

import java.io.File;
import java.util.Date;

import org.n52.sir.SirConfigurator;
import org.n52.sir.harvest.exec.IJSExecute;
import org.n52.sir.harvest.exec.impl.RhinoJSExecute;
import org.quartz.Job;
import org.quartz.JobDataMap;
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
		
		JobDataMap map = details.getJobDataMap();
		
		log.info("Executed at : "+new Date().getTime());
		String path = map.getString(QuartzConstants.SCRIPT_PATH);
		IJSExecute engine = (IJSExecute) map.get(QuartzConstants.SCRIPT_ENGINE);
		if(path !=null){
			File f = new File(path);
			engine.execute(f);
		}
		log.info("Harvesting sensor:"+path);
		try {
			arg0.getScheduler().unscheduleJob(arg0.getTrigger().getKey());
		} catch (SchedulerException e) {
			log.error("Cannot unscedule ",e);
		}
		
	}
	
}
