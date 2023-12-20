package com.sih.rakshak;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app_vertical_green, parent, false);
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
        private final ImageView appIconImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appNameTextView = itemView.findViewById(R.id.appNameTextView);
            appProbabilityTextView = itemView.findViewById(R.id.appVersionTextView);
            appIconImageView = itemView.findViewById(R.id.appIconImageView);
        }

        public void bind(AppInfoItem appInfoItem) {
            ApplicationInfo applicationInfo = appInfoItem.getApplicationInfo();
            String appProbability = appInfoItem.getAppProbability();

            appNameTextView.setText(applicationInfo.loadLabel(itemView.getContext().getPackageManager()));
            appProbabilityTextView.setText(getProbableText(appProbability));

            PackageManager pm = itemView.getContext().getPackageManager();
            appIconImageView.setImageDrawable(applicationInfo.loadIcon(pm));


        }

        private String getProbableText(String appProbability) {

            if (Integer.parseInt(appProbability) < 80) {
                return "Safe";
            }

            return "";
        }
    }
}
