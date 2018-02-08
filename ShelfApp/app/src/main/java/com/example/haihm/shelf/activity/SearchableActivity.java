package com.example.haihm.shelf.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.haihm.shelf.R;
import com.example.haihm.shelf.fragments.AuctionProductFragment;
import com.example.haihm.shelf.fragments.ShoppingProductFragment;
import com.example.haihm.shelf.model.SanPhamRaoVat;
import com.example.haihm.shelf.utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentStatePagerItemAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchableActivity extends AppCompatActivity {
    SmartTabLayout stlSearchableTab;
    ViewPager vpSearchable;
    List<SanPhamRaoVat> sanPhamRaoVatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        sanPhamRaoVatList = new ArrayList<>();
        stlSearchableTab = findViewById(R.id.stl_searchable_tab);
        vpSearchable = findViewById(R.id.vp_searchable_list);

        Intent intent = getIntent();
        String searchQuery = intent.getStringExtra(Intent.ACTION_SEARCH);
        setupSearchableTab(searchQuery);


    }

    private void setupSearchableTab(String searchQuery) {
        FragmentPagerItems fragmentPagerItems = new FragmentPagerItems(getApplicationContext());
        Bundle bundle = new Bundle();
        bundle.putBoolean(Utils.IS_SEARCHABLE, true);
        bundle.putString(Utils.SEARCH_QUERY, searchQuery);
        fragmentPagerItems.add(FragmentPagerItem.of("Rao vặt", ShoppingProductFragment.class, bundle));
        fragmentPagerItems.add(FragmentPagerItem.of("Đấu giá", AuctionProductFragment.class, bundle));

        FragmentStatePagerItemAdapter fragmentStatePagerItemAdapter = new FragmentStatePagerItemAdapter(getSupportFragmentManager(), fragmentPagerItems);
        vpSearchable.setAdapter(fragmentStatePagerItemAdapter);
        stlSearchableTab.setViewPager(vpSearchable);
    }


    private void doMySearch(String searchQuery) {
        sanPhamRaoVatList.clear();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.equalTo(searchQuery);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot spRaoVatSnapShot : dataSnapshot.getChildren()) {
                    SanPhamRaoVat sanPhamRaoVat = spRaoVatSnapShot.getValue(SanPhamRaoVat.class);
                    sanPhamRaoVatList.add(sanPhamRaoVat);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
