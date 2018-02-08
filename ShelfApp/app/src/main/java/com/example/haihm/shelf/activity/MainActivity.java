package com.example.haihm.shelf.activity;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.TextView;

import com.example.haihm.shelf.R;
import com.example.haihm.shelf.adapters.MainPagerAdapter;

public class MainActivity extends AppCompatActivity {
    SearchView searchView;
    TabLayout tlBottomBar;
    ViewPager vpMain;
    AppBarLayout clAppBar;
    TextView tvTabName;

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
        tvTabName = findViewById(R.id.tv_tab_layout_name);
        tvTabName.setText("Rao vặt");
        setupBottomTabLayout();

        MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        vpMain.setAdapter(mainPagerAdapter);
        vpMain.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tlBottomBar));
    }


    private void setupBottomTabLayout() {
        //add bottom bar tab
        tlBottomBar.addTab(tlBottomBar.newTab().setIcon(R.drawable.ic_shopping_cart_black_24dp));
        tlBottomBar.addTab(tlBottomBar.newTab().setIcon(R.drawable.ic_attach_money_black_24dp));
        tlBottomBar.addTab(tlBottomBar.newTab().setIcon(R.drawable.ic_person_black_24dp));
        tlBottomBar.setBackgroundColor(getResources().getColor(R.color.mainColor));

        tlBottomBar.getTabAt(0).setText("Rao vặt");
        tlBottomBar.getTabAt(1).setText("Đấu giá");
        tlBottomBar.getTabAt(2).setText("Tôi");
        tlBottomBar.setTabTextColors(Color.WHITE,Color.WHITE);
        tlBottomBar.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                vpMain.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0 || tab.getPosition() == 1) {
                    clAppBar.setVisibility(View.VISIBLE);
                } else {
                    clAppBar.setVisibility(View.GONE);
                }

                if (tab.getPosition() == 0){
                    tvTabName.setText("Rao vặt");
                } else {
                    tvTabName.setText("Đấu giá");
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

    //TODO: tìm kiếm

}
