package com.sih.rakshak;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.Arrays;

public class CleanerWithSActivity extends AppCompatActivity {

    private TextView textView1;
    private TextView textView2;

    private TextView tv1;
    private TextView tv2;
    private TextView tv3;
    private long totalTmpSize = 0;
    private long totalLogSize = 0;
    private long totalCacheSize = 0;

    ProgressBar pb1, pb2, pb3, pb4;

    private static final int REQUEST_MANAGE_ALL_FILES_ACCESS_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleaner_with_sactivity);

        textView1 = findViewById(R.id.checkingUpdate);
        textView2 = findViewById(R.id.checkResult);

        tv1 = findViewById(R.id.checkResult1);
        tv2 = findViewById(R.id.checkResult2);
        tv3 = findViewById(R.id.checkResult3);
        pb1 = findViewById(R.id.progress1);
        pb2 = findViewById(R.id.progress2);
        pb3 = findViewById(R.id.progress3);
        pb4 = findViewById(R.id.progress4);

        Button button = findViewById(R.id.cleanDataButton);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            button.setOnClickListener(v -> {
                Toast.makeText(this, "Under Development.", Toast.LENGTH_SHORT).show();
            });
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                new ScanJunkFilesTask().execute();
            } else {
                Toast.makeText(this, "Please Grant Permission", Toast.LENGTH_SHORT).show();
                requestManageAllFilesAccessPermission();
            }
        }

    }

    @SuppressLint("StaticFieldLeak")
    private class ScanJunkFilesTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return checkDeviceForJunkFiles();
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            textView1.setText("Scanning Done");
            textView2.setText(result);

            setTextViews();
        }

        @SuppressLint("SetTextI18n")
        private void setTextViews() {
            tv2.setText("" + formatSize(totalCacheSize));
            tv1.setText("" + formatSize(totalLogSize));
            tv3.setText("" + formatSize(totalTmpSize));

            pb4.setVisibility(View.GONE);
            pb3.setVisibility(View.GONE);
            pb2.setVisibility(View.GONE);
            pb1.setVisibility(View.GONE);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void requestManageAllFilesAccessPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
        startActivityForResult(intent, REQUEST_MANAGE_ALL_FILES_ACCESS_PERMISSION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_MANAGE_ALL_FILES_ACCESS_PERMISSION) {
            checkDeviceForJunkFiles();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @SuppressLint("SetTextI18n")
    private String checkDeviceForJunkFiles() {
        StringBuilder result = new StringBuilder();

        File externalStorage = Environment.getExternalStorageDirectory();

        long totalJunkSize = 0;

        totalJunkSize += calculateJunkSize(externalStorage);

        result.append(formatSize(totalJunkSize));

        return result.toString();
    }

    private long calculateJunkSize(File dir) {
        long totalSize = 0;

        StringBuilder junk = new StringBuilder();

        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (isJunkFile(file)) {
                        totalSize += file.length();
                        junk.append(file).append("\n");
                    } else if (file.isDirectory()) {
                        totalSize += calculateJunkSize(file);
                    }
                }
            }
        }

        Log.d("CALCULATE-JUNK", "isJunkFile: " + junk);

        return totalSize;
    }

    private void processJunkFile(File file) {
        String extension = getFileExtension(file);
        long fileSize = file.length();

        if (Arrays.asList("tmp", "log", "cache").contains(extension)) {
            switch (extension) {
                case "tmp":
                    totalTmpSize += fileSize;
                    break;
                case "log":
                    totalLogSize += fileSize;
                    break;
                case "cache":
                    totalCacheSize += fileSize;
                    break;
            }
        }

    }

    private String formatSize(long size) {
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        while (size > 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        return size + units[unitIndex];
    }

    private boolean isJunkFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File subFile : files) {
                    processJunkFile(subFile);
                }
                return files.length == 0;
            }
        } else {
            processJunkFile(file);
        }

        return false;
    }

    private String getFileExtension(File file) {
        String fileName = file.getName();
        int lastDotIndex = fileName.lastIndexOf(".");

        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }

        return "";
    }

}