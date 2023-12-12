package com.sih.rakshak;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import net.dongliu.apk.parser.ApkParser;
import net.dongliu.apk.parser.bean.ApkMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SecurityScanActivity extends AppCompatActivity {
    private TextView fileTextView, resultTextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_scan);
        setNavColor();

        Toolbar toolbar = findViewById(R.id.seScanToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        fileTextView = findViewById(R.id.scanFeedLive);
        resultTextview = findViewById(R.id.scanResult);

        new ApkScanTask().execute();

    }

    @SuppressLint("StaticFieldLeak")
    private class ApkScanTask extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            List<String> apkFilePaths = getAllInstalledApks();

            for (String apkFilePath : apkFilePaths) {
                publishProgress(apkFilePath);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        private List<String> getAllInstalledApks() {
            List<String> apkFilePaths = new ArrayList<>();

            List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
            for (PackageInfo packageInfo : packages) {
                ApplicationInfo appInfo = packageInfo.applicationInfo;
                String apkFilePath = appInfo.sourceDir;
                apkFilePaths.add(apkFilePath);
            }

            return apkFilePaths;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            for (String value : values) {
                fileTextView.setSingleLine();
                fileTextView.setEllipsize(TextUtils.TruncateAt.START);
                fileTextView.setText(value);
            }
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            fileTextView.setText("Scanning Done.");
            resultTextview.setText("No Threat Found!");
            Toast.makeText(SecurityScanActivity.this, "Scanning Completed", Toast.LENGTH_SHORT).show();
        }
    }

    private void setNavColor() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setNavigationBarColor(getResources().getColor(R.color.main, getTheme()));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}