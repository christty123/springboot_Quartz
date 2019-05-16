package com.job;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;


public class SummaryCountJob implements Job {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private String JobName;

    public String getJobName() {
        return JobName;
    }

    public void setJobName(String jobName) {
        JobName = jobName;
    }

    public SummaryCountJob() {
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("test come--------------");
        JobKey key = jobExecutionContext.getJobDetail().getKey();
        logger.info(key.getName());
        logger.info(getJobName() + " : [Hello World!  MyJob is executing.]");
    }
}
