package com.example.haihm.shelf.adapters;

import android.os.Bundle;
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
    public static final String IS_AUCTION = "IS_AUCTION";

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return setupFragment(false);

            case 1:
                return setupFragment(true);

            case 2: return new ProfileFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }

    private ShoppingFragment setupFragment(boolean isAuction){
        Bundle bundle = new Bundle();
        bundle.putBoolean(IS_AUCTION, isAuction);
        ShoppingFragment shoppingFragment = new ShoppingFragment();
        shoppingFragment.setArguments(bundle);
        return shoppingFragment;
    }
}
