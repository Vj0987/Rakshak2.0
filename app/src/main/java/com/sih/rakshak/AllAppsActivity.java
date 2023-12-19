package com.sih.rakshak;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.sih.rakshak.Fragments.Dashboard;

import java.util.ArrayList;
import java.util.List;

public class AllAppsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_apps);

        try {
            initiateRecycler();
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void initiateRecycler() throws PackageManager.NameNotFoundException {
        List<Dashboard.AppInfo> appsList = getListOfInstalledApps();
        RecyclerView recyclerView = findViewById(R.id.recyclerAllApps);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        AppListAdapter appListAdapter = new AppListAdapter(appsList);
        recyclerView.setAdapter(appListAdapter);
    }


    private List<Dashboard.AppInfo> getListOfInstalledApps() throws PackageManager.NameNotFoundException {
        List<Dashboard.AppInfo> appsList = new ArrayList<>();

        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager pm = getPackageManager();
        List<ResolveInfo> ril = pm.queryIntentActivities(mainIntent, 0);

        for (ResolveInfo ri : ril) {
            if (ri.activityInfo != null) {
                Resources res = pm.getResourcesForApplication(ri.activityInfo.applicationInfo);
                String name;
                if (ri.activityInfo.labelRes != 0) {
                    name = res.getString(ri.activityInfo.labelRes);
                } else {
                    name = ri.activityInfo.applicationInfo.loadLabel(pm).toString();
                }

                Drawable icon = ri.activityInfo.loadIcon(pm);

                Dashboard.AppInfo appInfo = new Dashboard.AppInfo(name, icon, ri);
                appsList.add(appInfo);
            }
        }

        return appsList;
    }

}