package com.sih.rakshak.Fragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sih.rakshak.R;

import java.util.ArrayList;
import java.util.List;

public class Dashboard extends Fragment {

    private FrameLayout greenFrameLayout, mainLightFrameLayout;
    private TextView progressTextView, currentBatteryText;

    private int currentProgress = 0;
    private static final int MAX_PROGRESS = 69;
    private static final int INCREMENT_AMOUNT = 1;
    private static final long LOOP_DELAY = 20;
    private Handler handler;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        final Toolbar toolbar = view.findViewById(R.id.toolbarDashboard);
        final NestedScrollView nestedScrollView = view.findViewById(R.id.nestedScrollView);

        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

            if (scrollY > oldScrollY) {
                toolbar.setTitle(R.string.app_name);
            } else if (scrollY < oldScrollY) {
                boolean isAtTop = !ViewCompat.canScrollVertically(nestedScrollView, -1);
                if (isAtTop) {
                    toolbar.setTitle("");
                }
            }
        });

        greenFrameLayout = view.findViewById(R.id.greenFrameLayout);
        mainLightFrameLayout = view.findViewById(R.id.mainLightFrameLayout);
        progressTextView = view.findViewById(R.id.progressTextView);

        handler = new Handler(Looper.getMainLooper());
        startProgressLoop();

        currentBatteryText = view.findViewById(R.id.currentLevel);
        registerBatteryReceiver();

        try {
            initiateRecycler();
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }

        return view;

    }

    private void initiateRecycler() throws PackageManager.NameNotFoundException {
        List<AppInfo> appsList = getListOfInstalledApps();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewApps);
         recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
//        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 4));
        AppListAdapter appListAdapter = new AppListAdapter(appsList);
        recyclerView.setAdapter(appListAdapter);

    }

    private List<AppInfo> getListOfInstalledApps() throws PackageManager.NameNotFoundException {
        List<AppInfo> appsList = new ArrayList<>();

        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager pm = requireActivity().getPackageManager();
        List<ResolveInfo> ril = pm.queryIntentActivities(mainIntent, 0);

        for (ResolveInfo ri : ril) {
            if (ri.activityInfo != null) {
                Resources res = pm.getResourcesForApplication(ri.activityInfo.applicationInfo);
                String name;
                if (ri.activityInfo.labelRes != 0) {
                    name = res.getString(ri.activityInfo.labelRes);
                } else {
                    name = ri.activityInfo.applicationInfo.loadLabel(pm).toString();
                }

                Drawable icon = ri.activityInfo.loadIcon(pm);

                AppInfo appInfo = new AppInfo(name, icon);
                appsList.add(appInfo);
            }
        }

        return appsList;
    }

    @Override
    public void onDestroy() {
        requireActivity().unregisterReceiver(batteryReceiver);
        super.onDestroy();
    }

    private void registerBatteryReceiver() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        requireActivity().registerReceiver(batteryReceiver, filter);
    }

    private final BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

                float batteryPct = (level / (float) scale) * 100;
                currentBatteryText.setText("Current Battery Percentage: " + batteryPct + " %");
            }
        }
    };

    public static class AppInfo {
        private final String appName;
        private final Drawable appIcon;

        public AppInfo(String appName, Drawable appIcon) {
            this.appName = appName;
            this.appIcon = appIcon;
        }

        public String getAppName() {
            return appName;
        }

        public Drawable getAppIcon() {
            return appIcon;
        }
    }

    private void startProgressLoop() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateProgress(currentProgress);
                currentProgress += INCREMENT_AMOUNT;
                if (currentProgress <= MAX_PROGRESS) {
                    handler.postDelayed(this, LOOP_DELAY);
                }
            }
        }, LOOP_DELAY);
    }

    @SuppressLint("SetTextI18n")
    private void updateProgress(int progress) {
        progress = Math.max(0, Math.min(MAX_PROGRESS, progress));

        int maxWidth = view.findViewById(R.id.linearLayout).getWidth();
        int newWidth = (int) ((progress) / 100.0 * maxWidth);

        ViewGroup.LayoutParams mainLightLayoutParams = greenFrameLayout.getLayoutParams();
        mainLightLayoutParams.width = newWidth;
        mainLightFrameLayout.setLayoutParams(mainLightLayoutParams);
        progressTextView.setText(progress + " %");
    }

    public static class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.AppViewHolder> {

        private final List<AppInfo> apps;

        public AppListAdapter(List<AppInfo> apps) {
            this.apps = apps;
        }

        @NonNull
        @Override
        public AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app, parent, false);
            return new AppViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AppViewHolder holder, int position) {
            AppInfo appInfo = apps.get(position);
            holder.appNameTextView.setText(appInfo.getAppName());

            // Assuming AppInfo has a property for app icon
            holder.appIconImageView.setImageDrawable(appInfo.getAppIcon());
        }

        @Override
        public int getItemCount() {
            return apps.size();
        }

        public static class AppViewHolder extends RecyclerView.ViewHolder {
            TextView appNameTextView;
            ImageView appIconImageView;

            public AppViewHolder(View itemView) {
                super(itemView);
                appNameTextView = itemView.findViewById(R.id.appNameTextView);
                appIconImageView = itemView.findViewById(R.id.appIconImageView);
            }
        }
    }
}