package com.sih.rakshak;

import android.app.ProgressDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogEntryAdapter extends RecyclerView.Adapter<LogEntryAdapter.ViewHolder> {

    private List<LogEntry> logEntries;
    private OnLogEntryClickListener clickListener;

    public LogEntryAdapter(List<LogEntry> logEntries, OnLogEntryClickListener clickListener) {
        this.logEntries = logEntries;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_entry_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LogEntry logEntry = logEntries.get(position);
        holder.timestampTextView.setText(logEntry.getTimestamp());
    }

    @Override
    public int getItemCount() {
        if (logEntries == null) {
            return 0;
        }
        return logEntries.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView timestampTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
            itemView.setOnClickListener(this);

            itemView.findViewById(R.id.reportMargin).setOnClickListener(view -> {
                showProgressDialog();
            });
        }

        private void showProgressDialog() {
            ProgressDialog progressDialog = new ProgressDialog(itemView.getContext());
            progressDialog.setMessage("Storing to Firestore...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            storeListToFirestore(timestampTextView.getText().toString(), progressDialog);
        }

        private void storeListToFirestore(String documentId, ProgressDialog progressDialog) {
            CollectionReference reportsRef = FirebaseFirestore.getInstance().collection("Reports");

            Map<String, Object> data = new HashMap<>();
            data.put("LOGS", logEntries);

            reportsRef.document(documentId)
                    .set(data)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(itemView.getContext(), "List stored to Firestore", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(itemView.getContext(), "Failed to store list to Firestore", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    });
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onLogEntryClick(getAdapterPosition());
            }
        }
    }

    public interface OnLogEntryClickListener {
        void onLogEntryClick(int position);
    }
}
