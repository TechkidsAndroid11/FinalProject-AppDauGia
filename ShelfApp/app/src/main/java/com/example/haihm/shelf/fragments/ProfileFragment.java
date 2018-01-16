package com.example.haihm.shelf.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.haihm.shelf.R;
import com.example.haihm.shelf.activity.DangSpDGActivity;
import com.example.haihm.shelf.activity.DangSpRvActivity;
import com.example.haihm.shelf.event.OnClickUserModelEvent;
import com.example.haihm.shelf.model.UserModel;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment{
    private static final String TAG = "ProfileFragment";
    View view;
    Button btnAuction, btnClassified;
    ImageView ivCover,ivAvatar;
    TextView tvName;
    UserModel userModel;
    public ProfileFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        setupUI();
        EventBus.getDefault().register(this);
        btnAuction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentPostAution();
            }
        });

        btnClassified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentPostClassified();
            }
        });
        return view;
    }
    @Subscribe(sticky = true)
    public void loadData(OnClickUserModelEvent onClickUserModelEvent)
    {
        userModel=onClickUserModelEvent.userModel;
        Log.d(TAG, "loadData: "+userModel.getHoten()+" "+userModel.getAnhAvatar());
        Picasso.with(getActivity()).load(userModel.getAnhAvatar()).into(ivAvatar);
//        Picasso.with(getActivity()).load(userModel.getAnhCover()).into(ivCover);
        tvName.setText(userModel.getHoten());
    }
    private void setupUI() {
        btnAuction = view.findViewById(R.id.btBuy);
        btnClassified = view.findViewById(R.id.btDauGia);
        ivCover = view.findViewById(R.id.iv_cover);
        ivAvatar = view.findViewById(R.id.iv_avatar);
        tvName = view.findViewById(R.id.tv_name);
    }


    private void intentPostClassified() {

        Intent intent = new Intent(getActivity(), DangSpDGActivity.class);
        startActivity(intent);
    }

    private void intentPostAution() {
        Intent intent = new Intent(getActivity(), DangSpRvActivity.class);
        startActivity(intent);
    }
}
