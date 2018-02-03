package com.example.haihm.shelf.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.haihm.shelf.fragments.HistoryAutionFragment;
import com.example.haihm.shelf.fragments.HistoryProductFragment;
import com.example.haihm.shelf.model.UserModel;

/**
 * Created by Trần_Tân on 01/02/2018.
 */

public class ViewPagerHistoryAdapter extends FragmentStatePagerAdapter {

    public ViewPagerHistoryAdapter(FragmentManager fm) {
        super(fm);

    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0 : return new HistoryProductFragment();
            case 1 : return new HistoryAutionFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
