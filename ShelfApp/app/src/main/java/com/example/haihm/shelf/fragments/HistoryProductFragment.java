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
import com.example.haihm.shelf.activity.ProfileOthersActivity;
import com.example.haihm.shelf.adapters.HistoryProductAdapter;
import com.example.haihm.shelf.model.SanPhamRaoVat;
import com.example.haihm.shelf.model.UserModel;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryProductFragment extends Fragment {
    private static final String TAG = "HistoryProductFragment";
    RecyclerView rvHistoryProduct;
    HistoryProductAdapter adapter;
    ArrayList<SanPhamRaoVat> listProduct = new ArrayList<>();
    SpinKitView spinKitView;
    View view;
    UserModel userModel;

    public HistoryProductFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_history_product, container, false);
        setupUI();
        loadProduct();
        return view;
    }

    private void loadProduct() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final String arr[] = getResources().getStringArray(R.array.loai_sp);
        firebaseDatabase.getReference("Product").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listProduct.clear();
                for(int i = 0; i < arr.length; i++){
                    for (int j = 0; j < userModel.listProduct.size(); j++) {
                        SanPhamRaoVat sanPhamRaoVat = dataSnapshot.child(arr[i]).child(userModel.listProduct.get(j))
                                .getValue(SanPhamRaoVat.class);
                        if(sanPhamRaoVat!=null){
                            sanPhamRaoVat.idSP = userModel.listProduct.get(j);
                            listProduct.add(sanPhamRaoVat);
                            Log.d(TAG, "onDataChange_history: "+i+" "+userModel.hoten);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                rvHistoryProduct.setVisibility(View.VISIBLE);
                spinKitView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
}

    private void setupUI() {
        userModel = ProfileOthersActivity.userModel;
        rvHistoryProduct = view.findViewById(R.id.rv_history_product);
        spinKitView = view.findViewById(R.id.sk_load);

        rvHistoryProduct.setVisibility(View.INVISIBLE);
        spinKitView.setVisibility(View.VISIBLE);
        listProduct.clear();
        adapter = new HistoryProductAdapter(listProduct,getContext());
        rvHistoryProduct.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),3);
        rvHistoryProduct.setLayoutManager(gridLayoutManager);
    }


}
