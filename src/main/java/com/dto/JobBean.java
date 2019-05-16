package com.dto;

import java.io.Serializable;

/**
 * Created by Dell on 2019/5/15.
 */
public class JobBean implements Serializable {
    private String jobName;

    private  String group;

    private  String con;

    public String getCon() {
        return con;
    }

    public void setCon(String con) {
        this.con = con;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
