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
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentStatePagerItemAdapter;

import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShoppingFragment extends Fragment {
    private static final String TAG = ShoppingFragment.class.toString();
    public static final String PRODUCT_TYPE = "Product_Type";
    SmartTabLayout stlProductType;
    ViewPager vpProductList;
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
        stlProductType = view.findViewById(R.id.stl_product_type);
        vpProductList = view.findViewById(R.id.vp_product_list);

        productTypes = getResources().getStringArray(R.array.loai_sp);
        productTypeList = Arrays.asList(productTypes);

        setupProductTypeTab(view);
    }


    private void setupProductTypeTab(View view) {
        FragmentPagerItems fragmentPagerItems = new FragmentPagerItems(view.getContext());
        for (String productType : productTypeList) {
            Bundle bundle = new Bundle();
            bundle.putString(PRODUCT_TYPE, productType);
            fragmentPagerItems.add(FragmentPagerItem.of(productType, ShoppingProductFragment.class, bundle));
        }

        final FragmentStatePagerItemAdapter fragmentStatePagerItemAdapter = new FragmentStatePagerItemAdapter(getChildFragmentManager(), fragmentPagerItems);

        vpProductList.setAdapter(fragmentStatePagerItemAdapter);
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
