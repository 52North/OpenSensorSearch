package org.n52.sir.script;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.n52.sir.SirConfigurator;
import org.n52.sir.harvest.exec.IJSExecute;
import org.n52.sir.scheduler.HarvestJob;
import org.n52.sir.scheduler.QuartzConstants;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("/script")
public class HarvestResource {
	private static Logger log = LoggerFactory.getLogger(HarvestResource.class);

	private IJSExecute jsEngine;
	private SchedulerFactory schedulerFactory;

	@Inject
	public HarvestResource(IJSExecute exec,SchedulerFactory schedulerFactory) {
		this.jsEngine = exec;
		this.schedulerFactory = schedulerFactory;
	}

	@POST
	@Path("/submit")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String uploadHarvester(
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

			File script = new File(pathStr + user + '\\' + fileName);

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
				return "";
			}
		}
		log.info(fileName + "." + type + ":was uploaded at:"
				+ System.currentTimeMillis());
		return id;
	}
	@GET
	@Path("/schedule")
	public String scheduleHarvest(@QueryParam("id")int sensorId,@QueryParam("date")long when){
		Date d ;
		if(when == 0 )d= new Date();
		else d = new Date(when);
		JobDetail detail = JobBuilder.newJob(HarvestJob.class).withIdentity("_J"+sensorId).usingJobData(QuartzConstants.SENSOR_ID_HARVEST_JOB_DATA,sensorId+"").build();
		
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
	
}
