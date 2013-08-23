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

package org.n52.sir.script;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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

    @Inject
    public HarvestResource() { // public HarvestResource(IJSExecute exec, SchedulerFactory schedulerFactory) {
    // this.jsEngine = exec;
    // this.schedulerFactory = schedulerFactory;
    }

    @GET
    public Object listHarvesters() {
        return "test";
    }

    @GET
    @Path("/{scriptid}")
    public Object showHarvester(@PathParam("scriptid") String id) {
        // FIXME implement full CRUD
        return "script " + id;
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
    String user) {

        String fileName = fileDetail.getFileName();
        String type = fileDetail.getType();
        String id = "";
        SirConfigurator config = SirConfigurator.getInstance();
        if (config != null) {
            String pathStr = config.getScriptsPath();
            log.info(pathStr);
            File dir = new File(pathStr + user);
            if ( !dir.exists())
                dir.mkdir();

            File script = new File(pathStr + user + '\\' + fileName);

            try {
                OutputStream writer = new FileOutputStream(script);
                int read = 0;
                byte[] bytes = new byte[1024];
                while ( (read = uploadedInputStream.read(bytes)) != -1)
                    writer.write(bytes, 0, read);
                writer.flush();
                writer.close();
                id = config.getFactory().insertHarvestScriptDAO().insertScript(fileName, user, 1);
                log.info("Storing for script at:" + script.getAbsolutePath());
                log.info("Executing script");
                String result = jsEngine.execute(script);
                log.info("Script result:" + result);
            }
            catch (Exception e) {
                log.error("Exception on executing script:", e);
                return Response.status(500).build();
            }
        }
        log.info(fileName + "." + type + ":was uploaded at:" + System.currentTimeMillis());
        return Response.ok(new Viewable("/success")).build();
    }

    @GET
    @Path("/schedule")
    public String scheduleHarvest(@QueryParam("id")
    int sensorId, @QueryParam("date")
    long when) {
        Date d;
        if (when == 0)
            d = new Date();
        else
            d = new Date(when);
        JobDetail detail = JobBuilder.newJob(HarvestJob.class).withIdentity("_J" + sensorId).usingJobData(QuartzConstants.SENSOR_ID_HARVEST_JOB_DATA,
                                                                                                          sensorId + "").build();

        try {
            Trigger tr = TriggerBuilder.newTrigger().withIdentity("_T" + sensorId).withSchedule(CronScheduleBuilder.cronSchedule("0/10 * * * * ?")).startAt(d).build();
            Scheduler sch = schedulerFactory.getScheduler();
            sch.scheduleJob(detail, tr);
            sch.start();
            log.info("Scheduled successfully :_J" + sensorId);
            return "_J" + sensorId;
        }
        catch (Exception e) {
            log.error("Error on scheduling", e);
            return "fail";
        }

    }

}
