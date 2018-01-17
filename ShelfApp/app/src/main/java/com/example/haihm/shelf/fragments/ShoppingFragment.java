package com.example.haihm.shelf.fragments;


import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.haihm.shelf.R;
import com.example.haihm.shelf.adapters.MainPagerAdapter;
import com.example.haihm.shelf.adapters.ProductTypeAdapter;
import com.example.haihm.shelf.model.SanPhamRaoVat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShoppingFragment extends Fragment {
    private static final String TAG = ShoppingFragment.class.toString();
    List<SanPhamRaoVat> sanPhamRaoVatList, productTypeList;
    RecyclerView rvItemTypeList;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ConstraintLayout clAuction;
    SmartTabLayout stlProductType;
    ViewPager vpProductList;
    boolean isAuction;

    public ShoppingFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shopping, container, false);
        setupUI(view);

        return view;
    }

    private void setupUI(final View view) {
        rvItemTypeList = view.findViewById(R.id.rv_list_product);
        clAuction = view.findViewById(R.id.cl_auction);
        stlProductType = view.findViewById(R.id.stl_product_type);
        vpProductList = view.findViewById(R.id.vp_product_list);

        //is auction or not
        Bundle bundle = this.getArguments();
        if (bundle != null){
            isAuction = bundle.getBoolean(MainPagerAdapter.IS_AUCTION);
        }

        //load database
        sanPhamRaoVatList = new ArrayList<>();
        productTypeList = new ArrayList<>();

        firebaseDatabase = FirebaseDatabase.getInstance();
        if (!isAuction) {
            databaseReference = firebaseDatabase.getReference("Product");
        } else {
            databaseReference = firebaseDatabase.getReference("Auction");
        }

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        })

//        loadData(view);
        setupProductTypeTab();
    }

    private void setupProductTypeTab() {
        stlProductType.setViewPager(vpProductList);
    }

//    private void loadData(final View view) {
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                //load data from firebase
//                for (DataSnapshot spRaoVatSnapShot : dataSnapshot.getChildren()){
//                    SanPhamRaoVat sanPhamRaoVat = spRaoVatSnapShot.getValue(SanPhamRaoVat.class);
//                    sanPhamRaoVatList.add(sanPhamRaoVat);
//                }
//
//                //setup recycler view
//                ProductTypeAdapter productTypeAdapter = new ProductTypeAdapter(sanPhamRaoVatList);
//                rvItemTypeList.setAdapter(productTypeAdapter);
//                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
//                linearLayoutManager.canScrollHorizontally();
//                rvItemTypeList.setLayoutManager(linearLayoutManager);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
//            }
//        });
//    }

}
