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
package org.n52.sir.scheduler;

import java.io.File;
import java.util.Date;

import org.n52.sir.SirConfigurator;
import org.n52.sir.harvest.exec.IJSExecute;
import org.n52.sir.harvest.exec.impl.RhinoJSExecute;
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
		String sensorId = details.getJobDataMap().getString(QuartzConstants.SENSOR_ID_HARVEST_JOB_DATA);
		System.out.println("Executed");
		log.info("Executed at : "+new Date().getTime());
		SirConfigurator config = SirConfigurator.getInstance();
		String path = config.getFactory().insertHarvestScriptDAO().getScriptPath(sensorId);
		if(path !=null){
			File f = new File(config.getScriptsPath()+path);
			IJSExecute executeEngine = new RhinoJSExecute();
			executeEngine.execute(f);
		}
		log.info("Harvesting sensor:"+path);
		try {
			arg0.getScheduler().unscheduleJob(arg0.getTrigger().getKey());
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			log.error("Cannot unscedule ",e);
		}
		
	}
	
}
