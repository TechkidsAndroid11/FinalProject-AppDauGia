package com.example.haihm.shelf.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.haihm.shelf.R;
import com.example.haihm.shelf.adapters.MainPagerAdapter;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

public class MainActivity extends AppCompatActivity {
    TabLayout tlBottomBar;
    ViewPager vpMain;
    Toolbar tbAppBar;
    MaterialSearchView svSearchView;
    RelativeLayout rlAppBar;
    boolean isSearchable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI();
        addListeners();
    }

    private void addListeners() {
        tlBottomBar.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setupToolbar(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        svSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), query, true);
                vpMain.setAdapter(mainPagerAdapter);
                vpMain.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tlBottomBar));
                isSearchable = true;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void setupToolbar(int position) {
        vpMain.setCurrentItem(position);
        if (position == 2) {
            tbAppBar.setVisibility(View.GONE);
            rlAppBar.setVisibility(View.GONE);
        } else {
            tbAppBar.setVisibility(View.VISIBLE);
            rlAppBar.setVisibility(View.VISIBLE);
        }

        if (position == 0){
            tbAppBar.setTitle("Rao vặt");
        } else {
            tbAppBar.setTitle("Đấu giá");
        }

    }


    @SuppressLint("WrongViewCast")
    private void setupUI() {
        tlBottomBar = findViewById(R.id.tl_bottom_bar);
        rlAppBar = findViewById(R.id.rl_app_bar);
        vpMain = findViewById(R.id.vp_main_activity);
        tbAppBar = findViewById(R.id.tb_app_bar);
        svSearchView = findViewById(R.id.sv_search_view);

        tbAppBar.setTitle("Rao vặt");
        if (isSearchable){
            tbAppBar.setVisibility(View.GONE);
        } else tbAppBar.setVisibility(View.VISIBLE);
        setSupportActionBar(tbAppBar);
        setupBottomTabLayout();

        MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), false);
        vpMain.setAdapter(mainPagerAdapter);
        vpMain.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tlBottomBar));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        svSearchView.setMenuItem(searchItem);
        return true;
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_search:
                svSearchView.setVisibility(View.VISIBLE);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (svSearchView.isSearchOpen()){
            svSearchView.closeSearch();
            svSearchView.setVisibility(View.GONE);
            isSearchable = false;
        } else
        super.onBackPressed();
    }

    //TODO: tìm kiếm

}
