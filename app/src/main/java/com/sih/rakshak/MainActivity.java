package com.sih.rakshak;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sih.rakshak.Fragments.Account;
import com.sih.rakshak.Fragments.Dashboard;
import com.sih.rakshak.Fragments.Notification;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setNavColor();

        loadFragment(new Dashboard());

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.dashboard) {
                loadFragment(new Dashboard());
                return true;
            } else if (item.getItemId() == R.id.notification) {
                loadFragment(new Notification());
                return true;
            } else if (item.getItemId() == R.id.account) {
                loadFragment(new Account());
                return true;
            } else {
                return false;
            }
        });

    }

    private void setNavColor() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setNavigationBarColor(getResources().getColor(R.color.main, getTheme()));
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof Dashboard) {
            super.onBackPressed();
        } else {
            bottomNavigationView.setSelectedItemId(R.id.dashboard);
        }
    }
}