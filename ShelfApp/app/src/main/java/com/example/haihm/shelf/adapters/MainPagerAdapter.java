package com.example.haihm.shelf.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.haihm.shelf.fragments.AuctionFragment;
import com.example.haihm.shelf.fragments.ProfileFragment;
import com.example.haihm.shelf.fragments.ShoppingFragment;

/**
 * Created by Son Hoang on 1/9/2018.
 */

public class MainPagerAdapter extends FragmentStatePagerAdapter {
    private static final int TAB_COUNT = 3;


    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ShoppingFragment();
            case 1:
                return new AuctionFragment();
            case 2:
                return new ProfileFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }

}
