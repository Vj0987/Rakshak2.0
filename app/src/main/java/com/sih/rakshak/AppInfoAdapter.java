package com.sih.rakshak;

import android.content.pm.ApplicationInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AppInfoAdapter extends RecyclerView.Adapter<AppInfoAdapter.ViewHolder> {
    private final List<AppInfoItem> appInfoItemList;

    public AppInfoAdapter(List<AppInfoItem> appInfoItemList) {
        this.appInfoItemList = appInfoItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app_vertical, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppInfoItem appInfoItem = appInfoItemList.get(position);
        holder.bind(appInfoItem);
    }

    @Override
    public int getItemCount() {
        return appInfoItemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView appNameTextView;
        private final TextView appProbabilityTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appNameTextView = itemView.findViewById(R.id.appNameTextView);
            appProbabilityTextView = itemView.findViewById(R.id.appVersionTextView);
        }

        public void bind(AppInfoItem appInfoItem) {
            ApplicationInfo applicationInfo = appInfoItem.getApplicationInfo();
            String appProbability = appInfoItem.getAppProbability();

            appNameTextView.setText(applicationInfo.loadLabel(itemView.getContext().getPackageManager()));
            appProbabilityTextView.setText(appProbability);
        }
    }
}
