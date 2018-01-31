package com.example.haihm.shelf.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.View;

import com.example.haihm.shelf.R;
import com.example.haihm.shelf.adapters.MainPagerAdapter;

public class MainActivity extends AppCompatActivity {
    SearchView searchView;
    TabLayout tlBottomBar;
    ViewPager vpMain;
    ConstraintLayout clAuction;
    AppBarLayout clAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI();
    }

    @SuppressLint("WrongViewCast")
    private void setupUI() {
        tlBottomBar = findViewById(R.id.tl_bottom_bar);
        clAppBar = findViewById(R.id.app_bar_layout);
        vpMain = findViewById(R.id.vp_main_activity);
        clAuction = findViewById(R.id.cl_auction);

        setupBottomTabLayout();

        MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        vpMain.setAdapter(mainPagerAdapter);
        vpMain.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tlBottomBar));
    }


    private void setupBottomTabLayout() {
        tlBottomBar.addTab(tlBottomBar.newTab().setIcon(R.drawable.ic_shopping_cart_black_24dp));
        tlBottomBar.addTab(tlBottomBar.newTab().setIcon(R.drawable.ic_attach_money_black_24dp));
        tlBottomBar.addTab(tlBottomBar.newTab().setIcon(R.drawable.ic_person_black_24dp));
        tlBottomBar.setBackgroundColor(getResources().getColor(R.color.mainColor));

        tlBottomBar.getTabAt(0).setText("Rao vặt");
        tlBottomBar.getTabAt(1).setText("Đấu giá");
        tlBottomBar.getTabAt(2).setText("Tôi");
        tlBottomBar.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vpMain.setCurrentItem(tab.getPosition());

                if (tab.getPosition() == 1) {
                    clAuction.setVisibility(View.VISIBLE);
                } else {
                    clAuction.setVisibility(View.GONE);
                }

                if (tab.getPosition() == 0 || tab.getPosition() == 1) {
                    clAppBar.setVisibility(View.VISIBLE);
                } else {
                    clAppBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    //TODO: tab layout đồ đấu giá / đang tham gia
    //TODO: lịch sử
    //TODO: rating bar
    //TODO: tìm kiếm
    //TODO: ko lưu sđt verify
    //TODO: join đấu giá - sửa lại UserModel
}
