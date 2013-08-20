package org.n52.sir.script;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.n52.sir.SirConfigurator;
import org.n52.sir.ds.IInsertSensorInfoDAO;
import org.n52.sir.harvest.exec.IJSExecute;
import org.n52.sir.scheduler.HarvestJob;
import org.n52.sir.scheduler.QuartzConstants;
import org.n52.sir.scheduler.RemoteHarvestJob;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("/script")
public class HarvestResource {
	private static Logger log = LoggerFactory.getLogger(HarvestResource.class);

	private IJSExecute jsEngine;
	private SchedulerFactory schedulerFactory;
	private IInsertSensorInfoDAO insertSensorInfoDao;
	@Inject
	public HarvestResource(IJSExecute exec,SchedulerFactory schedulerFactory,IInsertSensorInfoDAO insertSensorInfoDao) {
		this.jsEngine = exec;
		this.schedulerFactory = schedulerFactory;
		this.insertSensorInfoDao = insertSensorInfoDao;
	}

	@POST
	@Path("/submit")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadHarvester(
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail,
			@FormDataParam("user") String user) {

		String fileName = fileDetail.getFileName();
		String type = fileDetail.getType();
		String id = "";
		SirConfigurator config = SirConfigurator.getInstance();
		if (config != null) {
			String pathStr = config.getScriptsPath();
			log.info(pathStr);
			File dir = new File(pathStr + user);
			if (!dir.exists())
				dir.mkdir();
			
			File script = new File(pathStr + user + '/' + fileName);

			try {
				OutputStream writer = new FileOutputStream(script);
				int read =0 ;
				byte [] bytes = new byte[1024];
				while((read=uploadedInputStream.read(bytes))!=-1)
					writer.write(bytes,0,read);
				writer.flush();
				writer.close();
				id = config.getFactory().insertHarvestScriptDAO()
						.insertScript(fileName,user, 1);
				log.info("Storing for script at:"+script.getAbsolutePath());
				log.info("Executing script");
				String result = jsEngine.execute(script);
				log.info("Script result:" + result);
			} catch (Exception e) {
				log.error("Exception on executing script:", e);
				return Response.status(500).build();
			}
		}
		log.info(fileName + "." + type + ":was uploaded at:"
				+ System.currentTimeMillis());
		return Response.ok(new Viewable("/success")).build();
	}
	@GET
	@Path("/schedule")
	public String scheduleHarvest(@QueryParam("id")int sensorId,@QueryParam("date")long when){
		Date d ;
		if(when == 0 )d= new Date();
		else d = new Date(when);
		
		JobDataMap dataMap = new JobDataMap();
		SirConfigurator config = SirConfigurator.getInstance();
		String p = config.getFactory().insertHarvestScriptDAO().getScriptPath(sensorId+"");
		
		String path = config.getScriptsPath()+p;
		
		
		dataMap.put(QuartzConstants.SCRIPT_PATH, path);
		dataMap.put(QuartzConstants.SCRIPT_ENGINE, this.jsEngine);
		
		JobDetail detail = JobBuilder.newJob(HarvestJob.class).withIdentity("_J"+sensorId).usingJobData(dataMap).build();
		
		
		try{
			Trigger tr = TriggerBuilder.newTrigger().withIdentity("_T"+sensorId).withSchedule(CronScheduleBuilder.cronSchedule("0/10 * * * * ?")).startAt(d).build();
			Scheduler sch = schedulerFactory.getScheduler();
			sch.scheduleJob(detail, tr);
			sch.start();
			log.info("Scheduled successfully :_J"+sensorId);
			return "_J"+sensorId;
		}catch(Exception e){
			log.error("Error on scheduling",e);
			return "fail";
		}
		
	}
	@POST
	@Path("/remote/server")
	@Produces(MediaType.APPLICATION_JSON)
	public Response putServer(@FormParam("url")String serverURL){
		SirConfigurator config = SirConfigurator.getInstance();
		if(config !=null){
			String auth_token = config.getFactory().insertRemoteHarvestSensor().insertRemoteServer(serverURL);
			auth_token token = new auth_token();
			token.setAuthtoken(auth_token);
			String result = "{"+'"'+"auth_token"+'"'+":"+'"'+auth_token+'"'+"}";
			return Response.status(200).entity(result).header(HttpHeaders.CONTENT_LENGTH, result.length()).build();
		}else return Response.status(500).build();
		
	}
	@POST
	@Path("/remote/server/harvest")
	public Response harvestServer(@FormParam("auth_token")String auth_token){
		JobDataMap dataMap = new JobDataMap();
		SirConfigurator config = SirConfigurator.getInstance();
		String url = config.getFactory().insertRemoteHarvestSensor().harvestRemoteServer(auth_token);
		if(url==null){
			//TODO Yakoub sets the status to fail : HAR31
			return null;
		}
		dataMap.put(QuartzConstants.INSERTION_INTERFACE,this.insertSensorInfoDao);
		dataMap.put(QuartzConstants.REMOTE_SENSOR_URL,url);
		
		JobDetail detail = JobBuilder.newJob(RemoteHarvestJob.class).withIdentity("_I"+auth_token).usingJobData(dataMap).build();
		
		try{
			Trigger tr = TriggerBuilder.newTrigger().withIdentity("_T"+auth_token).withSchedule(CronScheduleBuilder.cronSchedule("0/10 * * * * ?")).build();
			Scheduler sch = schedulerFactory.getScheduler();
			sch.scheduleJob(detail, tr);
			sch.start();
			log.info("Scheduled successfully :_J"+auth_token);
			return Response.ok().build();
	
		}catch(Exception e){
			log.error("Error on scheduling",e);
			return Response.status(500).build();		
		}
	}
	
	public class auth_token{
		String authtoken;
		public String getAuthtoken(){
			return authtoken;
		}
		public void setAuthtoken(String authtoken){
			this.authtoken = authtoken;
		}
		
	}
	
}
