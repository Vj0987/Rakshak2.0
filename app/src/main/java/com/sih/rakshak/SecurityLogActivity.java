package com.sih.rakshak;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SecurityLogActivity extends AppCompatActivity implements LogEntryAdapter.OnLogEntryClickListener {


    private static final String PREF_KEY_LOG_ENTRIES = "log_entries";

    private List<LogEntry> logEntries;
    private RecyclerView recyclerView;
    private LogEntryAdapter logEntryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_log);

        logEntries = loadLogEntries();

        // Initialize RecyclerView and adapter
        recyclerView = findViewById(R.id.reportScans);
        logEntryAdapter = new LogEntryAdapter(logEntries, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(logEntryAdapter);

    }

    private List<LogEntry> loadLogEntries() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String jsonLogEntries = sharedPreferences.getString(PREF_KEY_LOG_ENTRIES, null);

        Gson gson = new Gson();
        Type logEntryType = new TypeToken<List<LogEntry>>() {
        }.getType();

        return gson.fromJson(jsonLogEntries, logEntryType);
    }

    @Override
    public void onLogEntryClick(int position) {
        LogEntry clickedLogEntry = logEntries.get(position);

        // Start AppReportActivity and pass the clicked log entry data
        Intent intent = new Intent(this, AppReportActivity.class);
        intent.putExtra("timestamp", clickedLogEntry.getTimestamp());
        intent.putParcelableArrayListExtra("appInfoList", new ArrayList<>(clickedLogEntry.getAppInfoList()));
        intent.putStringArrayListExtra("probability", new ArrayList<>(clickedLogEntry.getAppProbabilityList()));
        startActivity(intent);
    }
}