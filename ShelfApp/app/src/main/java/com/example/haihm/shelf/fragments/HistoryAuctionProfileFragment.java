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
import com.example.haihm.shelf.activity.ProfileOthersActivity;
import com.example.haihm.shelf.adapters.HistoryAuctionAdapter;
import com.example.haihm.shelf.event.OnClickUserModelEvent;
import com.example.haihm.shelf.model.SanPhamDauGia;
import com.example.haihm.shelf.model.UserModel;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryAuctionProfileFragment extends Fragment {
    RecyclerView rvHistoryAuction;
    HistoryAuctionAdapter adapter;
    ArrayList<SanPhamDauGia> listAuction = new ArrayList<>();
    SpinKitView spinKitView;
    View view;
    UserModel userModel;

    public HistoryAuctionProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_history_auction_profile, container, false);
        setupUI();
        EventBus.getDefault().register(this);
        loadAuction();
        return view;
    }

    private void setupUI() {
        rvHistoryAuction = view.findViewById(R.id.rv_history_auction);
        spinKitView = view.findViewById(R.id.sk_load);

        rvHistoryAuction.setVisibility(View.INVISIBLE);
        spinKitView.setVisibility(View.VISIBLE);

        adapter = new HistoryAuctionAdapter(listAuction,getContext());
        rvHistoryAuction.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2);
        rvHistoryAuction.setLayoutManager(gridLayoutManager);
    }
    @Subscribe(sticky = true)
    public void loadData(OnClickUserModelEvent onClickUserModelEvent) {

        userModel = onClickUserModelEvent.userModel;
    }
    private void loadAuction() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final String arr[] = getResources().getStringArray(R.array.loai_sp);
        firebaseDatabase.getReference("Auction").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<SanPhamDauGia> tmp = new ArrayList<>();
                for(int i = 0; i < arr.length; i++){
                    for (int j = 0; j < userModel.listAuction.size(); j++) {
                        SanPhamDauGia sanPhamDauGia = dataSnapshot.child(arr[i]).child(userModel.listAuction.get(j))
                                .getValue(SanPhamDauGia.class);
                        if(sanPhamDauGia!=null){
                            sanPhamDauGia.idSP = userModel.listAuction.get(j);
                            tmp.add(sanPhamDauGia);
                        }
                    }
                }
                listAuction.clear();
                listAuction.addAll(tmp);
                rvHistoryAuction.setVisibility(View.VISIBLE);
                spinKitView.setVisibility(View.INVISIBLE);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
