package org.n52.sir.scheduler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.n52.sir.SirConfigurator;
import org.n52.sir.datastructure.detailed.SirDetailedSensorDescription;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class RemoteHarvestJob implements Job {
	private static Logger log = LoggerFactory.getLogger(RemoteHarvestJob.class);
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		JobDetail details = arg0.getJobDetail();
		//get sensor id from here
		String auth_token = details.getJobDataMap().getString(QuartzConstants.REMOTE_SENSOR_AUTH_TOKEN);
		System.out.println("Executed");
		log.info("Executed at : "+new Date().getTime());
		SirConfigurator config = SirConfigurator.getInstance();
		String url = config.getFactory().insertRemoteHarvestSensor().harvestRemoteServer(auth_token);
		log.info("Harvesting server:"+url);
		
		if(url!=null){
			HttpGet get = new HttpGet(url+"/sensors");
			org.apache.http.client.HttpClient client = new DefaultHttpClient();
			try {
				HttpResponse resp = client.execute(get);
				StringBuilder builder = new StringBuilder();
				String s = null;
				BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
				while((s=reader.readLine())!=null)
					builder.append(s);
				Gson gson = new Gson();
				int [] ids = gson.fromJson(builder.toString(),int[].class);
				if(ids.length==0)log.info("Done!");
				else{
					for(int i=0;i<ids.length;i++){
						get = new HttpGet(url+"/sensors/"+ids[i]);
						resp = client.execute(get);
						builder = new StringBuilder();
						reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
						while((s=reader.readLine())!=null)
							builder.append(s);
						
						SirDetailedSensorDescription description = gson.fromJson(builder.toString(),SirDetailedSensorDescription.class);
						//insert here
						log.info(description.getId());
						log.info(description.getKeywords().toString());
						try {
							arg0.getScheduler().unscheduleJob(arg0.getTrigger().getKey());
						} catch (SchedulerException e) {
							log.error("Cannot unscedule ",e);
						}
					
					}
				}
			} catch (ClientProtocolException e) {
				log.error("Error on get call",e);
			} catch (IOException e) {
				log.error("Error on get call",e);
			}
			
			
		}
		
		try {
			arg0.getScheduler().unscheduleJob(arg0.getTrigger().getKey());
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			log.error("Cannot unscedule ",e);
		}
		
	}
	
}
