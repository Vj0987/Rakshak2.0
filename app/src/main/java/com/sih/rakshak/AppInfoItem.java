package com.sih.rakshak;

import android.content.pm.ApplicationInfo;

public class AppInfoItem {
    private ApplicationInfo applicationInfo;
    private String appProbability;

    public AppInfoItem(ApplicationInfo applicationInfo, String appProbability) {
        this.applicationInfo = applicationInfo;
        this.appProbability = appProbability;
    }

    public ApplicationInfo getApplicationInfo() {
        return applicationInfo;
    }

    public String getAppProbability() {
        return appProbability;
    }
}
