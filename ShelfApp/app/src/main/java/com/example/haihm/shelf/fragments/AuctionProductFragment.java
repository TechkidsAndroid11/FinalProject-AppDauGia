package com.example.haihm.shelf.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.haihm.shelf.R;
import com.example.haihm.shelf.adapters.AuctionProductAdapter;
import com.example.haihm.shelf.model.SanPhamDauGia;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class AuctionProductFragment extends Fragment {
    private static final String TAG = AuctionProductFragment.class.toString();
    RecyclerView rvProducts;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    List<SanPhamDauGia> sanPhamDauGiaList;
    String productType;

    public AuctionProductFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_auction_product, container, false);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            productType = bundle.getString(ShoppingFragment.PRODUCT_TYPE);
        }
        Log.d(TAG, "onCreateView: " + productType);
        setupUI(view);
        return view;
    }

    private void setupUI(View view) {
        rvProducts = view.findViewById(R.id.rv_list_auction_product);
        //load database
        setupDatabase();
        loadData(view);

    }

    private void setupDatabase() {
        sanPhamDauGiaList = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Auction").child(productType);
    }

    private void loadData(final View view) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //load data from firebase
                for (DataSnapshot spDauGiaSnapShot : dataSnapshot.getChildren()){
                    SanPhamDauGia sanPhamDauGia = spDauGiaSnapShot.getValue(SanPhamDauGia.class);
                    sanPhamDauGiaList.add(sanPhamDauGia);
                }
                setupRecyclerView(view);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });
    }

    private void setupRecyclerView(View view) {
        //setup recycler view
        AuctionProductAdapter auctionProductAdapter = new AuctionProductAdapter(sanPhamDauGiaList);
        rvProducts.setAdapter(auctionProductAdapter);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
//        linearLayoutManager.canScrollHorizontally();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(), 2);
//        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                return position;
//            }
//        });
        rvProducts.setLayoutManager(gridLayoutManager);
    }

}
