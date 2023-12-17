package com.sih.rakshak;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SecurityLogActivity extends AppCompatActivity {

    private TextView textView;
    private Handler handler;
    private volatile boolean stopLogging = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_log);

        textView = findViewById(R.id.logResult);
        handler = new Handler();

        displayLogs();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLogging = true;
        handler.removeCallbacksAndMessages(null);
    }

    private void displayLogs() {
        new Thread(() -> {
            try {
                Process process = Runtime.getRuntime().exec("logcat -d");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                StringBuilder log = new StringBuilder();
                String line;

                while (!stopLogging && (line = bufferedReader.readLine()) != null) {
                    log.append(line).append("\n");

                    String finalLogData = line;
                    handler.post(() -> textView.setText(finalLogData));
                    Log.d("DeviceLog", "displayLogs: " + log);

                    Thread.sleep(500);
                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
