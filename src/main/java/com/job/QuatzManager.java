package com.job;

import com.annotion.SchedulerJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by christ on 2019/5/15.
 */
@Component
public class QuatzManager {

    private static final String PACKAGE_NAME = "com.job";

    @Autowired
    private Scheduler scheduler;

    private List<Class> annotionJob = new ArrayList<>();

    public QuatzManager() {
        File file = new File(this.getClass().getResource("").getPath());
        getAllClassByFile(file);
    }

    public void loadJOB() {
        for (Class cl : annotionJob) {
            if (cl.isAnnotationPresent(SchedulerJob.class)) {
                SchedulerJob schedulerJob = (SchedulerJob) cl.getAnnotation(SchedulerJob.class);
                try {
                    scheduleJob(cl, schedulerJob);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }


    /***
     * add job to scheduler
     *
     * @param class_
     * @param schedulerJob
     * @throws SchedulerException
     */
    private void scheduleJob(Class class_, SchedulerJob schedulerJob) throws SchedulerException {
        String jobName = schedulerJob.job();
        String con = schedulerJob.con();
        String triggerName = schedulerJob.triggerName();
        String triggerGroup = schedulerJob.triggerGroupName();
        JobDetail jobDetail = JobBuilder.newJob(class_).withIdentity(jobName, triggerGroup).build();
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(con);
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerName, triggerGroup).withSchedule(scheduleBuilder).build();
        if (!scheduler.checkExists(new JobKey(jobName, triggerGroup))) {
            scheduler.scheduleJob(jobDetail, cronTrigger);
        } else {
            try {
                checkChange(triggerName, triggerGroup, con);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getAllClassByFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File fi : files) {
                getAllClassByFile(fi);
            }
        } else {
            try {
                String fileName = file.getName();
                fileName = fileName.substring(0, fileName.indexOf("."));
                annotionJob.add(Class.forName(PACKAGE_NAME + "." + fileName));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /***
     * find db qrtz_triggers  by   TRIGGER_NAME   and    TRIGGER_GROUP
     *
     * @param triggerName
     * @param trigerGroup
     * @throws Exception
     */
    private void checkChange(String triggerName, String trigerGroup, String cron) throws Exception {
        TriggerKey triggerKey = new TriggerKey(triggerName, trigerGroup);
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        String currentCron = trigger.getCronExpression();
        if (!currentCron.equals(cron)) {
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
            trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey.getName()).withSchedule(scheduleBuilder).build();
            scheduler.rescheduleJob(triggerKey, trigger);
        }
    }
}
