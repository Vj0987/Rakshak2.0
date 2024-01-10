package com.sih.rakshak;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppScanActivity extends AppCompatActivity {
    int cnt1 = 0;
    int cnt2 = 0;
    TextView appNameTextView, appVersionTextView;
    ImageView appIconImageView;
    TextView malApp, unMalApp;

    List<ApplicationInfo> appInfo = new ArrayList<>();
    List<String> appProbability = new ArrayList<>();

    private static final String PREF_KEY_LOG_ENTRIES = "log_entries";

    private List<LogEntry> logEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_scan);

        initiateViews();

        traverseInstalledApps();
    }

    private void initiateViews() {
        appNameTextView = findViewById(R.id.appNameTextView);
        appVersionTextView = findViewById(R.id.appVersionTextView);
        appIconImageView = findViewById(R.id.appIconImageView);

        malApp = findViewById(R.id.malApp);
        unMalApp = findViewById(R.id.unMallApp);

    }

    @SuppressLint("SetTextI18n")
    private void traverseInstalledApps() {
        long currentTime = System.currentTimeMillis();
        long sixMonthsInMillis = 30L * 24L * 60L * 60L * 1000L;

        @SuppressLint("InlinedApi") List<PackageInfo> packages = getPackageManager().getInstalledPackages(PackageManager.INSTALL_REASON_USER);
        for (PackageInfo packageInfo : packages) {
            ApplicationInfo appInfo = packageInfo.applicationInfo;
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && appInfo.name != null) {
                if (currentTime - packageInfo.lastUpdateTime <= sixMonthsInMillis) {
                    String apkFilePath = appInfo.sourceDir;
                    new Thread(() -> {
                        String shaKey = getSHA256Key(apkFilePath);
                        sendSHARequest(shaKey, appInfo);
                    }).start();
                }
            }
        }
    }

    private void sendSHARequest(String shaKey, ApplicationInfo applicationInfo) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://192.168.193.251:5000/").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<String> call = apiService.getSHAKey(shaKey);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    String result = response.body();
                    Log.d("RESULT-APP-T", "onResponse: " + result);
                    setChanges(applicationInfo, result);
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

    private void setChanges(ApplicationInfo applicationInfo, String result) {

        PackageManager packageManager = getPackageManager();
        CharSequence appName = applicationInfo.loadLabel(packageManager);
        appNameTextView.setText(appName);

        appVersionTextView.setText(applicationInfo.packageName);

        PackageManager pm = getPackageManager();
        Drawable appIcon = applicationInfo.loadIcon(pm);
        appIconImageView.setImageDrawable(appIcon);

        appInfo.add(applicationInfo);
        appProbability.add(result);

        setResultWithText(Integer.parseInt(result));
    }

    @SuppressLint("SetTextI18n")
    private void setResultWithText(int prob) {
        if (prob > 75) {
            cnt1++;
            malApp.setText(cnt1 + " ");
        } else {
            cnt2++;
            unMalApp.setText(cnt2 + "");
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

    public void openReport(View view) {
        logEntries = loadLogEntries();

        LogEntry logEntry = new LogEntry(String.valueOf(System.currentTimeMillis()), appInfo, appProbability);

        if (logEntries == null) {
            logEntries = new ArrayList<>();
        }

        logEntries.add(logEntry);
        saveLogEntriesToSharedPreferences(logEntries);

        Intent intent = new Intent(this, AppReportActivity.class);
        intent.putParcelableArrayListExtra("appInfoList", new ArrayList<>(appInfo));
        intent.putStringArrayListExtra("probability", new ArrayList<>(appProbability));
        startActivity(intent);

    }

    private void saveLogEntriesToSharedPreferences(List<LogEntry> logEntries) {
        Gson gson = new Gson();
        String jsonLogEntries = gson.toJson(logEntries);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_KEY_LOG_ENTRIES, jsonLogEntries);
        editor.apply();
    }

    private List<LogEntry> loadLogEntries() {
        // Retrieve existing log entries from SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String jsonLogEntries = sharedPreferences.getString(PREF_KEY_LOG_ENTRIES, null);

        // Convert JSON back to ArrayList using Gson
        Gson gson = new Gson();
        Type logEntryType = new TypeToken<List<LogEntry>>() {
        }.getType();

        return gson.fromJson(jsonLogEntries, logEntryType);
    }
}
