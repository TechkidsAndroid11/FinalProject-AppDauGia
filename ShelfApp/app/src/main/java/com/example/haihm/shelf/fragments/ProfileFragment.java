package com.example.haihm.shelf.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.haihm.shelf.R;
import com.example.haihm.shelf.activity.ChangePasswordActivity;
import com.example.haihm.shelf.activity.DangSpDGActivity;
import com.example.haihm.shelf.activity.DangSpRvActivity;

import com.example.haihm.shelf.activity.LoginActivity;
import com.example.haihm.shelf.adapters.ViewPagerHistoryProfileAdapter;
import com.example.haihm.shelf.event.OnClickAddSanPhamEvent;
import com.example.haihm.shelf.event.OnClickUserModelEvent;
import com.example.haihm.shelf.model.UserModel;
import com.example.haihm.shelf.utils.Utils;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {
    private static final String TAG = "ProfileFragment";

    Button btnAuction, btnClassified;
    ImageView ivCover, ivAvatar,ivSetting;
    TextView tvName;
    UserModel userModel;
    ViewPager vpHistory;
    TabLayout tabHistory;
    AppBarLayout appBar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;
    ScrollView scrollView;
    public ProfileFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        setupUI(view);
        ivSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view);
            }
        });
        EventBus.getDefault().register(this);
        loadHistory();
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


    public void showPopup(View v)
    {
        PopupMenu popupMenu = new PopupMenu(getActivity(),v);
        if(userModel.getPassword()!=null)
        {
            popupMenu.getMenu().add("Đổi mật khẩu").setTitle("Đổi mật khẩu");
        }
        popupMenu.getMenu().add("Đăng xuất").setTitle("Đăng xuất");
        popupMenu.setOnMenuItemClickListener(this);
       // popupMenu.inflate(R.menu.main);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        if(item.getTitle().equals("Đổi mật khẩu"))
        {
            Log.d(TAG, "onMenuItemClick: OKKKK");
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            startActivity(intent);

        }
        else if(item.getTitle().equals("Đăng xuất"))
        {
            logout();
        }
        return true;
    }

    private void logout() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPre", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =sharedPreferences.edit();

        String Uid = sharedPreferences.getString("UserId","NotFound");
        editor.clear();
        editor.commit();
        Toast.makeText(getActivity(), "Bạn đã đăng xuất thành công!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        Log.d(TAG, "logout: "+Uid);

    }

    private void loadHistory() {
        tabHistory.addTab(tabHistory.newTab().setText("Sản phẩm"));
        tabHistory.addTab(tabHistory.newTab().setText("Đấu giá"));

        tabHistory.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vpHistory.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        ViewPagerHistoryProfileAdapter adapter = new ViewPagerHistoryProfileAdapter(getFragmentManager());
        vpHistory.setAdapter(adapter);
        vpHistory.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabHistory));

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
        vpHistory = view.findViewById(R.id.vp_history);
        tabHistory = view.findViewById(R.id.tab_history);
        ivSetting = view.findViewById(R.id.iv_setting);
        appBar = view.findViewById(R.id.app_bar);
        collapsingToolbarLayout= view.findViewById(R.id.toolbar_layout);
        toolbar = view.findViewById(R.id.toolbar);
//        scrollView = view.findViewById(R.id.scrollView);
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
