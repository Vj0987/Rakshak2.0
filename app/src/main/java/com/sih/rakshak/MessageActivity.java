package com.sih.rakshak;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {

    private static final int READ_SMS_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        if (checkReadSmsPermission()) {
            readAndDisplayMessagesWithRecyclerView();
        } else {
            requestReadSmsPermission();
        }
    }

    private boolean checkReadSmsPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestReadSmsPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, READ_SMS_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_SMS_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            readAndDisplayMessagesWithRecyclerView();
        } else {
            Toast.makeText(this, "Please Grant Permission", Toast.LENGTH_SHORT).show();
        }
    }

    private void readAndDisplayMessagesWithRecyclerView() {
        ArrayList<String> messageList = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();
        Uri uri = Uri.parse("content://sms/inbox");

        Cursor cursor = contentResolver.query(uri, null, null, null, "date DESC LIMIT 20");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String sender = cursor.getString(cursor.getColumnIndex(Telephony.Sms.ADDRESS));
                @SuppressLint("Range") String messageBody = cursor.getString(cursor.getColumnIndex(Telephony.Sms.BODY));

                messageList.add("From: " + sender + "\nMessage: " + messageBody + "\n\n");
            } while (cursor.moveToNext());

            cursor.close();
        }

        RecyclerView recyclerView = findViewById(R.id.messageRecyclerView);
        RecyclerView.Adapter<MessageAdapter.ViewHolder> adapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void findSpam(View view) {
        Toast.makeText(this, "Under Development", Toast.LENGTH_SHORT).show();
    }
}
