package com.example.haihm.shelf.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.haihm.shelf.R;
import com.example.haihm.shelf.adapters.AuctionProductAdapter;
import com.example.haihm.shelf.model.SanPhamDauGia;
import com.example.haihm.shelf.utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;


/**
 * A simple {@link Fragment} subclass.
 */
public class AuctionProductFragment extends Fragment {
    private static final String TAG = AuctionProductFragment.class.toString();
    RecyclerView rvProducts;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    List<SanPhamDauGia> sanPhamDauGiaList;
    AVLoadingIndicatorView avAuctionLoading;
    private AuctionProductAdapter auctionProductAdapter;

    public AuctionProductFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_auction_product, container, false);
        setupUI(view);
        return view;
    }

    private void setupUI(View view) {
        rvProducts = view.findViewById(R.id.rv_list_auction_product);
        avAuctionLoading = view.findViewById(R.id.av_auction_loading);

        //load database
        avAuctionLoading.show();
        setupDatabase();
        setupRecyclerView(view);
        loadData();
    }

    private void setupDatabase() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String productType = bundle.getString(Utils.PRODUCT_TYPE);
            Log.d(TAG, "onCreateView: " + productType);
            sanPhamDauGiaList = new ArrayList<>();
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference("Auction").child(productType);
        }
    }

    private void loadData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sanPhamDauGiaList.clear();
                //load data from firebase
                for (DataSnapshot spDauGiaSnapShot : dataSnapshot.getChildren()){

                    SanPhamDauGia sanPhamDauGia = spDauGiaSnapShot.getValue(SanPhamDauGia.class);
                    sanPhamDauGia.idSP = spDauGiaSnapShot.getKey();
                    sanPhamDauGiaList.add(sanPhamDauGia);
                    auctionProductAdapter.notifyItemChanged(sanPhamDauGiaList.indexOf(sanPhamDauGia));
//                    auctionProductAdapter.notifyDataSetChanged();
                }
                avAuctionLoading.hide();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });
    }

    private void setupRecyclerView(View view) {
        //setup recycler view
        auctionProductAdapter = new AuctionProductAdapter(sanPhamDauGiaList,getContext());
        rvProducts.setAdapter(auctionProductAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        rvProducts.setLayoutManager(linearLayoutManager);
        rvProducts.setItemAnimator(new SlideInLeftAnimator());
        rvProducts.getItemAnimator().setAddDuration(300);

    }

}
