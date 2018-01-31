package com.example.haihm.shelf.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.haihm.shelf.R;
import com.example.haihm.shelf.adapters.ViewPagerAutionDetailsAdapter;
import com.example.haihm.shelf.event.OnClickAuctionEvent;
import com.example.haihm.shelf.model.SanPhamDauGia;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;

public class AuctionDetailsActivity extends AppCompatActivity {
    private static final String TAG = "AuctionDetailsActivity";
    ImageView ivBack;
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
//        testFireBase();
        loadData();
        addController();
    }

    @Subscribe(sticky = true)
    public void OnReceivedClickAuctionEvent(OnClickAuctionEvent onClickAuctionEvent) {
        sanPhamDauGia = onClickAuctionEvent.sanPhamDauGia;
        Log.d(TAG, "OnReceivedClickAuctionEvent: ");
    }

    private void setUpUI() {
        ivBack = findViewById(R.id.iv_back);
        tvNameAuction = findViewById(R.id.tv_name_auction);
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
                finish();
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
//                if(tab.getPosition() == 0){
//                    ivMyAvata.setVisibility(View.GONE);
//                    tvNameAuction.setText(sanPhamDauGia.tenSP);
//                }else {
//                    ivMyAvata.setVisibility(View.VISIBLE);
//                    tvNameAuction.setText("User Name");
//                }
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
        finish();
    }

    private void testFireBase() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Auction").child("Đồ gia dụng");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot spSnapSort : dataSnapshot.getChildren()) {
                    sanPhamDauGia = spSnapSort.getValue(SanPhamDauGia.class);
                    Log.d(TAG, " : " + sanPhamDauGia.toString());
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
