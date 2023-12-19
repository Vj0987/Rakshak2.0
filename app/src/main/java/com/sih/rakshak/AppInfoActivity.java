package com.sih.rakshak;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class AppInfoActivity extends AppCompatActivity {

    TextView aName, aVersion, aInstallDate, aUpdateDate, aInstallSource;

    ImageView circleImageView;

    List<String> appList = new ArrayList<>();

    RecyclerView riskyList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);

        initiateViews();

        riskyList = findViewById(R.id.listRisky);

        ResolveInfo resolveInfo = getIntent().getParcelableExtra("AppInfo");
        PackageManager pm = getPackageManager();

        assert resolveInfo != null;
        if (resolveInfo.activityInfo != null) {
            try {
                Resources res = pm.getResourcesForApplication(resolveInfo.activityInfo.applicationInfo);
                String aName;
                if (resolveInfo.activityInfo.labelRes != 0) {
                    aName = res.getString(resolveInfo.activityInfo.labelRes);
                } else {
                    aName = resolveInfo.activityInfo.applicationInfo.loadLabel(pm).toString();
                }

                PackageInfo packageInfo = pm.getPackageInfo(resolveInfo.activityInfo.packageName, PackageManager.GET_PERMISSIONS);
                String[] requestedPermissions = packageInfo.requestedPermissions;

                if (requestedPermissions != null) {
                    setValues(aName, packageInfo, resolveInfo);
                    Collections.addAll(appList, getRiskyPermissions(requestedPermissions));
                    setupRiskyPermissionsListView();
                }
            } catch (PackageManager.NameNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void setValues(String aName, PackageInfo packageInfo, ResolveInfo resolveInfo) throws PackageManager.NameNotFoundException {
        this.aName.setText(aName);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("IST"));

        Date installDate = new Date(packageInfo.firstInstallTime);
        Date updateDate = new Date(packageInfo.lastUpdateTime);

        aVersion.setText(packageInfo.versionName);
        aInstallDate.setText(installDate + "");
        aUpdateDate.setText(updateDate + "");

        String sourceDir = packageInfo.applicationInfo.sourceDir;
        String installationSource = getInstallationSource(sourceDir);
        aInstallSource.setText("" + installationSource);

        if (resolveInfo.activityInfo != null) {
            PackageManager pm = getPackageManager();
            Drawable appIcon = resolveInfo.activityInfo.applicationInfo.loadIcon(pm);
            circleImageView.setImageDrawable(appIcon);
        }
    }

    private String getInstallationSource(String sourceDir) {
        if (sourceDir.startsWith("/data/app/")) {
            return "Play Store (User Installed)";
        } else if (sourceDir.startsWith("/system/app/")) {
            return "System App";
        } else {
            return "Unknown";
        }
    }

    private String[] getRiskyPermissions(String[] allPermissions) {
        List<String> riskyPermissions = new ArrayList<>();
        for (String permission : allPermissions) {
            if (permission.equals("android.permission.READ_PHONE_STATE") || permission.equals("android.permission.READ_SMS")) {
                riskyPermissions.add(permission);
            }

            if (permission.equals("android.permission.READ_CONTACTS") || permission.equals("android.permission.READ_CALL_LOG")) {
                riskyPermissions.add(permission);
            }

            if (permission.equals("android.permission.WRITE_EXTERNAL_STORAGE") || permission.equals("android.permission.CAMERA")) {
                riskyPermissions.add(permission);
            }

            if (permission.equals("android.permission.RECORD_AUDIO") || permission.equals("android.permission.ACCESS_FINE_LOCATION")) {
                riskyPermissions.add(permission);
            }

            if (permission.equals("android.permission.ACCESS_COARSE_LOCATION") || permission.equals("android.permission.READ_EXTERNAL_STORAGE")) {
                riskyPermissions.add(permission);
            }

            if (permission.equals("android.permission.GET_ACCOUNTS")) {
                riskyPermissions.add(permission);
            }

            if (permission.equals("android.permission.SEND_SMS") || permission.equals("android.permission.RECEIVE_SMS")) {
                riskyPermissions.add(permission);
            }

            if (permission.equals("android.permission.CALL_PHONE") || permission.equals("android.permission.PROCESS_OUTGOING_CALLS")) {
                riskyPermissions.add(permission);
            }

            if (permission.equals("android.permission.BODY_SENSORS") || permission.equals("android.permission.READ_EXTERNAL_STORAGE")) {
                riskyPermissions.add(permission);
            }

            if (permission.equals("android.permission.WRITE_CONTACTS") || permission.equals("android.permission.WRITE_CALL_LOG")) {
                riskyPermissions.add(permission);
            }

            if (permission.equals("android.permission.USE_SIP") || permission.equals("android.permission.ADD_VOICEMAIL")) {
                riskyPermissions.add(permission);
            }

            if (permission.equals("android.permission.BLUETOOTH") || permission.equals("android.permission.BLUETOOTH_ADMIN")) {
                riskyPermissions.add(permission);
            }

            if (permission.equals("android.permission.WRITE_SETTINGS") || permission.equals("android.permission.SYSTEM_ALERT_WINDOW")) {
                riskyPermissions.add(permission);
            }

            if (permission.equals("android.permission.MANAGE_OWN_CALLS") || permission.equals("android.permission.WRITE_CONTACTS")) {
                riskyPermissions.add(permission);
            }

            if (permission.equals("android.permission.ACCESS_FINE_LOCATION") || permission.equals("android.permission.ACCESS_COARSE_LOCATION")) {
                riskyPermissions.add(permission);
            }

            if (permission.equals("android.permission.RECORD_AUDIO") || permission.equals("android.permission.CAPTURE_AUDIO_OUTPUT")) {
                riskyPermissions.add(permission);
            }

            if (permission.equals("android.permission.CAMERA") || permission.equals("android.permission.WRITE_EXTERNAL_STORAGE")) {
                riskyPermissions.add(permission);
            }

            if (permission.equals("android.permission.READ_CALL_LOG") || permission.equals("android.permission.WRITE_CALL_LOG")) {
                riskyPermissions.add(permission);
            }

        }

        return riskyPermissions.toArray(new String[0]);
    }

    private void setupRiskyPermissionsListView() {

        riskyList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        RiskyPermissionAdapter adapter = new RiskyPermissionAdapter(this, appList);
        riskyList.setAdapter(adapter);

    }

    private void initiateViews() {
        aName = findViewById(R.id.NameApp);
        aVersion = findViewById(R.id.versionApp);
        aInstallDate = findViewById(R.id.instTime);
        aInstallSource = findViewById(R.id.instSource);
        aUpdateDate = findViewById(R.id.lastUpdateTime);
        circleImageView = findViewById(R.id.appIcon);
    }
}
