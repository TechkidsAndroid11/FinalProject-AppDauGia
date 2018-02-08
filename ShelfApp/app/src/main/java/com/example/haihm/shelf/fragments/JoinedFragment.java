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
import com.example.haihm.shelf.event.OnClickUserModelEvent;
import com.example.haihm.shelf.model.SanPhamDauGia;
import com.example.haihm.shelf.model.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class JoinedFragment extends Fragment {
    private static final String TAG = "JoinedFragment";
    RecyclerView recyclerView;
    AuctionProductAdapter adapter;
    List<SanPhamDauGia> sanPhamDauGiaList = new ArrayList<>();
    UserModel userModel = new UserModel();
    String arr[];
    public JoinedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_joined, container, false);
        EventBus.getDefault().register(this);
        arr= getContext().getResources().getStringArray(R.array.loai_sp);
        recyclerView = view.findViewById(R.id.rv_joined);
        adapter = new AuctionProductAdapter(sanPhamDauGiaList,getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        loadListAution();
        return view;
    }

    @Subscribe(sticky = true)
    public void ReceivedUserModel(OnClickUserModelEvent onClickUserModelEvent) {
        userModel = onClickUserModelEvent.userModel;
        Log.d(TAG, "ReceivedUserModel: " + userModel.hoten+userModel.listJoinAuction.size());
    }

    private void loadListAution() {
        FirebaseDatabase firebaseData = FirebaseDatabase.getInstance();
        firebaseData.getReference("UserInfo").child(userModel.id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userModel = dataSnapshot.getValue(UserModel.class);
                //
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                firebaseDatabase.getReference("Auction").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        sanPhamDauGiaList.clear();
                        for(int i = 0; i < arr.length; i++){
                            for (int j = 0; j < userModel.listJoinAuction.size(); j++) {
                                SanPhamDauGia sanPhamDauGia = dataSnapshot.child(arr[i]).child(userModel.listJoinAuction.get(j))
                                        .getValue(SanPhamDauGia.class);
                                if(sanPhamDauGia!=null){
                                    sanPhamDauGia.idSP = userModel.listJoinAuction.get(j);
                                    sanPhamDauGiaList.add(sanPhamDauGia);
                                }
                            }
                        }
                        Log.d(TAG, "onDataChange_joined: "+sanPhamDauGiaList.size());
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
