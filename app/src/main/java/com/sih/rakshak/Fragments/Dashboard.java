package com.sih.rakshak.Fragments;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
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
import android.os.Process;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.sih.rakshak.AppInfoActivity;
import com.sih.rakshak.AppScanActivity;
import com.sih.rakshak.CheckURLActivity;
import com.sih.rakshak.CleanerActivity;
import com.sih.rakshak.CleanerWithSActivity;
import com.sih.rakshak.MessageActivity;
import com.sih.rakshak.R;
import com.sih.rakshak.SecurityLogActivity;
import com.sih.rakshak.SecurityScanActivity;
import com.sih.rakshak.WifiUtil;

import java.util.ArrayList;
import java.util.List;

public class Dashboard extends Fragment {

    private FrameLayout greenFrameLayout, mainLightFrameLayout;
    private TextView progressTextView, currentBatteryText;

    private int currentProgress = 0;
    private int MAX_PROGRESS = 0;
    private static final int INCREMENT_AMOUNT = 1;
    private static final long LOOP_DELAY = 20;
    private Handler handler;
    View view;

    LinearLayout linearLayout1, linearLayout2;

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

        currentBatteryText = view.findViewById(R.id.currentLevel);

        try {
            AppOpsManager appOps = (AppOpsManager) requireActivity().getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), requireActivity().getPackageName());

            if (mode == AppOpsManager.MODE_ALLOWED) {
                initiateRecycler();
            } else {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                startActivity(intent);
                Toast.makeText(requireContext(), "Please Grant Permission.", Toast.LENGTH_SHORT).show();
            }

        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }

        registerBatteryReceiver();
        handleViewVisibility();

        handleTextClicks();

        setBatteryGraph();

        return view;

    }

    private void setBatteryGraph() {
        LineChart lineChart = view.findViewById(R.id.lineChart);

        // Example data for demonstration
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(1, 20));
        entries.add(new Entry(2, 35));
        entries.add(new Entry(3, 18));
        entries.add(new Entry(4, 27));

        LineDataSet dataSet = new LineDataSet(entries, "Label");
        LineData lineData = new LineData(dataSet);

        lineChart.setData(lineData);
        lineChart.invalidate();

    }

    private void handleTextClicks() {
        view.findViewById(R.id.openBatterySettings).setOnClickListener(view1 -> {
            Intent intent = new Intent(android.provider.Settings.ACTION_BATTERY_SAVER_SETTINGS);
            startActivity(intent);
        });

        view.findViewById(R.id.openSystemPerm).setOnClickListener(view1 -> {
            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS);
            startActivity(intent);
        });

        view.findViewById(R.id.securityScan).setOnClickListener(view1 -> {
            startActivity(new Intent(getContext(), AppScanActivity.class));
        });

        view.findViewById(R.id.cleanerAct).setOnClickListener(view1 -> {
            startActivity(new Intent(getContext(), CleanerActivity.class));
            // startActivity(new Intent(getContext(), CleanerWithSActivity.class));
        });

        view.findViewById(R.id.wifiScan).setOnClickListener(view1 -> {
            startActivity(new Intent(getContext(), WifiUtil.class));
        });

        view.findViewById(R.id.securityLogActivity).setOnClickListener(view1 -> {
            startActivity(new Intent(getContext(), SecurityLogActivity.class));
        });

        view.findViewById(R.id.messageActivity).setOnClickListener(view1 -> {
            startActivity(new Intent(getContext(), MessageActivity.class));
        });

        view.findViewById(R.id.checkURLActivity).setOnClickListener(view1 -> {
            startActivity(new Intent(getContext(), CheckURLActivity.class));
        });
    }

    private void handleViewVisibility() {

        linearLayout1 = view.findViewById(R.id.scanningLayout);
        linearLayout2 = view.findViewById(R.id.scannerLayout);
        LottieAnimationView lottieAnimationView = view.findViewById(R.id.scannerAnimation);

        linearLayout2.setVisibility(View.GONE);
        linearLayout1.setVisibility(View.VISIBLE);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            linearLayout2.setVisibility(View.VISIBLE);
            lottieAnimationView.pauseAnimation();
            linearLayout1.setVisibility(View.GONE);
            startProgressLoop();
        }, 2200);
    }

    private void initiateRecycler() throws PackageManager.NameNotFoundException {
        List<AppInfo> appsList = getListOfInstalledApps();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewApps);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
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

                AppInfo appInfo = new AppInfo(name, icon, ri);
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

                MAX_PROGRESS = Math.round(batteryPct);
                int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
                float temperatureCelsius = temperature / 10.0f;

                currentBatteryText.setText("Overall Battery Health: Good\nBattery Temperature: " + temperatureCelsius + " °C");
            }
        }
    };

    public static class AppInfo {
        private final String appName;
        private final Drawable appIcon;

        private final ResolveInfo resolveInfo;

        public AppInfo(String appName, Drawable appIcon, ResolveInfo ri) {
            this.appName = appName;
            this.appIcon = appIcon;
            resolveInfo = ri;
        }

        public String getAppName() {
            return appName;
        }

        public ResolveInfo getResolveInfo() {
            return resolveInfo;
        }

        public Drawable getAppIcon() {
            return appIcon;
        }
    }

    private void startProgressLoop() {

        if (MAX_PROGRESS != 0) {
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
            holder.appIconImageView.setImageDrawable(appInfo.getAppIcon());

            holder.appIconImageView.setOnClickListener(view1 -> {
                Intent intent = new Intent(view1.getContext(), AppInfoActivity.class);
                intent.putExtra("AppInfo", appInfo.getResolveInfo());
                view1.getContext().startActivity(intent);
            });
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