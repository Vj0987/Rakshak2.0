package com.sih.rakshak;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class AppScanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_scan);

        traverseInstalledApps();
    }

    private void traverseInstalledApps() {
        List<String> apkFilePaths = new ArrayList<>();

        @SuppressLint("InlinedApi") List<PackageInfo> packages = getPackageManager().getInstalledPackages(PackageManager.INSTALL_REASON_USER);
        for (PackageInfo packageInfo : packages) {
            ApplicationInfo appInfo = packageInfo.applicationInfo;

            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && appInfo.name != null) {
                String apkFilePath = appInfo.sourceDir;
                apkFilePaths.add(apkFilePath);

                new Thread(() -> {
                    String sha256Key = getSHA256Key(apkFilePath);
                    Log.d("AppScanActivity", "traverseInstalledApps: " + appInfo.name + " " + sha256Key);
                }).start();
            }
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
}
