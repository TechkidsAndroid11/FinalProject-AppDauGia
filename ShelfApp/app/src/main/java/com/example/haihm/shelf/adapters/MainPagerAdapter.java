package com.example.haihm.shelf.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.haihm.shelf.fragments.AuctionFragment;
import com.example.haihm.shelf.fragments.ProfileFragment;
import com.example.haihm.shelf.fragments.ShoppingFragment;
import com.example.haihm.shelf.utils.Utils;

/**
 * Created by Son Hoang on 1/9/2018.
 */

public class MainPagerAdapter extends FragmentStatePagerAdapter {
    private static final int TAB_COUNT = 3;
    private String query;
    private boolean isSearchable;

    public MainPagerAdapter(FragmentManager fm, boolean isSearchable) {
        super(fm);
        this.isSearchable = isSearchable;
    }

    public MainPagerAdapter(FragmentManager fm, String query, boolean isSearchable) {
        super(fm);
        this.query = query;
        this.isSearchable = isSearchable;
    }

    @Override
    public Fragment getItem(int position) {
        if (!isSearchable) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(Utils.IS_SEARCHABLE, false);
            switch (position) {
                case 0:
                    ShoppingFragment shoppingFragment = new ShoppingFragment();
                    shoppingFragment.setArguments(bundle);
                    return shoppingFragment;
                case 1:
                    return new AuctionFragment();
                case 2:
                    return new ProfileFragment();
            }
        } else {
            Bundle bundle = new Bundle();
            bundle.putBoolean(Utils.IS_SEARCHABLE, true);
            bundle.putString(Utils.SEARCH_QUERY, query);
            switch (position){
                case 0:
                    ShoppingFragment shoppingFragment = new ShoppingFragment();
                    shoppingFragment.setArguments(bundle);
                    return shoppingFragment;
                case 1:
                    return new AuctionFragment();
                case 2:
                    return new ProfileFragment();
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }

}
