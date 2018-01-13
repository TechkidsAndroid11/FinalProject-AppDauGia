package com.example.haihm.shelf.activity;

import android.app.Fragment;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import com.example.haihm.shelf.adapters.LoginPagerAdapter;
import com.example.haihm.shelf.R;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    TabLayout tlLogin;
    ViewPager vpLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_login);
        setupUI();
    }

    private void setupUI() {
        tlLogin = findViewById(R.id.tl_login);
        vpLogin = findViewById(R.id.vp_login);
        setupTabLayout();
        LoginPagerAdapter loginPagerAdapter = new LoginPagerAdapter(getSupportFragmentManager(), this);
        vpLogin.setAdapter(loginPagerAdapter);
        vpLogin.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tlLogin));
    }

    private void setupTabLayout() {
        tlLogin.addTab(tlLogin.newTab().setText("Đăng nhập"));
        tlLogin.addTab(tlLogin.newTab().setText("Đăng ký"));
        tlLogin.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vpLogin.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");
        LoginPagerAdapter.loginFragment.onActivityResult(requestCode, resultCode, data);

    }
}
