package com.example.haihm.shelf.activity;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.example.haihm.shelf.R;
import com.example.haihm.shelf.adapters.ViewPagerAutionDetailsAdapter;
import com.example.haihm.shelf.event.OnClickAuctionEvent;
import com.example.haihm.shelf.model.SanPhamDauGia;
import com.example.haihm.shelf.utils.ImageUtils;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class AuctionDetailsActivity extends AppCompatActivity {
    private static final String TAG = "AuctionDetailsActivity";
    ImageView ivBack,ivMyAvata;
    TextView tvNameAuction;

    TabLayout tabLayout;
    ViewPager viewPager;
    public static SanPhamDauGia sanPhamDauGia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auction_details);
        EventBus.getDefault().register(this);
        setUpUI();
        testFireBase();
//        loadData();
        addController();
    }

    @Subscribe(sticky = true)
    public void OnReceivedClickAuctionEvent(OnClickAuctionEvent onClickAuctionEvent) {
        sanPhamDauGia = onClickAuctionEvent.sanPhamDauGia;
    }

    private void setUpUI() {
        ivBack = findViewById(R.id.iv_back);
        tvNameAuction = findViewById(R.id.tv_name_auction);
        ivMyAvata = findViewById(R.id.iv_my_avatar);
        tabLayout = findViewById(R.id.tab_aution);
        viewPager = findViewById(R.id.vp_aution);
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());

        ViewPagerAutionDetailsAdapter viewPagerAutionDetailsAdapter = new ViewPagerAutionDetailsAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAutionDetailsAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }
    private void loadData() {
        tvNameAuction.setText(sanPhamDauGia.tenSP);
    }

    private void addController() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0){
                    ivMyAvata.setVisibility(View.GONE);
                    tvNameAuction.setText(sanPhamDauGia.tenSP);
                }else {
                    ivMyAvata.setVisibility(View.VISIBLE);
                    tvNameAuction.setText("User Name");
                }
                viewPager.setCurrentItem(tab.getPosition());
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
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount()!=0){
            super.onBackPressed();
        }else {
            moveTaskToBack(true);
        }
    }
    private void testFireBase() {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Auction").child("Đồ gia dụng");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot spSnapSort : dataSnapshot.getChildren()) {
                    sanPhamDauGia = spSnapSort.getValue(SanPhamDauGia.class);
                    Log.d(TAG, "onDataChange: " + sanPhamDauGia.toString());
                }
                Date tmp = new Date();
                tmp.setTime(System.currentTimeMillis() + 3 * 3600 * 1000);
                sanPhamDauGia.tgianKthuc = tmp;

                loadData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
