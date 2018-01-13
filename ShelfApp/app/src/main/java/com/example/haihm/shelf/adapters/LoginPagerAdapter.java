package com.example.haihm.shelf.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.haihm.shelf.fragments.LoginFragment;
import com.example.haihm.shelf.fragments.RegisterFragment;

/**
 * Created by Son Hoang on 1/8/2018.
 */

public class LoginPagerAdapter extends FragmentStatePagerAdapter{
    private static final int TAB_COUNT = 2;
    Context context;
    public static Fragment loginFragment;

    public LoginPagerAdapter(FragmentManager fm,Context context) {
        super(fm);
        this.context= context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                loginFragment = new LoginFragment();
                return loginFragment;
            case 1: return new RegisterFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }
}
