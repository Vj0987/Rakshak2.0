package com.sih.rakshak;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppInfoActivity extends AppCompatActivity {

    TextView aName, aVersion, aInstallDate, aUpdateDate, aInstallSource;

    ImageView circleImageView;

    List<String> appList = new ArrayList<>();

    RecyclerView riskyList;

    FrameLayout frameLayout;

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

                String apkPath = packageInfo.applicationInfo.sourceDir;

                sendSHARequest(getSHA256Key(apkPath));

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

    private String getSHA256Key(String filePath) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            FileInputStream fis = new FileInputStream(filePath);

            byte[] byteArray = new byte[1024];
            int bytesCount;
            while ((bytesCount = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount);
            }
            fis.close();

            byte[] bytes = digest.digest();

            StringBuilder hashStringBuilder = new StringBuilder();
            for (byte aByte : bytes) {
                hashStringBuilder.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }

            return hashStringBuilder.toString();
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
            return null;
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

    private void sendSHARequest(String shaKey) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://192.168.193.251/").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<String> call = apiService.getSHAKey(shaKey);

        call.enqueue(new Callback<String>() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    String result = response.body();
                    Log.d("RESULT-APP-T", "onResponse: " + result);
                    assert result != null;
                    if (Integer.parseInt(result) > 75) {
                        frameLayout.setBackground(getDrawable(R.drawable.curved_bg_red));
                        aName.append("\t(Unsafe)");
                    } else {
                        frameLayout.setBackground(getDrawable(R.drawable.curved_bg));
                        aName.append("\t(Safe)");
                    }
                } else {
                    Log.d("RESULT-APP-F", "onResponse: " + response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.d("RESULT-APP-E", "onResponse: " + t);
            }
        });
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

        frameLayout = findViewById(R.id.backgroundFrame);
    }
}
