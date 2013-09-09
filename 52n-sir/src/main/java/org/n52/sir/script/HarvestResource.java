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
/** @author Yakoub
 */
package org.n52.sir.script;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.LinkedHashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.n52.sir.SirConfigurator;
import org.n52.sir.ds.IInsertHarvestScriptDAO;
import org.n52.sir.ds.IInsertRemoteHarvestServer;
import org.n52.sir.ds.IInsertSensorInfoDAO;
import org.n52.sir.ds.IUserAccountDAO;
import org.n52.sir.harvest.exec.IJSExecute;
import org.n52.sir.licenses.License;
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

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("/script")
@RequestScoped
public class HarvestResource {

    private static Logger log = LoggerFactory.getLogger(HarvestResource.class);

    private IJSExecute jsEngine;

    private SchedulerFactory schedulerFactory;

    private IInsertSensorInfoDAO dao;

    // private IInsertRemoteHarvestServer remoteDAO;

    private SirConfigurator config;

    @Inject
    public HarvestResource(SirConfigurator config, IJSExecute jsEngine) {
        log.info("SirConfigurator: {}", config.getFactory());
        this.config = config.getInstance();
        this.jsEngine = jsEngine;
    }

    @GET
    public Object listHarvesters() {
        return "test";
    }

    @GET
    @Path("/{scriptid}")
    public Response showHarvester(@PathParam("scriptid")
    String id) {
        try{
            String responseMsg = null;
            IInsertHarvestScriptDAO dao = this.config.getFactory().insertHarvestScriptDAO();
            String relativePath =  dao.getScriptFileForID(Integer.parseInt(id));
            String root = config.getScriptsPath();
            File f = new File(root+relativePath);
            if(!f.exists())
                responseMsg = "{error:'No such script stored at server'}";
            else{
                StringBuilder builder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new FileReader(f));
                String s = null;
                while((s=reader.readLine())!=null){
                    builder.append(s);
                    builder.append(System.getProperty("line.separator"));
    }
                responseMsg="{content:'"+builder.toString()+"'}";
            }
            return Response.ok(responseMsg).build();            
        }catch(Exception e){
            log.error("Error on showing harvest script",e);
            return Response.ok("{error:'cannot read script'}").build();
        }
    }

    @DELETE
    @Path("/<scriptid>")
    public Object deleteHarvester() {
        // FIXME implement full CRUD
        return null;
    }

    @POST
    @Path("/<scriptid>/update")
    public Object updateHarvester() {
        // FIXME implement full CRUD
        return null;
    }

    @POST
    @Path("/submit")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadHarvester(@FormDataParam("file")
    InputStream uploadedInputStream, @FormDataParam("file")
    FormDataContentDisposition fileDetail, @FormDataParam("user")
    String user,@FormDataParam("auth_token")String userToken,@FormDataParam("licenseCode")String code) {
    	IUserAccountDAO userDao = this.config.getFactory().userAccountDAO();
    	int userid = Integer.parseInt(userDao.getUserIDForToken(userToken));
        String fileName = fileDetail.getFileName();
        String type = fileDetail.getType();

        String pathStr = this.config.getScriptsPath();
        log.info(pathStr);
        File dir = new File(pathStr + user);
        if ( !dir.exists())
            dir.mkdir();

        File script = new File(pathStr + user + '\\' + fileName);
        try (OutputStream writer = new FileOutputStream(script);) {
        	writer.write(getLicenseForCode(code).getBytes());
            int read = 0;
            
            byte[] bytes = new byte[1024];
            while ( (read = uploadedInputStream.read(bytes)) != -1)
                writer.write(bytes, 0, read);
            writer.flush();
            writer.close();
            String id = this.config.getFactory().insertHarvestScriptDAO().insertScript(fileName, user, 1,userid);
            log.info("Storing for script at {}, id: {}" + script.getAbsolutePath(), id);
            log.info("Executing script");
            String result = this.jsEngine.execute(script);
            log.info("Script result:" + result);
        }
        catch (Exception e) {
            log.error("Exception on executing script:", e);
            return Response.status(500).build();
        }
        log.info(fileName + "." + type + ":was uploaded at:" + System.currentTimeMillis());

        // TODO add id to response
        return Response.ok(new Viewable("/success")).build();
    }

    @GET
    @Path("/schedule")
    public Response scheduleHarvest(@QueryParam("id")
    int scriptId, @QueryParam("date")
    @DefaultValue("0")
    long when,@QueryParam("authToken")String authToken, @QueryParam("schedule") @DefaultValue("0/10 * * * * ?")
    String schedule) {
    	IUserAccountDAO userDao = this.config.getFactory().userAccountDAO();
    	IInsertHarvestScriptDAO scriptDao = this.config.getFactory().insertHarvestScriptDAO();
    	String userid = userDao.getUserIDForToken(authToken);
    	String scriptowner = scriptDao.getScriptUserId(scriptId);
    	if(!userid.equals(scriptowner))
    		return Response.status(401).entity("Forbidden - You cannot access the following script").build();
        Date d;
        if (when == 0)
            d = new Date();
        else
            d = new Date(when);
        JobDetail detail = JobBuilder.newJob(HarvestJob.class).withIdentity("_J" + scriptId).usingJobData(QuartzConstants.SENSOR_ID_HARVEST_JOB_DATA,
        		scriptId + "").build();

        try {
            Trigger tr = TriggerBuilder.newTrigger().withIdentity("_T" + scriptId).withSchedule(CronScheduleBuilder.cronSchedule("0/10 * * * * ?")).startAt(d).build();
            Scheduler sch = this.schedulerFactory.getScheduler();
            sch.scheduleJob(detail, tr);
            sch.start();
            log.info("Scheduled successfully :_J" + scriptId);

            // return "_J" + sensorId;
            return Response.status(200).entity("_J" + scriptId).build();
        }
        catch (Exception e) {
            log.error("Error on scheduling", e);
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/remote/server")
    @Produces(MediaType.APPLICATION_JSON)
    public Response putServer(@FormParam("url")
    String serverURL) {
        // if (this.config == null)
        // log.info("Cannot instantiate remote dao");
        // if (this.config.getFactory() == null)
        // log.info("Cannot find factory");
        // if (this.config.getFactory().insertRemoteHarvestSensor() == null)
        // log.info("Cannot find remote factory");

        try {
            IInsertRemoteHarvestServer irhs = this.config.getFactory().insertRemoteHarvestSensor();
            String auth_token = irhs.insertRemoteServer(serverURL);
            String result = "{" + '"' + "auth_token" + '"' + ":" + '"' + auth_token + '"' + "}";
            return Response.status(200).entity(result).header(HttpHeaders.CONTENT_LENGTH, result.length()).build();
        }
        catch (Exception e) {
            log.error("Error handling post to /remote/server", e);
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    // TODO add a time parameter for a schedule time :Yakoub
    @POST
    @Path("/remote/server/harvest")
    public Response harvestServer(@FormParam("auth_token")
    String auth_token) {
        JobDataMap dataMap = new JobDataMap();

        // FIXME after merge with moh-yakoub
        String url = this.config.getFactory().insertRemoteHarvestSensor().harvestRemoteServer(auth_token);
        log.info("The result url:" + url);
         if (url == null) {
        return Response.status(404).build();
         }
         dataMap.put(QuartzConstants.INSERTION_INTERFACE, this.dao);
         dataMap.put(QuartzConstants.REMOTE_SENSOR_URL, url);
        
        JobDetail detail = JobBuilder.newJob(RemoteHarvestJob.class).withIdentity("_I" +
        auth_token).usingJobData(dataMap).build();

        try {
            Trigger tr = TriggerBuilder.newTrigger().withIdentity("_T" + auth_token).withSchedule(CronScheduleBuilder.cronSchedule("0/10 * * * * ?")).build();
            Scheduler sch = this.schedulerFactory.getScheduler();
            sch.scheduleJob(detail, tr);
            sch.start();
            log.info("Scheduled successfully :_J" + auth_token);
            return Response.ok().build();

        }
        catch (Exception e) {
            log.error("Error on scheduling", e);
            return Response.status(500).entity(e.getMessage()).build();
        }
    }
    private String getLicenseForCode(String code){
    	StringBuilder builder = new StringBuilder();
    	LinkedHashMap<String, License> map = SirConfigurator.getInstance().getLicenses();
    	
    	License l = map.get(code);
    	if(l==null)
    		builder.append("/* This file doesn't support any license */");
    	else
    	{
    		builder.append("/*");
    		builder.append("This work is licensed under:");
    		builder.append(l.description);
    		builder.append(" For more details please visit:");
    		builder.append(l.link);
    		builder.append("*/");
    		builder.append(System.lineSeparator());
    	}
    	return builder.toString();
    	
    }
}