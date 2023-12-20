package com.sih.rakshak;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DeviceViewHolder extends RecyclerView.ViewHolder {
    public TextView deviceInfoTextView;

    public DeviceViewHolder(@NonNull View itemView) {
        super(itemView);
        deviceInfoTextView = itemView.findViewById(R.id.listtext);
    }
}
