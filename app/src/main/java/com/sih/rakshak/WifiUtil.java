package com.sih.rakshak;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WifiUtil extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private ExecutorService executorService;

    private static final String TAG = "WifiUtils";
    private static final String BASE_IP_ADDRESS = "192.168.193."; //137
    private static final int TIMEOUT_MS = 1000;

    private RecyclerView recyclerView;
    private DeviceAdapter deviceAdapter;
    private List<String> deviceInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getAllWifiDevices();
        }

        recyclerView = findViewById(R.id.hotspotList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        deviceInfoList = new ArrayList<>();
        deviceAdapter = new DeviceAdapter(deviceInfoList);
        recyclerView.setAdapter(deviceAdapter);

    }

    private void getAllWifiDevices() {
        executorService = Executors.newFixedThreadPool(5);
        startNetworkDiscovery();

    }

    private void startNetworkDiscovery() {
        for (int i = 1; i <= 254; i++) {
            final int finalI = i;
            executorService.execute(() -> {
                String ipAddress = BASE_IP_ADDRESS + finalI;
                try {
                    InetAddress inetAddress = InetAddress.getByName(ipAddress);

                    if (inetAddress.isReachable(TIMEOUT_MS)) {
                        String hostName = getHostName(ipAddress);
                        String deviceInfo = "Device IP: " + ipAddress + "\nHost Name: " + hostName + "";
                        runOnUiThread(() -> updateUI(deviceInfo));
                    }

                } catch (IOException e) {
                    Log.e(TAG, "startNetworkDiscovery: ", e);
                    e.printStackTrace();
                }
            });
        }

        Log.d(TAG, "startNetworkDiscovery: Done");
    }

    private String getHostName(String ipAddress) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            return inetAddress.getHostName();
        } catch (UnknownHostException e) {
            Log.e(TAG, "Error getting host name: " + e.getMessage());
            return ipAddress;
        }
    }

    private void updateUI(String deviceInfo) {
        deviceInfoList.add(deviceInfo);
        deviceAdapter.notifyItemInserted(deviceInfoList.size() - 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdownNow();
    }
}
