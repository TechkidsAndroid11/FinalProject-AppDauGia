package com.example.haihm.shelf.fragments;


import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.haihm.shelf.R;
import com.example.haihm.shelf.adapters.MainPagerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShoppingFragment extends Fragment {
    private static final String TAG = ShoppingFragment.class.toString();
    public static final String PRODUCT_TYPE = "Product_Type";
    RecyclerView rvItemTypeList;
    ConstraintLayout clAuction;
    SmartTabLayout stlProductType;
    ViewPager vpProductList;
    boolean isAuction;
    private String[] productTypes;
    private List<String> productTypeList;

    public ShoppingFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shopping, container, false);
        setupUI(view);

        return view;
    }

    private void setupUI(final View view) {
        rvItemTypeList = view.findViewById(R.id.rv_list_product);
        clAuction = view.findViewById(R.id.cl_auction);
        stlProductType = view.findViewById(R.id.stl_product_type);
        vpProductList = view.findViewById(R.id.vp_product_list);

        productTypes = getResources().getStringArray(R.array.loai_sp);
        productTypeList = Arrays.asList(productTypes);

        //is auction or not
        Bundle bundle = this.getArguments();
        if (bundle != null){
            isAuction = bundle.getBoolean(MainPagerAdapter.IS_AUCTION);
        }

        if (isAuction){
            setupProductTypeTab(view);
        } else {
            setupProductTypeTab(view);
        }

    }


    private void setupProductTypeTab(View view) {
        FragmentPagerItems fragmentPagerItems = new FragmentPagerItems(view.getContext());
        for (String productType : productTypeList) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(MainPagerAdapter.IS_AUCTION, isAuction);
            bundle.putString(PRODUCT_TYPE, productType);
            fragmentPagerItems.add(FragmentPagerItem.of(productType, ProductTypeFragment.class, bundle));
        }

        final FragmentPagerItemAdapter fragmentPagerItemAdapter = new FragmentPagerItemAdapter(getChildFragmentManager(), fragmentPagerItems);

        vpProductList.setAdapter(fragmentPagerItemAdapter);
        stlProductType.setViewPager(vpProductList);

        stlProductType.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


}
