package com.example.haihm.shelf.fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Base64;
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

import com.example.haihm.shelf.event.OnClickAddSanPhamEvent;
import com.example.haihm.shelf.event.OnClickUserModelEvent;
import com.example.haihm.shelf.model.UserModel;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    CropCircleTransformation cropCircleTransformation = new CropCircleTransformation();
    View view;
    Button btnAuction, btnClassified;
    ImageView ivCover, ivAvatar;
    TextView tvName;
    UserModel userModel;
    String base64;
     TabLayout tabLayout;
    ViewPager viewPager;

    public ProfileFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        setupUI();
        loadDataForTabLayout();
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
    public void loadData(OnClickUserModelEvent onClickUserModelEvent) {

        userModel = onClickUserModelEvent.userModel;
        Log.d(TAG, "loadData: " + userModel.getHoten() + " " + userModel.getAnhAvatar());

        Picasso.with(getActivity()).load(userModel.getAnhAvatar()).transform(new CropCircleTransformation()).into(ivAvatar);

        base64 = userModel.getAnhAvatar();
        String[] sBase64 = base64.split(",");
        Bitmap bitmap = BitmapFactory.decodeByteArray(
                Base64.decode(sBase64[0], Base64.DEFAULT),
                0,// offset: vị trí bđ
                (Base64.decode(sBase64[0], Base64.DEFAULT)).length

        );

        ivAvatar.setImageBitmap(cropCircleTransformation.transform(bitmap));
        tvName.setText(userModel.getHoten());
    }

    private void setupUI() {
        btnAuction = view.findViewById(R.id.btBuy);
        btnClassified = view.findViewById(R.id.btDauGia);
        ivAvatar = view.findViewById(R.id.iv_avatar);
        tvName = view.findViewById(R.id.tv_name);

        viewPager = view.findViewById(R.id.vp_history);
         tabLayout = view.findViewById(R.id.tl_history);
    }

    public void loadDataForTabLayout() {
//        tabLayout.addTab(tabLayout.newTab().setContentDescription("PRODUCTS"));
//        tabLayout.addTab(tabLayout.newTab().setContentDescription("AUCTIONS"));
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                viewPager.setCurrentItem(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
//        ViewPagerProfileAdapter viewPagerProfileAdapter = new ViewPagerProfileAdapter(getFragmentManager());
//        viewPager.setAdapter(viewPagerProfileAdapter);
//        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    private void intentPostClassified() {
        EventBus.getDefault().postSticky(new OnClickAddSanPhamEvent(userModel) );
        Intent intent = new Intent(getActivity(), DangSpRvActivity.class);
        startActivity(intent);
    }

    private void intentPostAution() {
        EventBus.getDefault().postSticky(new OnClickAddSanPhamEvent(userModel) );
        Intent intent = new Intent(getActivity(), DangSpDGActivity.class);
        startActivity(intent);
    }
}
