package com.example.haihm.shelf.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

;

/**
 * Created by ThangPham on 11/22/2017.
 */

public class Utils {
    public static void openFragment(FragmentManager fragmentManagert, int layoutId, Fragment fragment) {
        // quản lý việc thêm, sửa, xóa or thay thế của Fragment
        FragmentTransaction fragmentTransaction = fragmentManagert.beginTransaction();
        fragmentTransaction.add(layoutId, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    public static void openFragmentNotBack(FragmentManager fragmentManagert, int layoutId, Fragment fragment) {
        // quản lý việc thêm, sửa, xóa or thay thế của Fragment
        FragmentTransaction fragmentTransaction = fragmentManagert.beginTransaction();
        fragmentTransaction.add(layoutId, fragment);
        fragmentTransaction.commit();
    }

}
