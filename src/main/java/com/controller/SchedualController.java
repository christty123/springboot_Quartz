package com.controller;

import com.dto.JobBean;
import com.job.SummaryCountJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * Created by Dell on 2019/5/15.
 */
@RestController
public class SchedualController {
    @Autowired
    Scheduler scheduler;

    @PostMapping("/schedual/add")
    public void addSchedual(@RequestBody JobBean jobBean) {
        JobDetail jobDetail = JobBuilder.newJob(SummaryCountJob.class)
                .withDescription("demo")
                .withIdentity(jobBean.getJobName(), "DEFAULT")
                .build();
        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withSchedule(CronScheduleBuilder.cronSchedule(jobBean.getCon()))
                .build();
        try {
            if (!scheduler.checkExists(JobKey.jobKey(jobBean.getJobName(), "DEFAULT"))) {
                scheduler.scheduleJob(jobDetail, trigger);
            }
            scheduler.scheduleJob(jobDetail, trigger);
            System.out.println("add job success");
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
    public void resumeJob(String jobName, String jobGroup) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(jobName + "_trigger", jobGroup + "_trigger");
            scheduler.resumeTrigger(triggerKey);
            System.out.println("=========================resume job:" + jobName + " success========================");
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

}
