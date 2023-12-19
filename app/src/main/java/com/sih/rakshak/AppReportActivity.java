package com.sih.rakshak;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.ApplicationInfo;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AppReportActivity extends AppCompatActivity {

    RecyclerView recyclerReportApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_report);

        List<String> appProbabilityList = getIntent().getStringArrayListExtra("probability");
        List<ApplicationInfo> appInfoList = getIntent().getParcelableArrayListExtra("appInfoList");

        recyclerReportApps = findViewById(R.id.recyclerReportApps);

        List<AppInfoItem> appInfoItems = new ArrayList<>();
        for (int i = 0; i < Objects.requireNonNull(appInfoList).size(); i++) {
            ApplicationInfo applicationInfo = appInfoList.get(i);
            assert appProbabilityList != null;
            String appProbability = appProbabilityList.get(i);
            appInfoItems.add(new AppInfoItem(applicationInfo, appProbability));
        }

        AppInfoAdapter adapter = new AppInfoAdapter(appInfoItems);
        recyclerReportApps.setAdapter(adapter);
        recyclerReportApps.setLayoutManager(new LinearLayoutManager(this));
    }
}