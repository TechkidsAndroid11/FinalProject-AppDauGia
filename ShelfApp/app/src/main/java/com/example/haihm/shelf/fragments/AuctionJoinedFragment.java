package com.example.haihm.shelf.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.haihm.shelf.R;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class AuctionJoinedFragment extends Fragment {
    public static final String PRODUCT_TYPE = "Product_Type";
    SmartTabLayout stlAuctionProduct;
    ViewPager vpAuctionProduct;
    private String[] productTypes;
    private List<String> productTypeList;
    private boolean isJoined;

    public AuctionJoinedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_auction_joined, container, false);
        setupUI(view);
        return view;
    }

    private void setupUI(View view) {
        stlAuctionProduct = view.findViewById(R.id.stl_auction_product);
        vpAuctionProduct = view.findViewById(R.id.vp_auction_product);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            isJoined = bundle.getBoolean(AuctionFragment.JOINED);
        }

        //get product type list
        productTypes = getResources().getStringArray(R.array.loai_sp);
        productTypeList = Arrays.asList(productTypes);

        if (!isJoined) {
            stlAuctionProduct.setVisibility(View.VISIBLE);
            setupProductTypeTab(view);
        } else {
            stlAuctionProduct.setVisibility(View.GONE);
        }
    }

    private void setupProductTypeTab(View view) {
        FragmentPagerItems fragmentPagerItems = new FragmentPagerItems(view.getContext());
        for (String productType : productTypeList) {
            Bundle bundle = new Bundle();
            bundle.putString(PRODUCT_TYPE, productType);
            fragmentPagerItems.add(FragmentPagerItem.of(productType, AuctionProductFragment.class, bundle));
        }
        FragmentPagerItemAdapter fragmentPagerItemAdapter = new FragmentPagerItemAdapter(getChildFragmentManager(), fragmentPagerItems);

        vpAuctionProduct.setAdapter(fragmentPagerItemAdapter);
        stlAuctionProduct.setViewPager(vpAuctionProduct);
    }

}
