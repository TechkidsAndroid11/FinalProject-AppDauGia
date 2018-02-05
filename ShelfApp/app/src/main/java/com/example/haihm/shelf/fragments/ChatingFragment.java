package com.example.haihm.shelf.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.haihm.shelf.R;
import com.example.haihm.shelf.activity.AuctionDetailsActivity;
import com.example.haihm.shelf.adapters.ChatingAdapter;
import com.example.haihm.shelf.event.OnClickUserModelEvent;
import com.example.haihm.shelf.model.SanPhamDauGia;
import com.example.haihm.shelf.model.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatingFragment extends Fragment {
    private static final String TAG = "ChatingFragment";
    RecyclerView rvChat;
    EditText etChat;
    ImageView ivSend;
    SanPhamDauGia sanPhamDauGia;
    UserModel userModel;
    ChatingAdapter chatingAdapter;

    public ChatingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chating, container, false);
        EventBus.getDefault().register(this);
        setUpUI(view);
//        testFireBase();
        addController();
        return view;
    }

    @Subscribe(sticky = true)
    public void ReceivedUserModel(OnClickUserModelEvent onClickUserModelEvent) {
        userModel = onClickUserModelEvent.userModel;
        Log.d(TAG, "ReceivedUserModel: " + userModel.sdt);
    }

    private void setUpUI(View view) {
        sanPhamDauGia = AuctionDetailsActivity.sanPhamDauGia;
        rvChat = view.findViewById(R.id.rv_chat);
        ivSend = view.findViewById(R.id.iv_send);
        etChat = view.findViewById(R.id.et_chat);
        Log.d(TAG, "setUpUI: " + sanPhamDauGia.idSP);
    }

    private void addController() {
        chatingAdapter = new ChatingAdapter(sanPhamDauGia.lchat, getContext());
        rvChat.setAdapter(chatingAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        rvChat.setLayoutManager(layoutManager);


        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                userModel = new UserModel();
//                userModel.hoten = "Nguyễn văn A";
//                userModel.anhAvatar = "https://tophinhanhdep.net/wp-content/uploads/2017/10/avatar-cap-doi-7.jpg";
                final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = firebaseDatabase.getReference("Auction").child(sanPhamDauGia.loaiSP)
                        .child(sanPhamDauGia.idSP).child("lchat");
                final SanPhamDauGia.Chat chat = new SanPhamDauGia.Chat();
                chat.nameMess = userModel.hoten;
                chat.avatarMess = userModel.anhAvatar;
                chat.contentMess = etChat.getText().toString();
                etChat.setText("");
                sanPhamDauGia.lchat.add(chat);
                databaseReference.setValue(sanPhamDauGia.lchat);
                //cap nhat phien đang tham gia
                final ArrayList<String> listJoinAuction = new ArrayList<>();
                DatabaseReference databaseReference1 = firebaseDatabase.getReference("UserInfo").child(userModel.id)
                        .child("listJoinAuction");
                databaseReference1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot idAuction : dataSnapshot.getChildren()){
                            String id = idAuction.getValue(String.class);
                            Log.d(TAG, "onDataChange_join: "+id);
                            listJoinAuction.add(id);
                        }
                        if(!checkExist(listJoinAuction,sanPhamDauGia.idSP)){
                            listJoinAuction.add(sanPhamDauGia.idSP);
                            firebaseDatabase.getReference("UserInfo").child(userModel.id)
                                    .child("listJoinAuction").setValue(listJoinAuction);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        changeChating();
    }
    private boolean checkExist(ArrayList<String> listJoinAuction, String id) {
        for(int i = 0; i < listJoinAuction.size(); i++){
            if(listJoinAuction.get(i).equals(id)){
                return true;
            }
        }
        return false;
    }
    public void changeChating() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Auction").child(sanPhamDauGia.loaiSP)
                .child(sanPhamDauGia.idSP).child("lchat");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: ");
                ArrayList<SanPhamDauGia.Chat> chats = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SanPhamDauGia.Chat chat = snapshot.getValue(SanPhamDauGia.Chat.class);
                    chats.add(chat);
                }
                sanPhamDauGia.lchat.clear();
                sanPhamDauGia.lchat.addAll(chats);
                chatingAdapter.notifyDataSetChanged();
                rvChat.smoothScrollToPosition(sanPhamDauGia.lchat.size() - 1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void testFireBase() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Auction").child("Đồ gia dụng");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot spSnapSort : dataSnapshot.getChildren()) {
                    sanPhamDauGia = spSnapSort.getValue(SanPhamDauGia.class);
                    sanPhamDauGia.idSP = spSnapSort.getKey();
                    Log.d(TAG, "onDataChange: " + sanPhamDauGia.toString());
                }
                Date tmp = new Date();
                tmp.setTime(System.currentTimeMillis() + 3 * 3600 * 1000);
                sanPhamDauGia.tgianKthuc = tmp;

                addController();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
