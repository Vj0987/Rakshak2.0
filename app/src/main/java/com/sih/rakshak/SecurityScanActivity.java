package com.sih.rakshak;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SecurityScanActivity extends AppCompatActivity {

    private TextView fileTextView, resultTextview;

    private ExecutorService executorService;
    private Handler handler;

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

        startApkScan();
    }

    private void startApkScan() {
        handler = new Handler(Looper.getMainLooper());
        executorService = Executors.newFixedThreadPool(5);

        getAllInstalledApks();
    }

    private void getAllInstalledApks() {
        executorService.execute(() -> {
            List<String> apkFilePaths = new ArrayList<>();

            List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
            for (PackageInfo packageInfo : packages) {
                ApplicationInfo appInfo = packageInfo.applicationInfo;
                String apkFilePath = appInfo.sourceDir;
                apkFilePaths.add(apkFilePath);
            }

            handler.post(() -> onApkScanComplete(apkFilePaths));
        });
    }

    @SuppressLint("SetTextI18n")
    private void onApkScanComplete(List<String> apkFilePaths) {
        int delayMillis = 300;
        final int[] currentIndex = {0};

        Runnable updateUiRunnable = new Runnable() {
            @Override
            public void run() {
                fileTextView.setSingleLine();
                fileTextView.setEllipsize(TextUtils.TruncateAt.START);

                if (currentIndex[0] < apkFilePaths.size()) {
                    String apkFilePath = apkFilePaths.get(currentIndex[0]);
                    fileTextView.setText(apkFilePath);

                    handler.postDelayed(this, delayMillis);

                    currentIndex[0]++;
                } else {
                    fileTextView.setText("Scanning Done.");
                    resultTextview.setText("No Threat Found!");
                    Toast.makeText(SecurityScanActivity.this, "Scanning Completed", Toast.LENGTH_SHORT).show();
                }
            }
        };

        handler.postDelayed(updateUiRunnable, delayMillis);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdownNow();
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
