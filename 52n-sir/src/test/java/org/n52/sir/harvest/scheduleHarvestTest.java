package org.n52.sir.harvest;

import java.io.InputStream;
import java.util.Date;

import javax.servlet.UnavailableException;

import org.junit.Test;
import org.n52.sir.SirConfigurator;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.scheduler.HarvestJob;
import org.n52.sir.scheduler.QuartzConstants;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class scheduleHarvestTest {
	@Test
	public void harvestAtTime() throws UnavailableException, OwsExceptionReport{
		if (SirConfigurator.getInstance() == null) {
			InputStream dbStream = ClassLoader
					.getSystemResourceAsStream("prop/db.PROPERTIES");
			InputStream sirStream = ClassLoader
					.getSystemResourceAsStream("prop/sir.PROPERTIES");
			// Read configurator if null
			SirConfigurator.getInstance(sirStream, dbStream, null, null);

		}
		// TODO replace the value with a returned value
		SchedulerFactory factory = new StdSchedulerFactory();
		JobDetail detail = JobBuilder.newJob(HarvestJob.class)
				.withIdentity("_J59")
				.usingJobData(QuartzConstants.SENSOR_ID_HARVEST_JOB_DATA, "59")
				.build();

		try {
			Trigger tr = TriggerBuilder
					.newTrigger()
					.withIdentity("_T59")
					.withSchedule(
							CronScheduleBuilder.cronSchedule("0/10 * * * * ?"))
					.startAt(new Date()).build();
			Scheduler sch = factory.getScheduler();
			sch.scheduleJob(detail, tr);
			sch.start();
			Thread t = Thread.currentThread();
			t.sleep(10000);
			//TODO check that the sensors are added here
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
