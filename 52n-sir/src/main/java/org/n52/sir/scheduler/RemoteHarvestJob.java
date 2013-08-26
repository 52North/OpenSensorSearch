package org.n52.sir.scheduler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.n52.sir.datastructure.SirSensor;
import org.n52.sir.datastructure.detailed.SirDetailedSensorDescription;
import org.n52.sir.ds.IInsertSensorInfoDAO;
import org.n52.sir.ows.OwsExceptionReport;
import org.quartz.Job;
import org.quartz.JobDataMap;
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
		JobDataMap params = details.getJobDataMap();
		String url = params.getString(QuartzConstants.REMOTE_SENSOR_URL);		
		IInsertSensorInfoDAO insertDAO = (IInsertSensorInfoDAO)params.get(QuartzConstants.INSERTION_INTERFACE);

		
		log.info("Executed at : "+new Date().getTime());
		log.info(url);
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
						
						SirSensor sensor = new SirSensor();
						Collection<String> keywords = new ArrayList<String>();
						Iterator<String> it = description.getKeywords().iterator();
						while(it.hasNext())keywords.add(it.next().toString());
						
						sensor.setKeywords(keywords);
						
						try {
							insertDAO.insertSensor(sensor);
						} catch (OwsExceptionReport e1) {
							e1.printStackTrace();
						}
						
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
			log.error("Cannot unscedule ",e);
		}
		
	}
	
}
