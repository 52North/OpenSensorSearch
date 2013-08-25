package org.n52.sir.harvest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Date;

import org.junit.Test;
import org.n52.sir.harvest.exec.IJSExecute;
import org.n52.sir.scheduler.HarvestJob;
import org.n52.sir.scheduler.QuartzConstants;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class quartzTest {

	private static final String MOCK_STRING = "FILENAME";
	private static final String MOCK_PATH = "/test/path";
	private IJSExecute mockEngine = new IJSExecute() {
		@Override
		public String execute(File f) {
			System.setProperty(MOCK_STRING, f.getPath());
			return f.getName();
		}

		@Override
		public String execute(String s) {
			System.setProperty(MOCK_STRING, s);
			return s;
		}
	};

	@Test
	public void runQuartzTask() throws SchedulerException, InterruptedException {
		JobDataMap map = new JobDataMap();
		map.put(QuartzConstants.SCRIPT_ENGINE, mockEngine);
		map.put(QuartzConstants.SCRIPT_PATH, MOCK_PATH);
		JobDetail detail = JobBuilder.newJob(HarvestJob.class)
				.withIdentity("_J59").usingJobData(map).build();

		Trigger tr = TriggerBuilder
				.newTrigger()
				.withIdentity("_T59")
				.withSchedule(
						CronScheduleBuilder.cronSchedule("0/10 * * * * ?"))
				.startAt(new Date()).build();
		SchedulerFactory factory = new StdSchedulerFactory();
		Scheduler sch = factory.getScheduler();
		sch.scheduleJob(detail, tr);
		sch.start();
		Thread t = Thread.currentThread();
		t.sleep(10000);
		assertEquals(System.getProperty(MOCK_STRING),new File(MOCK_PATH).getPath());
	}
}
