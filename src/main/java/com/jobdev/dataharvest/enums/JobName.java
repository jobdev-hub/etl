package com.jobdev.dataharvest.enums;

public enum JobName {

    SYNC_WORK("syncWorkJob");

    private final String jobName;

    JobName(String jobName) {
        this.jobName = jobName;
    }

    public String getName() {
        return jobName;
    }

    @Override
    public String toString() {
        return jobName;
    }
}