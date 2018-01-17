package com.example.haihm.shelf.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by ThangPham on 1/16/2018.
 */

public class ViewPagerProfileAdapter extends FragmentPagerAdapter {
    public ViewPagerProfileAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
            {
                break;
            }
            case 1:
            {
                break;
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
