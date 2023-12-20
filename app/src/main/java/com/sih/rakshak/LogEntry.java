package com.sih.rakshak;

import android.content.pm.ApplicationInfo;

import java.util.List;

public class LogEntry {
    private String timestamp;
    private List<ApplicationInfo> appInfoList;
    private List<String> appProbabilityList;

    public LogEntry(){}
    public LogEntry(String timestamp, List<ApplicationInfo> appInfoList, List<String> appProbabilityList) {
        this.timestamp = timestamp;
        this.appInfoList = appInfoList;
        this.appProbabilityList = appProbabilityList;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public List<ApplicationInfo> getAppInfoList() {
        return appInfoList;
    }

    public List<String> getAppProbabilityList() {
        return appProbabilityList;
    }
}
