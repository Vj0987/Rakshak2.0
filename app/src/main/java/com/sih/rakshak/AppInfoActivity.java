package com.sih.rakshak;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class AppInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);

        TextView textView = findViewById(R.id.appInfo);

        ResolveInfo resolveInfo = getIntent().getParcelableExtra("AppInfo");
        PackageManager pm = getPackageManager();

        assert resolveInfo != null;
        if (resolveInfo.activityInfo != null) {
            try {
                Resources res = pm.getResourcesForApplication(resolveInfo.activityInfo.applicationInfo);
                String name;
                if (resolveInfo.activityInfo.labelRes != 0) {
                    name = res.getString(resolveInfo.activityInfo.labelRes);
                } else {
                    name = resolveInfo.activityInfo.applicationInfo.loadLabel(pm).toString();
                }

                PackageInfo packageInfo = pm.getPackageInfo(resolveInfo.activityInfo.packageName, PackageManager.GET_PERMISSIONS);
                String[] requestedPermissions = packageInfo.requestedPermissions;

                if (requestedPermissions != null) {
                    name = name.concat("\n\nPermissions:\n\n");
                    for (String permission : requestedPermissions) {
                        name = name.concat("\n" + permission);
                    }
                }

                textView.setText(name);

            } catch (PackageManager.NameNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

    }
}