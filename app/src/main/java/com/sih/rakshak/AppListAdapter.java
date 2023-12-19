package com.sih.rakshak;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sih.rakshak.Fragments.Dashboard;

import java.util.List;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.AppViewHolder> {

    private final List<Dashboard.AppInfo> apps;

    public AppListAdapter(List<Dashboard.AppInfo> apps) {
        this.apps = apps;
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app_vertical, parent, false);
        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AppViewHolder holder, int position) {
        Dashboard.AppInfo appInfo = apps.get(position);
        holder.appNameTextView.setText(appInfo.getAppName());
        holder.appIconImageView.setImageDrawable(appInfo.getAppIcon());

        ResolveInfo appCom = appInfo.getResolveInfo();
        String version = appCom.activityInfo.packageName;
        holder.appversionTextView.setText(version);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), AppInfoActivity.class);
            intent.putExtra("AppInfo", appInfo.getResolveInfo());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    public static class AppViewHolder extends RecyclerView.ViewHolder {
        public TextView appNameTextView;
        public TextView appversionTextView;
        public ImageView appIconImageView;

        public AppViewHolder(View itemView) {
            super(itemView);
            appNameTextView = itemView.findViewById(R.id.appNameTextView);
            appversionTextView = itemView.findViewById(R.id.appVersionTextView);
            appIconImageView = itemView.findViewById(R.id.appIconImageView);
        }
    }
}
