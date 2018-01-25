package com.example.haihm.shelf.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.haihm.shelf.fragments.AuctionDetailsFragment;
import com.example.haihm.shelf.fragments.ChatingFragment;
import com.example.haihm.shelf.model.SanPhamDauGia;

/**
 * Created by Trần_Tân on 20/01/2018.
 */

public class ViewPagerAutionDetailsAdapter extends FragmentStatePagerAdapter {

    public ViewPagerAutionDetailsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new AuctionDetailsFragment();
            case 1:
                return new ChatingFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
