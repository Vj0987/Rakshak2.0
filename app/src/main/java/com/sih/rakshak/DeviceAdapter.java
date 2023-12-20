package com.sih.rakshak;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceViewHolder> {
    private final List<String> deviceInfoList;

    public DeviceAdapter(List<String> deviceInfoList) {
        this.deviceInfoList = deviceInfoList;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_list_item, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        String deviceInfo = deviceInfoList.get(position);
        holder.deviceInfoTextView.setText(deviceInfo);

        String ipAddress;

        String[] separatedStrings = deviceInfo.split("\n");
        if (separatedStrings.length >= 2) {
            ipAddress = separatedStrings[0];
        } else {
            ipAddress = "";
            System.out.println("Invalid input string format");
        }

        holder.deviceInfoTextView.setOnClickListener(view -> CustomDialog.show(holder.deviceInfoTextView.getContext(), ipAddress));

    }

    @Override
    public int getItemCount() {
        return deviceInfoList.size();
    }
}
