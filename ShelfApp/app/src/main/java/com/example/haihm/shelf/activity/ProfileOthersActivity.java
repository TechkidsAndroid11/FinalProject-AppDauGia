package com.example.haihm.shelf.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.haihm.shelf.R;

public class ProfileOthersActivity extends AppCompatActivity {
    ImageView ivBack,ivCall,ivStar,ivAvatar;
    TextView tvName,tvAddress;
    RatingBar ratingBar;
    TabLayout tabLayout;
    ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_others);
        setupUI();
    }

    private void setupUI() {
        ivBack = findViewById(R.id.iv_back);
        ivStar = findViewById(R.id.iv_star);
        ivCall = findViewById(R.id.iv_call);
        ivAvatar = findViewById(R.id.iv_avatar);
        tvAddress = findViewById(R.id.tv_address);
        tvName = findViewById(R.id.tv_name);
        tabLayout = findViewById(R.id.tab_history);
        viewPager = findViewById(R.id.vp_history);

        tabLayout.addTab(tabLayout.newTab().setText("Rao Vặt"));
        tabLayout.addTab(tabLayout.newTab().setText("Đấu giá"));


    }
}
