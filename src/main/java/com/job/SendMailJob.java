package com.job;

import com.annotion.SchedulerJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;

/**
 * Created by Dell on 2019/5/15.
 */
@SchedulerJob(job = "sendMailJobTask", con = "0/15 * * * * ?",triggerName = "sendMailJobTaskTrigger")
public class SendMailJob extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("come----------"+new Date());
    }
}
