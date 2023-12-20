package com.sih.rakshak;

import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

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

        PieChart pieChart = findViewById(R.id.radarChart);

        assert appProbabilityList != null;
        int totalItemsList1 = getTotalOfList(appProbabilityList);
        int totalItemsList2 = getTotalOfList2(appProbabilityList);

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(totalItemsList1, "Safe App"));
        entries.add(new PieEntry(totalItemsList2, "Unsafe App"));

        PieDataSet dataSet = new PieDataSet(entries, "List Comparison");
        dataSet.setColors(Color.RED, Color.GREEN);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.invalidate();
    }

    private int getTotalOfList(List<String> appProbabilityList) {
        int n = 0;

        for (int i = 0; i < appProbabilityList.size(); i++) {

            if (Integer.parseInt(appProbabilityList.get(i)) > 75) {
                n++;
            }

        }
        return n;
    }

    private int getTotalOfList2(List<String> appProbabilityList) {
        int n = 0;

        for (int i = 0; i < appProbabilityList.size(); i++) {

            if (Integer.parseInt(appProbabilityList.get(i)) < 75) {
                n++;
            }

        }
        return n;
    }

}