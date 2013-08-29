package org.n52.sir.harvest.exec;

import java.util.Date;

import org.quartz.JobDataMap;
import org.quartz.SchedulerException;

public interface IHarvestJobExec {
	public void execute(JobDataMap map,String identity,String triggerIdentity,Date start)throws SchedulerException;
}
