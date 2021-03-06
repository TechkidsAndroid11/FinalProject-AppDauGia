package com.example.haihm.shelf.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.haihm.shelf.R;
import com.example.haihm.shelf.adapters.ShoppingProductAdapter;
import com.example.haihm.shelf.model.SanPhamRaoVat;
import com.example.haihm.shelf.utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShoppingProductFragment extends Fragment {
    private static final String TAG = ShoppingProductFragment.class.toString();
    RecyclerView rvProducts;
    DatabaseReference databaseReference;
    List<SanPhamRaoVat> sanPhamRaoVatList;
    AVLoadingIndicatorView avShoppingProduct;
    private ShoppingProductAdapter shoppingProductAdapter;
    Bundle bundle;

    public ShoppingProductFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shopping_product, container, false);
        setupUI(view);
        return view;
    }

    private void setupUI(View view) {
        rvProducts = view.findViewById(R.id.rv_list_shopping_product);
        avShoppingProduct = view.findViewById(R.id.av_product_loading);
        avShoppingProduct.show();

        //if searchable
        bundle = this.getArguments();
        setupDatabase();
        boolean isSearchable = bundle.getBoolean(Utils.IS_SEARCHABLE);
        Log.d(TAG, "setupUI: " + isSearchable);
        if (isSearchable) {
            searchData();
        } else {
            loadData();
        }
        setupRecyclerView(view);
    }

    private void setupDatabase() {
        sanPhamRaoVatList = new ArrayList<>();
        String productType = bundle.getString(Utils.PRODUCT_TYPE);
        Log.d(TAG, "onCreateView: " + productType);
        databaseReference = FirebaseDatabase.getInstance().getReference("Product").child(productType);
    }

    private void searchData() {
        String query = bundle.getString(Utils.SEARCH_QUERY);
        Log.d(TAG, "searchData: " + query);
        Query searchQuery = databaseReference.orderByChild("tenSP").equalTo(query);
        searchQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sanPhamRaoVatList.clear();
                for (DataSnapshot spRaoVatSnapShot : dataSnapshot.getChildren()) {
                    SanPhamRaoVat sanPhamRaoVat = spRaoVatSnapShot.getValue(SanPhamRaoVat.class);
                    sanPhamRaoVatList.add(sanPhamRaoVat);
                    shoppingProductAdapter.notifyItemChanged(sanPhamRaoVatList.indexOf(sanPhamRaoVat));
                    Log.d(TAG, "onDataChange: " + sanPhamRaoVatList.lastIndexOf(sanPhamRaoVat));
                }
                avShoppingProduct.hide();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });
    }

    private void loadData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sanPhamRaoVatList.clear();
                //load data from firebase
                for (DataSnapshot spRaoVatSnapShot : dataSnapshot.getChildren()) {
                    SanPhamRaoVat sanPhamRaoVat = spRaoVatSnapShot.getValue(SanPhamRaoVat.class);
                    sanPhamRaoVatList.add(sanPhamRaoVat);
                    shoppingProductAdapter.notifyItemChanged(sanPhamRaoVatList.indexOf(sanPhamRaoVat));
                    Log.d(TAG, "onDataChange: " + sanPhamRaoVatList.lastIndexOf(sanPhamRaoVat));
                }
                avShoppingProduct.hide();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });
    }

    private void setupRecyclerView(View view) {
        //setup recycler view
        shoppingProductAdapter = new ShoppingProductAdapter(sanPhamRaoVatList, getContext());
        rvProducts.setAdapter(shoppingProductAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(), 2);

        rvProducts.setLayoutManager(gridLayoutManager);
        rvProducts.setItemAnimator(new SlideInLeftAnimator());
        rvProducts.getItemAnimator().setAddDuration(300);
    }


}
