package com.sih.rakshak;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CustomDialog {

    @SuppressLint("StaticFieldLeak")
    static TextView messageTextView;

    public static void show(Context context, String title) {
        final Dialog dialog = new Dialog(context);

        String ip = extractIPAddress(title);

        new UrlCheckerTask().execute(ip);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);

        TextView titleTextView = dialog.findViewById(R.id.titleTextView);
        messageTextView = dialog.findViewById(R.id.messageTextView);

        titleTextView.setText(title);
        messageTextView.setText(title);

        dialog.show();
    }

    private static String extractIPAddress(String input) {
        // Define the pattern for matching IP addresses
        Pattern pattern = Pattern.compile("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b");

        // Create a matcher object
        Matcher matcher = pattern.matcher(input);

        // Find the IP address in the input string
        if (matcher.find()) {
            // Use substring to extract the matched IP address
            return matcher.group();
        } else {
            return null;
        }
    }

    public static class UrlCheckerTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Log.e("UrlCheckerTask", "Error checking URL: " + params);
            String apiUrl = "http://172.92.1.125:5000/get_ip?key=" + params[0];

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    return result.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                Log.e("UrlCheckerTask", "Error checking URL: " + e.getMessage(), e);
                return null;
            }
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                // Handle the result from the Flask API
                Log.d("UrlCheckerTask", "API Response: " + result);

                messageTextView.setText("Risk Percentage: " + result + "");
            } else {
                // Handle the case where there was an error
                Log.e("UrlCheckerTask", "API Request failed");
            }
        }
    }

}
