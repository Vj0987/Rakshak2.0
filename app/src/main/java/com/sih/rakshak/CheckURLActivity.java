package com.sih.rakshak;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CheckURLActivity extends AppCompatActivity {

    TextInputEditText textInputEditText;

    static TextView resultT, meta;

    static MaterialCardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_urlactivity);

        textInputEditText = findViewById(R.id.urlLink);

        resultT = findViewById(R.id.resultText);
        meta = findViewById(R.id.metaText);

        cardView = findViewById(R.id.outPutCard);
    }

    public void submitUrl(View view) {

        if (Objects.requireNonNull(textInputEditText.getText()).toString().isEmpty()) {
            Toast.makeText(this, "Enter URL", Toast.LENGTH_SHORT).show();
        } else {
            checkURL(Objects.requireNonNull(textInputEditText.getText()).toString());
        }

    }

    private void checkURL(String url) {
        new UrlCheckerTask().execute(url);
    }

    public static class UrlCheckerTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String apiUrl = "http://192.168.193.100:5000/sha?url=" + params[0];

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
                cardView.setVisibility(View.VISIBLE);
                Log.d("UrlCheckerTask", "API Response: " + result);
                resultT.setText(result);

                if (result.equals("1C")) {
                    resultT.setText("The link is Malicious");
                    Toast.makeText(meta.getContext(), "Database Updated Successfully", Toast.LENGTH_LONG).show();
                } else {
                    if (result.equals("1")) {
                        resultT.setText("The link is Malicious");
                    } else {
                        resultT.setText("The link is Malicious");
                    }
                }
            } else {
                Log.e("UrlCheckerTask", "API Request failed");
            }
        }
    }

}
