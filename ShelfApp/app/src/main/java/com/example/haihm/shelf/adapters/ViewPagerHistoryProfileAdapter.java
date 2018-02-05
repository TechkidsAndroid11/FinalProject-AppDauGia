package com.example.haihm.shelf.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.example.haihm.shelf.fragments.HistoryAuctionProfileFragment;
import com.example.haihm.shelf.fragments.HistoryProductProfileFragment;

/**
 * Created by ThangPham on 2/3/2018.
 */

public class ViewPagerHistoryProfileAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = "ViewPagerHistoryProfile";
    public ViewPagerHistoryProfileAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
            {
                return new HistoryProductProfileFragment();
            }
            case 1:
            {
                return new HistoryAuctionProfileFragment();
            }
        }
        return  null;
    }

    @Override
    public int getCount() {
        Log.d(TAG, "getCount: history");
        return 2;
    }
}
