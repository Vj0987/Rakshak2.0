package com.sih.rakshak;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RiskyPermissionAdapter extends RecyclerView.Adapter<RiskyPermissionAdapter.ViewHolder> {

    private final List<String> riskyPermissions;
    private final LayoutInflater inflater;

    public RiskyPermissionAdapter(Context context, List<String> riskyPermissions) {
        this.riskyPermissions = riskyPermissions;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String permission = riskyPermissions.get(position);
        holder.permissionTextView.setText(permission);
    }

    @Override
    public int getItemCount() {
        return riskyPermissions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView permissionTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            permissionTextView = itemView.findViewById(R.id.listtext);
        }
    }
}
