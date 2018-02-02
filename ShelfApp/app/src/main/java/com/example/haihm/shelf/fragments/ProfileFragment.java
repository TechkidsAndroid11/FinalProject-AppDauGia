package com.example.haihm.shelf.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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
import com.example.haihm.shelf.adapters.ViewPagerProfileAdapter;
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

    Button btnAuction, btnClassified;
    ImageView ivCover, ivAvatar;
    TextView tvName;
    UserModel userModel;
    String base64;
    ViewPager viewPager;
    TabLayout tabLayout;
    public ProfileFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        setupUI(view);
       //loadDataForTabLayout();
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
        Log.d(TAG, "loadData: "+userModel.getHoten()+" "+userModel.getSdt());
        Log.d(TAG, userModel.getAnhAvatar());
        Picasso.with(getActivity()).load(userModel.getAnhAvatar()).transform(new CropCircleTransformation()).into(ivAvatar);
        tvName.setText(userModel.getHoten());
    }

    private void setupUI(View view) {
        btnAuction = view.findViewById(R.id.btBuy);
        btnClassified = view.findViewById(R.id.btDauGia);
        ivAvatar = view.findViewById(R.id.iv_avatar);
        tvName = view.findViewById(R.id.tv_name);
        tabLayout=view.findViewById(R.id.tab_history);
        viewPager = view.findViewById(R.id.vp_history);

    }

    public void loadDataForTabLayout() {
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.getTabAt(0).setText("Sản Phẩm");
        tabLayout.getTabAt(1).setText("Đấu Giá");

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        ViewPagerProfileAdapter viewPagerProfileAdapter = new ViewPagerProfileAdapter(getFragmentManager());
        viewPager.setAdapter(viewPagerProfileAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    private void intentPostClassified() {
        EventBus.getDefault().postSticky(new OnClickAddSanPhamEvent(userModel));
        Intent intent = new Intent(getActivity(), DangSpRvActivity.class);
        startActivity(intent);
    }

    private void intentPostAution() {
        EventBus.getDefault().postSticky(new OnClickAddSanPhamEvent(userModel));
        Intent intent = new Intent(getActivity(), DangSpDGActivity.class);
        startActivity(intent);
    }
}
