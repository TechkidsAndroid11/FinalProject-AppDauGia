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
public class ShoppingProductFragment extends Fragment {
    private static final String TAG = ShoppingProductFragment.class.toString();
    RecyclerView rvProducts;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    List<SanPhamRaoVat> sanPhamRaoVatList;

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

        //load database
        setupDatabase();
        loadData(view);
    }

    private void setupDatabase() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String productType = bundle.getString(ShoppingFragment.PRODUCT_TYPE);
            Log.d(TAG, "onCreateView: " + productType);
            sanPhamRaoVatList = new ArrayList<>();
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference("Product").child(productType);
        }


    }

    private void loadData(final View view) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //load data from firebase
                for (DataSnapshot spRaoVatSnapShot : dataSnapshot.getChildren()){
                    SanPhamRaoVat sanPhamRaoVat = spRaoVatSnapShot.getValue(SanPhamRaoVat.class);
                    sanPhamRaoVatList.add(sanPhamRaoVat);
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
        ShoppingProductAdapter shoppingProductAdapter = new ShoppingProductAdapter(sanPhamRaoVatList, getContext());
        rvProducts.setAdapter(shoppingProductAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(), 2);

        rvProducts.setLayoutManager(gridLayoutManager);
    }


}
